package ufrpe.sbpc.botpcd.service

import lombok.extern.java.Log
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.repository.PWDRepository
import ufrpe.sbpc.botpcd.entity.Disability
import ufrpe.sbpc.botpcd.entity.MessageExchange
import ufrpe.sbpc.botpcd.entity.PWD
import ufrpe.sbpc.botpcd.repository.MessageExchangeRepository
import ufrpe.sbpc.botpcd.util.NumberOptionDontNeedSupport

@Service
class RegisterPWDService(
	private val pwdRepository: PWDRepository,
	private val whatsappService: WhatsappService,
	private val messageExchangeRepository: MessageExchangeRepository
) {
	private val logger = LoggerFactory.getLogger(RegisterPWDService::class.java)
	fun shouldRegisterNewUser(lastBotMessageText: String?, message: String) =
		(lastBotMessageText ?: "") == Disability.getOptions() && message in Disability.entries.map { (it.ordinal + 1).toString() }.toMutableList().also { it.add(NumberOptionDontNeedSupport.toString()) }

	fun handleDisabilitySelected(botNumber: String, message: String, phoneNumber: String) {
		val disabilityNumber = message.toInt()
		val ordinalDisability = disabilityNumber - 1
		val disability = Disability.getByOrdinal(ordinalDisability)
		if (disabilityNumber == NumberOptionDontNeedSupport) {
			whatsappService.sendMessage(
				botNumber,
				phoneNumber,
				"Agradecemos o contato! Este canal é exclusivo para atendimento de pessoas com deficiência ou mobilidade reduzida que participarão do evento. Desejamos a você uma excelente participação na 77ª Reunião Anual da SBPC."
			)
		} else if (disability == null) {
			logger.warn("Foi passado um numero de deficiencia incorreto numero da disability $disabilityNumber")
			whatsappService.sendMessage(botNumber, phoneNumber, "Digite um número válido.")
		} else {
			registerDisability(pwdPhoneNumber = phoneNumber, disability = disability)
			whatsIsYourName(botPhoneNumber = botNumber, pwdPhoneNumber = phoneNumber)
		}
	}
	fun registerDisability(pwdPhoneNumber: String, disability: Disability) {
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
