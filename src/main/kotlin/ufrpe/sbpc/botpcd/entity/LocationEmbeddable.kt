package ufrpe.sbpc.botpcd.entity

import com.whatsapp.api.domain.webhook.Location
import jakarta.persistence.Embeddable

@Embeddable
class LocationEmbeddable(
    var address: String,
    var latitude: Double,
    var longitude: Double,
    var name: String,
    var url: String
)  {
    // Construtor secundário para criar a partir do Location da API
    constructor(location: Location) : this(
        address = requireNotNull(location.address()) { "Address cannot be null" },
        latitude = requireNotNull(location.latitude()) { "Latitude cannot be null" },
        longitude = requireNotNull(location.longitude()) { "Longitude cannot be null" },
        name = requireNotNull(location.name()) { "Name cannot be null" },
        url = requireNotNull(location.url()) { "URL cannot be null" }
    )

    // Converter de volta para o objeto Location da API (se necessário)
    fun toApiLocation(): Location {
        return Location(
            address,
            latitude,
            name,
            longitude,
            url
        )
    }
}