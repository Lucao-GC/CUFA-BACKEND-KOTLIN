package cufa.conecta.com.application.controller.empresas

import cufa.conecta.com.application.dto.response.empresa.PublicacaoResponseDto
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notifications")
class NotificationController(
    private val rabbitTemplate: RabbitTemplate
) {

    @GetMapping
    fun getNotifications(): List<PublicacaoResponseDto> {
        val notifications = mutableListOf<PublicacaoResponseDto>()

        while (true) {
            val message = rabbitTemplate.receiveAndConvert("fila-notificacoes")
            if (message == null) break
            notifications.add(message as PublicacaoResponseDto)
        }

        return notifications
    }
}