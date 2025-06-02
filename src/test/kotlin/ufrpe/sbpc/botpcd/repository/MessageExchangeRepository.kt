package ufrpe.sbpc.botpcd.repository

import org.hibernate.annotations.processing.SQL
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.JpaRepository
import ufrpe.sbpc.botpcd.entity.MessageExchange


interface MessageExchangeRepository: JpaRepository<MessageExchange, Long> {
    @SQL("SELECT * FROM MessageExchange ORDER BY createAt ASC LIMIT 1")
    fun findLatestMessage(): MessageExchange?
}