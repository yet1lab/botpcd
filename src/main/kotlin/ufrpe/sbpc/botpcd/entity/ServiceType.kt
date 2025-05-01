package ufrpe.sbpc.botpcd.entity

/**
 *
 */
sealed class ServiceType(val provider: Provider, val disability: Set<Disability>) {
    object Libras : ServiceType(provider = Provider.MONITOR, disability = mutableSetOf(Disability.DEAFNESS)) {
        val monitorAssistanceType = MonitorAssistanceType.LIBRAS_MONITOR
    }
    object LibrasInterpreter: ServiceType(provider = Provider.COMMITTEE_MEMBER, disability = mutableSetOf(Disability.DEAFNESS))
    object Mobility: ServiceType(provider = Provider.MONITOR, disability = mutableSetOf(Disability.MOBILITY_IMPAIRED, Disability.PHYSICAL_DISABILITY, Disability.BLINDED)) {
        val monitorAssistanceType = MonitorAssistanceType.MOBILITY_MONITOR
    }
    object AudioDescription: ServiceType(provider = Provider.COMMITTEE_MEMBER, disability = mutableSetOf(Disability.DEAFNESS))
    object NeurodivergentSupport: ServiceType(provider = Provider.MONITOR, disability = mutableSetOf(Disability.NEURODIVERGENT)) {
        val monitorAssistanceType = MonitorAssistanceType.NEURODIVERGENT_SUPPORT_MONITOR
    }
    object GuideInterpreter: ServiceType(provider = Provider.COMMITTEE_MEMBER, disability = mutableSetOf(Disability.DEAFBLINDNESS))
    object HygieneAndNutrition: ServiceType(provider = Provider.COMMITTEE_MEMBER, disability = mutableSetOf(Disability.MOBILITY_IMPAIRED, Disability.PHYSICAL_DISABILITY))
    object Car : ServiceType(provider = Provider.COMMITTEE_MEMBER, disability = mutableSetOf(Disability.PHYSICAL_DISABILITY, Disability.MOBILITY_IMPAIRED
        ));
    fun getServicesByDisability(disability: Disability): List<ServiceType> {
        return ServiceType::class.sealedSubclasses.mapNotNull{ it.objectInstance }.filter{ disability in it.disability }
    }
}