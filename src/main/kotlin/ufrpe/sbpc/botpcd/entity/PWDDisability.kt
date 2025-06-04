package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name="tb_pwd_disability")
class PWDDisability(
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    val id: Long?= null,

    @Enumerated(EnumType.STRING)
    val disability: Disability,

    @ManyToOne
    @JoinColumn(name="pwd_id")
    val pwd: PWD
)