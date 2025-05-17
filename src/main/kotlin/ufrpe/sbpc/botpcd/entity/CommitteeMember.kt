package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.validation.constraints.NotEmpty

/**
 *
 */
@Entity
class CommitteeMember(
    name: String,
    phoneNumber: String,
    status: UserStatus
): Attendant(name = name, phoneNumber = phoneNumber, status = status)