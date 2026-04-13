package cufa.conecta.com.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["cufa.conecta.com.*"])
@EntityScan("cufa.conecta.com.resources.*")
@EnableJpaRepositories("cufa.conecta.com.resources")
@ComponentScan("cufa.conecta.com")
class CufaConectaApplication

fun main(args: Array<String>) {
	runApplication<CufaConectaApplication>(*args)
}
