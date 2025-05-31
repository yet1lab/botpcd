package ufrpe.sbpc.botpcd.service

import com.whatsapp.api.domain.webhook.Change
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.controller.WhatsappWebhookController
import ufrpe.sbpc.botpcd.entity.Disability
import ufrpe.sbpc.botpcd.repository.CommitteeMemberRepository
import ufrpe.sbpc.botpcd.repository.MonitorRepository
import ufrpe.sbpc.botpcd.repository.PWDRepository

@Service
class FirstContactService(
    private val pwdRepository: PWDRepository,
    private val monitorRepository: MonitorRepository,
    private val committeeMemberRepository: CommitteeMemberRepository,
    private val registerPWDService: RegisterPWDService,
    private val whatsappService: WhatsappService
) {
    val logger: Logger = LoggerFactory.getLogger(WhatsappWebhookController::class.java)
    fun redirectFluxByUserType(phoneNumber: String, change: Change) {
        // to get a message from the change change.value.messages[0].text.body
        val disabilityNumberOptions = Disability.entries.map { it.ordinal.toString() }.toMutableList().apply {  this.add("7") }
        val botNumber = change.value.metadata.phoneNumberId
        val message = change.value.messages[0].text.body.trim()
        when {
            pwdRepository.findByPhoneNumber(phoneNumber) != null -> {
                val pwd = pwdRepository.findByPhoneNumber(phoneNumber)!!
                // Nome ainda não registrado
                if(pwd.name == null) {
                    registerPWDService.registerName(pwd, message)
                    whatsappService.sendMessage(botNumber, phoneNumber, "Cadastro realizado.")
                } else {
                    // Completar com o resto da menssagem
                    whatsappService.sendMessage(botNumber, phoneNumber, "Olá, ${pwd.name}.")
                }
            }
            monitorRepository.findByPhoneNumber(phoneNumber) != null -> {

            }
            committeeMemberRepository.findByPhoneNumber(phoneNumber) != null -> {

            }
            message in disabilityNumberOptions -> {
                val disabilityNumber = message.toInt()
                val ordinalDisability = disabilityNumber - 1
                val disability = Disability.getByOrdinal(ordinalDisability)
                if(disabilityNumber == 7){
                    whatsappService.sendMessage(botNumber, phoneNumber, "Agradecemos o contato! Este canal é exclusivo para atendimento de pessoas com deficiência ou mobilidade reduzida que participarão do evento. Desejamos a você uma excelente participação na 77ª Reunião Anual da SBPC.")
                } else if(disability == null) {
                    logger.warn("Foi passado um numero de deficiencia incorreto numero da disability $disabilityNumber")
                    whatsappService.sendMessage(botNumber, phoneNumber, "Digite um número válido.")
                } else {
                    registerPWDService.registerDisability(botNumber,phoneNumber, disability)
                    registerPWDService.whatsIsYourName(botNumber, phoneNumber)
                }
            }
            else -> {
                // Usuario não cadastrado
                registerPWDService.whatIsYourDisability(botNumber, phoneNumber)
            }
        }
    }
}