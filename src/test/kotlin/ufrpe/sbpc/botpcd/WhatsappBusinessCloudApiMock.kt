package ufrpe.sbpc.botpcd

import com.whatsapp.api.domain.messages.Contact
import com.whatsapp.api.domain.messages.Message
import com.whatsapp.api.domain.messages.response.MessageResponse
import com.whatsapp.api.impl.WhatsappBusinessCloudApi

class WhatsappBusinessCloudApiMock(token: String?) : WhatsappBusinessCloudApi(token) {
    var capturedPhoneNumberId: String? = null
        private set
    var capturedMessage: Message? = null
        private set
    override fun sendMessage(phoneNumberId: String?, message: Message?): MessageResponse {
        this.capturedPhoneNumberId = phoneNumberId
        this.capturedMessage = message
        // Retorna uma resposta simulada
        return MessageResponse(
            "whatsapp",
            listOf(null),
            listOf(null)
        );
    }
}