package ufrpe.sbpc.botpcd.service

import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.entity.Attendant
import ufrpe.sbpc.botpcd.entity.UserStatus
import ufrpe.sbpc.botpcd.repository.AttendanceRepository

@Service
class AttendantFlowService(
    private val attendantStatusService: AttendantStatusService,
    private val attendanceRepository: AttendanceRepository,
    private val attendanceService: AttendanceService
) {
    val botPcdRegex = Regex("^\\s*bot\\s*pcd\\s*$", RegexOption.IGNORE_CASE)
    fun redirect(botNumber: String, lastBotMessageText: String?, attendant: Attendant, message: String) {
        if (lastBotMessageText in UserStatus.entries.map { it.statusChangeMessage }) {
            attendantStatusService.processStatusChangeResponse(attendant, message, botNumber)
        } else if (botPcdRegex.matches(message)) {
            attendantStatusService.sendStatusChanger(attendant, botNumber)
        } else {
            attendanceRepository.findStartedAttendanceOfAttendance(attendant)?.let { attendance ->
                attendanceService.redirectMessageToPwd(botNumber, message, attendance.pwd, attendant)
            }
        }
    }
}