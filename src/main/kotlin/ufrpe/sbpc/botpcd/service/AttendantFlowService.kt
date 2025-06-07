package ufrpe.sbpc.botpcd.service

import org.springframework.stereotype.Service

@Service
class AttendantFlowService() {
    fun redirect() {
        if (lastBotMessageText in statusQuestionMessages) {
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