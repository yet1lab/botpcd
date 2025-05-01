package ufrpe.sbpc.botpcd.controller

import lombok.extern.java.Log
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.logging.Logger

@RestController
@Log
class WhatsappWebhookController {
	@PostMapping("/whatsapp")
	fun whatsappCall(@RequestBody request: String): ResponseEntity<String> {
		Logger.getLogger(WhatsappWebhookController::class.java.name).info("Webhook recebido: $request")
		return ResponseEntity.ok("Webhook recebido com sucesso: $request")
	}
}

