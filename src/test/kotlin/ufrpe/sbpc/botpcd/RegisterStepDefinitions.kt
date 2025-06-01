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
        val mensagemEsperada = """Olá, qual sua deficiência?
Digite 1 para Deficiência visual 
Digite 2 para Deficiência auditiva/surdez 
Digite 3 para Surdocegueira 
Digite 4 para Transtorno do Espectro Autista/Neurodivergente 
Digite 5 para Deficiência física 
Digite 6 para Não tenho deficiência, mas tenho mobilidade reduzida 
Digite 7 para Não preciso de suporte.
        """.trimIndent()
        val apiMock = whatsappBusinessCloudApiMock
        val mensagemEnviada = apiMock.capturedMessage!!.textMessage!!.body
        assertEquals(mensagemEnviada, mensagemEsperada)
    }
}
