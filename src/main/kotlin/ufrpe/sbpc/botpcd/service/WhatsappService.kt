package ufrpe.sbpc.botpcd.service

import com.whatsapp.api.domain.messages.Action
import com.whatsapp.api.domain.messages.Body
import com.whatsapp.api.domain.messages.Button
import com.whatsapp.api.domain.messages.InteractiveMessage
import com.whatsapp.api.domain.messages.Message
import com.whatsapp.api.domain.messages.Reply
import com.whatsapp.api.domain.messages.TextMessage
import com.whatsapp.api.domain.messages.type.ButtonType
import com.whatsapp.api.domain.messages.type.InteractiveMessageType
import com.whatsapp.api.impl.WhatsappBusinessCloudApi
import org.springframework.stereotype.Service


@Service
class WhatsappService(
    private val cloudApi: WhatsappBusinessCloudApi
) {

    fun sendMessage(destinyNumberID: String, msg: String) {
        val message = Message.MessageBuilder.builder()
            .setTo(destinyNumberID)
            .buildTextMessage(TextMessage().setBody(msg))
        cloudApi.sendMessage(destinyNumberID, message)
    }

    fun sendButtons(destinyNumberID: String, buttons: List<String>, titleMessage: String) {
        val action = Action()
        buttons.forEachIndexed { index, title ->
            val button = Button()
                .setType(ButtonType.REPLY)
                .setReply(
                    Reply()
                        .setId("BUTTON_ID_$index")
                        .setTitle(title)
                )
            action.addButton(button)
        }
        val interactiveMessage = InteractiveMessage.build()
            .setAction(action)
            .setType(InteractiveMessageType.BUTTON)
            .setBody(Body().setText(titleMessage))
        val message = Message.MessageBuilder.builder()
            .setTo(destinyNumberID)
            .buildInteractiveMessage(interactiveMessage)
        cloudApi.sendMessage(destinyNumberID, message)
    }
}
 

