package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table
import jakarta.validation.constraints.NotEmpty

/**

 *
 * @param id The unique identifier of the user.
 * @param name The name of the user.
 * @param phoneNumber The phone number of the user.
 * @param phoneNumberId used by whatsapp to callback a message when you recive a message in webhook
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "tb_user")
abstract class User(
    @Id @GeneratedValue
    var id: Long? = null,
    var name: String? = null,
    @NotEmpty(message = "The User needs to have a phone number")
    var phoneNumber: String
)