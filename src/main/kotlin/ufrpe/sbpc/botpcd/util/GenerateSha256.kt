package ufrpe.sbpc.botpcd.util

import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun hmacSha256Hex(secret: String, data: String): String {
    val hmacSha256 = "HmacSHA256"
    val secretKey = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), hmacSha256)
    val mac = Mac.getInstance(hmacSha256)
    mac.init(secretKey)
    val hash = mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
    return "sha256=${hash.joinToString("") { "%02x".format(it) }}"
}