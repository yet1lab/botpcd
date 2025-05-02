package ufrpe.sbpc.botpcd.repository

import org.springframework.data.jpa.repository.JpaRepository
import ufrpe.sbpc.botpcd.entity.PWD

interface PWDRepository: JpaRepository<PWD, Long> {
    fun findByPhoneNumber(phoneNumber: String): PWD?
}