package ufrpe.sbpc.botpcd.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ufrpe.sbpc.botpcd.entity.*
import ufrpe.sbpc.botpcd.repository.AttendanceRepository

@Service
class AttendanceService(
    private val attendanceRepository: AttendanceRepository,
    private val whatsappService: WhatsappService,
    private val attendantStatusService: AttendantStatusService
) {
    fun sendServices(botNumber: String, userNumber: String, disability: Disability) {
        val serviceList = ServiceType.getServicesByDisability(disability)
        val serviceDescriptions = serviceList.map { it.description }
        val message = whatsappService.createOptions(options = serviceDescriptions, header = "Escolha um dos serviços abaixo:")
        whatsappService.sendMessage(botNumber, userNumber, message)
    }

    @Transactional
    fun beginAttendance(attendance: Attendance) {
        when (attendance.attendantType) {
            Provider.MONITOR -> {
                val monitor = attendance.attendant as? Monitor
                monitor?.let { attendantStatusService.setMonitorStatus(it, UserStatus.BUSY) }
            }
            Provider.COMMITTEE_MEMBER -> {
                val committeeMember = attendance.attendant as? CommitteeMember
                committeeMember?.let { attendantStatusService.setCommitteeMemberStatus(it, UserStatus.BUSY) }
            }
        }
        attendanceRepository.save(attendance)
    }
}