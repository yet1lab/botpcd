package ufrpe.sbpc.botpcd.service

import ufrpe.sbpc.botpcd.entity.PWD
import ufrpe.sbpc.botpcd.entity.Disability
import org.springframework.stereotype.Service
import com.whatsapp.api.domain.webhook.Change
import ufrpe.sbpc.botpcd.repository.PWDRepository
import com.whatsapp.api.impl.WhatsappBusinessCloudApi
import org.springframework.beans.factory.annotation.Value

@Service
class RegisterService(
	private val pwdRepository: PWDRepository,
	private val whatsappBusinessCloudApi: WhatsappBusinessCloudApi,
) {
	fun registerPWD(phoneNumber: String, change: Change) {
		val menssage = change.value.menssages[0].text
		val User = pwdRepository.findByPhoneNumber(phoneNumber)
		val isDesability = Disability.parse(menssage) !in listOf(null, Disability.NOTHING);
		val isCadastred = User != null;
		
		if(!isCadastred && !isDisability){ // 1. ASK DISABILITY
			// ask disability
			return 200
		}

		if(!isCadastred && isDisability){  // 2. REGISTER AND ASK NAME
			val pwd = PWD(
				phoneNumber   = phoneNumber,
				phoneNumberID = change.value.phoneNumberID,
				disability    = Disability.parse(menssage)
			)

			pwdRepository.save(pwd)
			// ask name
			return 200
		}
	
		if(isCadastred && !isDisability){  // 3. REGISTER NAME AND PUT IN QUEUE
			val pwd = PWD(name = menssage)
			pwdRepository.save(pwd)
			// put user in queue
			return 200
		}
	}
}
