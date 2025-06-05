package ufrpe.sbpc.botpcd.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ufrpe.sbpc.botpcd.entity.PWD

interface PWDRepository: JpaRepository<PWD, Long> {
    fun findByPhoneNumber(phoneNumber: String): PWD?
    @Query("SELECT p FROM PWD p JOIN FETCH p.disabilities WHERE p.phoneNumber = :phoneNumber")
    fun findByPhoneNumberWithDisabilities(phoneNumber: String): PWD?
}