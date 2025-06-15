package ufrpe.sbpc.botpcd.service

import com.whatsapp.api.impl.WhatsappBusinessCloudApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import ufrpe.sbpc.botpcd.entity.MessageExchange
import ufrpe.sbpc.botpcd.repository.MessageExchangeRepository
import java.time.LocalDateTime


@ExtendWith(MockitoExtension::class)
class WhatsappServiceTest {

    private val cloudApi = mock(WhatsappBusinessCloudApi::class.java)
    private val messageRepo = mock(MessageExchangeRepository::class.java)
    private val service = WhatsappService(cloudApi, messageRepo)
    private val userNumber = "551297652348"
    private val botNumber = "558812389418"
    private val sampleMessage = "Olá"

    @Test
    fun `envia mensagem quando a ultima foi ha menos de 24h`() {
        `when`(
            messageRepo.lastExchangeMessage(
                botNumber,
                userNumber
            )
        ).thenReturn(
            MessageExchange(
                fromPhoneNumber = userNumber,
                toPhoneNumber = botNumber,
                message = sampleMessage
            )
        )
        service.sendMessage(botNumber, userNumber, sampleMessage)
        verify(cloudApi).sendMessage(eq(botNumber), any())
        verify(messageRepo).save(any(MessageExchange::class.java))
    }

    @Test
    fun `nao envia mensagem quando a ultima foi ha mais de 24h`() {
        `when`(
            messageRepo.lastExchangeMessage(
                botNumber,
                userNumber
            )
        ).thenReturn(
            MessageExchange(
                fromPhoneNumber = userNumber,
                toPhoneNumber = botNumber,
                message = sampleMessage
            ).apply {
                createAt = LocalDateTime.now().minusHours(25)
            }
        )
        service.sendMessage(botNumber, userNumber, "Test message")
        verify(cloudApi, never()).sendMessage(eq(userNumber), any())
        verify(messageRepo, never()).save(any(MessageExchange::class.java))
    }

    @Test
    fun `envia mensagem com autor definido`() {
        `when`(
            messageRepo.lastExchangeMessage(
                botNumber,
                userNumber
            )
        ).thenReturn(
            MessageExchange(
                fromPhoneNumber = userNumber,
                toPhoneNumber = botNumber,
                message = sampleMessage
            ).apply {
                createAt = LocalDateTime.now()
            }
        )
        service.sendMessage(botNumber, userNumber, "Test message", "AutorX")
        val captor = ArgumentCaptor.forClass(MessageExchange::class.java)
        verify(messageRepo).save(captor.capture())
        assertEquals("*AutorX:*\n Test message", captor.value.message)
    }
}