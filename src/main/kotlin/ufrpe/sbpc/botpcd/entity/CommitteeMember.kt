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
    phoneNumber: String
): User(name = name, phoneNumber = phoneNumber)