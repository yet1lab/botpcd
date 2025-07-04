package ufrpe.sbpc.botpcd.entity

/**
 *
 */
sealed class ServiceType(
    val attendantType: Provider,
    val disability: Set<Disability>,
    val description: String
) {
    object Libras : ServiceType(
        attendantType = Provider.MONITOR,
        disability = mutableSetOf(Disability.DEAFNESS),
        description = "informações em Libras"
    ), MonitorServiceType {

        override val monitorAssistanceType = MonitorAssistanceType.LIBRAS_MONITOR
    }
    object LibrasInterpreter : ServiceType(
        attendantType = Provider.COMMITTEE_MEMBER,
        disability = mutableSetOf(Disability.DEAFNESS),
        description = "atividade com interpretação em Libras"
    )
    object Mobility : ServiceType(
        attendantType = Provider.MONITOR,
        disability = mutableSetOf(Disability.MOBILITY_IMPAIRED, Disability.PHYSICAL_DISABILITY, Disability.BLINDED),
        description = "ajuda na mobilidade"
    ), MonitorServiceType {
        override val monitorAssistanceType = MonitorAssistanceType.MOBILITY_MONITOR
    }

    object AudioDescription : ServiceType(
        attendantType = Provider.COMMITTEE_MEMBER,
        disability = mutableSetOf(Disability.BLINDED),
        description = "programação com audiodescrição"
    )

    object NeurodivergentSupport : ServiceType(
        attendantType = Provider.MONITOR,
        disability = mutableSetOf(Disability.NEURODIVERGENT),
        description = "suporte para pessoas neurodivergentes"
    ), MonitorServiceType {
        override val monitorAssistanceType = MonitorAssistanceType.NEURODIVERGENT_SUPPORT_MONITOR
    }

    object Car : ServiceType(
        attendantType = Provider.COMMITTEE_MEMBER,
        disability = mutableSetOf(Disability.PHYSICAL_DISABILITY, Disability.MOBILITY_IMPAIRED),
        description = "transporte para deslocamento no evento"
    )

    companion object {
        @JvmStatic
        fun getServicesByDisability(disability: Disability): List<ServiceType> {
            return ServiceType::class.sealedSubclasses
                .mapNotNull { it.objectInstance }
                .filter { disability in it.disability }.sortedBy { it.description.getAlphabeticOrder() }
        }
        fun getServiceByMonitorAssistanceType(monitorAssistanceType: MonitorAssistanceType): ServiceType {
            return ServiceType::class.sealedSubclasses
                .mapNotNull { it.objectInstance }
                .filterIsInstance<MonitorServiceType>()
                .first { it.monitorAssistanceType == monitorAssistanceType } as ServiceType
        }
        fun getByDescription(description: String): ServiceType {
            return ServiceType::class.sealedSubclasses
                .mapNotNull { it.objectInstance }
                .first { it.description == description } as ServiceType
        }
    }
}

fun String.getAlphabeticOrder() = this.replace("[^a-zA-Z]".toRegex(), "").slice(0..9).reduce {prev, curr -> prev + curr.code}

interface MonitorServiceType {
    val monitorAssistanceType: MonitorAssistanceType
}