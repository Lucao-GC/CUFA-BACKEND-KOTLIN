package cufa.conecta.com.domain.service.ai

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class IaGenerativaService(
    builder: WebClient.Builder
) {

    private val webClient = builder
        .baseUrl("http://localhost:11434")
        .build()

    fun gerarResposta(prompt: String): String? {
        val body = mapOf(
            "model" to "llama3",
            "prompt" to prompt,
            "stream" to false
        )

        val response = webClient.post()
            .uri("/api/generate")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map::class.java)
            .block()

        return response?.get("response") as? String
    }
}