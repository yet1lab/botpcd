package ufrpe.sbpc.botpcd.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ufrpe.sbpc.botpcd.entity.Monitor
import ufrpe.sbpc.botpcd.entity.MonitorAssistanceType
import ufrpe.sbpc.botpcd.entity.UserStatus

interface MonitorRepository : JpaRepository<Monitor, Long> {
    fun findByPhoneNumber(phoneNumber: String): Monitor?
    fun findByName(phoneName: String): Monitor?
    fun findByStatus(status: UserStatus): List<Monitor>

    @Query(
        """
  SELECT m
  FROM Monitor m
  LEFT JOIN Attendance a ON a.attendant.id = m.id
  WHERE m.status = :status
    AND m.assistanceType = :assistanceType
  ORDER BY a.endDateTime ASC
"""
    )
    fun findAvailableMonitor(
        status: UserStatus,
        assistanceType: MonitorAssistanceType
    ): List<Monitor>
}