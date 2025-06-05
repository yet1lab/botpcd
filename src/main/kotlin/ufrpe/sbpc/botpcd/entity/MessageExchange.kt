package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
class MessageExchange(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var fromPhoneNumber: String,
    var toPhoneNumber: String,
    @Column(columnDefinition = "TEXT")
    var message: String
) {
    @CreationTimestamp
    var createAt: LocalDateTime? = null
}