package ufrpe.sbpc.botpcd.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
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
import java.time.LocalDateTime

@Service
class AttendanceService(
		private val queryService: QueueService,
    private val attendanceRepository: AttendanceRepository,
    private val whatsappService: WhatsappService,
    private val attendantStatusService: AttendantStatusService,
    private val monitorRepository: MonitorRepository,
    private val committeeMemberRepository: CommitteeMemberRepository,
    private val messageExchangeRepository: MessageExchangeRepository,
) {
    val logger: Logger = LoggerFactory.getLogger(AttendanceService::class.java)
    fun sendServices(botNumber: String, pwd: PWD) {
        whatsappService.sendMessage(botNumber, pwd.phoneNumber, createSendServicesMessage(pwd.disabilities.first(), pwd))
    }

    fun createSendServicesMessage(disability: Disability, pwd: PWD): String {
        val serviceList = ServiceType.getServicesByDisability(disability)
        val adjective = disability.adjective

        return whatsappService.createOptions(
            serviceList.map { it -> it.description },
            header = "Olá ${pwd.name}. Percebi que você ${if (disability == Disability.MOBILITY_IMPAIRED) "tem $adjective" else "é $adjective"}. Os serviços disponíveis para você são\n"
					)
    }

    fun makeAttendantBusy(attendant: Attendant) {
        when (attendant) {
            is Monitor -> {
                attendantStatusService.setMonitorStatus(attendant, UserStatus.BUSY)
            }
            is CommitteeMember -> {
                attendantStatusService.setCommitteeMemberStatus(attendant, UserStatus.BUSY)
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
                        attendanceRepository.acceptPendingAttendanceForPwd(pwd, monitor, LocalDateTime.now())
                        makeAttendantBusy(monitor)
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
                        attendanceRepository.acceptPendingAttendanceForPwd(pwd, member, LocalDateTime.now())
												makeAttendantBusy(member)
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

fun sendWaitListMessage(botNumber: String, pwd: PWD) {
    whatsappService.sendMessage(
        botNumber,
        pwd.phoneNumber,
        "No momento não há atendentes disponíveis. Por favor, aguarde na fila de espera e retornaremos assim que possível."
    )
}
}
