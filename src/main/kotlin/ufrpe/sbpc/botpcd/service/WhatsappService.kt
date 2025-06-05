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
    fun sendMessage(botNumber: String, destinyNumberID: String, msg: String) {
        val message = Message.MessageBuilder.builder()
            .setTo(destinyNumberID)
            .buildTextMessage(TextMessage().setBody(msg))
        cloudApi.sendMessage(botNumber, message)
        messageExchangeRepository.save(MessageExchange(fromPhoneNumber = botNumber, toPhoneNumber = destinyNumberID, message = msg))
    }

    fun createOptions(options: List<String>, header: String = ""): String {
        var mensage = "${header}\n"
        for (i in options.indices) {
            mensage += "- Digite ${i + 1} para ${options[i]}\n"
        }
        return mensage
    }
}
 

