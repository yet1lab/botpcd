package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import jakarta.validation.constraints.NotEmpty

@Entity
@Table(name = "tb_monitor")
class Monitor(
    name: String,
    phoneNumber: String,
    status: UserStatus,
    @Enumerated(value = EnumType.STRING)
    @NotEmpty(message = "Monitor needs to have an assistance type")
    var assistanceType: MonitorAssistanceType
) : Attendant(name = name, phoneNumber = phoneNumber, status = status)