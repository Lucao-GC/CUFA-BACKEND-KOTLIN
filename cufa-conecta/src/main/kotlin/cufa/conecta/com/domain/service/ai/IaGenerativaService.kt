package cufa.conecta.com.domain.service.ai

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class IaGenerativaService(
    builder: WebClient.Builder,
    @Value("\${ia.ollama.base-url:http://127.0.0.1:11434}") baseUrl: String,
    @Value("\${ia.ollama.model:llama3}") private val model: String,
) {

    private val webClient = builder
        .baseUrl(baseUrl.trimEnd('/'))
        .build()

    /**
     * Chama Ollama `/api/generate`. Retorna `null` se o serviço não estiver acessível ou a resposta for inválida.
     */
    fun gerarResposta(prompt: String): String? {
        val body = mapOf(
            "model" to model,
            "prompt" to prompt,
            "stream" to false
        )

        return runCatching {
            val response = webClient.post()
                .uri("/api/generate")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map::class.java)
                .block()
            response?.get("response") as? String
        }.getOrNull()
    }
}
