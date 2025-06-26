package ufrpe.sbpc.botpcd.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ufrpe.sbpc.botpcd.entity.MessageExchange

interface MessageExchangeRepository: JpaRepository<MessageExchange, Long> {
    @Query(
        "SELECT m FROM MessageExchange m WHERE m.toPhoneNumber = :toPhoneNumber AND m.fromPhoneNumber = :fromPhoneNumber ORDER BY m.sendAt DESC LIMIT 1"
    )
    fun lastExchangeMessage(toPhoneNumber: String, fromPhoneNumber: String): MessageExchange?
    @Query(
        "SELECT m FROM MessageExchange m WHERE m.toPhoneNumber = :toPhoneNumber AND m.fromPhoneNumber = :fromPhoneNumber ORDER BY m.sendAt ASC"
    )
    fun listExchangeMessage(toPhoneNumber: String, fromPhoneNumber: String): List<MessageExchange>
}