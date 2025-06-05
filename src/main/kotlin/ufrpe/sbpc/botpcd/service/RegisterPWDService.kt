package ufrpe.sbpc.botpcd.service

import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.repository.PWDRepository
import ufrpe.sbpc.botpcd.entity.Disability
import ufrpe.sbpc.botpcd.entity.MessageExchange
import ufrpe.sbpc.botpcd.entity.PWD
import ufrpe.sbpc.botpcd.repository.MessageExchangeRepository

@Service
class RegisterPWDService(
	private val pwdRepository: PWDRepository,
	private val whatsappService: WhatsappService,
	private val messageExchangeRepository: MessageExchangeRepository
) {
	fun registerDisability(botPhoneNumber: String, pwdPhoneNumber: String, disability: Disability) {
		val pwd = PWD(disabilities = mutableSetOf(disability), phoneNumber = pwdPhoneNumber)
		pwdRepository.save(pwd)
	}
	fun registerName(pwd: PWD, name: String) {
		pwd.name = name
			pwdRepository.save(pwd)
	}
	fun whatIsYourDisability(botPhoneNumber: String, pwdPhoneNumber: String) {
		whatsappService.sendMessage(botPhoneNumber,pwdPhoneNumber, Disability.getOptions())
	}

	fun whatsIsYourName(botPhoneNumber: String, pwdPhoneNumber: String) {
		whatsappService.sendMessage(botPhoneNumber, pwdPhoneNumber, "Qual o seu nome?")
	}
}
