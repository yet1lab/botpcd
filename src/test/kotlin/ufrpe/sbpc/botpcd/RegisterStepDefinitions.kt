package ufrpe.sbpc.botpcd

import com.fasterxml.jackson.databind.json.JsonMapper
import com.whatsapp.api.domain.webhook.WebHook
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ufrpe.sbpc.botpcd.entity.Disability
import ufrpe.sbpc.botpcd.entity.MessageExchange
import ufrpe.sbpc.botpcd.repository.MessageExchangeRepository
import ufrpe.sbpc.botpcd.repository.PWDRepository
import java.io.File

class RegisterStepDefinitions(
    private val mockMvc: MockMvc,
    val whatsappBusinessCloudApiMock: WhatsappBusinessCloudApiMock,
    val pwdRepository: PWDRepository,
    val payloadMockFactory: PayloadMockFactory,
    val messageExchangeRepository: MessageExchangeRepository,
) {
    private val numberUserNotRegister: String = "558187654321"
    @Given("usuário não cadastrado")
    fun `usuário não cadastrado`() {
        assertEquals(pwdRepository.findByPhoneNumber(numberUserNotRegister), null)
    }

    @When("usuário mandar qualquer mensagem para o botpcd")
    fun `usuário mandar qualquer mensagem para o botpcd`() {
        var payload = payloadMockFactory.loadPayload("src/test/resources/ufrpe/sbpc/botpcd/mocks/usuario-manda-oi.json")
        payload = payloadMockFactory.changeUserNumber(payload, numberUserNotRegister)
        mockMvc.perform(
            post("/webhooks")
                .content(payload)
                .contentType("application/json")
        ).andExpect(status().isOk)
        messageExchangeRepository.save(MessageExchange(
            fromNumber = numberUserNotRegister,
            toNumber = payloadMockFactory.getBotNumber(payload),
            message = payloadMockFactory.getMessage(payload)
        ))
    }

    @Then("bot envia mensagem {string}")
    fun `bot envia mensagem`(message: String) {
        var mensagemEsperada = ""
        if(message == "com os tipos de deficiencias") {
            mensagemEsperada = """Olá, qual sua deficiência?
Digite 1 para Deficiência visual 
Digite 2 para Deficiência auditiva/surdez 
Digite 3 para Surdocegueira 
Digite 4 para Transtorno do Espectro Autista/Neurodivergente 
Digite 5 para Deficiência física 
Digite 6 para Não tenho deficiência, mas tenho mobilidade reduzida 
Digite 7 para Não preciso de suporte.
        """.trimIndent()
        } else {
            mensagemEsperada = message
        }
        val apiMock = whatsappBusinessCloudApiMock
        val mensagemEnviada = apiMock.capturedMessage!!.textMessage!!.body
        assertEquals(mensagemEnviada, mensagemEsperada)
    }
    @Given("usuário que recebeu a mensagem {string}")
    fun `usuário que recebeu a mensagem`(message: String) {
        val latestMessage = messageExchangeRepository.findLatestMessage()
    }
}
