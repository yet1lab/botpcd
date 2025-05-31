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
	fun whatIsYourDisability(botNumber: String, pwdPhoneNumber: String) {
		val message = """Olá, qual sua deficiência?
			|Digite 1 para Deficiência visual, 
			|Digite 2 para Deficiência auditiva/surdez, 
			|Digite 3 para Surdocegueira, 
			|Digite 4 para TEA/Neurodivergente, 
			|Digite 5 para Deficiência física, 
			|Digite 6 para Mobilidade reduzida, 
			|Digite 7 para Não preciso de suporte.""".trimMargin()
		whatsappService.sendMessage(botNumber,pwdPhoneNumber, message)
	}

	fun whatsIsYourName(pwdPhoneNumber: String) {

	}
}
