package ufrpe.sbpc.botpcd.service

import com.whatsapp.api.domain.webhook.Change
import com.whatsapp.api.impl.WhatsappBusinessCloudApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.entity.PWD
import ufrpe.sbpc.botpcd.repository.PWDRepository

@Service
class RegisterService(
    private val pwdRepository: PWDRepository,
    private val whatsappBusinessCloudApi: WhatsappBusinessCloudApi,
) {
    /**
     * Essa função vai ser chamada várias vezes no processo de cadastro
     */
    fun registerPWD(phoneNumber: String, change: Change) {
        if(pwdRepository.findByPhoneNumber(phoneNumber) != null) {

        }
        // primeiro eu posso perguntar o tipo de deficiencia que é o mais importante para fazer o cadastro
        // depois de ter salvado eu pergunto o nome
    }
}