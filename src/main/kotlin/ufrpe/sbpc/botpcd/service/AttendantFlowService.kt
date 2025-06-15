package ufrpe.sbpc.botpcd.service

import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.entity.Attendant
import ufrpe.sbpc.botpcd.entity.UserStatus
import ufrpe.sbpc.botpcd.repository.AttendanceRepository

@Service
class AttendantFlowService(
    private val attendanceRepository: AttendanceRepository,
    private val attendanceService: AttendanceService
) {
    val botPcdRegex = Regex("^\\s*bot\\s*pcd\\s*$", RegexOption.IGNORE_CASE)
    fun redirect(botNumber: String, lastBotMessageText: String?, attendant: Attendant, message: String) {
        if (botPcdRegex.matches(message)) {
            attendanceService.sendStatusChanger(attendant, botNumber)
        } else if (lastBotMessageText in attendanceService.changeStatusTextOptionsFor.values) {
            attendanceService.processStatusChangeResponse(botNumber, attendant, message, botNumber)
        } else {
            attendanceRepository.findStartedAttendanceOfAttendant(attendant)?.let { attendance ->
                attendanceService.redirectMessageToPwd(botNumber, message, attendance.pwd, attendant)
            }
        }
    }
}
