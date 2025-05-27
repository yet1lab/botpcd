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

    fun sendButtons(botNumber: String, destinyNumberID: String, buttons: List<String>, titleMessage: String) {
        val section = Section().setTitle("Selecione")

        buttons.forEachIndexed { index, buttonText ->
            val row = com.whatsapp.api.domain.messages.Row()
                .setId("row_$index")
                .setTitle("Opção ${index + 1}")
                .setDescription(buttonText)
            section.addRow(row)
        }

        val interactiveMessage = InteractiveMessage.build()
            .setAction(Action().setButtonText(titleMessage).addSection(section))
            .setType(InteractiveMessageType.LIST)
            .setHeader(com.whatsapp.api.domain.messages.Header().setType(com.whatsapp.api.domain.messages.type.HeaderType.TEXT).setText(titleMessage))
            .setBody(Body().setText("Selecione seu tipo de deficiẽncia:"))
            .setFooter(com.whatsapp.api.domain.messages.Footer().setText("Rodapé"))

        val message = Message.MessageBuilder.builder()
            .setTo(destinyNumberID)
            .buildInteractiveMessage(interactiveMessage)

        cloudApi.sendMessage(botNumber, message)
    }
}
 

