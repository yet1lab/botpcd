package ufrpe.sbpc.botpcd.service

import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.entity.*
import ufrpe.sbpc.botpcd.repository.MonitorRepository
import ufrpe.sbpc.botpcd.repository.CommitteeMemberRepository
import org.springframework.transaction.annotation.Transactional

@Service
class AttendantStatusService(
    private val monitorRepository: MonitorRepository,
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

    fun sendStatusChanger(attendant: Attendant) {
        if (attendant.status == UserStatus.UNAVAILABLE) {
            /*1 - Ficar Disponível*/
            /*
            "Olá, você está Indisponível no momento. Deseja ficar Disponível para receber atendimentos?"
            */
        }
        if (attendant.status == UserStatus.BUSY) {
            /*
            1 - Encerrar Atendimento (vai internamente mudar o status dele pra disponível)

            2 - Ficar Indisponível (ele para de ser chamado para atendimentos)
            */
            /*
            "Olá, você está em atendimento. Deseja encerrá-lo e continuar Disnpoível ou deseja ficar Indisponível?"
             */
        }
        if (attendant.status == UserStatus.AVAILABLE) {
            /*1 - Ficar Indisponível*/
            /*
            "Olá, você está Disponível no momento. Deseja ficar Indisponível para não receber atendimentos?"
            */
        }
    }
    
    fun findAvailableMonitors(): List<Monitor> {
        return monitorRepository.findByStatus(UserStatus.AVAILABLE)
    }
    
    fun findAvailableCommitteeMembers(): List<CommitteeMember> {
        return committeeMemberRepository.findByStatus(UserStatus.AVAILABLE)
    }
    
    // Método para filtrar por tipo de assistência também
    fun findAvailableMonitorsByType(assistanceType: MonitorAssistanceType): List<Monitor> {
        val availableMonitors = findAvailableMonitors()
        return availableMonitors.filter { it.assistanceTypes == assistanceType }
    }
}