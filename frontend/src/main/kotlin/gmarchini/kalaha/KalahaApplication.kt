package gmarchini.kalaha

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KalahaApplication

fun main(args: Array<String>) {
	runApplication<KalahaApplication>(*args)
}
