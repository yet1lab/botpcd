package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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
    @NotEmpty(message = "The PWD needs to have a disability")
    @Enumerated(value = EnumType.STRING)
    var disability: MutableSet<Disability>
): User(name = name, phoneNumber = phoneNumber)