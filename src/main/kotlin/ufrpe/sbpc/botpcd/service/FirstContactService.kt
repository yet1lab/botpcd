package ufrpe.sbpc.botpcd.service

import com.whatsapp.api.domain.webhook.Change
import org.springframework.stereotype.Service
import ufrpe.sbpc.botpcd.repository.CommitteeMemberRepository
import ufrpe.sbpc.botpcd.repository.MonitorRepository
import ufrpe.sbpc.botpcd.repository.PWDRepository

@Service
class FirstContactService(
    private val pwdRepository: PWDRepository,
    private val monitorRepository: MonitorRepository,
    private val committeeMemberRepository: CommitteeMemberRepository,
    private val registerPWDService: RegisterPWDService,
    private val whatsappService: WhatsappService
) {
    fun redirectFluxByUserType(phoneNumber: String, change: Change) {
        // to get a message from the change change.value.messages[0].text.body
        val botNumber = change.value.metadata.phoneNumberId
        when {
            pwdRepository.findByPhoneNumber(phoneNumber) != null -> {
                val pwd = pwdRepository.findByPhoneNumber(phoneNumber)!!
                if(pwd.name == null) {
//                    registerService.
                }
            }
            monitorRepository.findByPhoneNumber(phoneNumber) != null -> {

            }
            committeeMemberRepository.findByPhoneNumber(phoneNumber) != null -> {

            }
            else -> {
                // Usuario não cadastrado
                registerPWDService.whatIsYourDisability(botNumber, phoneNumber)
            }
        }
    }
}