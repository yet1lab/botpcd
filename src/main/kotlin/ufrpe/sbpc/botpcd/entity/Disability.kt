package ufrpe.sbpc.botpcd.entity


/**
 * Represents the type of assistance a monitor can provide.
 */
enum class Disability(val textOption: String) {
	BLINDED("Deficiência visual"),
	DEAFNESS("Deficiência auditiva/surdez"),
	DEAFBLINDNESS("Surdocegueira"),
	NEURODIVERGENT("Transtorno do Espectro Autista/Neurodivergente"),
	PHYSICAL_DISABILITY("Deficiência física"),
	MOBILITY_IMPAIRED("Não tenho deficiência, mas tenho mobilidade reduzida");


	companion object {
		fun parse(text: String): Disability {
			val disability = Disability.entries.find { it.textOption.equals(text, ignoreCase = true) }
			if(disability == null) {
				throw IllegalArgumentException("Invalid disability type: $text")
			}
			return disability
		}
		fun getOptions(): String {
			var message = "Olá, qual sua deficiência?\n"
			for (disability in Disability.entries) {
				message += "Digite ${disability.ordinal + 1} para ${disability.textOption} \n"
			}
			message += "Digite 7 para Não preciso de suporte."
			return message
		}
		fun getByOrdinal(ordinal: Int) = Disability.entries.find { it.ordinal == ordinal }
	}
}
