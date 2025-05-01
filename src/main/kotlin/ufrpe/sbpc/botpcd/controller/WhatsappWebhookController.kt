package ufrpe.sbpc.botpcd.controller

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class WhatsappWebhookController {
	@PostMapping("/whatsapp")
	fun paginaExemplo(@RequestBody request: String): String {
		return "pagina";
	}
}

