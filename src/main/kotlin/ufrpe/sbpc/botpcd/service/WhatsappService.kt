package ufrpe.sbpc.botpcd.service

import com.whatsapp.api.impl.WhatsappBusinessCloudApi

class WhatsappService {
	fun sendMensage(phoneNumber: String, msg: String) {
    val factory = WhatsappApiFactory.newInstance(TestUtils.TOKEN)
    val whatsappBusinessCloudApi = factory.newBusinessCloudApi()

    val message = MessageBuilder.builder()
        .setTo(phoneNumber)
        .buildTextMessage(TextMessage().setBody(msg))
        .setPreviewUrl(false)

    whatsappBusinessCloudApi.sendMessage(PHONE_NUMBER_ID, message)
	}

	fun sendButtons(phoneNumber: String, btn: Array<String>) {
		
	}
}

