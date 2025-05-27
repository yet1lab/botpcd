package ufrpe.sbpc.botpcd.repository

import org.springframework.data.jpa.repository.JpaRepository
import ufrpe.sbpc.botpcd.entity.Attendance

interface AttendanceRepository : JpaRepository<Attendance, Long>