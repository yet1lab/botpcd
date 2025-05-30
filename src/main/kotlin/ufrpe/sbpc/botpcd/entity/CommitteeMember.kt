package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table

/**
 *
 */
@Entity
@Table(name = "committee_member")
class CommitteeMember(
    name: String,
    phoneNumber: String,
    status: UserStatus
): Attendant(name = name, phoneNumber = phoneNumber, status = status)