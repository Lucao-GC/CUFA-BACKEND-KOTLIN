package cufa.conecta.com.application.controller.usuarios

import cufa.conecta.application.dto.response.usuario.CandidaturaFilaDto
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notifications-candidaturas")
class NotificationsCandidaturasController(
    private val rabbitTemplate: RabbitTemplate
) {

    @GetMapping
    fun getNotifications(): List<CandidaturaFilaDto> {
        val notifications = mutableListOf<CandidaturaFilaDto>()

        while (true) {
            val message = rabbitTemplate.receiveAndConvert("fila-candidaturas")
                ?: break

            if (message is CandidaturaFilaDto) {
                notifications.add(message)
            }
        }

        return notifications
    }
}