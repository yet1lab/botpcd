package ufrpe.sbpc.botpcd.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ufrpe.sbpc.botpcd.entity.CommitteeMember
import ufrpe.sbpc.botpcd.entity.Monitor
import ufrpe.sbpc.botpcd.entity.MonitorAssistanceType
import ufrpe.sbpc.botpcd.entity.UserStatus

interface CommitteeMemberRepository: JpaRepository<CommitteeMember, Long> {
    fun findByPhoneNumber(phoneNumber: String): CommitteeMember?
    fun findByStatus(status: UserStatus): List<CommitteeMember>
    @Query("SELECT cm from CommitteeMember cm where cm.status = :status")
    fun findAvailableCommitteeMember(status: UserStatus): List<CommitteeMember>
}