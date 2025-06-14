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

    // Steps existentes (mantidos para compatibilidade, podem ser revisados/removidos se não mais usados)
    @Dado("usuário recebeu mensagem {string}")
    fun `usuário recebeu mensagem`(message: String) {
        mockUserRecievedMessage(numberUserNotRegister, message)
    }

    @Dado("usuário recebeu mensagem")
    fun `usuário recebeu mensagem docs string`(message: String) {
        mockUserRecievedMessage(numberUserNotRegister, message)
    }

    @Dado("pcd está cadastrado completo")
    fun pcdEstaCadastrado() {
        // Implementar lógica para garantir que o PCD está cadastrado no sistema
        // Ex: pwdRepository.save(PWD(name = "PCD Teste Completo", phoneNumber = numberUserNotRegister, disabilities = mutableSetOf(Disability.DEAFNESS)))
    }

    @Dado("usuário não cadastrado")
    fun `usuário não cadastrado`() {
        pwdRepository.findByPhoneNumber(numberUserNotRegister)?.let { pwdRepository.delete(it) }
        assertNull(pwdRepository.findByPhoneNumber(numberUserNotRegister))
    }

    @Dado("usuário possui deficiência cadastrada de {string}")
    fun `usuário que possui deficiencia cadastrada`(tipoDeDeficiencia: String) {
        pwdRepository.findByPhoneNumber(numberUserNotRegister)?.let { pwdRepository.delete(it) }
        pwdRepository.save(
            PWD(
                phoneNumber = numberUserNotRegister,
                disabilities = mutableSetOf(Disability.getByAdjective(tipoDeDeficiencia.replace("\"", "")))
            )
        )
    }

    @Quando("usuário envia mensagem {string}")
    fun `usuario envia mensagem`(mensagemEnviada: String) {
        userSendMessage(mensagemEnviada.replace("\"", ""), numberUserNotRegister)
    }

    @Entao("bot registrará que usuário tem ou possui {string}")
    fun `bot registra deficiencia do usuário`(deficienciaAdjtivo: String) {
        val pwd = pwdRepository.findByPhoneNumberWithDisabilities(numberUserNotRegister)!!
        assertEquals(pwd.disabilities.first().adjective, deficienciaAdjtivo.replace("\"", ""))
        pwdRepository.delete(pwd)
    }

    @Entao("bot salvará o nome do usuário {string}")
    fun `bot salva o nome do usuário`(nome: String) {
        val pwd = pwdRepository.findByPhoneNumber(numberUserNotRegister)
        assertEquals(pwd!!.name, nome.replace("\"", ""))
        pwdRepository.delete(pwd)
    }

    @Entao("usuário receberá mensagem {string}")
    fun `usuario receberá mensagem`(message: String) {
        testarUltimaMensagemRecebidaDoUsuario(message.replace("\"", ""), numberUserNotRegister)
    }

    @Entao("usuário receberá mensagem")
    fun `usuario receberá mensagem docs string`(message: String) {
        testarUltimaMensagemRecebidaDoUsuario(message, numberUserNotRegister) // DocString não precisa de replace
    }

    @Entao("A penúltima mensagem recebida pelo usuário será {string}")
    fun `a penultima mensagem recebida pelo usuário será`(penultimaMensagem: String) {
        testarPenultimaMensagemRecebidaDoUsuario(
            mensagemEsperada = penultimaMensagem.replace("\"", ""),
            userPhoneNumber = numberUserNotRegister
        )
    }

    @Entao("bot enviará opcções de serviço {string} de acordo com a deficiência {string} do pcd")
    fun `bot enviará opcçoes de seviço de acordo com a deficiência do pcd`(
        opcoesDeServico: String,
        tipoDeDeficiencia: String
    ) {
        val ultimaMensagem = messageExchangeRepository.lastExchangeMessage(
            toPhoneNumber = numberUserNotRegister,
            fromPhoneNumber = currentBotNumber // Usar currentBotNumber
        )?.message ?: ""
        val opcoesEsperadas = opcoesDeServico.replace("\"", "").split(",")
        for (opcao in opcoesEsperadas) {
            assertTrue(
                ultimaMensagem.contains(opcao.trim()),
                "A opção '${opcao.trim()}' não foi encontrada na mensagem do bot: $ultimaMensagem"
            )
        }
        assertTrue(
            ultimaMensagem.contains(tipoDeDeficiencia.replace("\"", ""), ignoreCase = true),
            "O tipo de deficiência '${tipoDeDeficiencia.replace("\"", "")}' não foi mencionado na mensagem: $ultimaMensagem"
        )
    }

    private fun createAttendant(phoneNumber: String, tipoAtendente: String, status: UserStatus): Attendant {
        // Limpa atendente existente com o mesmo número para evitar conflitos entre testes
        attendantRepository.findByPhoneNumber(phoneNumber)?.let { attendantRepository.delete(it) }

        return when (tipoAtendente.lowercase().replace("\"", "")) {
            "monitor" -> {
                val monitor = Monitor(
                    name = "Monitor Teste $phoneNumber", // Nome único para debug
                    phoneNumber = phoneNumber,
                    status = status,
                    assistanceType = MonitorAssistanceType.MOBILITY_MONITOR // Tipo padrão, ajuste se necessário
                )
                monitorRepository.save(monitor)
            }
            "membro da comissão" -> {
                val committeeMember = CommitteeMember(
                    name = "Comissão Teste $phoneNumber", // Nome único para debug
                    phoneNumber = phoneNumber,
                    status = status
                )
                committeeMemberRepository.save(committeeMember)
            }
            else -> throw IllegalArgumentException("Tipo de atendente desconhecido: $tipoAtendente")
        }
    }

    @Dado("que sou um {string} com número {int} e status inicial {string}")
    fun `que sou um tipo_de_atendente com número e status inicial`(
        tipoAtendente: String,
        numeroAtendente: String,
        statusInicialStr: String
    ) {
        val status = UserStatus.valueOf(statusInicialStr.replace("\"", ""))
        val phoneNumber = numeroAtendente.replace("\"", "")
        createAttendant(phoneNumber, tipoAtendente, status)
    }

    @Quando("o {string} com número {int} envia a mensagem {string}")
    fun `o tipo_de_atendente com número envia a mensagem`(
        tipoAtendente: String, // Não usado na lógica, mas presente no Gherkin
        numeroAtendente: String,
        mensagemEnviada: String
    ) {
        val phoneNumber = numeroAtendente.replace("\"", "")
        val message = mensagemEnviada.replace("\"", "")
        checkIfHasCorrectAttendantType(phoneNumber, tipoAtendente)
        userSendMessage(message, phoneNumber)
    }

    @Entao("o {string} com número {int} receberá a mensagem")
    fun `o tipo_de_atendente com número receberá a mensagem`(
        tipoAtendente: String, // Não usado na lógica, mas presente no Gherkin
        numeroAtendente: String,
        mensagemEsperadaDocString: String
    ) {
        val phoneNumber = numeroAtendente.replace("\"", "")
        checkIfHasCorrectAttendantType(phoneNumber, tipoAtendente)
        // DocString já vem formatada, não precisa de replace de aspas aqui.
        testarUltimaMensagemRecebidaDoUsuario(mensagemEsperadaDocString, phoneNumber)
    }

    private fun checkIfHasCorrectAttendantType(phoneNumber: String, tipoAtendente: String) {
        val attendant = attendantRepository.findByPhoneNumber(phoneNumber)
        when (tipoAtendente) {
            "Monitor" -> {
                assertTrue(attendant is Monitor)
            }

            "Membro da Comissão" -> {
                assertTrue(attendant is CommitteeMember)
            }

            else -> {
                throw Exception("Foi passado um tipo inválido nos testes")
            }
        }
    }


    @Dado("o {string} com número {string} recebeu a mensagem de opções para status {string}")
    fun `o tipo_de_atendente com número recebeu a mensagem de opções para status`(
        @Suppress("UNUSED_PARAMETER") tipoAtendente: String, // Não usado na lógica, mas presente no Gherkin
        numeroAtendente: String,
        statusAntigoStr: String
    ) {
        val phoneNumber = numeroAtendente.replace("\"", "")
        val statusAntigo = UserStatus.valueOf(statusAntigoStr.replace("\"", ""))

        // Obtém a mensagem formatada diretamente do AttendantStatusService
        // As chaves no mapa `changeStatusTextOptionsFor` são os nomes dos enums (ex: "AVAILABLE")
        val expectedMessageContent = attendantStatusService.changeStatusTextOptionsFor[statusAntigo.name]
            ?: throw IllegalArgumentException("Mensagem de opção não encontrada para status $statusAntigoStr no AttendantStatusService.changeStatusTextOptionsFor. Chaves disponíveis: ${attendantStatusService.changeStatusTextOptionsFor.keys}")

        // A mensagem real enviada pelo WhatsappService já inclui o autor "BotPCD" se o AttendantStatusService o passou.
        // O `changeStatusTextOptionsFor` já formata com autor "BotPCD".
        mockUserRecievedMessage(phoneNumber, expectedMessageContent)
    }

    @Entao("o status do {string} com número {string} deve ser {string}")
    fun `o status do tipo_de_atendente com número deve ser`(
        tipoAtendente: String, // Não usado na lógica, mas presente no Gherkin
        numeroAtendente: String,
        statusNovoEsperadoStr: String
    ) {
        val phoneNumber = numeroAtendente.replace("\"", "")
        val statusNovoEsperado = UserStatus.valueOf(statusNovoEsperadoStr.replace("\"", ""))
        checkIfHasCorrectAttendantType(phoneNumber, tipoAtendente)
        val attendant = attendantRepository.findByPhoneNumber(phoneNumber)
        assertTrue(attendant != null, "Atendente com número $phoneNumber não encontrado no banco de dados.")
        assertEquals(statusNovoEsperado, attendant!!.status, "Status do atendente não foi atualizado corretamente para $statusNovoEsperado. Estava ${attendant.status}.")
        // Considerar limpar o atendente aqui ou em um @After hook se necessário para isolamento de teste
        // attendantRepository.delete(attendant)
    }

    // Funções auxiliares adaptadas para usar currentBotNumber
    fun mockUserRecievedMessage(userNumber: String, message: String) {
        messageExchangeRepository.save(
            MessageExchange(
                fromPhoneNumber = currentBotNumber, // Usar currentBotNumber
                toPhoneNumber = userNumber,
                message = message.trimIndent() // trimIndent para DocStrings
            )
        )
    }

    fun testarUltimaMensagemRecebidaDoUsuario(mensagemEsperada: String, userPhoneNumber: String) {
        val actualMessage = messageExchangeRepository.lastExchangeMessage(
            toPhoneNumber = userPhoneNumber,
            fromPhoneNumber = currentBotNumber // Usar currentBotNumber
        )?.message
        assertEquals(mensagemEsperada.trimIndent(), actualMessage?.trimIndent(), "A última mensagem recebida pelo usuário não foi a esperada.")
    }

    fun testarPenultimaMensagemRecebidaDoUsuario(mensagemEsperada: String, userPhoneNumber: String) {
        val messageList =
            messageExchangeRepository.listExchangeMessage(toPhoneNumber = userPhoneNumber, fromPhoneNumber = currentBotNumber) // Usar currentBotNumber
        assertTrue(messageList.size >= 2, "Não há mensagens suficientes para verificar a penúltima.")
        assertEquals(mensagemEsperada.trimIndent(), messageList[messageList.lastIndex - 1].message.trimIndent(), "A penúltima mensagem recebida pelo usuário não foi a esperada.")
    }

    private fun userSendMessage(mensagemEnviada: String, userPhoneNumber: String) {
        val payload = loadPayload("src/test/resources/ufrpe/sbpc/botpcd/mocks/usuario-manda-oi.json")
            .changeUserNumber(userPhoneNumber)
            .changeUserMessage(mensagemEnviada)
            .changeBotNumber(currentBotNumber) // Usar currentBotNumber
        mockMvc.perform(
            post("/webhooks")
                .content(payload)
                .contentType("application/json")
        ).andExpect(status().isOk)
    }

    // Steps antigos que podem precisar de revisão ou não são usados pelo novo feature
    @Quando("O PCD com a deficiência de {string} envia mensagem {string}")
    fun pcdComDeficienciaMandaMensagem(deficiencia: String, mensagem: String) {
        // Implementar lógica para simular a mensagem enviada pelo PCD
        // Ex: Criar PCD com deficiência, depois userSendMessage
        logger.warn("Step 'O PCD com a deficiência de {string} envia mensagem {string}' não implementado completamente.")
    }

    @Entao("O bot vai enviar uma lista de opções de acordo com a {string}")
    fun botEnviaListaOpcoes(opcoes: String) {
        // Implementar lógica para verificar se a resposta do bot corresponde às opções esperadas
        logger.warn("Step 'O bot vai enviar uma lista de opções de acordo com a {string}' não implementado completamente.")
    }
}

// Funções de manipulação de payload (mantidas como estavam)
fun loadPayload(filePath: String): String {
    return File(filePath).readText(Charsets.UTF_8)
}

fun String.changeUserNumber(newNumber: String): String {
    return this.replace(Regex("(?<=\"wa_id\": \")\\d+(?=\")"), newNumber)
}

fun String.changeUserMessage(newMessage: String): String {
    // Escapa caracteres especiais na nova mensagem para que funcionem bem dentro de uma string JSON
    val escapedNewMessage = newMessage
        .replace("\\", "\\\\") // Escapa barras invertidas
        .replace("\"", "\\\"") // Escapa aspas duplas
        .replace("\n", "\\n")  // Escapa novas linhas
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