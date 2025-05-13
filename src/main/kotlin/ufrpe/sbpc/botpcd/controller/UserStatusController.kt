package ufrpe.sbpc.botpcd.controller

import org.springframework.web.bind.annotation.*
import ufrpe.sbpc.botpcd.entity.UserStatus
import ufrpe.sbpc.botpcd.repository.MonitorRepository
import ufrpe.sbpc.botpcd.repository.CommitteeMemberRepository
import ufrpe.sbpc.botpcd.service.UserStatusService

@RestController
@RequestMapping("/user-status")
class UserStatusController(
    private val userStatusService: UserStatusService,
    private val monitorRepository: MonitorRepository,
    private val committeeMemberRepository: CommitteeMemberRepository
) {

    @PostMapping("/monitor/{id}")
    fun updateMonitorStatus(
        @PathVariable id: Long,
        @RequestParam status: UserStatus
    ) {
        val monitor = monitorRepository.findById(id).orElseThrow()
        userStatusService.setMonitorStatus(monitor, status)
    }

    @PostMapping("/committee/{id}")
    fun updateCommitteeMemberStatus(
        @PathVariable id: Long,
        @RequestParam status: UserStatus
    ) {
        val member = committeeMemberRepository.findById(id).orElseThrow()
        userStatusService.setCommitteeMemberStatus(member, status)
    }
}