package ufrpe.sbpc.botpcd.service

import com.whatsapp.api.domain.webhook.Value
import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.entity.PWD
import ufrpe.sbpc.botpcd.repository.PWDRepository

@Service
class RegisterService(
    private val pwdRepository: PWDRepository
) {
    fun registerService(phoneNumber: String, changeValue: Value) {

    }
}