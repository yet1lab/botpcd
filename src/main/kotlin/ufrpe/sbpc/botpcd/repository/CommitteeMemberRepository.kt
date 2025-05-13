package ufrpe.sbpc.botpcd.repository

import org.springframework.data.jpa.repository.JpaRepository
import ufrpe.sbpc.botpcd.entity.CommitteeMember
import ufrpe.sbpc.botpcd.entity.UserStatus

interface CommitteeMemberRepository: JpaRepository<CommitteeMember, Long> {
    fun findByPhoneNumber(phoneNumber: String): CommitteeMember?
    fun findByStatus(status: UserStatus): List<CommitteeMember>
}