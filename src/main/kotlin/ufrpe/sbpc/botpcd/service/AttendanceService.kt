package ufrpe.sbpc.botpcd.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.controller.WhatsappWebhookController
import ufrpe.sbpc.botpcd.entity.Attendance
import ufrpe.sbpc.botpcd.entity.Disability
import ufrpe.sbpc.botpcd.entity.MonitorServiceType
import ufrpe.sbpc.botpcd.entity.PWD
import ufrpe.sbpc.botpcd.entity.Provider
import ufrpe.sbpc.botpcd.entity.ServiceType
import ufrpe.sbpc.botpcd.entity.UserStatus
import ufrpe.sbpc.botpcd.repository.AttendanceRepository
import ufrpe.sbpc.botpcd.repository.MonitorRepository

@Service
class AttendanceService(
    private val attendanceRepository: AttendanceRepository,
    private val whatsappService: WhatsappService,
    private val monitorRepository: MonitorRepository,
) {
    val logger: Logger = LoggerFactory.getLogger(AttendanceService::class.java)
    fun sendServices(botNumber: String, userNumber: String, disability: Disability) {
        whatsappService.sendMessage(botNumber, userNumber, createSendServicesMessage(disability))
    }
    fun createSendServicesMessage(disability: Disability): String {
        val serviceList = ServiceType.getServicesByDisability(disability)
        return whatsappService.createOptions(serviceList.map { it -> it.description }, header = "Percebi que você tem ${if(disability.ordinal + 1 != 6) disability.textOption else "mobilidade reduzida"}. Os serviços disponíveis para você são:")
    }
     fun startAttendance(
        disability: Disability,
        message: String,
        pwd: PWD,
        botNumber: String,
    ) {
        val service = ServiceType.getServicesByDisability(disability)[message.toInt() - 1]
        if (service.providerType == Provider.MONITOR) {
            val monitor = monitorRepository.findAvailableMonitor(
                UserStatus.AVAILABLE,
                (service as MonitorServiceType).monitorAssistanceType
            )
            if(monitor != null) {
                whatsappService.sendMessage(botNumber, monitor.phoneNumber, "Você irá começar o atendimento de ${pwd.name}")
//                attendanceRepository.save(Attendance())
            } else {
             // fila de espera
            }
        } else if (service.providerType == Provider.COMMITTEE_MEMBER) {

        } else {
            logger.error("There is wrong provider")
        }
    }
}