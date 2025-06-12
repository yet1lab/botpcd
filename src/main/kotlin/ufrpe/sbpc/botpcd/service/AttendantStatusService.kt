package ufrpe.sbpc.botpcd.service

import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.entity.*
import ufrpe.sbpc.botpcd.repository.MonitorRepository
import ufrpe.sbpc.botpcd.repository.AttendanceRepository
import ufrpe.sbpc.botpcd.repository.CommitteeMemberRepository
import org.springframework.transaction.annotation.Transactional
@Service
class AttendantStatusService(
    private val whatsappService: WhatsappService,
    private val monitorRepository: MonitorRepository,
    private val attendanceRepository: AttendanceRepository,
    private val committeeMemberRepository: CommitteeMemberRepository
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

    private fun updateAttendantStatus(attendant: Attendant, newStatus: UserStatus) {
        when (attendant) {
            is Monitor -> setMonitorStatus(attendant, newStatus)
            is CommitteeMember -> setCommitteeMemberStatus(attendant, newStatus)
        }
    }

    fun sendStatusChanger(attendant: Attendant, botPhoneNumber: String) {
        val userPhoneNumber = attendant.phoneNumber
        val messageToSend = when (attendant.status) {
            UserStatus.AVAILABLE ->  whatsappService.createOptionsWithCancel(
							listOf("Continuar Disponível", "Ficar Indisponível"),
							"Você está *Disponível* no momento", "BotPCD"
						)
						UserStatus.UNAVAILABLE -> whatsappService.createOptionsWithCancel(
							listOf("Continuar Indisponível", "Ficar Disponível"),
							"Você está *Indisponível* no momento", "BotPCD"
						)
            UserStatus.BUSY ->  whatsappService.createOptionsWithCancel(
							listOf(
								"Continuar atendimento", 
								"Encerrar atendimento e Ficar Disponível",
								"Encerrar atendimento e Ficar Indisponível"),
							"Você está *em atendimento*", "BotPCD"
						)
        }
        whatsappService.sendMessage(botPhoneNumber, userPhoneNumber, messageToSend)
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
							when (userResponse) {
									"1" -> {
											updateAttendantStatus(attendant, UserStatus.AVAILABLE)
											whatsappService.sendMensage(botPhoneNumber, attendance.pwd.phoneNumber, "Atendimento encerrado", "BotPCD")
											confirmationMessage = "Atendimento encerrado. Seu status foi atualizado para Disponível."
									}
									"2" -> {
											updateAttendantStatus(attendant, UserStatus.UNAVAILABLE)
											whatsappService.sendMensage(botPhoneNumber, attendance.pwd.phoneNumber, "Atendimento encerrado", "BotPCD")
											confirmationMessage = "Atendimento encerrado. Seu status foi atualizado para Indisponível."
									}
									"cancelar" -> {
											confirmationMessage = "Você continua em atendimento."
									}
									else -> {
											confirmationMessage = "Opção inválida. Seu status permanece Ocupado."
									}
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
