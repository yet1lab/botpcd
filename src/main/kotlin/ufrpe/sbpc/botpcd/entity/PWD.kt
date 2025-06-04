package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
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
    name: String? = null,
    phoneNumber: String,
    @ElementCollection(targetClass = Disability::class)
    @CollectionTable(name = "tb_pwd_disabilities", joinColumns = [JoinColumn(name = "pwd_id")])
    @Column(name = "disability")
    @Enumerated(EnumType.STRING)
    var disabilities: MutableSet<Disability> = mutableSetOf()
): User(name = name, phoneNumber = phoneNumber)