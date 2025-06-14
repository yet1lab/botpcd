package ufrpe.sbpc.botpcd

import com.whatsapp.api.domain.webhook.Value
import com.whatsapp.api.domain.webhook.WebHook
import io.cucumber.java.pt.Dado
import io.cucumber.java.pt.Entao
import io.cucumber.java.pt.Quando
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ufrpe.sbpc.botpcd.entity.Attendant
import ufrpe.sbpc.botpcd.entity.CommitteeMember
import ufrpe.sbpc.botpcd.entity.Disability
import ufrpe.sbpc.botpcd.entity.MessageExchange
import ufrpe.sbpc.botpcd.entity.Monitor
import ufrpe.sbpc.botpcd.entity.MonitorAssistanceType
import ufrpe.sbpc.botpcd.entity.PWD
import ufrpe.sbpc.botpcd.entity.UserStatus
import ufrpe.sbpc.botpcd.repository.AttendantRepository
import ufrpe.sbpc.botpcd.repository.CommitteeMemberRepository
import ufrpe.sbpc.botpcd.repository.MessageExchangeRepository
import ufrpe.sbpc.botpcd.repository.MonitorRepository
import ufrpe.sbpc.botpcd.repository.PWDRepository
import ufrpe.sbpc.botpcd.service.AttendantStatusService
import java.io.File
import java.math.BigInteger
import kotlin.test.assertTrue


class StepDefinitions(
    private val mockMvc: MockMvc,
    val pwdRepository: PWDRepository,
    val messageExchangeRepository: MessageExchangeRepository,
    val monitorRepository: MonitorRepository,
    val committeeMemberRepository: CommitteeMemberRepository,
    val attendantRepository: AttendantRepository,
    val attendantStatusService: AttendantStatusService
) {
    private var currentBotNumber: String = "15556557522"
    private val numberUserNotRegister: String = "558187654321"
    val logger: Logger = LoggerFactory.getLogger(StepDefinitions::class.java)

    @Dado("^usuário recebeu mensagem \"([^\"]*)\"$")
    fun `usuário recebeu mensagem`(message: String) {
        mockUserRecievedMessage(numberUserNotRegister, message)
    }

    @Dado("^usuário recebeu mensagem$")
    fun `usuário recebeu mensagem docs string`(message: String) {
        mockUserRecievedMessage(numberUserNotRegister, message)
    }

    @Dado("^pcd está cadastrado completo$")
    fun pcdEstaCadastrado() {
        // Implementar lógica para garantir que o PCD está cadastrado no sistema
        // Ex: pwdRepository.save(PWD(name = "PCD Teste Completo", phoneNumber = numberUserNotRegister, disabilities = mutableSetOf(Disability.DEAFNESS)))
    }

    @Dado("^usuário não cadastrado$")
    fun `usuário não cadastrado`() {
        pwdRepository.findByPhoneNumber(numberUserNotRegister)?.let { pwdRepository.delete(it) }
        assertNull(pwdRepository.findByPhoneNumber(numberUserNotRegister))
    }

    @Dado("^usuário possui deficiência cadastrada de \"([^\"]*)\"$")
    fun `usuário que possui deficiencia cadastrada`(tipoDeDeficiencia: String) {
        pwdRepository.findByPhoneNumber(numberUserNotRegister)?.let { pwdRepository.delete(it) }
        pwdRepository.save(
            PWD(
                phoneNumber = numberUserNotRegister,
                disabilities = mutableSetOf(Disability.getByAdjective(tipoDeDeficiencia))
            )
        )
    }

    @Quando("^usuário envia mensagem \"([^\"]*)\"$")
    fun `usuario envia mensagem`(mensagemEnviada: String) {
        userSendMessage(mensagemEnviada, numberUserNotRegister)
    }

    @Entao("^bot registrará que usuário tem ou possui \"([^\"]*)\"$")
    fun `bot registra deficiencia do usuário`(deficienciaAdjtivo: String) {
        val pwd = pwdRepository.findByPhoneNumberWithDisabilities(numberUserNotRegister)!!
        assertEquals(pwd.disabilities.first().adjective, deficienciaAdjtivo)
        pwdRepository.delete(pwd)
    }

    @Entao("^bot salvará o nome do usuário \"([^\"]*)\"$")
    fun `bot salva o nome do usuário`(nome: String) {
        val pwd = pwdRepository.findByPhoneNumber(numberUserNotRegister)
        assertEquals(pwd!!.name, nome)
        pwdRepository.delete(pwd)
    }

    @Entao("^usuário receberá mensagem \"([^\"]*)\"$")
    fun `usuario receberá mensagem`(message: String) {
        testarUltimaMensagemRecebidaDoUsuario(message, numberUserNotRegister)
    }

    @Entao("^usuário receberá mensagem$")
    fun `usuario receberá mensagem docs string`(message: String) {
        testarUltimaMensagemRecebidaDoUsuario(message, numberUserNotRegister)
    }

    @Entao("^A penúltima mensagem recebida pelo usuário será \"([^\"]*)\"$")
    fun `a penultima mensagem recebida pelo usuário será`(penultimaMensagem: String) {
        testarPenultimaMensagemRecebidaDoUsuario(
            mensagemEsperada = penultimaMensagem,
            userPhoneNumber = numberUserNotRegister
        )
    }

    @Entao("^bot enviará opcções de serviço \"([^\"]*)\" de acordo com a deficiência \"([^\"]*)\" do pcd$")
    fun `bot enviará opcçoes de seviço de acordo com a deficiência do pcd`(
        opcoesDeServico: String,
        tipoDeDeficiencia: String
    ) {
        val ultimaMensagem = messageExchangeRepository.lastExchangeMessage(
            toPhoneNumber = numberUserNotRegister,
            fromPhoneNumber = currentBotNumber
        )?.message ?: ""
        val opcoesEsperadas = opcoesDeServico.split(",")
        for (opcao in opcoesEsperadas) {
            assertTrue(
                ultimaMensagem.contains(opcao.trim()),
                "A opção '${opcao.trim()}' não foi encontrada na mensagem do bot: $ultimaMensagem"
            )
        }
        assertTrue(
            ultimaMensagem.contains(tipoDeDeficiencia, ignoreCase = true),
            "O tipo de deficiência '$tipoDeDeficiencia' não foi mencionado na mensagem: $ultimaMensagem"
        )
    }

    private fun createAttendant(phoneNumber: String, tipoAtendente: String, status: UserStatus): Attendant {
        attendantRepository.findByPhoneNumber(phoneNumber)?.let { attendantRepository.delete(it) }

        return when (tipoAtendente.lowercase()) {
            "monitor" -> {
                val monitor = Monitor(
                    name = "Monitor Teste $phoneNumber",
                    phoneNumber = phoneNumber,
                    status = status,
                    assistanceType = MonitorAssistanceType.MOBILITY_MONITOR
                )
                monitorRepository.save(monitor)
            }
            "membro da comissão" -> {
                val committeeMember = CommitteeMember(
                    name = "Comissão Teste $phoneNumber",
                    phoneNumber = phoneNumber,
                    status = status
                )
                committeeMemberRepository.save(committeeMember)
            }
            else -> throw IllegalArgumentException("Tipo de atendente desconhecido: $tipoAtendente")
        }
    }

    @Dado("^que sou um \"(.*)\" com número \"(.*)\" e status inicial \"(.*)\"$")
    fun `que sou um tipo_de_atendente com número e status inicial`(
        tipoAtendente: String,
        numeroAtendente: String, // Mantido como Int conforme original desta função
        statusInicialStr: String
    ) {
        val status = UserStatus.valueOf(statusInicialStr)
        val phoneNumber = numeroAtendente.toString()
        createAttendant(phoneNumber, tipoAtendente, status)
    }

    @Dado("que sou um {string} com número {biginteger} e status inicial {string}")
    fun `tipo_de_atendente com número e status inicial`(
        tipoAtendente: String,
        numeroAtendente: BigInteger, // Mantido como Int conforme original desta função
        statusInicialStr: String
    ) {
        val status = UserStatus.valueOf(statusInicialStr)
        val phoneNumber = numeroAtendente.toString()
        createAttendant(phoneNumber, tipoAtendente, status)
    }


    @Quando("^o \"([^\"]*)\" com número \"(\\d+)\" envia a mensagem \"([^\"]*)\"$")
    fun `o tipo_de_atendente com número envia a mensagem`(
        tipoAtendente: String,
        numeroAtendente: String,
        mensagemEnviada: String
    ) {
        val phoneNumber = numeroAtendente
        checkIfHasCorrectAttendantType(phoneNumber, tipoAtendente)
        userSendMessage(mensagemEnviada, phoneNumber)
    }

    @Entao("^o \"([^\"]*)\" com número \"(\\d+)\" receberá a mensagem$")
    fun `o tipo_de_atendente com número receberá a mensagem`(
        tipoAtendente: String,
        numeroAtendente: String,
        mensagemEsperadaDocString: String
    ) {
        val phoneNumber = numeroAtendente
        checkIfHasCorrectAttendantType(phoneNumber, tipoAtendente)
        testarUltimaMensagemRecebidaDoUsuario(mensagemEsperadaDocString, phoneNumber)
    }

    private fun checkIfHasCorrectAttendantType(phoneNumber: String, tipoAtendente: String) {
        val attendant = attendantRepository.findByPhoneNumber(phoneNumber)
        when (tipoAtendente) { // tipoAtendente já vem sem aspas
            "Monitor" -> {
                assertTrue(attendant is Monitor)
            }
            "Membro da Comissão" -> {
                assertTrue(attendant is CommitteeMember)
            }
            else -> {
                throw Exception("Foi passado um tipo inválido nos testes: $tipoAtendente")
            }
        }
    }

    @Dado("^o \"([^\"]*)\" com número \"(\\d+)\" recebeu a mensagem de opções para status \"([^\"]*)\"$")
    fun `o tipo_de_atendente com número recebeu a mensagem de opções para status`(
        @Suppress("UNUSED_PARAMETER") tipoAtendente: String,
        numeroAtendente: String,
        statusAntigoStr: String
    ) {
        val phoneNumber = numeroAtendente.toString()
        val statusAntigo = UserStatus.valueOf(statusAntigoStr)

        val expectedMessageContent = attendantStatusService.changeStatusTextOptionsFor[statusAntigo.name]
            ?: throw IllegalArgumentException("Mensagem de opção não encontrada para status $statusAntigoStr no AttendantStatusService.changeStatusTextOptionsFor. Chaves disponíveis: ${attendantStatusService.changeStatusTextOptionsFor.keys}")

        mockUserRecievedMessage(phoneNumber, expectedMessageContent)
    }

    @Entao("^o status do \"([^\"]*)\" com número \"(\\d+)\" deve ser \"([^\"]*)\"$")
    fun `o status do tipo_de_atendente com número deve ser`(
        tipoAtendente: String,
        numeroAtendente: String,
        statusNovoEsperadoStr: String
    ) {
        val phoneNumber = numeroAtendente.toString()
        val statusNovoEsperado = UserStatus.valueOf(statusNovoEsperadoStr)
        checkIfHasCorrectAttendantType(phoneNumber, tipoAtendente)
        val attendant = attendantRepository.findByPhoneNumber(phoneNumber)
        assertTrue(attendant != null, "Atendente com número $phoneNumber não encontrado no banco de dados.")
        assertEquals(statusNovoEsperado, attendant!!.status, "Status do atendente não foi atualizado corretamente para $statusNovoEsperado. Estava ${attendant.status}.")
    }

    fun mockUserRecievedMessage(userNumber: String, message: String) {
        messageExchangeRepository.save(
            MessageExchange(
                fromPhoneNumber = currentBotNumber,
                toPhoneNumber = userNumber,
                message = message.trimIndent()
            )
        )
    }

    fun testarUltimaMensagemRecebidaDoUsuario(mensagemEsperada: String, userPhoneNumber: String) {
        val actualMessage = messageExchangeRepository.lastExchangeMessage(
            toPhoneNumber = userPhoneNumber,
            fromPhoneNumber = currentBotNumber
        )?.message
        assertEquals(mensagemEsperada.trimIndent(), actualMessage?.trimIndent(), "A última mensagem recebida pelo usuário não foi a esperada.")
    }

    fun testarPenultimaMensagemRecebidaDoUsuario(mensagemEsperada: String, userPhoneNumber: String) {
        val messageList =
            messageExchangeRepository.listExchangeMessage(toPhoneNumber = userPhoneNumber, fromPhoneNumber = currentBotNumber)
        assertTrue(messageList.size >= 2, "Não há mensagens suficientes para verificar a penúltima.")
        assertEquals(mensagemEsperada.trimIndent(), messageList[messageList.lastIndex - 1].message.trimIndent(), "A penúltima mensagem recebida pelo usuário não foi a esperada.")
    }

    private fun userSendMessage(mensagemEnviada: String, userPhoneNumber: String) {
        val payload = loadPayload("src/test/resources/ufrpe/sbpc/botpcd/mocks/usuario-manda-oi.json")
            .changeUserNumber(userPhoneNumber)
            .changeUserMessage(mensagemEnviada) // mensagemEnviada já está correta
            .changeBotNumber(currentBotNumber)
        mockMvc.perform(
            post("/webhooks")
                .content(payload)
                .contentType("application/json")
        ).andExpect(status().isOk)
    }

    @Quando("^O PCD com a deficiência de \"([^\"]*)\" envia mensagem \"([^\"]*)\"$")
    fun pcdComDeficienciaMandaMensagem(deficiencia: String, mensagem: String) {
        logger.warn("Step 'O PCD com a deficiência de \"$deficiencia\" envia mensagem \"$mensagem\"' não implementado completamente.")
    }

    @Entao("^O bot vai enviar uma lista de opções de acordo com a \"([^\"]*)\"$")
    fun botEnviaListaOpcoes(opcoes: String) {
        logger.warn("Step 'O bot vai enviar uma lista de opções de acordo com a \"$opcoes\"' não implementado completamente.")
    }
}

fun loadPayload(filePath: String): String {
    return File(filePath).readText(Charsets.UTF_8)
}

fun String.changeUserNumber(newNumber: String): String {
    return this.replace(Regex("(?<=\"wa_id\": \")\\d+(?=\")"), newNumber)
}

fun String.changeUserMessage(newMessage: String): String {
    val escapedNewMessage = newMessage
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
    return this.replace(Regex("(?<=\\\"body\\\": \\\").*?(?=\\\")"), escapedNewMessage)
}

fun String.changeBotNumber(newNumber: String): String {
    return this.replace(Regex("(?<=\\\"phone_number_id\\\": \\\").*?(?=\\\")"), newNumber)
}

fun String.getChange(): Value {
    return WebHook.constructEvent(this).entry[0].changes[0].value
}

fun String.getBotNumber(): String {
    return this.getChange().metadata.displayPhoneNumber
}

fun String.getMessageBody(): String {
    return this.getChange().messages[0].text.body
}