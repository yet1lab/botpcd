package ufrpe.sbpc.botpcd.entity

import jakarta.annotation.Nullable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table
import jakarta.validation.constraints.NotEmpty

@Entity
@Table(name = "tb_attendant")
@Inheritance(strategy = InheritanceType.JOINED)
abstract class Attendant(
    @Enumerated(EnumType.STRING)
    var status: UserStatus = UserStatus.AVAILABLE,
    @Id @GeneratedValue
    @Nullable
    var id: Long? = null,
    var name: String? = null,
    @NotEmpty(message = "The User needs to have a phone number")
    @Column(unique = true)
    var phoneNumber: String
)