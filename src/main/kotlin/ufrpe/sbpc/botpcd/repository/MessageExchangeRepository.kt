package ufrpe.sbpc.botpcd.repository

import org.hibernate.annotations.processing.SQL
import org.springframework.data.jpa.repository.JpaRepository
import ufrpe.sbpc.botpcd.entity.MessageExchange

interface MessageExchangeRepository: JpaRepository<MessageExchange, Long> {
    fun findFirstByFromNumberOrderByCreateAtDesc(fromNumber: String): MessageExchange?
    fun findFirstByToNumberOrderByCreateAtDesc(toNumber: String): MessageExchange?
}