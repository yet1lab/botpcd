package ufrpe.sbpc.botpcd.entity

import jakarta.annotation.Nullable
import jakarta.persistence.CascadeType
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.repository.NoRepositoryBean

/**
 * Represents a person with disabilities.
 * Pessoa com deficiência
 *
 */
@Entity
@Table(name = "tb_pwd")
class PWD(
    @Id @GeneratedValue
    @Nullable
    var id: Long? = null,
    @ElementCollection(targetClass = Disability::class)
    @CollectionTable(name = "tb_pwd_disabilities", joinColumns = [JoinColumn(name = "pwd_id")])
    @Column(name = "disability")
    @Enumerated(EnumType.STRING)
    var disabilities: Set<Disability> = setOf(),
    var name: String? = null,
    @NotEmpty(message = "The User needs to have a phone number")
    @Column(unique = true)
    var phoneNumber: String
)