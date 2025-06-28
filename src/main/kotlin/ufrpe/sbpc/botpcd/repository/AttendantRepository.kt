package ufrpe.sbpc.botpcd.repository

import org.springframework.data.jpa.repository.JpaRepository
import ufrpe.sbpc.botpcd.entity.Attendant

interface AttendantRepository: JpaRepository<Attendant, Long> {
    fun findByPhoneNumber(phone: String): Attendant?
    fun findByName(name: String): Attendant?
}