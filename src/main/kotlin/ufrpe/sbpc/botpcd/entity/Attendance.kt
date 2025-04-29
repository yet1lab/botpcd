package ufrpe.sbpc.botpcd.entity

import com.whatsapp.api.domain.webhook.Location
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Entity
@Table(name = "tb_attendance")
class Attendance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @NotNull(message = "The service type is required")
    val serviceType: AssistanceType,

    @NotNull(message = "The request date and time is required")
    val requestDateTime: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "pwd_id")
    @NotNull(message = "The PWD is required")
    val pwd: PWD,

    @ManyToOne
    @JoinColumn(name = "monitor_id")
    @NotNull(message = "The monitor is required")
    val monitor: Monitor,

    // Campos opcionais que serão atualizados durante o ciclo de vida do atendimento
    var acceptDateTime: LocalDateTime? = null,

    var monitorArrivalDateTime: LocalDateTime? = null,

    @Embedded
    var serviceLocation: LocationEmbeddable? = null,

    var endDateTime: LocalDateTime? = null
)

