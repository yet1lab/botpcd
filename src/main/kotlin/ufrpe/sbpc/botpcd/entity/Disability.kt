package ufrpe.sbpc.botpcd.entity


enum class Disability(val textOption: String, val shortText: String) {
	BLINDED("Deficiência visual", "cegueira"),
	DEAFNESS("Deficiência auditiva/surdez", "surdez"),
	DEAFBLINDNESS("Surdocegueira", "surdocegueira"),
	NEURODIVERGENT("Transtorno do Espectro Autista/Neurodivergente", "neurodivergente"),
	PHYSICAL_DISABILITY("Deficiência física", "deficiência física"),
	MOBILITY_IMPAIRED("Não tenho deficiência, mas tenho mobilidade reduzida", "mobilidade reduzida");

	companion object {
		fun getByTextOption(text: String): Disability {
			val disability = Disability.entries.find { it.textOption.equals(text, ignoreCase = true) }
			if (disability == null) {
				throw IllegalArgumentException("Invalid disability type: $text")
			}
			return disability
		}
		fun getByShortText(shortText: String): Disability {
			val disability = Disability.entries.find { it.shortText.equals(shortText, ignoreCase = true) }
			if (disability == null) {
				throw IllegalArgumentException("Invalid disability type: $shortText")
			}
			return disability
		}
		fun getOptions(): String {
			var message = "Olá, qual sua deficiência?\n"
			for (disability in Disability.entries) {
				message += "- Digite ${disability.ordinal + 1} para ${disability.textOption}\n"
			}
			message += "- Digite 7 para Não preciso de suporte."
			return message
		}
		fun getByOrdinal(ordinal: Int) = Disability.entries.find { it.ordinal == ordinal }
	}
}
