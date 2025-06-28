package ufrpe.sbpc.botpcd.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.slf4j.Logger

@ControllerAdvice
class GlobalExceptionHandler {
    val logger: Logger = LoggerFactory.getLogger(WhatsappWebhookController::class.java)

    @ExceptionHandler(RuntimeException::class)
    fun runtimeExceptionHandler(runtimeException: RuntimeException): ResponseEntity<Map<String, String>> {
        logger.error("Erro inesperado ao chamar a API", runtimeException)
        return ResponseEntity(mapOf("message" to "Some error ocorrer calling the API"), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}