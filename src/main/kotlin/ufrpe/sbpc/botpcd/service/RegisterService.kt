package ufrpe.sbpc.botpcd.service

import ufrpe.sbpc.botpcd.entity.PWD
import ufrpe.sbpc.botpcd.entity.Disability
import org.springframework.stereotype.Service
import com.whatsapp.api.domain.webhook.Change
import ufrpe.sbpc.botpcd.repository.PWDRepository
import com.whatsapp.api.impl.WhatsappBusinessCloudApi

@Service
class RegisterService(
	private val pwdRepository: PWDRepository,
	private val whatsappBusinessCloudApi: WhatsappBusinessCloudApi,
) {
//	fun registerPWD(phoneNumber: String, change: Change): Int {
//		val message = change.value.messages[0].text.body
//		val user = pwdRepository.findByPhoneNumber(phoneNumber)
//		val isDisability = Disability.parse(message) != null
//		val isCadastred = user != null;
//
//		if(!isCadastred && !isDisability){ // 1. ASK DISABILITY
//			// ask disability
//			return 200
//		}
//
//		if(!isCadastred && isDisability){  // 2. REGISTER AND ASK NAME
//			val pwd = PWD(
//				phoneNumber   = phoneNumber,
//				phoneNumberId = change.value.metadata.phoneNumberId(),
//				disability    = mutableSetOf(Disability.parse(message))
//			)
//
//			pwdRepository.save(pwd)
//			// ask name
//			return 200
//		}
//
//		if(!isDisability){ // 3. REGISTER NAME AND PUT IN QUEUE
//			val pwd = PWD(name = message)
//			pwdRepository.save(pwd)
//			// put user in queue
//			return 200
//		}
//	}
}
