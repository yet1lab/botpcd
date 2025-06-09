package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

/**
 *
 */
@Entity
@Table(name = "tb_attendance")
class Attendance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @NotNull(message = "The service type is required")
    @Convert(converter = ServiceTypeConverter::class)
    var serviceType: ServiceType,

    @ManyToOne
    @JoinColumn(name = "pwd_id")
    @NotNull(message = "The PWD is required")
    var pwd: PWD,

    @ManyToOne
    @JoinColumn(name = "attendant_id")
    var attendant: Attendant? = null,

    @Enumerated(EnumType.STRING)
    @NotNull(message = "The attendant type is required")
    var attendantType: Provider,

    // Campos opcionais que serão atualizados durante o ciclo de vida do atendimento
    var startDateTime: LocalDateTime? = null,

    var monitorArrivalDateTime: LocalDateTime? = null,

    @Embedded
    var serviceLocation: LocationEmbeddable? = null,

    var endDateTime: LocalDateTime? = null
) {
    @CreationTimestamp
    lateinit var requestDateTime: LocalDateTime
}
