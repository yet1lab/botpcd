package ufrpe.sbpc.botpcd.repository

import org.springframework.data.jpa.repository.JpaRepository
import ufrpe.sbpc.botpcd.entity.MessageExchange

interface MessageExchangeRepository: JpaRepository<MessageExchange, Long> {

}