package ufrpe.sbpc.botpcd.service

import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.repository.PWDRepository
import ufrpe.sbpc.botpcd.entity.Disability
import ufrpe.sbpc.botpcd.entity.PWD

@Service
class RegisterPWDService(
	private val pwdRepository: PWDRepository,
	private val whatsappService: WhatsappService
) {
	fun registerDisability(botPhoneNumber: String, pwdPhoneNumber: String, disability: Disability) {
		val pwd = PWD(disability = mutableSetOf(disability), phoneNumber = pwdPhoneNumber)
		pwdRepository.save(pwd)
	}
	fun registerName(pwd: PWD, name: String) {
		pwd.name = name
			pwdRepository.save(pwd)
	}
	fun whatIsYourDisability(botPhoneNumber: String, pwdPhoneNumber: String) {
		var message = "Olá, qual sua deficiência?\n"
		for (disability in Disability.entries) {
			message += "Digite ${disability.ordinal + 1} para ${disability.textOption} \n"
		}
		message += "Digite 7 para Não preciso de suporte."
		whatsappService.sendMessage(botPhoneNumber,pwdPhoneNumber, message)
	}

	fun whatsIsYourName(botPhoneNumber: String, pwdPhoneNumber: String) {
		whatsappService.sendMessage(botPhoneNumber, pwdPhoneNumber, "Qual o seu nome?")
	}
}
