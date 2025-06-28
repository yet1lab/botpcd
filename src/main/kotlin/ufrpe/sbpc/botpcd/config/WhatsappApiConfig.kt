package ufrpe.sbpc.botpcd.config

import com.whatsapp.api.WhatsappApiFactory
import com.whatsapp.api.configuration.ApiVersion
import com.whatsapp.api.impl.WhatsappBusinessCloudApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.beans.BeanProperty

@Profile("!test")
@Configuration
class WhatsappApiConfig {
    @Value("\${whatsapp.api.token}")
    private lateinit var token: String

    @Bean
    fun whatsappBusinessCloudApi(): WhatsappBusinessCloudApi {
        val factory = WhatsappApiFactory.newInstance(token)
        val whatsappBusinessCloudApi = factory.newBusinessCloudApi(ApiVersion.V20_0)
        return whatsappBusinessCloudApi
    }
}