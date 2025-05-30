package ufrpe.sbpc.botpcd.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ufrpe.sbpc.botpcd.entity.*
import ufrpe.sbpc.botpcd.repository.AttendanceRepository

@Service
class AttendanceService(
    private val attendanceRepository: AttendanceRepository,
    private val userStatusService: AttendantStatusService
) {

    fun searchAttendances(): List<Attendance> {
        return attendanceRepository.findAll()
    }

    fun getAttendant(attendant: Attendant): Any {
        return when (attendant) {
            Attendant.MONITOR -> attendance.attendant as Monitor
            Attendant.COMMITTEE_MEMBER -> attendance.provider as CommitteeMember
        }
    }

    @Transactional
    fun beginAttendance(attendance: Attendance) {
        when (attendance.attendantType) {
            Attendant.MONITOR -> userStatusService.setMonitorStatus(attendance.attendant as Monitor, UserStatus.BUSY)
            Attendant.COMMITTEE_MEMBER -> userStatusService.setCommitteeMemberStatus(attendance.attendant as CommitteeMember, UserStatus.BUSY)
        }
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