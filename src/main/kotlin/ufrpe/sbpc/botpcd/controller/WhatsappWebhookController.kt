package ufrpe.sbpc.botpcd.controller

import com.google.common.hash.Hashing
import com.whatsapp.api.domain.webhook.WebHook
import com.whatsapp.api.domain.webhook.WebHookEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ufrpe.sbpc.botpcd.repository.MessageExchangeRepository
import ufrpe.sbpc.botpcd.service.FirstContactService
import ufrpe.sbpc.botpcd.util.hmacSha256Hex
import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


@RestController
class WhatsappWebhookController(private val firstContactService: FirstContactService, private val messageExchangeRepository: MessageExchangeRepository) {
	@Value("\${whatsapp.verify.token}")
	lateinit var VERIFY_TOKEN: String
	@Value("\${whatsapp.app.secret}")
	lateinit var APP_SECRET: String
	val logger: Logger = LoggerFactory.getLogger(WhatsappWebhookController::class.java)

	@PostMapping("/webhooks")
	fun eventNotification(
		@RequestHeader("X-Hub-Signature-256", required = false) signature: String?,
		@RequestBody body: String
	): ResponseEntity<String> {
		val sha256hex = hmacSha256Hex(secret=APP_SECRET, body)
		if(sha256hex == signature) {
			logger.warn(
				body
			)
			val event: WebHookEvent = WebHook.constructEvent(body)
			for(entry in event.entry) {
				for(change in entry.changes) {
					if(change.value?.messages == null) {
						return ResponseEntity.ok("We don't handle this type of message")
					}
					val userPhoneNumber = change.value.contacts[0].waId
					firstContactService.redirectFluxByUserType(userPhoneNumber, change)
				}
			}
			// Opcional: validar assinatura com 'signature'
			return ResponseEntity.ok("Event process")
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid signature")
		}
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

