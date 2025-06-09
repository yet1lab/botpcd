package ufrpe.sbpc.botpcd.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ufrpe.sbpc.botpcd.entity.Attendance
import ufrpe.sbpc.botpcd.entity.Attendant
import ufrpe.sbpc.botpcd.entity.PWD
import java.time.LocalDateTime

interface AttendanceRepository : JpaRepository<Attendance, Long> {
	@Query("SELECT COUNT(att) FROM Attendance att WHERE att.serviceType = :serviceType AND att.acceptDateTime IS NULL")
	fun countRequestAttendanceOfService(serviceType: ServiceType): Long

  @Query("SELECT att from Attendance att where att.serviceType = :serviceType AND att.acceptDateTime is null order by att requestDateTime asc")
  fun findRequestAttendanceOfService(serviceType: ServiceType) : Attendance?
	
	@Query("SELECT att from Attendance att where att.pwd = :pwd AND att.startDateTime is null order by att.requestDateTime asc")
  fun findRequestAttendanceOfPwd(pwd: PWD) : Attendance?
  
	@Query("SELECT att from Attendance att where att.pwd = :pwd AND att.startDateTime is not null and att.endDateTime is null order by att.requestDateTime desc")
  fun  findStartedAttendanceOfPwd(pwd: PWD): Attendance?
  
	@Query("SELECT att from Attendance att where att.attendant = :attendant AND att.startDateTime is not null and att.endDateTime is null order by att.requestDateTime desc")
  fun findStartedAttendanceOfAttendant(attendant: Attendant): Attendance?
  
	@Modifying 
  @Query("UPDATE Attendance a SET a.attendant = :attendant, a.startDateTime = :acceptDateTime WHERE a.pwd = :pwd AND a.startDateTime IS NULL")
	fun acceptPendingAttendanceForPwd(
		@Param("pwd") pwd: PWD,
		@Param("attendant") attendant: Attendant,
		@Param("acceptDateTime") startDateTime: LocalDateTime
	): Int 
}
