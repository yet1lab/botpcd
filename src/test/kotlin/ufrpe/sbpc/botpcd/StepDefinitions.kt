package ufrpe.sbpc.botpcd

import com.whatsapp.api.domain.webhook.Value
import com.whatsapp.api.domain.webhook.WebHook
import io.cucumber.java.pt.Dado
import io.cucumber.java.pt.Entao
import io.cucumber.java.pt.Quando
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ufrpe.sbpc.botpcd.entity.Disability
import ufrpe.sbpc.botpcd.entity.MessageExchange
import ufrpe.sbpc.botpcd.entity.PWD
import ufrpe.sbpc.botpcd.repository.MessageExchangeRepository
import ufrpe.sbpc.botpcd.repository.PWDRepository
import java.io.File


class StepDefinitions(
    private val mockMvc: MockMvc,
    val pwdRepository: PWDRepository,
    val messageExchangeRepository: MessageExchangeRepository,
) {
    private val numberUserNotRegister: String = "558187654321"
    private val botNumber: String = "15556557522"
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
    }
    @Dado("usuário não cadastrado")
    fun `usuário não cadastrado`() {
        assertNull(pwdRepository.findByPhoneNumber(numberUserNotRegister))
    }
    @Dado("usuário possui deficiência cadastrada")
    fun `usuário que possui deficiencia cadastrada`() {
        pwdRepository.save(PWD(phoneNumber = numberUserNotRegister, disabilities = mutableSetOf(Disability.BLINDED)))
    }
    @Quando("usuário envia mensagem {string}")
    fun `usuario envia mensagem`(mensagemEnviada: String) {
        userSendMessage(mensagemEnviada, numberUserNotRegister)
    }
    @Entao("bot registrará o usuário com deficiencia {string}")
    fun `bot registra deficiencia do usuário`(deficiencia: String) {
        val pwd = pwdRepository.findByPhoneNumberWithDisabilities(numberUserNotRegister)!!
        assertEquals(pwd.disabilities.first().textOption, deficiencia)
        pwdRepository.delete(pwd)
    }
    @Entao("bot salvará o nome do usuário {string}")
    fun `bot salva o nome do usuário`(nome: String) {
        assertEquals(pwdRepository.findByPhoneNumber(numberUserNotRegister)!!.name, nome)
    }
    @Entao("usuário receberá mensagem {string}")
    fun `usuario receberá mensagem`(message: String) {
        testarMensagemRecebidaDoUsuario(message, numberUserNotRegister)
    }
    @Entao("usuário receberá mensagem")
    fun `usuario receberá mensagem docs string`(message: String) {
        testarMensagemRecebidaDoUsuario(message, numberUserNotRegister)
    }
    fun mockUserRecievedMessage(userNumber: String, message: String) {
        messageExchangeRepository.save(MessageExchange(fromPhoneNumber = botNumber, toPhoneNumber =  numberUserNotRegister, message = message))
    }
    fun testarMensagemRecebidaDoUsuario(mensagemEsperada: String, userPhoneNumber: String) {
        assertEquals(mensagemEsperada, messageExchangeRepository.lastExchangeMessage(toPhoneNumber = userPhoneNumber, fromPhoneNumber = botNumber)?.message)
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