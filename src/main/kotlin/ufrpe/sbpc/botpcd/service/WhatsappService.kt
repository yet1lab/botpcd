package ufrpe.sbpc.botpcd.service

import java.time.LocalDateTime
import org.springframework.stereotype.Service
import com.whatsapp.api.domain.messages.Message
import ufrpe.sbpc.botpcd.entity.MessageExchange
import com.whatsapp.api.domain.messages.TextMessage
import com.whatsapp.api.impl.WhatsappBusinessCloudApi
import org.slf4j.LoggerFactory
import ufrpe.sbpc.botpcd.repository.MessageExchangeRepository
import kotlin.math.log


@Service
class WhatsappService(
    private val cloudApi: WhatsappBusinessCloudApi,
    private val messageExchangeRepository: MessageExchangeRepository
) {
	private val logger = LoggerFactory.getLogger(WhatsappService::class.java)
   fun sendMessage(botNumber: String, destinyNumberID: String, msg: String, author: String = "") {
				val text = if (author != "") "*${author}:*\n ${msg}" else msg

				val message = Message.MessageBuilder.builder()
            .setTo(destinyNumberID)
            .buildTextMessage(TextMessage().setBody(text))
				
				val lastMessageTime = messageExchangeRepository.lastExchangeMessage(
						fromPhoneNumber = destinyNumberID,
						toPhoneNumber = botNumber
						)?.createAt ?: LocalDateTime.now().minusHours(25)

				if (LocalDateTime.now().minusHours(24) < lastMessageTime) {
						cloudApi.sendMessage(botNumber, message)
		
						messageExchangeRepository.save(MessageExchange(
							fromPhoneNumber = botNumber,
							toPhoneNumber = destinyNumberID,
							message = text))
				} else {
					logger.warn("Teve uma tentativa de enviar a mensagem ${message} para o número ${destinyNumberID}, mas ele não tinha mandando mensagem.")
				}
    }
}
 

