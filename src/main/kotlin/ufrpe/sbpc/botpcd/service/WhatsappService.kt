//======================================================================
package ufrpe.sbpc.botpcd.service

import com.whatsapp.api.impl.WhatsappBusinessCloudApi
//======================================================================
object WhatsappService {
    private const val TOKEN = "seu_token_aqui"
    private const val PhoneNumber = "seu_phone_number"

    private val factory: WhatsappApiFactory = WhatsappApiFactory.newInstance(TOKEN)
    private val cloudApi: WhatsappBusinessCloudApi = factory.newBusinessCloudApi()
//======================================================================
    fun sendMensage(destinyNumberID: String, msg: String) {
        val message = MessageBuilder.builder()
            .setTo(phoneNumber)
            .buildTextMessage(TextMessage().setBody(msg))

        cloudApi.sendMessage(destinyNumberID, message)
    }
//======================================================================
	fun sendButtons(destinyNumberID: String, btn: Array<String>) {
    val action = Action()
    btn.forEachIndexed { index, title ->
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
			.setBody(Body().setText("Escolha uma opção:"))

    val message = MessageBuilder.builder()
			.setTo(phoneNumber)
			.buildInteractiveMessage(interactiveMessage)

    cloudApi.sendMessage(PHONE_NUMBER_ID, message)
	}
}
//======================================================================

