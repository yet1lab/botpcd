package ufrpe.sbpc.botpcd.service

import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.repository.PWDRepository
import com.whatsapp.api.impl.WhatsappBusinessCloudApi

@Service
class RegisterPWDService(
	private val pwdRepository: PWDRepository,
	private val whatsappBusinessCloudApi: WhatsappBusinessCloudApi,
) {
	fun registerDisability() {

	}
}
