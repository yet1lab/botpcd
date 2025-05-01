package ufrpe.sbpc.botpcd.entity

/**
 *
 */
sealed class ServiceType(val needsCommitteeMember: Boolean = false, val disability: Set<Disability>) {
    object Libras : ServiceType(disability = mutableSetOf(Disability.DEAFNESS)) {
        val monitorAssistanceType = MonitorAssistanceType.LIBRAS_MONITOR
    }
    object LibrasInterpreter: ServiceType(needsCommitteeMember = true, disability = mutableSetOf(Disability.DEAFNESS))
    object Mobility: ServiceType(disability = mutableSetOf(Disability.MOBILITY_IMPAIRED, Disability.PHYSICAL_DISABILITY, Disability.BLINDED)) {
        val monitorAssistanceType = MonitorAssistanceType.MOBILITY_MONITOR
    }
    object AudioDescription: ServiceType(needsCommitteeMember = true, disability = mutableSetOf(Disability.DEAFNESS))
    object NeurodivergentSupport: ServiceType(disability = mutableSetOf(Disability.NEURODIVERGENT)) {
        val monitorAssistanceType = MonitorAssistanceType.NEURODIVERGENT_SUPPORT_MONITOR
    }
    object GuideInterpreter: ServiceType(needsCommitteeMember = true, disability = mutableSetOf(Disability.DEAFBLINDNESS))
    object HygieneAndNutrition: ServiceType(needsCommitteeMember = true, disability = mutableSetOf(Disability.MOBILITY_IMPAIRED, Disability.PHYSICAL_DISABILITY))
    object Car : ServiceType(needsCommitteeMember = true, disability = mutableSetOf(Disability.PHYSICAL_DISABILITY, Disability.MOBILITY_IMPAIRED
        ));
    fun getServicesByDisability(disability: Disability): List<ServiceType> {
        return ServiceType::class.sealedSubclasses.mapNotNull{ it.objectInstance }.filter{ disability in it.disability }
    }
}