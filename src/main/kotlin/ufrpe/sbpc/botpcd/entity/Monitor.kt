package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotEmpty
import lombok.Data

@Entity
@Data
@Table(name = "tb_monitor")
class Monitor(
    id: Long,
    name: String,
    phoneNumber: String,
    @Enumerated(value = EnumType.STRING)
    @NotEmpty(message = "Monitor needs to have an assistance type")
    var assistanceTypes: AssistanceType,
) : User(id, name, phoneNumber)