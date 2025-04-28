package ufrpe.sbpc.botpcd.entity
import ufrpe.sbpc.botpcd.entity.AssistanceType.LIBRAS_MONITOR

enum class Disability(val assistanceType: AssistanceType) {
    DEAFNESS(LIBRAS_MONITOR),
    BLINDED("MobilityMonitor"),
    NEURODIVERDENT(NEURODIVERGENT_WELCOME_MONITOR,
        AUDIO_DESCRIPTION_PROFESSIONAL,
        LIBRAS_INTERPRETER_PROFESSIONAL
}