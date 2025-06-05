package ufrpe.sbpc.botpcd.config

import com.whatsapp.api.impl.WhatsappBusinessCloudApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import ufrpe.sbpc.botpcd.WhatsappBusinessCloudApiMock

@Configuration
@Profile("test")
class MockConfig {
    @Bean
    fun whatsappMock(): WhatsappBusinessCloudApi {
        return WhatsappBusinessCloudApiMock("fakeToken")
    }
}