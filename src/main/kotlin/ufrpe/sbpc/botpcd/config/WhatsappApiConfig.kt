package ufrpe.sbpc.botpcd.config

import com.whatsapp.api.WhatsappApiFactory
import com.whatsapp.api.configuration.ApiVersion
import com.whatsapp.api.impl.WhatsappBusinessCloudApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.beans.BeanProperty

@Configuration
class WhatsappApiConfig {
    @Value("\${whatsapp.api.version")
    private lateinit var version: String
    @Value("\${whatsapp.api.token}")
    private lateinit var token: String

    @Bean
    fun whatsappBusinessCloudApi(): WhatsappBusinessCloudApi {
        val factory = WhatsappApiFactory.newInstance(token)
        val whatsappBusinessCloudApi = factory.newBusinessCloudApi(ApiVersion.valueOf(version))
        return whatsappBusinessCloudApi
    }
}