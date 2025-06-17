package ufrpe.sbpc.botpcd

import io.cucumber.java.pt.Dado
import io.cucumber.java.pt.Entao
import io.cucumber.java.pt.Quando
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ufrpe.sbpc.botpcd.entity.*
import ufrpe.sbpc.botpcd.repository.*
import ufrpe.sbpc.botpcd.service.AttendanceService
import ufrpe.sbpc.botpcd.service.QueueService
import ufrpe.sbpc.botpcd.util.createOptions 
import java.io.File
import java.time.LocalDateTime
import io.cucumber.java.Before

class AcessAssistanceServices(
    private val mockMvc: MockMvc,
    private val pwdRepository: PWDRepository,
    private val messageExchangeRepository: MessageExchangeRepository,
    private val monitorRepository: MonitorRepository,
    private val committeeMemberRepository: CommitteeMemberRepository,
    private val attendantRepository: AttendantRepository,
    private val attendanceRepository: AttendanceRepository,
    private val attendanceService: AttendanceService,
    @Suppress("unused") private val queueService: QueueService
) {
    @Before
    fun limparBanco() {
        attendanceRepository.deleteAll()
        messageExchangeRepository.deleteAll()
        monitorRepository.deleteAll()
        committeeMemberRepository.deleteAll()
        attendantRepository.deleteAll()
        pwdRepository.deleteAll()
    }

    private var currentBotNumber: String = "15556557522"
    private val logger: Logger = LoggerFactory.getLogger(AcessAssistanceServices::class.java)

    private fun loadPayload(filePath: String): String {
        return File(filePath).readText(Charsets.UTF_8)
    }

    private fun String.changeUserNumber(newNumber: String): String {
        return this.replace(Regex("(?<=\"wa_id\": \")\\d+(?=\")"), newNumber)
    }

    private fun String.changeUserMessage(newMessage: String): String {
        val escapedNewMessage = newMessage
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
        return this.replace(Regex("(?<=\\\"body\\\": \\\").*?(?=\\\")"), escapedNewMessage)
    }

    private fun String.changeBotNumber(newNumber: String): String {
        return this.replace(Regex("(?<=\\\"phone_number_id\\\": \\\").*?(?=\\\")"), newNumber)
    }

    private fun userSendMessage(mensagemEnviada: String, userPhoneNumber: String) {
        val payload = loadPayload("src/test/resources/ufrpe/sbpc/botpcd/mocks/usuario-manda-oi.json")
            .changeUserNumber(userPhoneNumber)
            .changeUserMessage(mensagemEnviada)
            .changeBotNumber(currentBotNumber)
        mockMvc.perform(
            post("/webhooks")
                .content(payload)
                .contentType("application/json")
        ).andExpect(status().isOk)
    }

    private fun mockUserRecievedMessage(userNumber: String, message: String) {
        messageExchangeRepository.save(
            MessageExchange(
                fromPhoneNumber = currentBotNumber,
                toPhoneNumber = userNumber,
                message = message.trimIndent(),       ))
    }

    private fun testarUltimaMensagemRecebidaDoUsuario(mensagemEsperada: String, userPhoneNumber: String) {
        val actualMessage = messageExchangeRepository.lastExchangeMessage(
            toPhoneNumber = userPhoneNumber,
            fromPhoneNumber = currentBotNumber
        )?.message

        System.err.println("=== DEBUG MENSAGEM ===")
        System.err.println("Esperado:\n${mensagemEsperada.trimIndent()}")
        System.err.println("Recebido:\n${actualMessage?.trimIndent()}")
        System.err.println("======================")

        assertEquals(mensagemEsperada.trimIndent(), actualMessage?.trimIndent(), "A última mensagem recebida pelo usuário $userPhoneNumber não foi a esperada.")
    }

    // Steps
    @Dado("um bot configurado com o número {string}")
    fun `um bot configurado com o número`(botNumber: String) {
        currentBotNumber = botNumber
        // Limpar mensagens antigas do bot para evitar interferência entre cenários
        val messagesFromBot = messageExchangeRepository.findAll().filter { it.fromPhoneNumber == currentBotNumber }
        messageExchangeRepository.deleteAll(messagesFromBot)
        logger.info("Bot configurado com o número: $currentBotNumber. Mensagens anteriores do bot limpas.")
    }

    @Dado("um PCD cadastrado com o número {string}, nome {string} e deficiência {string}")
    fun `um PCD cadastrado com o número nome e deficiência`(
        numeroPcd: String,
        nomePcd: String,
        tipoDeficiencia: String
    ) {
        pwdRepository.findByPhoneNumber(numeroPcd)?.let {
            // Limpar atendimentos associados antes de deletar o PCD para evitar constraint violations
            val attendances = attendanceRepository.findAll().filter { att -> att.pwd == it }
            attendanceRepository.deleteAll(attendances)
            pwdRepository.delete(it)
        }
        val disability = Disability.entries.find { 
            it.adjective.equals(tipoDeficiencia, ignoreCase = true) || 
            it.textOption.equals(tipoDeficiencia, ignoreCase = true)
        }
        assertNotNull(disability, "Deficiência '$tipoDeficiencia' não encontrada.")
        pwdRepository.save(
            PWD(
                phoneNumber = numeroPcd,
                name = nomePcd,
                disabilities = mutableSetOf(disability!!)
            )
        )
        logger.info("PCD cadastrado: $nomePcd, $numeroPcd, $tipoDeficiencia")
    }

    @Dado("um {string} disponível com nome {string}, número {string} e tipo de assistência {string} se aplicável")
    fun `um atendente disponível`(
        tipoAtendente: String,
        nomeAtendente: String,
        numeroAtendente: String,
        tipoAssistenciaMonitorStr: String?
    ) {
        // Limpa todos os atendentes do tipo antes de cadastrar o novo
        when (tipoAtendente.lowercase()) {
            "monitor" -> {
                monitorRepository.findAll().forEach { monitorRepository.delete(it) }
            }
            "membro da comissão" -> {
                committeeMemberRepository.findAll().forEach { committeeMemberRepository.delete(it) }
            }
        }

        when (tipoAtendente.lowercase()) {
            "monitor" -> {
                val assistanceType = tipoAssistenciaMonitorStr?.takeIf { it.isNotBlank() }?.let { 
                    MonitorAssistanceType.entries.find { enum -> enum.name.equals(it, ignoreCase = true) }
                }
                assertNotNull(assistanceType, "Tipo de assistência do monitor não pode ser nulo ou vazio para Monitor.")
                monitorRepository.save(
                    Monitor(
                        name = nomeAtendente,
                        phoneNumber = numeroAtendente,
                        status = UserStatus.AVAILABLE,
                        assistanceType = assistanceType!!
                    )
                )
            }
            "membro da comissão" -> {
                committeeMemberRepository.save(
                    CommitteeMember(
                        name = nomeAtendente,
                        phoneNumber = numeroAtendente,
                        status = UserStatus.AVAILABLE
                    )
                )
            }
            else -> throw IllegalArgumentException("Tipo de atendente desconhecido: $tipoAtendente")
        }
        // Simula que o atendente enviou uma mensagem recentemente para ser considerado ativo
        messageExchangeRepository.save(
            MessageExchange(
                fromPhoneNumber = numeroAtendente,
                toPhoneNumber = currentBotNumber,
                message = "Olá BotPCD, estou disponível.",
            )
        )
        logger.info("Atendente disponível: $nomeAtendente, $numeroAtendente, Tipo: $tipoAtendente")

        logger.info("Monitores cadastrados:")
        monitorRepository.findAll().forEach {
            logger.info("Monitor: ${it.name}, status: ${it.status}, tipo: ${it.assistanceType}, número: ${it.phoneNumber}")
        }
        logger.info("Membros da comissão cadastrados:")
        committeeMemberRepository.findAll().forEach {
            logger.info("Comissão: ${it.name}, status: ${it.status}, número: ${it.phoneNumber}")
        }
    }

    @Dado("o PCD com número {string} recebeu a mensagem com as opções de serviço para sua deficiência")
    fun `o PCD recebeu a mensagem com as opções de serviço`(numeroPcd: String) {
        userSendMessage("oi", numeroPcd)
        logger.info("PCD $numeroPcd enviou 'oi' para iniciar o fluxo e receber as opções de serviço.")

        // Exemplo de mensagem esperada (ajuste conforme o texto real do bot)
        val pwd = pwdRepository.findByPhoneNumber(numeroPcd)
        assertNotNull(pwd)
        val disability = pwd!!.disabilities.first()
        val opcoes = createOptions(listOf(disability.name)) // Corrigido: agora passa uma lista de String
        testarUltimaMensagemRecebidaDoUsuario(opcoes, numeroPcd)
    }

    @Quando("o PCD com número {string} envia a mensagem escolhendo a opção de serviço {string} que corresponde ao {string}")
    fun `o PCD envia a mensagem escolhendo a opção de serviço`(
        numeroPcd: String,
        opcaoServico: String,
        @Suppress("UNUSED_PARAMETER") servicoEscolhido: String 
    ) {
        userSendMessage(opcaoServico, numeroPcd)
        logger.info("PCD $numeroPcd enviou opção de serviço: $opcaoServico (Serviço: $servicoEscolhido)")
    }

    @Entao("o PCD com número {string} recebe uma mensagem informando que o atendente {string} irá realizar o atendimento")
    fun `o PCD recebe uma mensagem informando o atendente`(numeroPcd: String, nomeAtendente: String) {
        val attendant = attendantRepository.findAll().find { att -> att.name.equals(nomeAtendente, ignoreCase = true) }
        assertNotNull(attendant, "Atendente $nomeAtendente não encontrado para construir mensagem.")
        val tipoAtendenteStr = if (attendant is Monitor) "monitor" else "membro da comissão"
        val expectedMessage = "O $tipoAtendenteStr $nomeAtendente irá realizar seu atendimento."
        testarUltimaMensagemRecebidaDoUsuario(expectedMessage, numeroPcd)
        logger.info("Verificado: PCD $numeroPcd recebeu mensagem de direcionamento para $nomeAtendente.")
    }

    @Entao("o Atendente com número {string} recebe uma mensagem informando que irá atender o PCD {string}")
    fun `o Atendente recebe uma mensagem informando o PCD`(numeroAtendente: String, nomePcd: String) {
        val expectedMessage = "Você irá começar o atendimento de $nomePcd."
        testarUltimaMensagemRecebidaDoUsuario(expectedMessage, numeroAtendente)
        logger.info("Verificado: Atendente $numeroAtendente recebeu mensagem para atender $nomePcd.")
    }

    @Entao("um registro de atendimento é criado para o PCD com número {string} e o Atendente com número {string} com o serviço {string} e status iniciado")
    fun `um registro de atendimento é criado com status iniciado`(
        numeroPcd: String,
        numeroAtendente: String,
        servicoEscolhidoStr: String
    ) {
        val pwd = pwdRepository.findByPhoneNumber(numeroPcd)
        assertNotNull(pwd, "PCD com número $numeroPcd não encontrado.")
        val attendant = attendantRepository.findByPhoneNumber(numeroAtendente)
        assertNotNull(attendant, "Atendente com número $numeroAtendente não encontrado.")

        val serviceType = ServiceType::class.sealedSubclasses
            .mapNotNull { it.objectInstance }
            .find { it::class.simpleName == servicoEscolhidoStr }
            ?: error("ServiceType '$servicoEscolhidoStr' não encontrado")

        val attendance = attendanceRepository.findStartedAttendanceOfPwd(pwd!!)
        assertNotNull(attendance, "Nenhum atendimento iniciado encontrado para o PCD $numeroPcd.")
        assertEquals(pwd.id, attendance!!.pwd.id)
        assertEquals(attendant!!.id, attendance.attendant?.id)
        assertEquals(serviceType, attendance.serviceType)
        assertNotNull(attendance.startDateTime, "A data de início do atendimento não deveria ser nula.")
        assertNull(attendance.endDateTime, "A data de fim do atendimento deveria ser nula.")
        logger.info("Verificado: Registro de atendimento criado e iniciado para PCD $numeroPcd e Atendente $numeroAtendente, serviço $servicoEscolhidoStr.")
    }

    @Entao("o status do Atendente com número {string} muda para OCUPADO")
    fun `o status do Atendente muda para OCUPADO`(numeroAtendente: String) {
        val attendant = attendantRepository.findByPhoneNumber(numeroAtendente)
        assertNotNull(attendant, "Atendente com número $numeroAtendente não encontrado.")
        assertEquals(UserStatus.BUSY, attendant!!.status)
        logger.info("Verificado: Status do Atendente $numeroAtendente mudou para OCUPADO.")
    }

    @Dado("não há {string} disponível para o serviço {string}")
    fun `não há atendente disponível para o serviço`(
        tipoAtendente: String,
        @Suppress("UNUSED_PARAMETER") servicoEscolhidoStr: String
    ) {
        when (tipoAtendente.lowercase()) {
            "monitor" -> {
                // Torna todos os monitores indisponíveis
                monitorRepository.findAll().forEach {
                    it.status = UserStatus.UNAVAILABLE
                    monitorRepository.save(it)
                }
                // Remove todos os membros da comissão
                committeeMemberRepository.findAll().forEach { committeeMemberRepository.delete(it) }
            }
            "membro da comissão" -> {
                // Torna todos os membros da comissão indisponíveis
                committeeMemberRepository.findAll().forEach {
                    it.status = UserStatus.UNAVAILABLE
                    committeeMemberRepository.save(it)
                }
                // Remove todos os monitores
                monitorRepository.findAll().forEach { monitorRepository.delete(it) }
            }
            else -> throw IllegalArgumentException("Tipo de atendente desconhecido: $tipoAtendente")
        }
    }
    @Entao("o PCD com número {string} recebe uma mensagem informando que foi colocado na fila de espera")
    fun `o PCD recebe mensagem de fila de espera`(numeroPcd: String) {
        val expectedMessage = "No momento não há atendentes disponíveis. Por favor, aguarde na fila de espera e retornaremos assim que possível."
        testarUltimaMensagemRecebidaDoUsuario(expectedMessage, numeroPcd)
        logger.info("Verificado: PCD $numeroPcd recebeu mensagem de fila de espera.")
    }

    @Entao("um registro de atendimento é criado para o PCD com número {string} com o serviço {string} e status solicitado aguardando atendente")
    fun `um registro de atendimento é criado com status solicitado`(
        numeroPcd: String,
        servicoEscolhidoStr: String
    ) {
        val pwd = pwdRepository.findByPhoneNumber(numeroPcd)
        assertNotNull(pwd, "PCD com número $numeroPcd não encontrado.")
        val serviceType = ServiceType::class.sealedSubclasses
    .mapNotNull { it.objectInstance }
    .find { it::class.simpleName == servicoEscolhidoStr }
    ?: error("ServiceType '$servicoEscolhidoStr' não encontrado")
        val attendance = attendanceRepository.findAll().find { 
            it.pwd?.id == pwd?.id && it.serviceType == serviceType && it.attendant == null
        }
        assertNotNull(attendance, "Nenhum atendimento solicitado (na fila) encontrado para o PCD $numeroPcd.")
    }
}