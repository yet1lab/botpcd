package ufrpe.sbpc.botpcd.service

import com.whatsapp.api.domain.messages.Action
import com.whatsapp.api.domain.messages.Body
import com.whatsapp.api.domain.messages.InteractiveMessage
import com.whatsapp.api.domain.messages.Message
import com.whatsapp.api.domain.messages.Section
import com.whatsapp.api.domain.messages.TextMessage
import com.whatsapp.api.domain.messages.type.InteractiveMessageType
import com.whatsapp.api.impl.WhatsappBusinessCloudApi
import org.springframework.stereotype.Service


@Service
class WhatsappService(
    private val cloudApi: WhatsappBusinessCloudApi
) {
    fun sendMessage(botNumber: String, destinyNumberID: String, msg: String) {
        val message = Message.MessageBuilder.builder()
            .setTo(destinyNumberID)
            .buildTextMessage(TextMessage().setBody(msg))
        cloudApi.sendMessage(botNumber, message)
    }

    fun createOptions(options: List<String>, header: String = ""): String {
        var mensage = "${header}\n"
        for (i in options.indices) {
            mensage += "- Digite ${i + 1} para ${options[i]}\n"
        }
        return mensage
    }
}
 

