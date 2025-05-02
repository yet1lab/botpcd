package ufrpe.sbpc.botpcd.service

import com.whatsapp.api.domain.webhook.Value
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.entity.PWD
import ufrpe.sbpc.botpcd.repository.PWDRepository

@Service
class RegisterService(
    private val pwdRepository: PWDRepository
) {
    @Value("\${whatsapp.api.version")
    private var whatsappApiVersion: String
    fun registerPWD(phoneNumber: String, changeValue: Value) {
    }
}