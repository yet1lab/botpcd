package ufrpe.sbpc.botpcd.service

import com.whatsapp.api.domain.webhook.Change
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.controller.WhatsappWebhookController
import ufrpe.sbpc.botpcd.entity.Disability
import ufrpe.sbpc.botpcd.entity.MessageExchange
import ufrpe.sbpc.botpcd.entity.ServiceType
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
    private val messageExchangeRepository: MessageExchangeRepository
) {
    val logger: Logger = LoggerFactory.getLogger(WhatsappWebhookController::class.java)

    fun redirectFluxByUserType(phoneNumber: String, change: Change) {
        val disabilityNumberOptions =
            Disability.entries.map { (it.ordinal + 1).toString() }.toMutableList().apply { this.add("7") }
        val botNumber = change.value.metadata.phoneNumberId
        val message = change.value.messages[0].text.body.trim()
        messageExchangeRepository.save(
            MessageExchange(
                fromPhoneNumber = phoneNumber,
                toPhoneNumber = botNumber,
                message = message
            )
        )
        val lastBotMessage =
            messageExchangeRepository.lastExchangeMessage(fromPhoneNumber = botNumber, toPhoneNumber = phoneNumber)
        when {
            pwdRepository.findByPhoneNumber(phoneNumber) != null -> {
                val pwd = pwdRepository.findByPhoneNumber(phoneNumber)!!
                val disability = pwd.disabilities.first()
                when {
                    // registra o nome
                    (lastBotMessage?.message ?: "") == "Qual o seu nome?" && pwd.name == null -> {
                        registerPWDService.registerName(pwd, message)
                        whatsappService.sendMessage(botNumber, phoneNumber, "Cadastro realizado.")
                    }
                    // escolhe um tipo de serviço e é redirecionado
                    message in ServiceType.getServicesByDisability(disability).indices.map { (it + 1).toString() } -> {

                    }
                    (lastBotMessage?.message
                        ?: "") == attendanceService.createSendServicesMessage(disability) && message in ServiceType.getServicesByDisability(disability).indices-> {

                    }
                    // envia servicos disponiveis
                    else -> {
                        whatsappService.sendMessage(botNumber, phoneNumber, "Olá, ${pwd.name}.")
                        attendanceService.sendServices(botNumber, phoneNumber, pwd.disabilities.first())
                    }
                }
            }

            monitorRepository.findByPhoneNumber(phoneNumber) != null || committeeMemberRepository.findByPhoneNumber(
                phoneNumber
            ) != null -> {

            }

            (lastBotMessage?.message ?: "") == Disability.getOptions() && message in disabilityNumberOptions -> {
                val disabilityNumber = message.toInt()
                val ordinalDisability = disabilityNumber - 1
                val disability = Disability.getByOrdinal(ordinalDisability)
                if (disabilityNumber == 7) {
                    whatsappService.sendMessage(
                        botNumber,
                        phoneNumber,
                        "Agradecemos o contato! Este canal é exclusivo para atendimento de pessoas com deficiência ou mobilidade reduzida que participarão do evento. Desejamos a você uma excelente participação na 77ª Reunião Anual da SBPC."
                    )
                } else if (disability == null) {
                    logger.warn("Foi passado um numero de deficiencia incorreto numero da disability $disabilityNumber")
                    whatsappService.sendMessage(botNumber, phoneNumber, "Digite um número válido.")
                } else {
                    registerPWDService.registerDisability(botNumber, phoneNumber, disability)
                    whatsappService.sendMessage(botNumber, phoneNumber, "Entendi que você ${disability.textOption}")
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