package ufrpe.sbpc.botpcd.service

import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.entity.*
import ufrpe.sbpc.botpcd.repository.MonitorRepository
import ufrpe.sbpc.botpcd.repository.CommitteeMemberRepository
import org.springframework.transaction.annotation.Transactional

@Service
class UserStatusService(
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