package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.time.LocalDateTime

/**
 * sentAt é quando a mensagem foi enviada no dispositivo esse valor vai não vai aplicar o timezone do Brasil que é -3 UTC
 */
@Entity
class MessageExchange(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var fromPhoneNumber: String,
    var toPhoneNumber: String,
    @Column(columnDefinition = "TEXT")
    var message: String,
    var sendAt: Instant
) {
     var createAt: LocalDateTime = LocalDateTime.now()
}