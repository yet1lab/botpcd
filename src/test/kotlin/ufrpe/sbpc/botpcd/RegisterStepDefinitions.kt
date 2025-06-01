package ufrpe.sbpc.botpcd

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import ufrpe.sbpc.botpcd.entity.Disability
import java.io.File

class RegisterStepDefinitions {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var whatsappBusinessCloudApiMock: WhatsappBusinessCloudApiMock
    @Given("usuário não cadastrado")
    fun `usuário não cadastrado`() {

    }
    @When("usuário mandar qualquer mensagem para o botpcd")
    fun `usuário mandar qualquer mensagem para o botpcd`() {
        val payload = File("src/test/resources/ufrpe/sbpc/botpcd/mocks/usuario-manda-oi.json").readText(Charsets.UTF_8)
        mockMvc.perform(
            post("/webhooks")
                .content(payload)
                .contentType("application/json")
        )
    }
    @Then("^bot envia mensagens com o tipo de deficiencia$")
    fun `bot envia qualquer mensagem`() {
        val mensagemEsperada = Disability.getOptions()
        val apiMock = whatsappBusinessCloudApiMock
        val mensagemEnviada = apiMock.capturedMessage!!.textMessage!!.body
        assertEquals(mensagemEnviada, mensagemEsperada)
    }
}
