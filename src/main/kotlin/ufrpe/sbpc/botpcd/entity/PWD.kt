package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.Entity
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
    val id: Long,
    var name: String,
    @NotEmpty(message = "The PWD needs to have a phone number")
    val phoneNumber: String,
)