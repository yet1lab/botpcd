package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import jakarta.validation.constraints.NotEmpty

@Entity
@Table(name = "tb_attendant")
abstract class Attendant(
    name: String,
    phoneNumber: String,
    @Enumerated(EnumType.STRING)
    var status: UserStatus = UserStatus.AVAILABLE
) : User(name = name, phoneNumber = phoneNumber) {

}