package ufrpe.sbpc.botpcd.entity

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class ServiceTypeConverter : AttributeConverter<ServiceType, String> {
    override fun convertToDatabaseColumn(attribute: ServiceType?): String? {
        return attribute?.let { it::class.simpleName }
    }

    override fun convertToEntityAttribute(dbData: String?): ServiceType? {
        return when (dbData) {
            "Libras" -> ServiceType.Libras
            "LibrasInterpreter" -> ServiceType.LibrasInterpreter
            "Mobility" -> ServiceType.Mobility
            "AudioDescription" -> ServiceType.AudioDescription
            "NeurodivergentSupport" -> ServiceType.NeurodivergentSupport
            "GuideInterpreter" -> ServiceType.GuideInterpreter
            "HygieneAndNutrition" -> ServiceType.HygieneAndNutrition
            "Car" -> ServiceType.Car
            else -> null
        }
    }
}