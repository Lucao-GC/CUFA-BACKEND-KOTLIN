package cufa.conecta.com.application.controller

import cufa.conecta.com.domain.service.ai.IaGenerativaService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ai")
class IaGenerativaController(
    private val aiService: IaGenerativaService
) {

    @GetMapping("/teste")
    fun teste(): String? {
        return aiService.gerarResposta("Explique o que é inclusão social em uma frase.")
    }
}