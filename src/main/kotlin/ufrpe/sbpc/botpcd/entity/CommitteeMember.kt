package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Entity

/**
 *
 */
@Entity
class CommitteeMember(
    id: Long,
    name: String,
    phoneNumber: String
): User(id, name, phoneNumber)