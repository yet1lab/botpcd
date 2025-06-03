package ufrpe.sbpc.botpcd

import com.whatsapp.api.domain.webhook.Value
import com.whatsapp.api.domain.webhook.WebHook
import io.cucumber.java.pt.Dado
import io.cucumber.java.pt.Entao
import io.cucumber.java.pt.Quando
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ufrpe.sbpc.botpcd.entity.MessageExchange
import ufrpe.sbpc.botpcd.repository.MessageExchangeRepository
import ufrpe.sbpc.botpcd.repository.PWDRepository
import java.io.File

class StepDefinitions(
    private val mockMvc: MockMvc,
    val whatsappBusinessCloudApiMock: WhatsappBusinessCloudApiMock,
    val pwdRepository: PWDRepository,
    val messageExchangeRepository: MessageExchangeRepository,
) {
    private val numberUserNotRegister: String = "558187654321"
    private val botNumber: String = "15556557522"
    @Dado("usuário não cadastrado")
    fun `usuário não cadastrado`() {
        assertEquals(pwdRepository.findByPhoneNumber(numberUserNotRegister), null)
    }
    @Quando("usuário envia mensagem {string}")
    fun `usuário envia a mensagem`(mensagemEnviada: String) {
        userSendMessage(mensagemEnviada, numberUserNotRegister)
    }
    @Entao("bot envia mensagem {string}")
    fun `bot envia mensagem quotes strings`(mensagemEsperada: String) {
        testarMensagemEnviada(mensagemEsperada)
    }
    @Entao("bot registrar o usuário com {string}")
    fun `bot registra deficiencia do usuário`(deficiencia: String) {
        assertEquals(pwdRepository.findByPhoneNumber(numberUserNotRegister)!!.disability.first().textOption, deficiencia)
    }
    @Entao("bot envia mensagem")
    fun `bot envia mensagem docs string`(mensagemEsperada: String) {
        testarMensagemEnviada(mensagemEsperada)
    }
    @Dado("usuário recebeu mensagem {string}")
    fun `usuário recebeu mensagem`(message: String) {
        testarMensagemRecibida(message)
    }
    @Dado("usuário recebeu mensagem")
    fun `usuário recebeu mensagem docs string`(message: String) {
        testarMensagemRecibida(message)
    }
    @Dado("pcd está cadastrado")
    fun pcdEstaCadastrado() {
        // Implementar lógica para garantir que o PCD está cadastrado no sistema
    }
    fun testarMensagemEnviada(mensagemEsperada: String) {
        val apiMock = whatsappBusinessCloudApiMock
        val mensagemEnviada = apiMock.capturedMessage!!.textMessage!!.body
        assertEquals(mensagemEnviada, mensagemEsperada)
    }
    fun testarMensagemRecibida(mensagemRecebida: String) {
        val latestMessage = messageExchangeRepository.findFirstByToNumberOrderByCreateAtDesc(this.numberUserNotRegister)
        assertEquals(latestMessage!!.message, mensagemRecebida)
    }
    private fun userSendMessage(mensagemEnviada: String, userPhoneNumber: String) {
        val payload = loadPayload("src/test/resources/ufrpe/sbpc/botpcd/mocks/usuario-manda-oi.json")
            .changeUserNumber(userPhoneNumber)
            .changeUserMessage(mensagemEnviada)
            .changeBotNumber(botNumber)

        mockMvc.perform(
            post("/webhooks")
                .content(payload)
                .contentType("application/json")
        ).andExpect(status().isOk)

        messageExchangeRepository.save(
            MessageExchange(
                fromNumber = numberUserNotRegister,
                toNumber = payload.getBotNumber(),
                message = payload.getMessageBody()
            )
        )
    }

    @Quando("O PCD com a deficiência de {string} envia mensagem {string}")
    fun pcdComDeficienciaMandaMensagem(deficiencia: String, mensagem: String) {
        // Implementar lógica para simular a mensagem enviada pelo PCD
    }

    @Entao("O bot vai enviar uma lista de opções de acordo com a {string}")
    fun botEnviaListaOpcoes(opcoes: String) {
        // Implementar lógica para verificar se a resposta do bot corresponde às opções esperadas
    }
}

fun loadPayload(filePath: String): String {
    return File(filePath).readText(Charsets.UTF_8)
}
fun String.changeUserNumber(newNumber: String): String {
    return this.replace(Regex("(?<=\"wa_id\": \")\\d+(?=\")"), newNumber)
}
fun String.changeUserMessage(newMessage: String): String {
    return this.replace(Regex("(?<=\\\"body\\\": \\\").*?(?=\\\")"), newMessage)
}
fun String.changeBotNumber(newNumber: String): String {
    return this.replace(Regex("(?<=\\\"display_phone_number\\\": \\\").*?(?=\\\")"), newNumber)
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