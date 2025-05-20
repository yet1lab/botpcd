package ufrpe.sbpc.botpcd

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
class BotPcdApplication

fun main(args: Array<String>) {
    runApplication<BotPcdApplication>(*args)
}
