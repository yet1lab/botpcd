package ufrpe.sbpc.botpcd.service

import com.whatsapp.api.domain.webhook.Change
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.controller.WhatsappWebhookController
import ufrpe.sbpc.botpcd.entity.Attendant
import ufrpe.sbpc.botpcd.entity.Disability
import ufrpe.sbpc.botpcd.entity.MessageExchange
import ufrpe.sbpc.botpcd.entity.MonitorServiceType
import ufrpe.sbpc.botpcd.entity.Provider
import ufrpe.sbpc.botpcd.entity.ServiceType
import ufrpe.sbpc.botpcd.entity.UserStatus
import ufrpe.sbpc.botpcd.repository.AttendanceRepository
import ufrpe.sbpc.botpcd.repository.AttendantRepository
import ufrpe.sbpc.botpcd.repository.CommitteeMemberRepository
import ufrpe.sbpc.botpcd.repository.MessageExchangeRepository
import ufrpe.sbpc.botpcd.repository.MonitorRepository
import ufrpe.sbpc.botpcd.repository.PWDRepository

@Service
class FirstContactService(
    private val pwdRepository: PWDRepository,
    private val attendantRepository: AttendantRepository,
    private val registerPWDService: RegisterPWDService,
    private val whatsappService: WhatsappService,
    private val attendanceService: AttendanceService,
    private val messageExchangeRepository: MessageExchangeRepository,
    private val attendantStatusService: AttendantStatusService,
    private val attendanceRepository: AttendanceRepository,
    private val monitorRepository: MonitorRepository,
    private val committeeMemberRepository: CommitteeMemberRepository
) {
    val logger: Logger = LoggerFactory.getLogger(WhatsappWebhookController::class.java)

    fun redirectFluxByUserType(phoneNumber: String, change: Change) {
        val disabilityNumberOptions =
            Disability.entries.map { (it.ordinal + 1).toString() }.toMutableList().apply { this.add("7") }
        val botNumber = change.value.metadata.phoneNumberId
        val message = change.value.messages[0].text.body.trim().sanitizeInput()
        messageExchangeRepository.save(
            MessageExchange(
                fromPhoneNumber = phoneNumber,
                toPhoneNumber = botNumber,
                message = message
            )
        )
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
                val disability = pwd.disabilities.first()
                when {
                    // registra o nome
                    (lastBotMessage?.message ?: "") == "Qual o seu nome?" && pwd.name == null -> {
                        registerPWDService.registerName(pwd, message)
                        whatsappService.sendMessage(botNumber, phoneNumber, "Cadastro realizado.")
                        attendanceService.sendServices(botNumber, pwd, )
                    }
                    // escolhe um tipo de serviço e é redirecionado
                    (lastBotMessage?.message
                        ?: "") == attendanceService.createSendServicesMessage(disability) -> {
                        if(message in ServiceType.getServicesByDisability(disability).indices.map { (it + 1).toString() }) {
                            val service = ServiceType.getServicesByDisability(disability)[message.toInt() - 1]
                            attendanceService.startAttendance(pwd = pwd, botNumber = botNumber, service = service)
                        } else {
                            attendanceService.sendServices(botNumber, pwd)
                        }
                    }
                    // redireciona mensagenns para o atendente
                    attendanceRepository.findStartedAttendanceOfPwd(pwd) != null -> {
                        val attendance = attendanceRepository.findStartedAttendanceOfPwd(pwd)
                        if(attendance?.attendant == null) {
                            logger.error("Atendimento com id ${attendance?.id} foi iniciado com atendente Nulo!")
                        } else {
                            attendanceService.redirectMessageToAttendance(botNumber, message, attendance.attendant!!, pwd)
                        }
                    }

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

fun String.sanitizeInput(): String {
    val dangerousKeywords = listOf(
        "select", "drop", "insert", "delete", "update", "truncate", "exec",
        "execute", "union", "alter", "create", "shutdown", "grant", "revoke", "--", "/*", "*/"
    )

    // Remove palavras-chave perigosas, ignorando maiúsculas/minúsculas
    var sanitized = this
    dangerousKeywords.forEach { keyword ->
        val regex = Regex("\\b$keyword\\b", RegexOption.IGNORE_CASE)
        sanitized = sanitized.replace(regex, "")
    }

    // Remove ou substitui caracteres especiais perigosos
    sanitized = sanitized.replace(Regex("[\"'`;\\\\/<>&]"), "") // remove aspas, ponto e vírgula, barra, etc.

    // Remove múltiplos espaços seguidos e trim final
    sanitized = sanitized.replace(Regex("\\s+"), " ").trim()

    return sanitized
}
