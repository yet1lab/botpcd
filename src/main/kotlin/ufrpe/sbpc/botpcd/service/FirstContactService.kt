package ufrpe.sbpc.botpcd.service

import com.whatsapp.api.domain.webhook.Change
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.controller.WhatsappWebhookController
import ufrpe.sbpc.botpcd.entity.Attendant 
import ufrpe.sbpc.botpcd.entity.Disability
import ufrpe.sbpc.botpcd.entity.MessageExchange
import ufrpe.sbpc.botpcd.repository.CommitteeMemberRepository
import ufrpe.sbpc.botpcd.repository.MessageExchangeRepository
import ufrpe.sbpc.botpcd.repository.MonitorRepository
import ufrpe.sbpc.botpcd.repository.PWDRepository

@Service
class FirstContactService(
    private val pwdRepository: PWDRepository,
    private val monitorRepository: MonitorRepository,
    private val committeeMemberRepository: CommitteeMemberRepository,
    private val registerPWDService: RegisterPWDService, 
    private val whatsappService: WhatsappService, 
    private val attendanceService: AttendanceService, 
    private val messageExchangeRepository: MessageExchangeRepository, 
    private val attendantStatusService: AttendantStatusService 
) {
    val logger: Logger = LoggerFactory.getLogger(WhatsappWebhookController::class.java)

    fun redirectFluxByUserType(phoneNumber: String, change: Change) {
        val disabilityNumberOptions = Disability.entries.map { (it.ordinal + 1).toString() }.toMutableList().apply {  this.add("7") }
        val botNumber = change.value.metadata.phoneNumberId
        val message = change.value.messages[0].text.body.trim()
        messageExchangeRepository.save(MessageExchange(fromPhoneNumber = phoneNumber, toPhoneNumber = botNumber, message = message))
        val lastBotMessage = messageExchangeRepository.lastExchangeMessage(fromPhoneNumber = botNumber, toPhoneNumber = phoneNumber)
        val lastBotMessageText = lastBotMessage?.message ?: ""

        val attendant: Attendant? = monitorRepository.findByPhoneNumber(phoneNumber) ?: committeeMemberRepository.findByPhoneNumber(phoneNumber)

        val statusQuestionMessages = listOf(
            AttendantStatusService.StatusChangeMessages.UNAVAILABLE_ATTENDANT_TEXT,
            AttendantStatusService.StatusChangeMessages.BUSY_ATTENDANT_TEXT,
            AttendantStatusService.StatusChangeMessages.AVAILABLE_ATTENDANT_TEXT
        )

        when {
            // Atendente respondende a pergunta de mudança de status
            attendant != null && lastBotMessageText in statusQuestionMessages -> {
                attendantStatusService.processStatusChangeResponse(attendant, message, botNumber)
            }

            // Atendente escreve "BotPCD"
            attendant != null && message.equals("BotPCD", ignoreCase = true) -> {
                attendantStatusService.sendStatusChanger(attendant, botNumber)
            }
            
            // Usuario ainda desconhecido
            pwdRepository.findByPhoneNumber(phoneNumber) != null -> {
                val pwd = pwdRepository.findByPhoneNumber(phoneNumber)!! 
                if(lastBotMessageText == "Qual o seu nome?" && pwd.name == null) {
                    registerPWDService.registerName(pwd, message)
                    whatsappService.sendMessage(botNumber, phoneNumber, "Cadastro realizado.")
                } else {
                    whatsappService.sendMessage(botNumber, phoneNumber, "Olá, ${pwd.name}.")
                    pwd.disabilities.firstOrNull()?.let { disability -> // Usar firstOrNull para segurança
                        attendanceService.sendServices(botNumber,phoneNumber, disability)
                    } ?: whatsappService.sendMessage(botNumber, phoneNumber, "Não encontramos deficiências registradas para você.")
                }
            }
            
            // PCD responde a pergunta de deficiência
            lastBotMessageText == Disability.getOptions() && message in disabilityNumberOptions -> {
                val disabilityNumber = message.toInt()
                val ordinalDisability = disabilityNumber - 1
                
                if(disabilityNumber == 7){ 
                    whatsappService.sendMessage(botNumber, phoneNumber, "Agradecemos o contato! Este canal é exclusivo para atendimento de pessoas com deficiência ou mobilidade reduzida que participarão do evento. Desejamos a você uma excelente participação na 77ª Reunião Anual da SBPC.")
                } else {
                    val disability = Disability.getByOrdinal(ordinalDisability)
                    if(disability == null) {
                        logger.warn("Foi passado um numero de deficiencia incorreto numero da disability $disabilityNumber")
                        whatsappService.sendMessage(botNumber, phoneNumber, "Digite um número válido.")
                    } else {
                        registerPWDService.registerDisability(botNumber,phoneNumber, disability)
                        whatsappService.sendMessage(botNumber, phoneNumber, "Entendi que você ${disability.textOption}")
                        registerPWDService.whatsIsYourName(botNumber, phoneNumber)
                    }
                }
            }
            
            // Atendente enviou outra mensagem sem ser "BotPCD" (talvez isso mude no futuro pq vai ter a conversa entre o usuário e o atendente)
            else -> {
                if (attendant != null) {
                    whatsappService.sendMessage(botNumber, phoneNumber, "Comando não reconhecido. Se deseja alterar seu status, envie 'BotPCD'.")
                } else {
                    registerPWDService.whatIsYourDisability(botNumber, phoneNumber)
                }
            }
        }
    }
}