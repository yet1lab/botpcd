package ufrpe.sbpc.botpcd

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import ufrpe.sbpc.botpcd.entity.Monitor
import ufrpe.sbpc.botpcd.entity.MonitorAssistanceType
import ufrpe.sbpc.botpcd.entity.UserStatus
import ufrpe.sbpc.botpcd.repository.MonitorRepository
import java.time.Duration
import java.time.Instant
import java.time.temporal.TemporalAmount

/**
 * Teste para lidar com o caso do servidor do whatszapp enviar mensagens
 * que já foram enviadas antes, e que estão atrasadas.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test", "default")
class WhatszappServerSendPreviuosMessageTest {
    @Autowired
 private lateinit var  mockMvc: MockMvc
    @Autowired
    private lateinit var monitorRepository: MonitorRepository

    private val currentBotNumber: String = "15556557522"
    private val monitorPhoneNumber: String = "5581999999901"

    @BeforeEach
    fun setup() {
        // Limpa o repositório antes de cada teste
        monitorRepository.deleteAll()
    }

    @Test
    @Profile("test")
    fun `bot lida com mensagens enviada de forma atrasada`() {
        // 1. Criar um monitor disponível
        val initialMonitorStatus = UserStatus.AVAILABLE
        val monitor = Monitor(
            name = "Monitor Teste",
            phoneNumber = monitorPhoneNumber,
            status = initialMonitorStatus,
            assistanceType = MonitorAssistanceType.NEURODIVERGENT_SUPPORT_MONITOR
        )
        monitorRepository.save(monitor)

        // 2. Simular o envio de "Bot PCD"
        userSendMessage(
            mensagemEnviada = "Bot PCD",
            userPhoneNumber = monitorPhoneNumber,
            currentBotNumber = currentBotNumber,
            mockMvc = mockMvc
        )

        // 3. Simular o envio de "1" que foi enviado antes do bot pcd
        userSendMessage(
            mensagemEnviada = "1",
            userPhoneNumber = monitorPhoneNumber,
            currentBotNumber = currentBotNumber,
            mockMvc = mockMvc,
            sentAt = (Instant.now() - Duration.ofHours(5))
        )

        // 4. Verificar se o status do monitor foi atualizado para INDISPONÍVEL
        val monitorAtualizado = monitorRepository.findByPhoneNumber(monitorPhoneNumber)
        assertEquals(initialMonitorStatus, monitorAtualizado?.status)
    }
}
