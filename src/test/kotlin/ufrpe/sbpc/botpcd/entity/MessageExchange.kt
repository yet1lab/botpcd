package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import org.springframework.context.annotation.Profile
import java.time.LocalDateTime

@Entity
@Profile("test")
class MessageExchange(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var fromNumber: String,
    var toNumber: String,
    @Column(columnDefinition = "TEXT")
    var message: String
) {
    @CreationTimestamp
    var createAt: LocalDateTime? = null
}