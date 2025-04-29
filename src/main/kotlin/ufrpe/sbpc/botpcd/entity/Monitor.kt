package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.Data

@Entity
@Data
@Table(name = "tb_monitor")
class Monitor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    @Enumerated(value = EnumType.STRING)
    var assistanceTypes: MutableSet<AssistanceType> = mutableSetOf(),
)