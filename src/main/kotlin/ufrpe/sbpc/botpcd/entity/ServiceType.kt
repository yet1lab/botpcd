package ufrpe.sbpc.botpcd.entity

/**
 *
 */
sealed class ServiceType(val monitorAssistanceType: MonitorAssistanceType? = null, val needsCommitteeMember: Boolean = false, val disability: Set<Disability>) {
    object Libras : ServiceType(monitorAssistanceType = MonitorAssistanceType.LIBRAS_MONITOR, disability = mutableSetOf(Disability.DEAFNESS))
    object LibrasInterpreter: ServiceType(needsCommitteeMember = true, disability = mutableSetOf(Disability.DEAFNESS))
    object Mobility: ServiceType(monitorAssistanceType = MonitorAssistanceType.MOBILITY_MONITOR, disability = mutableSetOf(Disability.MOBILITY_IMPAIRED, Disability.PHYSICAL_DISABILITY, Disability.BLINDED))
    object AudioDescription: ServiceType(needsCommitteeMember = true, disability = mutableSetOf(Disability.DEAFNESS))
    object NeurodivergentSupport: ServiceType(monitorAssistanceType = MonitorAssistanceType.NEURODIVERGENT_SUPPORT_MONITOR, disability = mutableSetOf(Disability.NEURODIVERGENT))
    object GuideInterpreter: ServiceType(needsCommitteeMember = true, disability = mutableSetOf(Disability.DEAFBLINDNESS))
    object HygieneAndNutrition: ServiceType(needsCommitteeMember = true, disability = mutableSetOf(Disability.MOBILITY_IMPAIRED, Disability.PHYSICAL_DISABILITY))
    object Car : ServiceType(needsCommitteeMember = true, disability = mutableSetOf(Disability.PHYSICAL_DISABILITY, Disability.MOBILITY_IMPAIRED
        ));
    fun getServicesByDisability(disability: Disability): List<ServiceType> {
        return ServiceType::class.sealedSubclasses.mapNotNull{ it.objectInstance }.filter{ disability in it.disability }
    }
}