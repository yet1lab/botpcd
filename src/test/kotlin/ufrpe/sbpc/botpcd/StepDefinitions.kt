package ufrpe.sbpc.botpcd

import com.whatsapp.api.domain.webhook.Value
import com.whatsapp.api.domain.webhook.WebHook
import io.cucumber.java.After
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
import ufrpe.sbpc.botpcd.entity.Attendance
import ufrpe.sbpc.botpcd.entity.Attendant
import ufrpe.sbpc.botpcd.entity.CommitteeMember
import ufrpe.sbpc.botpcd.entity.Disability
import ufrpe.sbpc.botpcd.entity.MessageExchange
import ufrpe.sbpc.botpcd.entity.Monitor
import ufrpe.sbpc.botpcd.entity.MonitorAssistanceType
import ufrpe.sbpc.botpcd.entity.MonitorServiceType
import ufrpe.sbpc.botpcd.entity.PWD
import ufrpe.sbpc.botpcd.entity.ServiceType
import ufrpe.sbpc.botpcd.entity.UserStatus
import ufrpe.sbpc.botpcd.entity.Provider
import ufrpe.sbpc.botpcd.repository.AttendanceRepository
import ufrpe.sbpc.botpcd.repository.AttendantRepository
import ufrpe.sbpc.botpcd.repository.CommitteeMemberRepository
import ufrpe.sbpc.botpcd.repository.MessageExchangeRepository
import ufrpe.sbpc.botpcd.repository.MonitorRepository
import ufrpe.sbpc.botpcd.repository.PWDRepository
import ufrpe.sbpc.botpcd.service.AttendanceService
import java.io.File
import java.time.LocalDateTime
// import java.math.BigInteger // Não é mais necessário para os steps de atendente
import kotlin.test.assertTrue

class StepDefinitions(
    private val mockMvc: MockMvc,
    val pwdRepository: PWDRepository,
    val monitorRepository: MonitorRepository,
    val attendanceService: AttendanceService,
    val attendantRepository: AttendantRepository,
    val attendanceRepository: AttendanceRepository,
    val messageExchangeRepository: MessageExchangeRepository,
    val committeeMemberRepository: CommitteeMemberRepository,
) {
    private var currentBotNumber: String = "15556557522"
    private val numberUserNotRegister: String = "558187654321"
    private val currentTestAttendantPhoneNumber: String = "5581999999901" // Número fixo para o atendente em teste
    private val pwdInAttendancePhoneNumberForAttendantChangeStatus = "558179654321"
    private val pwdAcessarServicesPhoneNumber = "551965741234"
    private val possibleItialsMessages = mutableListOf("Oi", "Olá", "Bom dia", "Boa noite", "Boa Tarde", "teste")

    val logger: Logger = LoggerFactory.getLogger(StepDefinitions::class.java)


		@Dado("que o atendente {string} de {string} do tipo {string} estava indisponível")
		fun atendenteIndisponivel(nome: String, numero: String, tipoDeAtendente: String) {

				val provider = when (tipoDeAtendente.lowercase()) {
						"monitor" -> Provider.MONITOR
						"membro da comissão" -> Provider.COMMITTEE_MEMBER
						else -> throw IllegalArgumentException("Tipo de atendente desconhecido: $tipoDeAtendente")
				}

				if (provider == Provider.MONITOR) {
						val monitor = monitorRepository.findByPhoneNumber(numero)
								?: monitorRepository.save(
										Monitor(
												name = nome,
												phoneNumber = numero,
												status = UserStatus.UNAVAILABLE,
												assistanceType = MonitorAssistanceType.NEURODIVERGENT_SUPPORT_MONITOR // ou ajuste se quiser
										)
								)
						monitor.status = UserStatus.UNAVAILABLE
						monitorRepository.save(monitor)
				} else if (provider == Provider.COMMITTEE_MEMBER) {
						val member = attendantRepository.findByPhoneNumber(numero)
								?: attendantRepository.save(
										CommitteeMember(
												name = nome,
												phoneNumber = numero,
												status = UserStatus.UNAVAILABLE
										)
								)
						member.status = UserStatus.UNAVAILABLE
						attendantRepository.save(member)
				}
		}

		@Quando("o atendente {string} fica disponível")
		fun atendenteFicaDisponivel(nome: String) {
				val phone = "9999-${nome.replace(" ", "_")}"

				// Tenta encontrar no repositório de monitores
				val monitor = monitorRepository.findByPhoneNumber(phone)
				if (monitor != null) {
						monitor.status = UserStatus.AVAILABLE
						monitorRepository.save(monitor)
						return
				}

				// Se não encontrar no repositório de monitores, procura no de membros da comissão
				val member = attendantRepository.findByPhoneNumber(phone)
				if (member != null) {
						member.status = UserStatus.AVAILABLE
						attendantRepository.save(member)
						return
				}

				// Se não encontrou em nenhum dos dois
				throw IllegalArgumentException("Atendente '$nome' não encontrado.")
		}

		@Dado("que {string} PCD solicitou o serviço {string} e está na fila de espera")
		fun pcdSolicitouServicoFila(adjetivoDeficiencia: String, servicoDescricao: String) {
				val service = ServiceType.getByDescription(servicoDescricao)

				// Usa o método do enum para buscar pela forma textual do adjetivo
				val disability = Disability.getByAdjective(adjetivoDeficiencia)
				val phone = "81999999999"

				// Cria ou reutiliza o PCD
				val pcd = pwdRepository.findByPhoneNumber(phone) ?: pwdRepository.save(
						PWD(
								name = "PCD $adjetivoDeficiencia",
								phoneNumber = phone,
								disabilities = setOf(disability)
						)
				)

				// Garante que existe uma solicitação em espera (não iniciada)
				if (attendanceRepository.findRequestAttendanceOfPwd(pcd) == null) {
						attendanceRepository.save(
								Attendance(
										pwd = pcd,
										serviceType = service,
										attendantType = service.attendantType,
										attendant = null,
										endDateTime = null,
										startDateTime = null,
										serviceLocation = null,
										monitorArrivalDateTime = null
								)
						)
				}
		}
	
		@Dado("PCD possuia serviço requisitado que ainda não foi iniciado")
		fun pcdPossuiServicoRequisitadoNaoIniciado() {
				val phone = "81999999999"
				val service = ServiceType.NeurodivergentSupport
				val disabilities = service.disability

				// Reutiliza ou cria o PCD com deficiências compatíveis com o serviço
				val pcd = pwdRepository.findByPhoneNumber(phone) ?: pwdRepository.save(
						PWD(
								name = "PCD Teste",
								phoneNumber = phone,
								disabilities = disabilities
						)
				)

				// Evita criar duplicata se já existir
				val existente = attendanceRepository.findRequestAttendanceOfPwd(pcd)
				if (existente == null) {
						attendanceRepository.save(
								Attendance(
										pwd = pcd,
										serviceType = service,
										attendantType = service.attendantType,
										attendant = null,
										endDateTime = null,
										startDateTime = null,
										serviceLocation = null,
										monitorArrivalDateTime = null
								)
						)
				}
		}

		@Dado("que nenhum atendente para o {string} está disponível")
		fun nenhumAtendenteDisponivel(servicoDescricao: String) {
			// repositorio comeca vazio, nao precisa fazer nada
		}

		@Dado("usuário recebeu mensagem {string}")
    fun `usuário recebeu mensagem`(message: String) {
        mockUserRecievedMessage(numberUserNotRegister, message)
    }

    @Dado("usuário recebeu mensagem") // Para DocString
    fun `usuário recebeu mensagem docs string`(message: String) {
        mockUserRecievedMessage(numberUserNotRegister, message)
    }

    @Dado("pcd está cadastrado completo")
    fun pcdEstaCadastrado() {
        // Implementar lógica para garantir que o PCD está cadastrado no sistema
        // Ex: pwdRepository.save(PWD(name = "PCD Teste Completo", phoneNumber = numberUserNotRegister, disabilities = mutableSetOf(Disability.DEAFNESS)))
    }
    @Dado("que não tem atendente disponível")
    fun `que não tem atendente disponível`() {

    }
    @Dado("que {string} PCD recebeu mensagem de opcções de serviço")
    fun `pcd recebue mensagem de opção de serviço`(adjetivoPCD: String) {
        // ver se existe ou crirar um pcd com adjetivoPCD
        val pwd = createPWDIfNotExists(phoneNumber = pwdAcessarServicesPhoneNumber,  Disability.getByAdjective(adjetivoPCD))
        val servicesText = attendanceService.createSendServicesMessage(pwd.disabilities.first(), pwd)
        messageExchangeRepository.save(
            MessageExchange(
                fromPhoneNumber = currentBotNumber,
                toPhoneNumber = pwdAcessarServicesPhoneNumber,
                message = servicesText
            )
        )
        // pegar no proprio código qual seria a mensagem de serviço
    }
    @After
    fun cleanup() {
        // Esta linha deleta os dados em uma ordem que evita erros de chave estrangeira
        listOf(attendanceRepository, messageExchangeRepository, monitorRepository, committeeMemberRepository, pwdRepository, attendantRepository).forEach { it.deleteAllInBatch() }
    }
    @Dado("atendente que se chama {string} enviou uma mensagem nas ultimas 24 horas para o bot")
    fun `atendente enviou uma mensagem nas ultimas 24 horas`(nomeAtendente: String) {
        messageExchangeRepository.save(
            MessageExchange(
                fromPhoneNumber = currentTestAttendantPhoneNumber,
                toPhoneNumber = currentBotNumber,
                message = "teste "
            )
        )
    }

    /**
     * Cria um atendente (Monitor ou Membro da Comissão) com o nome especificado,
     * define seu status como DISPONÍVEL e garante que ele seja elegível para
     * o serviço desejado. Também simula uma mensagem recente do atendente para
     * que o sistema o considere ativo.
     */
    @Dado("atendente que se chama {string} está disponível para o {string}")
    fun `atendente está disponível`(nomeAtendente: String, servicoDesejado: String) {
        // Gera um número de telefone único para o atendente para evitar conflitos
        attendantRepository.findByPhoneNumber(currentTestAttendantPhoneNumber)?.let { attendantRepository.delete(it) }
        val serviceType = ServiceType.getByDescription(servicoDesejado)
        val tipoAtendente = if (nomeAtendente.contains("Monitor", ignoreCase = true)) "monitor" else "membro da comissão"
        val attendant: Attendant = when (tipoAtendente) {
            "monitor" -> {
                val monitorService = serviceType as? MonitorServiceType
                    ?: throw IllegalArgumentException("Serviço '$servicoDesejado' não é compatível com Monitor")
                monitorRepository.save(
                    Monitor(
                        name = nomeAtendente,
                        phoneNumber = currentTestAttendantPhoneNumber,
                        status = UserStatus.AVAILABLE,
                        assistanceType = monitorService.monitorAssistanceType
                    )
                )
            }
            "membro da comissão" -> {
                committeeMemberRepository.save(
                    CommitteeMember(
                        name = nomeAtendente,
                        phoneNumber = currentTestAttendantPhoneNumber,
                        status = UserStatus.AVAILABLE
                    )
                )
            }
            else -> throw IllegalArgumentException("Tipo de atendente desconhecido: $tipoAtendente")
        }
        // Simula que o atendente enviou uma mensagem recentemente para ser considerado ativo
        mockUserRecievedMessage(attendant.phoneNumber, "Estou online")
    }

    /**
     * Simula o envio de uma mensagem pelo PCD para selecionar um serviço.
     * O número do serviço corresponde à opção na lista de serviços recebida.
     */
    @Quando("{string} PCD envia a mensagem {string}")
    fun `pcd envia mensagem de serviço`(adjetivoPCD: String, numeroServico: String) {
        // O adjetivoPCD é usado para contexto, o número de telefone identifica o usuário
        val pwd = createPWDIfNotExists(phoneNumber = pwdAcessarServicesPhoneNumber,  Disability.getByAdjective(adjetivoPCD))
        userSendMessage(numeroServico, pwd.phoneNumber)
    }

    /**
     * Verifica se o PCD recebeu a mensagem correta informando qual atendente
     * irá realizar o seu atendimento.
     */
    @Entao("{string} PCD receberá mensagem {string}")
    fun `pcd recebe mensagem de direcionamento`(adjetivoPCD: String, mensagemEsperada: String) {
        // O adjetivoPCD é usado para contexto, a verificação é feita pelo número de telefone
        testarUltimaMensagemRecebidaDoUsuario(mensagemEsperada, pwdAcessarServicesPhoneNumber)

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
                disabilities = mutableSetOf(Disability.getByAdjective(tipoDeDeficiencia))
            )
        )
    }

    @Quando("usuário envia mensagem {string}")
    fun `usuario envia mensagem`(mensagemEnviada: String) {
        userSendMessage(mensagemEnviada, numberUserNotRegister)
    }

    @Quando("PCD {string} mandar qualquer mensagem")
    fun `PCD mandar qualquer mensagem`(adjetivoPCD: String) {
        val message = possibleItialsMessages.random()
        val pwd = createIfNotExistsPWDWithDisability(adjetivoPCD, pwdAcessarServicesPhoneNumber)
        userSendMessage(message, pwd.phoneNumber)
    }

    fun createIfNotExistsPWDWithDisability(adjetivoPCD: String, pwdPhoneNumber: String): PWD {
        var pwd = pwdRepository.findByPhoneNumber(pwdPhoneNumber)
        val disability = Disability.getByAdjective(adjetivoPCD)
        if(pwd == null) {
            return pwdRepository.save(PWD(name=adjetivoPCD, phoneNumber = pwdPhoneNumber, disabilities = mutableSetOf(disability)))
        }
        if(pwd.disabilities.first() != disability) {
            pwd.disabilities = mutableSetOf(disability)
            pwdRepository.save(pwd)
        }
        return pwd
    }
    @Entao("bot registrará que usuário tem ou possui {string}")
    fun `bot registra deficiencia do usuário`(deficienciaAdjtivo: String) {
        val pwd = pwdRepository.findByPhoneNumberWithDisabilities(numberUserNotRegister)!!
        assertEquals(pwd.disabilities.first().adjective, deficienciaAdjtivo)
        pwdRepository.delete(pwd)
    }

    @Entao("bot salvará o nome do usuário {string}")
    fun `bot salva o nome do usuário`(nome: String) {
        val pwd = pwdRepository.findByPhoneNumber(numberUserNotRegister)
        assertEquals(pwd!!.name, nome)
        pwdRepository.delete(pwd)
    }

    @Entao("usuário receberá mensagem {string}")
    fun `usuario receberá mensagem`(message: String) {
        testarUltimaMensagemRecebidaDoUsuario(message, numberUserNotRegister)
    }

    @Entao("usuário receberá mensagem") // Para DocString
    fun `usuario receberá mensagem docs string`(message: String) {
        testarUltimaMensagemRecebidaDoUsuario(message, numberUserNotRegister)
    }

    @Entao("A penúltima mensagem recebida pelo usuário será {string}")
    fun `a penultima mensagem recebida pelo usuário será`(penultimaMensagem: String) {
        testarPenultimaMensagemRecebidaDoUsuario(
            mensagemEsperada = penultimaMensagem,
            userPhoneNumber = numberUserNotRegister
        )
    }

    @Entao("{string} PCD receberá mensagem de opcções de serviço {string}")
    fun `bot enviará opcçoes de seviço de acordo com a deficiência do pcd`(
        tipoDeDeficiencia: String,
        opcoesDeServico: String
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
    @Dado("que sou um {string} com status inicial {string}")
    fun `que sou um tipo_de_atendente com status inicial`(
        tipoAtendente: String,
        statusInicialStr: String
    ) {
        val status = UserStatus.valueOf(statusInicialStr.uppercase())
        val attendant = createAttendant(currentTestAttendantPhoneNumber, tipoAtendente, status)
        if (status == UserStatus.BUSY) {
            // Limpar PWD existente com este número para evitar conflitos
            val pwd = pwdRepository.findByPhoneNumber(pwdInAttendancePhoneNumberForAttendantChangeStatus) ?: pwdRepository.save(PWD(
                name = "PCD em Atendimento Teste",
                phoneNumber = pwdInAttendancePhoneNumberForAttendantChangeStatus,
                disabilities = mutableSetOf(Disability.MOBILITY_IMPAIRED) // Deficiência padrão para o teste
            ))

            // Determinar o tipo de serviço e provedor com base no tipo de atendente
            val serviceType: ServiceType
            val provider: Provider
            if (attendant is Monitor) {
                serviceType = ServiceType.Mobility // Exemplo, pode precisar ser mais dinâmico
                provider = Provider.MONITOR
            } else if (attendant is CommitteeMember) {
                serviceType = ServiceType.Car // Exemplo, pode precisar ser mais dinâmico
                provider = Provider.COMMITTEE_MEMBER
            } else {
                throw IllegalStateException("Tipo de atendente desconhecido durante a criação do atendimento de teste.")
            }

            val attendance = Attendance(
                pwd = pwd,
                attendant = attendant,
                serviceType = serviceType,
                attendantType = provider,
                startDateTime = LocalDateTime.now()
            )
            attendanceRepository.save(attendance)
        }
    }
    @Quando("o {string} envia a mensagem {string}")
    fun `o tipo_de_atendente envia a mensagem`(
        tipoAtendente: String,
        mensagemEnviada: String
    ) {
        checkIfHasCorrectAttendantType(currentTestAttendantPhoneNumber, tipoAtendente)
        userSendMessage(mensagemEnviada, currentTestAttendantPhoneNumber)
    }

    @Entao("o {string} receberá a mensagem") // Para DocString
    fun `o tipo_de_atendente receberá a mensagem`(
        tipoAtendente: String,
        mensagemEsperadaDocString: String
    ) {
        checkIfHasCorrectAttendantType(currentTestAttendantPhoneNumber, tipoAtendente)
        testarUltimaMensagemRecebidaDoUsuario(mensagemEsperadaDocString, currentTestAttendantPhoneNumber)
    }

    private fun checkIfHasCorrectAttendantType(phoneNumber: String, tipoAtendente: String) {
        val attendant = attendantRepository.findByPhoneNumber(phoneNumber)
        assertTrue(attendant != null, "Atendente com número $phoneNumber não encontrado para o tipo $tipoAtendente.")
        when (tipoAtendente) {
            "Monitor" -> {
                assertTrue(attendant is Monitor, "Atendente $phoneNumber não é um Monitor.")
            }
            "Membro da Comissão" -> {
                assertTrue(attendant is CommitteeMember, "Atendente $phoneNumber não é um Membro da Comissão.")
            }
            else -> {
                throw Exception("Foi passado um tipo inválido nos testes: $tipoAtendente")
            }
        }
    }

    @Dado("o {string} recebeu a mensagem de opções para status {string}")
    fun `o tipo_de_atendente recebeu a mensagem de opções para status`(
        @Suppress("UNUSED_PARAMETER") tipoAtendente: String,
        statusAntigoStr: String
    ) {
        val statusAntigo = UserStatus.valueOf(statusAntigoStr.uppercase())
        val expectedMessageContent = attendanceService.changeStatusTextOptionsFor[statusAntigo.name]
            ?: throw IllegalArgumentException("Mensagem de opção não encontrada para status $statusAntigoStr no attendanceService.changeStatusTextOptionsFor. Chaves disponíveis: ${attendanceService.changeStatusTextOptionsFor.keys}")

        mockUserRecievedMessage(currentTestAttendantPhoneNumber, expectedMessageContent)
    }

    @Entao("o status do {string} deve ser {string}")
    fun `o status do tipo_de_atendente deve ser`(
        tipoAtendente: String,
        statusNovoEsperadoStr: String
    ) {
        val statusNovoEsperado = UserStatus.valueOf(statusNovoEsperadoStr.uppercase())
        checkIfHasCorrectAttendantType(currentTestAttendantPhoneNumber, tipoAtendente)
        val attendant = attendantRepository.findByPhoneNumber(currentTestAttendantPhoneNumber)
        assertTrue(attendant != null, "Atendente com número $currentTestAttendantPhoneNumber não encontrado no banco de dados.")
        assertEquals(statusNovoEsperado, attendant!!.status, "Status do atendente não foi atualizado corretamente para $statusNovoEsperado. Estava ${attendant.status}.")
    }
    fun createPWDIfNotExists(phoneNumber: String, disability: Disability): PWD {
        var pwd = pwdRepository.findByPhoneNumber(phoneNumber)
        if(pwd == null) {
            return pwdRepository.save(PWD(phoneNumber = phoneNumber, disabilities = mutableSetOf(disability)))
        } else {
            return pwd
        }
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
            .changeUserMessage(mensagemEnviada)
            .changeBotNumber(currentBotNumber)
        mockMvc.perform(
            post("/webhooks")
                .content(payload)
                .contentType("application/json")
        ).andExpect(status().isOk)
    }

    @Quando("O PCD com a deficiência de {string} envia mensagem {string}")
    fun pcdComDeficienciaMandaMensagem(deficiencia: String, mensagem: String) {
        logger.warn("Step 'O PCD com a deficiência de \"$deficiencia\" envia mensagem \"$mensagem\"' não implementado completamente.")
    }

    @Entao("O bot vai enviar uma lista de opções de acordo com a {string}")
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
