package ufrpe.sbpc.botpcd.util

fun createOptions(options: List<String>, header: String = "", author: String = ""): String {
    var msg = ""

    for (i in options.indices) {
        msg += "- Digite ${i + 1} para ${options[i]}\n"
    }
    if (header != ""){ msg = "${header}\n${msg}"; }
    if (author != ""){ msg = "*${author}:*\n${msg}"; }

    return msg
}