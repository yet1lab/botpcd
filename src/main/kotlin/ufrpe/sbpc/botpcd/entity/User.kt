package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.validation.constraints.NotEmpty

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    var name: String,
    @NotEmpty(message = "The User needs to have a phone number")
    val phoneNumber: String
)