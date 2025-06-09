package ufrpe.sbpc.botpcd.service

import java.time.LocalDateTime
import ufrpe.sbpc.botpcd.model.PWD
import ufrpe.sbpc.botpcd.model.Attendance
import ufrpe.sbpc.botpcd.model.ServiceType
import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.repository.AttendanceRepository

@Service
class QueueService(private val attendanceRepository: AttendanceRepository){
	fun add(service: ServiceType, pwd: PWD) {
		attendanceRepository.save(
			Attendance(serviceType = service, pwd = pwd, attendantType = service.attendantType)
		)
	}

	fun len(service: ServiceType): Long {
		return attendanceRepository.countRequestAttendanceOfService(service)
	}

	fun pop(service: ServiceType): Attendance? {
		val firstAtt = attendanceRepository.findRequestAttendanceOfService(service)

		firstAtt?.let {
			attendanceRepository.acceptPendingAttendanceForPwd(it.pwd, it.attendant, LocalDateTime.now())
		}
		return firstAtt
	}
}
