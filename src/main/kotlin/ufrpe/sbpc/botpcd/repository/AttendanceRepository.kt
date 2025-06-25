package ufrpe.sbpc.botpcd.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ufrpe.sbpc.botpcd.entity.Attendance
import ufrpe.sbpc.botpcd.entity.Attendant
import ufrpe.sbpc.botpcd.entity.PWD
import ufrpe.sbpc.botpcd.entity.ServiceType
import java.time.LocalDateTime

interface AttendanceRepository : JpaRepository<Attendance, Long> {
	@Query("SELECT COUNT(att) FROM Attendance att WHERE att.serviceType = :serviceType AND att.startDateTime IS NULL")
	fun countRequestAttendanceOfService(serviceType: ServiceType): Long
	@Query("SELECT att FROM Attendance att WHERE att.startDateTime IS NULL AND att.attendantType = 'COMMITTEE_MEMBER'")
	fun findPendingCommitteeMemberAttendances(): List<Attendance>
  @Query("SELECT att from Attendance att where att.serviceType = :serviceType AND att.startDateTime is null order by att.requestDateTime asc")
  fun findRequestAttendanceOfService(serviceType: ServiceType) : List<Attendance>
	
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
		@Param("attendant") attendant: Attendant?,
		@Param("acceptDateTime") startDateTime: LocalDateTime
	): Int

	@Modifying(flushAutomatically = true, clearAutomatically = true) // Modificação aqui
	@Query("DELETE FROM Attendance a WHERE a.pwd = :pwd")
	fun deleteAllByPwd(@Param("pwd") pwd: PWD)

	@Modifying(flushAutomatically = true, clearAutomatically = true) // Modificação aqui
	@Query("DELETE FROM Attendance a WHERE a.attendant = :att")
	fun deleteAllByAttendant(@Param("att") att: Attendant)
}
