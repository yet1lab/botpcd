package ufrpe.sbpc.botpcd.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.entity.Disability
import ufrpe.sbpc.botpcd.entity.MessageExchange
import ufrpe.sbpc.botpcd.entity.PWD
import ufrpe.sbpc.botpcd.entity.ServiceType
import ufrpe.sbpc.botpcd.repository.AttendanceRepository

@Service
class PWDFlowService(
    private val registerPWDService: RegisterPWDService,
    private val whatsappService: WhatsappService,
    private val attendanceService: AttendanceService,
    private val attendanceRepository: AttendanceRepository

    ) {
    val logger: Logger? = LoggerFactory.getLogger(PWDFlowService::class.java)
    fun redirect(
        pwd: PWD,
        botNumber: String,
        phoneNumber: String,
        message: String,
        lastBotMessage: MessageExchange?
    ) {
        val disability = pwd.disabilities.first()
        val attendance = attendanceRepository.findStartedAttendanceOfPwd(pwd)
        val requestAttendancePWD = attendanceRepository.findRequestAttendanceOfPwd(pwd)
        when {
            requestAttendancePWD != null -> {
                attendanceService.sendWaitListMessage(botNumber, pwd)
            }
            isRegisteringName(lastBotMessage, pwd) -> {
                registerPWDService.registerName(pwd, message)
                whatsappService.sendMessage(botNumber, phoneNumber, "Cadastro realizado.")
                attendanceService.sendServices(botNumber, pwd)
            }
            attendance != null -> {
                if (attendance.attendant == null) {
                    logger?.error("Atendimento com id ${attendance.id} foi iniciado com atendente Nulo!")
                } else {
                    attendanceService.redirectMessageToAttendance(
                        botNumber,
                        message,
                        attendance.attendant!!,
                        pwd
                    )
                }
            }
            isChoosingService(lastBotMessage, pwd) -> {
                if (isValidService(message, disability)) {
                    val service = ServiceType.getServicesByDisability(disability)[message.toInt() - 1]
                    attendanceService.startAttendance(pwd = pwd, botNumber = botNumber, service = service)
                } else {
                    attendanceService.sendServices(botNumber, pwd)
                }
            }
            else -> {
                attendanceService.sendServices(botNumber = botNumber, pwd = pwd)
            }
        }
    }
    private fun isRegisteringName(lastBotMessage: MessageExchange?, pwd: PWD) = (lastBotMessage?.message ?: "") == "Qual o seu nome?" && pwd.name == null
    private fun isChoosingService(lastBotMessage: MessageExchange?, pwd: PWD) = (lastBotMessage?.message
        ?: "") == attendanceService.createSendServicesMessage(pwd.disabilities.first(), pwd)
    private fun isValidService(message: String, disability: Disability) = message in ServiceType.getServicesByDisability(disability).indices.map { (it + 1).toString() }

}