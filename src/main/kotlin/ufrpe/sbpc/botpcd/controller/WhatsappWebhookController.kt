/*
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResquestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class WhatsappWebhookController {
	@PostMapping("/whatsapp")
	fun paginaExemplo(@ResquestBody request: String): String {
		return "pagina";
	}
}

