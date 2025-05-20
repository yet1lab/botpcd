package ufrpe.sbpc.botpcd.service

import com.whatsapp.api.domain.webhook.Change
import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.entity.UserStatus
import ufrpe.sbpc.botpcd.repository.CommitteeMemberRepository
import ufrpe.sbpc.botpcd.repository.MonitorRepository
import ufrpe.sbpc.botpcd.repository.PWDRepository

@Service
class FirstContactService(
    private val pwdRepository: PWDRepository,
    private val monitorRepository: MonitorRepository,
    private val committeeMemberRepository: CommitteeMemberRepository,
    private val registerService: RegisterService,
    private val attendanceStatusService: AttendantStatusService
) {
    fun redirectFluxByUserType(phoneNumber: String, change: Change) {
        when {
            change.value.messages[0].text.body == "BotPCD" && (monitorRepository.findByPhoneNumber(phoneNumber) != null || (committeeMemberRepository.findByPhoneNumber(
                phoneNumber
            ) != null)) -> {
                val attendant = monitorRepository.findByPhoneNumber(phoneNumber) ?: committeeMemberRepository.findByPhoneNumber(
                    phoneNumber)!!
                attendanceStatusService.sendStatusChanger(attendant)
            }
            pwdRepository.findByPhoneNumber(phoneNumber) != null -> {

            }
            monitorRepository.findByPhoneNumber(phoneNumber) != null -> {

            }

            committeeMemberRepository.findByPhoneNumber(phoneNumber) != null -> {

            }

            else -> {
                // Usuario não cadastrado
//                registerService.registerPWD(phoneNumber, change)
            }
        }
    }
}