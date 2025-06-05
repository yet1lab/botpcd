package ufrpe.sbpc.botpcd.entity

/**
 *
 */
sealed class ServiceType(
    val provider: Provider,
    val disability: Set<Disability>,
    val description: String
) {
    object Libras : ServiceType(
        provider = Provider.MONITOR,
        disability = mutableSetOf(Disability.DEAFNESS),
        description = "informações em Libras"
    ) {
        val monitorAssistanceType = MonitorAssistanceType.LIBRAS_MONITOR
    }
    object LibrasInterpreter : ServiceType(
        provider = Provider.COMMITTEE_MEMBER,
        disability = mutableSetOf(Disability.DEAFNESS),
        description = "atividade com interpretação em Libras"
    )
    object Mobility : ServiceType(
        provider = Provider.MONITOR,
        disability = mutableSetOf(Disability.MOBILITY_IMPAIRED, Disability.PHYSICAL_DISABILITY, Disability.BLINDED),
        description = "ajuda na mobilidade"
    ) {
        val monitorAssistanceType = MonitorAssistanceType.MOBILITY_MONITOR
    }

    object AudioDescription : ServiceType(
        provider = Provider.COMMITTEE_MEMBER,
        disability = mutableSetOf(Disability.BLINDED),
        description = "programação com audiodescrição"
    )

    object NeurodivergentSupport : ServiceType(
        provider = Provider.MONITOR,
        disability = mutableSetOf(Disability.NEURODIVERGENT),
        description = "suporte para pessoas neurodivergentes"
    ) {
        val monitorAssistanceType = MonitorAssistanceType.NEURODIVERGENT_SUPPORT_MONITOR
    }

    object GuideInterpreter : ServiceType(
        provider = Provider.COMMITTEE_MEMBER,
        disability = mutableSetOf(Disability.DEAFBLINDNESS),
        description = "guia-intérprete"
    )

    object HygieneAndNutrition : ServiceType(
        provider = Provider.COMMITTEE_MEMBER,
        disability = mutableSetOf(Disability.MOBILITY_IMPAIRED, Disability.PHYSICAL_DISABILITY),
        description = "ajuda com alimentação e higiene"
    )

    object Car : ServiceType(
        provider = Provider.COMMITTEE_MEMBER,
        disability = mutableSetOf(Disability.PHYSICAL_DISABILITY, Disability.MOBILITY_IMPAIRED),
        description = "transporte para deslocamento no evento"
    )

    companion object {
        @JvmStatic
        fun getServicesByDisability(disability: Disability): List<ServiceType> {
            return ServiceType::class.sealedSubclasses
                .mapNotNull { it.objectInstance }
                .filter { disability in it.disability }
        }
    }
}