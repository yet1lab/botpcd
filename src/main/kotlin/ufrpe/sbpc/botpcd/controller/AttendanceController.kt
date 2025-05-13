package ufrpe.sbpc.botpcd.controller

import org.springframework.web.bind.annotation.*
import ufrpe.sbpc.botpcd.entity.Attendance
import ufrpe.sbpc.botpcd.service.AttendanceService

@RestController
@RequestMapping("/attendances")
class AttendanceController(private val attendanceService: AttendanceService) {

    @PostMapping("/begin")
    fun begin(@RequestBody attendance: Attendance) = attendanceService.beginAttendance(attendance)

    @PostMapping("/end")
    fun end(@RequestBody attendance: Attendance) = attendanceService.endAttendance(attendance)

    @GetMapping
    fun list(): List<Attendance> = attendanceService.searchAttendances()
}