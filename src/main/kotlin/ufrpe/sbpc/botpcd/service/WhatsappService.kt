package ufrpe.sbpc.botpcd.service

import com.whatsapp.api.domain.messages.Message
import com.whatsapp.api.domain.messages.TextMessage
import com.whatsapp.api.impl.WhatsappBusinessCloudApi
import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.entity.MessageExchange
import ufrpe.sbpc.botpcd.repository.MessageExchangeRepository


@Service
class WhatsappService(
    private val cloudApi: WhatsappBusinessCloudApi,
    private val messageExchangeRepository: MessageExchangeRepository
) {
    fun sendMessage(botNumber: String, destinyNumberID: String, msg: String, author: String = "") {
				if (author != ""){ val msg = "*${author}:*\n ${msg}"; }  

				val message = Message.MessageBuilder.builder()
            .setTo(destinyNumberID)
            .buildTextMessage(TextMessage().setBody(msg))
        cloudApi.sendMessage(botNumber, message)
				
        messageExchangeRepository.save(MessageExchange(fromPhoneNumber = botNumber, toPhoneNumber = destinyNumberID, message = msg))
    }

    fun createOptions(options: Array<String>, header: String = "", author: String = ""): String {
        var msg = ""

				for (i in options.indices) {
            msg += "- Digite ${i + 1} para ${options[i]}\n"
        }
				if (header != ""){ msg = "*${header}*\n ${msg}"; }
				if (author != ""){ msg = "*${author}:*\n ${msg}"; }

				return msg
    }
}
 

