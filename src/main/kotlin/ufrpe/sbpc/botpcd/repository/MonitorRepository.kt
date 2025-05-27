package ufrpe.sbpc.botpcd.repository

import org.springframework.data.jpa.repository.JpaRepository
import ufrpe.sbpc.botpcd.entity.Monitor
import ufrpe.sbpc.botpcd.entity.UserStatus

interface MonitorRepository : JpaRepository<Monitor, Long>{
    fun findByPhoneNumber(phoneNumber: String): Monitor?
    fun findByStatus(status: UserStatus): List<Monitor>
}