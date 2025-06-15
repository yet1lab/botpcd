package ufrpe.sbpc.botpcd.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ufrpe.sbpc.botpcd.entity.*
import ufrpe.sbpc.botpcd.entity.Attendance
import ufrpe.sbpc.botpcd.entity.Attendant
import ufrpe.sbpc.botpcd.entity.Disability
import ufrpe.sbpc.botpcd.entity.Monitor
import ufrpe.sbpc.botpcd.entity.MonitorServiceType
import ufrpe.sbpc.botpcd.entity.PWD
import ufrpe.sbpc.botpcd.entity.Provider
import ufrpe.sbpc.botpcd.entity.ServiceType
import ufrpe.sbpc.botpcd.entity.UserStatus
import ufrpe.sbpc.botpcd.repository.AttendanceRepository
import ufrpe.sbpc.botpcd.repository.CommitteeMemberRepository
import ufrpe.sbpc.botpcd.repository.MessageExchangeRepository
import ufrpe.sbpc.botpcd.repository.MonitorRepository
import ufrpe.sbpc.botpcd.service.QueueService
import ufrpe.sbpc.botpcd.util.createOptions
import java.time.LocalDateTime

@Service
class AttendanceService(
		private val queryService: QueueService,
    private val attendanceRepository: AttendanceRepository,
    private val whatsappService: WhatsappService,
    private val monitorRepository: MonitorRepository,
    private val committeeMemberRepository: CommitteeMemberRepository,
    private val messageExchangeRepository: MessageExchangeRepository,
) {
    @Transactional
    fun setMonitorStatus(monitor: Monitor, status: UserStatus) {
        monitor.status = status
        monitorRepository.save(monitor)
    }

    @Transactional
    fun setCommitteeMemberStatus(member: CommitteeMember, status: UserStatus) {
        member.status = status
        committeeMemberRepository.save(member)
    }

    private fun updateAttendantStatus(botNumber: String, attendant: Attendant, newStatus: UserStatus) {
        when (attendant) {
            is Monitor -> {
                setMonitorStatus(attendant, newStatus)
                if(newStatus == UserStatus.AVAILABLE) {
                    val serviceType = ServiceType.getServiceByMonitorAssistanceType(attendant.assistanceType)
                    attendanceRepository.findRequestAttendanceOfService(serviceType)?.let{requestAttendance ->
                        directToAvailableAttendant(botNumber, requestAttendance.pwd, serviceType)
                    }
                }
            }
            is CommitteeMember -> {
                setCommitteeMemberStatus(attendant, newStatus)
                if(newStatus == UserStatus.AVAILABLE) {
                    attendanceRepository.findPendingCommitteeMemberAttendances().firstOrNull()?.let { attendance ->
                        directToAvailableAttendant(botNumber, attendance.pwd, attendance.serviceType)
                    }
                }
            }
        }
    }
    fun createOptionsWithCancel(options: List<String>, header: String = "", author: String = ""): String {
        var msg = createOptions(options, header, author)
        return "${msg}- Escreva \"cancelar\" para sair do menu"
    }
    val changeStatusTextOptionsFor = mapOf(
        "AVAILABLE" to createOptionsWithCancel(
            listOf("Ficar Indisponível"),
            "Você está *Disponível* no momento", "BotPCD"
        ),
        "UNAVAILABLE" to createOptionsWithCancel(
            listOf("Ficar Disponível"),
            "Você está *Indisponível* no momento", "BotPCD"
        ),
        "BUSY" to createOptionsWithCancel(
            listOf(
                "Encerrar atendimento e Ficar Disponível",
                "Encerrar atendimento e Ficar Indisponível"
            ),
            "Você está *em atendimento*", "BotPCD"
        )
    )

    fun sendStatusChanger(attendant: Attendant, botPhoneNumber: String) {
        val userPhoneNumber = attendant.phoneNumber
        val messageToSend = when (attendant.status) {
            UserStatus.AVAILABLE -> changeStatusTextOptionsFor[UserStatus.AVAILABLE.toString()]
            UserStatus.UNAVAILABLE -> changeStatusTextOptionsFor[UserStatus.UNAVAILABLE.toString()]
            UserStatus.BUSY -> changeStatusTextOptionsFor[UserStatus.BUSY.toString()]
        }
        whatsappService.sendMessage(botPhoneNumber, userPhoneNumber, messageToSend ?: "")
    }

    @Transactional
    fun processStatusChangeResponse(botNumber: String, attendant: Attendant, userResponse: String, botPhoneNumber: String) {
        var confirmationMessage: String? = null
        val userPhoneNumber = attendant.phoneNumber

        when (attendant.status) {
            UserStatus.AVAILABLE -> {
                when (userResponse.lowercase()) {
                    "1" -> {
                        updateAttendantStatus(botNumber, attendant, UserStatus.UNAVAILABLE)
                        confirmationMessage = "Seu status foi atualizado para Indisponível."
                    }

                    "cancelar" -> {
                        confirmationMessage = "Você continua Disponível."
                    }

                    else -> {
                        confirmationMessage = "Opção inválida. Seu status permanece Disponível."
                    }
                }
            }

            UserStatus.UNAVAILABLE -> {
                when (userResponse.lowercase()) {
                    "1" -> {
                        updateAttendantStatus(botNumber, attendant, UserStatus.AVAILABLE)
                        confirmationMessage = "Seu status foi atualizado para Disponível."
                    }

                    "cancelar" -> {
                        confirmationMessage = "Você continua Indisponível."
                    }

                    else -> {
                        confirmationMessage = "Opção inválida. Seu status permanece Indisponível."
                    }
                }
            }

            UserStatus.BUSY -> {
                val attendance = attendanceRepository.findStartedAttendanceOfAttendant(attendant)
                if (attendance != null) {
                    when (userResponse.lowercase()) {
                        "1" -> {
                            updateAttendantStatus(botNumber, attendant, UserStatus.AVAILABLE)
                            whatsappService.sendMessage(
                                botPhoneNumber,
                                attendance.pwd.phoneNumber,
                                "Atendimento encerrado",
                                "BotPCD"
                            )
                            confirmationMessage = "Atendimento encerrado. Seu status foi atualizado para Disponível."
                            finishAttendance(attendance)
                        }
                        "2" -> {
                            updateAttendantStatus(botNumber, attendant, UserStatus.UNAVAILABLE)
                            whatsappService.sendMessage(
                                botPhoneNumber,
                                attendance.pwd.phoneNumber,
                                "Atendimento encerrado",
                                "BotPCD"
                            )
                            confirmationMessage = "Atendimento encerrado. Seu status foi atualizado para Indisponível."
                            finishAttendance(attendance)
                        }

                        "cancelar" -> {
                            confirmationMessage = "Você continua em atendimento."
                        }

                        else -> {
                            confirmationMessage = "Opção inválida. Seu status permanece Ocupado."
                        }
                    }
                } else {
                    logger.warn("Atendente está ocupado  ${attendant.name} sem atendimento")
                }
            }
        }

        confirmationMessage?.let {
            whatsappService.sendMessage(botPhoneNumber, userPhoneNumber, it, "BotPCD")
        }
    }

    fun findAvailableMonitors(): List<Monitor> {
        return monitorRepository.findByStatus(UserStatus.AVAILABLE)
    }

    fun findAvailableCommitteeMembers(): List<CommitteeMember> {
        return committeeMemberRepository.findByStatus(UserStatus.AVAILABLE)
    }

    fun findAvailableMonitorsByType(assistanceType: MonitorAssistanceType): List<Monitor> {
        val availableMonitors = findAvailableMonitors()
        return availableMonitors.filter { it.assistanceType == assistanceType }
    }
    fun finishAttendance(attendance: Attendance) {
        attendance.apply {
            endDateTime = LocalDateTime.now()
        }
        attendanceRepository.save(attendance)
    }
    val awaitServiceMessage = "No momento não há atendentes disponíveis. Por favor, aguarde na fila de espera e retornaremos assim que possível."
    fun sendWaitListMessage(botNumber: String, pwd: PWD) {
        whatsappService.sendMessage(
            botNumber,
            pwd.phoneNumber,
            awaitServiceMessage
        )
    }
    val logger: Logger = LoggerFactory.getLogger(AttendanceService::class.java)

    fun sendServices(botNumber: String, pwd: PWD) {
        whatsappService.sendMessage(botNumber, pwd.phoneNumber, createSendServicesMessage(pwd.disabilities.first(), pwd))
    }

    fun createSendServicesMessage(disability: Disability, pwd: PWD): String {
        val serviceList = ServiceType.getServicesByDisability(disability)
        val adjective = disability.adjective

        return createOptions(
            serviceList.map { it -> it.description },
            header = "Olá ${pwd.name}. Percebi que você ${if (disability == Disability.MOBILITY_IMPAIRED) "tem $adjective" else "é $adjective"}. Os serviços disponíveis para você são\n"
					)
    }

    fun makeAttendantBusy(attendant: Attendant) {
        when (attendant) {
            is Monitor -> {
                setMonitorStatus(attendant, UserStatus.BUSY)
            }
            is CommitteeMember -> {
                setCommitteeMemberStatus(attendant, UserStatus.BUSY)
            }
        }
    }

    fun requestAttendance(pwd: PWD, service: ServiceType) {
        attendanceRepository.save(Attendance(serviceType = service, pwd = pwd, attendantType = service.attendantType))
    }

    fun startAttendance(
        pwd: PWD,
        botNumber: String,
        service: ServiceType
    ) {
        requestAttendance(pwd, service)
        directToAvailableAttendant(botNumber, pwd, service)
    }

    fun directToAvailableAttendant(botNumber: String, pwd: PWD, service: ServiceType) {
        val attendance = attendanceRepository.findRequestAttendanceOfPwd(pwd)
        if(attendance == null) {
            logger.warn("Um atendimento que não foi requsitado está tentando ser começado.")
            return
        }
        when (service.attendantType) {
            Provider.MONITOR -> {
                val monitors = monitorRepository.findAvailableMonitor(
                    UserStatus.AVAILABLE,
                    (service as MonitorServiceType).monitorAssistanceType
                )
                for (monitor in monitors) {
                    if (LocalDateTime.now().minusHours(24) < (messageExchangeRepository.lastExchangeMessage(
                            fromPhoneNumber = monitor.phoneNumber,
                            toPhoneNumber = botNumber
                        )?.createAt ?: LocalDateTime.now().minusHours(25))
                    ) {
                        whatsappService.sendMessage(
                            botNumber,
                            monitor.phoneNumber,
                            "Você irá começar o atendimento de ${pwd.name}."
                        )
                        whatsappService.sendMessage(
                            botNumber,
                            pwd.phoneNumber,
                            "O monitor ${monitor.name} irá realizar seu atendimento."
                        )
                        attendance.apply {
                            attendant = monitor
                            startDateTime = LocalDateTime.now()
                        }
                        attendanceRepository.save(attendance)
                        makeAttendantBusy(monitor)
                        return
                    } else {
                        logger.warn("Monitor ${monitor.name} com número ${monitor.phoneNumber} não enviou mensagem nas ultimas 24")
                    }
                }
                // fila de espera
                sendWaitListMessage(botNumber, pwd)
            }
            Provider.COMMITTEE_MEMBER -> {
                val committeeMembers = committeeMemberRepository.findAvailableCommitteeMember(UserStatus.AVAILABLE)
                for (member in committeeMembers) {
                    val lastMessageTime = messageExchangeRepository.lastExchangeMessage(
                        fromPhoneNumber = member.phoneNumber,
                        toPhoneNumber = botNumber
                    )?.createAt ?: LocalDateTime.now().minusHours(25)

                    if (LocalDateTime.now().minusHours(24) < lastMessageTime) {
                        whatsappService.sendMessage(
                            botNumber,
                            member.phoneNumber,
                            "Você irá começar o atendimento de ${pwd.name}."
                        )
                        whatsappService.sendMessage(
                            botNumber,
                            pwd.phoneNumber,
                            "O membro da comissão ${member.name} irá realizar seu atendimento."
                        )
                        attendance.apply {
                            attendant = member
                            startDateTime = LocalDateTime.now()
                        }
                        attendanceRepository.save(attendance)
												makeAttendantBusy(member)
                        return
                    } else {
                        logger.warn("Membro da comissão ${member.name} com número ${member.phoneNumber} não enviou mensagem nas ultimas 24")
                    }
                }
                sendWaitListMessage(botNumber, pwd)
            }
        }
    }

fun redirectMessageToAttendance(botNumber: String, message: String, attendant: Attendant, pwd: PWD) {
    whatsappService.sendMessage(
        botNumber,
        destinyNumberID = attendant.phoneNumber,
        """
                *${pwd.name}*:
$message
            """.trimIndent()
    )
}

fun redirectMessageToPwd(botNumber: String, message: String, pwd: PWD, attendant: Attendant) {
    whatsappService.sendMessage(
        botNumber,
        destinyNumberID = pwd.phoneNumber,
        """
                *${attendant.name}*:
$message
            """.trimIndent()
    )
}
}
