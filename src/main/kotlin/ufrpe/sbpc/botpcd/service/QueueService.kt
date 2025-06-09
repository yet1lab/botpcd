package ufrpe.sbpc.botpcd.service
import java.time.LocalDateTime

@Service
class QueueService(){
	fun pop(service: ServiceType){
		// get first attendance of list    TODO: switch pwd uses to new pop function
		val firstAtt = AttendanceRepository.findRequestAttendanceOfService(service) 
		// "remove" from list
		AttendanceRepository.acceptPendingAttendanceForPwd(firstAtt.pwd, firstAtt.Attendant, LocalDateTime.now())
	}

	fun push(service: ServiceType, pwd: PWD) {
    attendanceRepository.save(Attendance(serviceType = service, pwd = pwd, attendantType = service.attendantType))
  }
}
}
