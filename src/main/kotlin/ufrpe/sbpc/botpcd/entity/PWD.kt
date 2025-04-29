package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import lombok.Data
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.repository.NoRepositoryBean

@Entity
@Data
/**
 * Represents a person with disabilities.
 * Pessoa com deficiência
 */
class PWD(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    var name: String,
    @NotEmpty(message = "The PWD needs to have a phone number")
    val phoneNumber: String,
    @NotEmpty(message = "The PWD needs to have a disability")
    @Enumerated(value = EnumType.STRING)
    var disability: MutableSet<Disability> = mutableSetOf()
)