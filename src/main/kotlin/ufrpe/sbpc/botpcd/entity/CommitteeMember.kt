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
    @Enumerated(EnumType.STRING)
    var status: UserStatus = UserStatus.AVAILABLE
): User(name = name, phoneNumber = phoneNumber)