package ufrpe.sbpc.botpcd.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ufrpe.sbpc.botpcd.entity.*
import ufrpe.sbpc.botpcd.repository.AttendanceRepository

@Service
class AttendanceService(
    private val attendanceRepository: AttendanceRepository,
    private val userStatusService: UserStatusService
) {

    fun searchAttendances(): List<Attendance> {
        return attendanceRepository.findAll()
    }

    @Transactional
    fun beginAttendance(attendance: Attendance) {
        userStatusService.setMonitorStatus(attendance.monitor, UserStatus.BUSY)
        userStatusService.setCommitteeMemberStatus(attendance.committeemember, UserStatus.BUSY)
        attendanceRepository.save(attendance)
    }

    @Transactional
    fun endAttendance(attendance: Attendance) {
        userStatusService.setMonitorStatus(attendance.monitor, UserStatus.AVAILABLE)
        userStatusService.setCommitteeMemberStatus(attendance.committeemember, UserStatus.AVAILABLE)
        attendance.endDateTime = java.time.LocalDateTime.now()
        attendanceRepository.save(attendance)
    }

}