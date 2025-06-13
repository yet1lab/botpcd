package ufrpe.sbpc.botpcd.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.entity.*
import ufrpe.sbpc.botpcd.repository.MonitorRepository
import ufrpe.sbpc.botpcd.repository.AttendanceRepository
import ufrpe.sbpc.botpcd.repository.CommitteeMemberRepository
import org.springframework.transaction.annotation.Transactional
import ufrpe.sbpc.botpcd.util.createOptions

@Service
class AttendantStatusService(
    private val whatsappService: WhatsappService,
    private val monitorRepository: MonitorRepository,
    private val attendanceRepository: AttendanceRepository,
    private val committeeMemberRepository: CommitteeMemberRepository
) {
    val logger: Logger = LoggerFactory.getLogger(AttendantStatusService::class.java)

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

    private fun updateAttendantStatus(attendant: Attendant, newStatus: UserStatus) {
        when (attendant) {
            is Monitor -> setMonitorStatus(attendant, newStatus)
            is CommitteeMember -> setCommitteeMemberStatus(attendant, newStatus)
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
    fun processStatusChangeResponse(attendant: Attendant, userResponse: String, botPhoneNumber: String) {
        var confirmationMessage: String? = null
        val userPhoneNumber = attendant.phoneNumber

        when (attendant.status) {
            UserStatus.AVAILABLE -> {
                when (userResponse) {
                    "1" -> {
                        updateAttendantStatus(attendant, UserStatus.UNAVAILABLE)
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
                when (userResponse) {
                    "1" -> {
                        updateAttendantStatus(attendant, UserStatus.AVAILABLE)
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
                    when (userResponse) {
                        "1" -> {
                            updateAttendantStatus(attendant, UserStatus.AVAILABLE)
                            whatsappService.sendMessage(
                                botPhoneNumber,
                                attendance.pwd.phoneNumber,
                                "Atendimento encerrado",
                                "BotPCD"
                            )
                            confirmationMessage = "Atendimento encerrado. Seu status foi atualizado para Disponível."
                        }

                        "2" -> {
                            updateAttendantStatus(attendant, UserStatus.UNAVAILABLE)
                            whatsappService.sendMessage(
                                botPhoneNumber,
                                attendance.pwd.phoneNumber,
                                "Atendimento encerrado",
                                "BotPCD"
                            )
                            confirmationMessage = "Atendimento encerrado. Seu status foi atualizado para Indisponível."
                        }

                        "cancelar" -> {
                            confirmationMessage = "Você continua em atendimento."
                        }

                        else -> {
                            confirmationMessage = "Opção inválida. Seu status permanece Ocupado."
                        }
                    }
                } else {
                    logger.warn("Atendente está ocupardo  ${attendant.name} sem atendimento")
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
}
