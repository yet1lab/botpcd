package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Entity

/**
 *
 */
@Entity
class CommitteeMember(
    name: String,
    phoneNumber: String
): User(name = name, phoneNumber = phoneNumber)