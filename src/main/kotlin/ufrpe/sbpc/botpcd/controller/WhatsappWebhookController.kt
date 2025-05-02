package ufrpe.sbpc.botpcd.controller

import com.whatsapp.api.domain.webhook.WebHook
import com.whatsapp.api.domain.webhook.WebHookEvent
import lombok.extern.java.Log
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ufrpe.sbpc.botpcd.service.FirstContactService
import java.util.logging.Logger


@RestController
@Log
class WhatsappWebhookController(private val firstContactService: FirstContactService) {
	@Value("\${whatsapp.token}")
	lateinit var VERIFY_TOKEN: String

	@PostMapping("/webhooks")
	fun eventNotification(
		@RequestHeader("X-Hub-Signature-256", required = false) signature: String?,
		@RequestBody body: String
	): ResponseEntity<String> {
		val mapper = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper()
		Logger.getLogger(WhatsappWebhookController::class.java.name).info(
			"Evento recebido, $body"
		)
		val event: WebHookEvent = WebHook.constructEvent(body)
		for(entry in event.entry) {
			for(change in entry.changes) {
				firstContactService.redirectFluxByUserType(change.value.phoneNumber, change.value)
			}
		}
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

