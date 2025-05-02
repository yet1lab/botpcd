package ufrpe.sbpc.botpcd.controller

import lombok.extern.java.Log
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.Mapping
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController;
import java.util.logging.Logger

@RestController
@Log
class WhatsappWebhookController {
	@Value("\${whatsapp.token}")
	lateinit var VERIFY_TOKEN: String

	@PostMapping("/webhooks")
	fun eventNotification(
		@RequestHeader("X-Hub-Signature-256", required = false) signature: String?,
		@RequestBody body: String
	): ResponseEntity<String> {
		val mapper = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper()
		val payload = mapper.readTree(body)

		Logger.getLogger(WhatsappWebhookController::class.java.name).info(
			"Evento recebido, objeto: \${payload.get(\"object\")?.asText()}"
		)

		// Opcional: validar assinatura com 'signature'
		return ResponseEntity.ok("Evento processado")
	}

	@GetMapping("/webhooks")
	fun verifyToken(
		@RequestParam("hub.mode") mode: String?,
		@RequestParam("hub.challenge") challenge: String?,
		@RequestParam("hub.verify_token") token: String?
	): ResponseEntity<String> {
		return if (mode == "subscribe" && token == this.VERIFY_TOKEN && challenge != null) {
			ResponseEntity.ok(challenge)
		} else {
			ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token inválido")
		}
	}
}

