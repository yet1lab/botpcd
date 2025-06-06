package ufrpe.sbpc.botpcd.entity

/**
 *
 */
sealed class ServiceType(
    val providerType: Provider,
    val disability: Set<Disability>,
    val description: String
) {
    object Libras : ServiceType(
        providerType = Provider.MONITOR,
        disability = mutableSetOf(Disability.DEAFNESS),
        description = "informações em Libras"
    ), MonitorServiceType {

        override val monitorAssistanceType = MonitorAssistanceType.LIBRAS_MONITOR
    }
    object LibrasInterpreter : ServiceType(
        providerType = Provider.COMMITTEE_MEMBER,
        disability = mutableSetOf(Disability.DEAFNESS),
        description = "atividade com interpretação em Libras"
    )
    object Mobility : ServiceType(
        providerType = Provider.MONITOR,
        disability = mutableSetOf(Disability.MOBILITY_IMPAIRED, Disability.PHYSICAL_DISABILITY, Disability.BLINDED),
        description = "ajuda na mobilidade"
    ), MonitorServiceType {
        override val monitorAssistanceType = MonitorAssistanceType.MOBILITY_MONITOR
    }

    object AudioDescription : ServiceType(
        providerType = Provider.COMMITTEE_MEMBER,
        disability = mutableSetOf(Disability.BLINDED),
        description = "programação com audiodescrição"
    )

    object NeurodivergentSupport : ServiceType(
        providerType = Provider.MONITOR,
        disability = mutableSetOf(Disability.NEURODIVERGENT),
        description = "suporte para pessoas neurodivergentes"
    ), MonitorServiceType {
        override val monitorAssistanceType = MonitorAssistanceType.NEURODIVERGENT_SUPPORT_MONITOR
    }

    object GuideInterpreter : ServiceType(
        providerType = Provider.COMMITTEE_MEMBER,
        disability = mutableSetOf(Disability.DEAFBLINDNESS),
        description = "guia-intérprete"
    )

    object HygieneAndNutrition : ServiceType(
        providerType = Provider.COMMITTEE_MEMBER,
        disability = mutableSetOf(Disability.MOBILITY_IMPAIRED, Disability.PHYSICAL_DISABILITY),
        description = "ajuda com alimentação e higiene"
    )

    object Car : ServiceType(
        providerType = Provider.COMMITTEE_MEMBER,
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
interface MonitorServiceType {
    val monitorAssistanceType: MonitorAssistanceType
}