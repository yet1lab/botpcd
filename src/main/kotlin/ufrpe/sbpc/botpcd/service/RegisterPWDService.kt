package ufrpe.sbpc.botpcd.service

import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.repository.PWDRepository
import com.whatsapp.api.impl.WhatsappBusinessCloudApi
import org.springframework.aot.hint.TypeReference.listOf
import ufrpe.sbpc.botpcd.entity.Disability
import ufrpe.sbpc.botpcd.entity.PWD

@Service
class RegisterPWDService(
	private val pwdRepository: PWDRepository,
	private val whatsappService: WhatsappService
) {
	fun registerDisability(pwdPhoneNumber: String, disability: Disability) {

	}
	fun registerName(pwd: PWD, name: String) {

	}
	fun whatIsYourDisability(pwdPhoneNumber: String) {
		whatsappService.sendButtons(pwdPhoneNumber, Disability.disabilitiesOptions(), "Olá, qual sua deficiência:")
	}

	fun whatsIsYourName(pwdPhoneNumber: String) {

	}
}
