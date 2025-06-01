package ufrpe.sbpc.botpcd

import com.whatsapp.api.domain.webhook.Change
import com.whatsapp.api.domain.webhook.Value
import com.whatsapp.api.domain.webhook.WebHook
import org.hibernate.sql.ast.tree.insert.Values
import org.springframework.stereotype.Service
import java.io.File

@Service
class PayloadMockFactory {

    fun loadPayload(filePath: String): String {
        return File(filePath).readText(Charsets.UTF_8)
    }

    fun changeUserNumber(payload: String, newNumber: String): String {
        return payload.replace(Regex("(?<=\"wa_id\": \")\\d+(?=\")"), newNumber)
    }

    fun changeUserMessage(payload: String, newMessage: String): String {
        // Exemplo simples substituindo o texto da mensagem.
        // Ajuste de acordo com a estrutura do JSON.
        return payload.replace(Regex("(?<=\\\"body\\\": \\\").*?(?=\\\")"), newMessage)
    }
    fun getBotNumber(payload: String): String {
        return getChange(payload).metadata.displayPhoneNumber
    }
    fun getChange(payload: String): Value {
        return WebHook.constructEvent(payload).entry[0].changes[0].value
    }
    fun getMessage(payload: String) = getChange(payload).messages[0].text.body
}