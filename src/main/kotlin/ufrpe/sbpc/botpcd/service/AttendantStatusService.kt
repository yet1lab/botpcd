package ufrpe.sbpc.botpcd.service

import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.entity.*
import ufrpe.sbpc.botpcd.repository.MonitorRepository
import ufrpe.sbpc.botpcd.repository.CommitteeMemberRepository
import org.springframework.transaction.annotation.Transactional

@Service
class AttendantStatusService(
    private val monitorRepository: MonitorRepository,
    private val committeeMemberRepository: CommitteeMemberRepository,
    private val whatsappService: WhatsappService
) {

    object StatusChangeMessages {
        const val UNAVAILABLE_ATTENDANT_TEXT = "Olá, você está Indisponível no momento. Digite 1 se Deseja ficar Disponível para receber atendimentos?\n1 - Ficar Disponível"
        const val BUSY_ATTENDANT_TEXT = "Olá, você está em atendimento. Deseja encerrá-lo e continuar Disponível ou deseja ficar Indisponível?\n1 - Encerrar Atendimento\n2 - Ficar Indisponível"
        const val AVAILABLE_ATTENDANT_TEXT = "Olá, você está Disponível no momento. Deseja ficar Indisponível para não receber atendimentos?\n1 - Ficar Indisponível"
    }

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
            UserStatus.UNAVAILABLE -> StatusChangeMessages.UNAVAILABLE_ATTENDANT_TEXT
            UserStatus.BUSY -> StatusChangeMessages.BUSY_ATTENDANT_TEXT
            UserStatus.AVAILABLE -> StatusChangeMessages.AVAILABLE_ATTENDANT_TEXT
        }
        whatsappService.sendMessage(botPhoneNumber, userPhoneNumber, messageToSend)
    }

    @Transactional
    fun processStatusChangeResponse(attendant: Attendant, userResponse: String, botPhoneNumber: String) {
        val userPhoneNumber = attendant.phoneNumber
        var confirmationMessage: String? = null

        when (attendant.status) {
            UserStatus.UNAVAILABLE -> {
                if (userResponse == "1") {
                    updateAttendantStatus(attendant, UserStatus.AVAILABLE)
                    confirmationMessage = "Seu status foi atualizado para Disponível."
                } else {
                    confirmationMessage = "Opção inválida. Seu status permanece Indisponível."
                }
            }
            UserStatus.BUSY -> {
                when (userResponse) {
                    "1" -> {
                        updateAttendantStatus(attendant, UserStatus.AVAILABLE)
                        // terminar o atendimento
                        confirmationMessage = "Atendimento encerrado. Seu status foi atualizado para Disponível."
                    }
                    "2" -> {
                        updateAttendantStatus(attendant, UserStatus.UNAVAILABLE)
                        // terminar o atendimento
                        confirmationMessage = "Seu status foi atualizado para Indisponível."
                    }
                    else -> {
                        confirmationMessage = "Opção inválida. Seu status permanece Ocupado."
                    }
                }
            }
            UserStatus.AVAILABLE -> {
                if (userResponse == "1") {
                    updateAttendantStatus(attendant, UserStatus.UNAVAILABLE)
                    confirmationMessage = "Seu status foi atualizado para Indisponível."
                } else {
                    confirmationMessage = "Opção inválida. Seu status permanece Disponível."
                }
            }
        }
        confirmationMessage?.let { whatsappService.sendMessage(botPhoneNumber, userPhoneNumber, it) }
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