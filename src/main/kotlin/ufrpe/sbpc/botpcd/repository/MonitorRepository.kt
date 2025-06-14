package ufrpe.sbpc.botpcd.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ufrpe.sbpc.botpcd.entity.Monitor
import ufrpe.sbpc.botpcd.entity.MonitorAssistanceType
import ufrpe.sbpc.botpcd.entity.UserStatus

interface MonitorRepository : JpaRepository<Monitor, Long>{
    fun findByPhoneNumber(phoneNumber: String): Monitor?
    fun findByStatus(status: UserStatus): List<Monitor>
    @Query("SELECT m from Monitor m where m.status = :status and m.assistanceType = :assistanceType")
    fun findAvailableMonitor(status: UserStatus, assistanceType: MonitorAssistanceType): List<Monitor>
}