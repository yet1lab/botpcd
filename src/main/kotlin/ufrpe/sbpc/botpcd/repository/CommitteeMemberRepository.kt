package ufrpe.sbpc.botpcd.repository

import org.springframework.data.jpa.repository.JpaRepository
import ufrpe.sbpc.botpcd.entity.CommitteeMember

interface CommitteeMemberRepository: JpaRepository<CommitteeMember, Long> {
    fun findByPhoneNumber(phoneNumber: String): CommitteeMember?
}