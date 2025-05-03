package ufrpe.sbpc.botpcd.entity

//=============================================================
//  Represents the type of assistance a monitor can provide.
//=============================================================
enum class Disability(val textOption: String) {
	BLINDED("Deficiência visual"),
	DEAFNESS("Deficiência auditiva/surdez"),
	DEAFBLINDNESS("Surdocegueira"),
	AUTIST("Transtorno do Espectro Autista"),
	PHYSICAL_DISABILITY("Deficiência física"),
	INTELLECTUAL_DISABILITY("Deficiência intelectual"),
	MOBILITY_IMPAIRED("Não tenho deficiência, mas tenho mobilidade reduzida"),
	NOTHING("Não tenho deficiência, nem mobilidade reduzida");

	companion object {
		fun parse(text: String): Disability? {
			return Disability.entries.find { it.textOption.equals(text, ignoreCase = true) }
		}
		fun textList(): Array<String> {
			return Disability.entries.map { it.textOption }.toTypedArray()
		}
	}
}
