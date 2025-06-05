package ufrpe.sbpc.botpcd.service

import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.entity.Disability
import ufrpe.sbpc.botpcd.entity.ServiceType
import ufrpe.sbpc.botpcd.repository.AttendanceRepository

@Service
class AttendanceService(
    private val attendanceRepository: AttendanceRepository,
    private val whatsappService: WhatsappService,
) {
    fun sendServices(botNumber: String, userNumber: String, disability: Disability) {
        val serviceList = ServiceType.getServicesByDisability(disability)
        val message = whatsappService.createOptions(serviceList.map { it -> it.description }, header = "Percebi que você tem ${if(disability.ordinal + 1 != 6) disability.textOption else "mobilidade reduzida"}. Os serviços disponíveis para você são:")
        whatsappService.sendMessage(botNumber, userNumber, message)
    }
}