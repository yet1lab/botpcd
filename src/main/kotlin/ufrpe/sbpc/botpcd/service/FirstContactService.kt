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
    private val committeeMemberRepository: CommitteeMemberRepository,
    private val pwdFlowService: PWDFlowService
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
        val lastBotMessage =
            messageExchangeRepository.lastExchangeMessage(fromPhoneNumber = botNumber, toPhoneNumber = phoneNumber)
        val lastBotMessageText = lastBotMessage?.message ?: ""
        val attendant: Attendant? =
            monitorRepository.findByPhoneNumber(phoneNumber) ?: committeeMemberRepository.findByPhoneNumber(phoneNumber)
        val statusQuestionMessages = listOf(
            AttendantStatusService.StatusChangeMessages.UNAVAILABLE_ATTENDANT_TEXT,
            AttendantStatusService.StatusChangeMessages.BUSY_ATTENDANT_TEXT,
            AttendantStatusService.StatusChangeMessages.AVAILABLE_ATTENDANT_TEXT
        )
        val botPcdRegex = Regex("^\\s*bot\\s*pcd\\s*$", RegexOption.IGNORE_CASE)
        val pwd = pwdRepository.findByPhoneNumber(phoneNumber)
        when {
            attendant != null -> {
            }
             pwd != null -> pwdFlowService.redirectMessageToPwd(pwd = pwd)
            (lastBotMessage?.message ?: "") == Disability.getOptions() && message in disabilityNumberOptions -> {
                registerPWDService.handleDisabilitySelected(botNumber, message, phoneNumber)
            }
            else -> {
                // Usuario não cadastrado
                registerPWDService.whatIsYourDisability(botNumber, phoneNumber)
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
}
