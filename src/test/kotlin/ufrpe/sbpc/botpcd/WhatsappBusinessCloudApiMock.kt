package ufrpe.sbpc.botpcd

import com.whatsapp.api.domain.messages.Contact
import com.whatsapp.api.domain.messages.Message
import com.whatsapp.api.domain.messages.response.MessageResponse
import com.whatsapp.api.impl.WhatsappBusinessCloudApi
import org.springframework.beans.factory.annotation.Autowired
import ufrpe.sbpc.botpcd.entity.MessageExchange
import ufrpe.sbpc.botpcd.repository.MessageExchangeRepository

class WhatsappBusinessCloudApiMock(token: String?) : WhatsappBusinessCloudApi(token) {
    var capturedPhoneNumberId: String? = null
        private set
    var capturedMessage: Message? = null
        private set
    @Autowired
    private lateinit var messageExchangeRepository: MessageExchangeRepository
    override fun sendMessage(phoneNumberId: String?, message: Message?): MessageResponse {
        this.capturedPhoneNumberId = phoneNumberId
        this.capturedMessage = message
        // Retorna uma resposta simulada
        messageExchangeRepository.save(
            MessageExchange(
                fromNumber = phoneNumberId!!,
                toNumber = message!!.to,
                message = message.textMessage.body
            )
        )
        return MessageResponse(
            "whatsapp",
            listOf(null),
            listOf(null)
        );
    }
}