package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

/**
 * Tanto quem tem deficiencia visua quanto de mobilidade pode requisitar um monitor de mobilidade
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

    @NotNull(message = "The request date and time is required")
    var requestDateTime: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "pwd_id")
    @NotNull(message = "The PWD is required")
    var pwd: PWD,

    @ManyToOne
    @JoinColumn(name = "attendant_id")
    @NotNull(message = "The attendant ID is required")
    var attendant: Attendant,

    @Enumerated(EnumType.STRING)
    @NotNull(message = "The attendant type is required")
    var attendantType: Provider,

    // Campos opcionais que serão atualizados durante o ciclo de vida do atendimento
    var acceptDateTime: LocalDateTime? = null,

    var monitorArrivalDateTime: LocalDateTime? = null,

    @Embedded
    var serviceLocation: LocationEmbeddable? = null,

    var endDateTime: LocalDateTime? = null
)
