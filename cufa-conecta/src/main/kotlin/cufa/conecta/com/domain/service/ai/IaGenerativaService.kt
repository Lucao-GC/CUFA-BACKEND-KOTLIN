package cufa.conecta.com.domain.service.ai

import cufa.conecta.com.application.dto.response.vagas.VagaProximaDto
import cufa.conecta.com.model.data.result.UsuarioResult
import cufa.conecta.com.model.data.usuario.Experiencia
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.Locale

@Service
class IaGenerativaService(
    builder: WebClient.Builder,
    @Value("\${ai.ollama.base-url:http://localhost:11434}")
    private val baseUrl: String,
    @Value("\${ai.ollama.model:llama3.1}")
    private val model: String
) {

    private val webClient = builder
        .baseUrl(baseUrl)
        .build()

    fun gerarResposta(prompt: String, respostaEmJson: Boolean = false): String? {
        val body = mutableMapOf<String, Any>(
            "model" to model,
            "prompt" to prompt,
            "stream" to false,
            "options" to mapOf("temperature" to 0.3)
        )

        if (respostaEmJson) {
            body["format"] = "json"
        }

        val response = webClient.post()
            .uri("/api/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(JsonNode::class.java)
            .block()

        return response
            ?.path("choices")
            ?.get(0)
            ?.path("message")
            ?.path("content")
            ?.asText()
    }

    fun construirPromptRecomendacaoLocal(
        usuario: UsuarioResult,
        experiencias: List<Experiencia>,
        vagas: List<VagaProximaDto>
    ): String {
        val nome = usuario.nome ?: "Não informado"
        val escolaridade = usuario.escolaridade ?: "Não informada"
        val cidade = usuario.cidade ?: "Não informada"
        val estado = usuario.estado ?: "Não informado"
        val biografia = usuario.biografia ?: "Não informada"

        val perfilUsuario = """
            Nome: $nome
            Escolaridade: $escolaridade
            Cidade: $cidade
            Estado: $estado
            Biografia: $biografia
        """.trimIndent()

        val historicoExperiencias = if (experiencias.isEmpty()) {
            "Sem experiências cadastradas."
        } else {
            experiencias.joinToString(separator = "\n") {
                val cargo = it.cargo ?: "Não informado"
                val empresa = it.empresa ?: "Não informada"
                val dtInicio = it.dtInicio?.toString() ?: "?"
                val dtFim = it.dtFim?.toString() ?: "?"

                "- Cargo: $cargo; Empresa: $empresa; Período: $dtInicio até $dtFim"
            }
        }

        val listaVagas = vagas.joinToString(separator = "\n") {
            "- ID=${it.publicacaoId}; Empresa=${it.nomeEmpresa}; Título=${it.titulo}; Contrato=${it.tipoContrato}; DistânciaKm=${String.format(Locale.US, "%.2f", it.distancia)}"
        }

        val mapaDemanda = vagas
            .groupingBy { it.titulo.trim() }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .joinToString(separator = "\n") { "- ${it.key}: ${it.value} vaga(s)" }

        return """
            Você é um analista de mercado de trabalho e empregabilidade para pessoas com baixa escolaridade.

            Objetivo:
            - Avaliar o mercado local com base nas vagas próximas já filtradas por geolocalização.
            - Identificar as ocupações com maior demanda na região.
            - Relacionar essas ocupações ao perfil e às experiências do usuário.
            - Priorizar vagas que combinam o perfil do candidato com a demanda local.

            Regras:
            - Use somente os dados fornecidos.
            - Não invente cargos, empresas ou competências.
            - Retorne SOMENTE um JSON válido.
            - O JSON deve ter a estrutura:
              {
                "analiseMercado": {
                  "resumo": "string",
                  "ocupacoesEmAlta": [
                    {
                      "ocupacao": "string",
                      "quantidadeVagas": 0,
                      "relacaoComPerfil": "string"
                    }
                  ]
                },
                "recomendacoes": [
                  {
                    "id": 0,
                    "nomeEmpresa": "string",
                    "idEmpresa": 0,
                    "titulo": "string",
                    "tipoContrato": "string"
                  }
                ]
              }
            - Limite as recomendações a no máximo 5 vagas.
            - As recomendações precisam usar apenas IDs existentes na lista de vagas.
            - Mantenha a ordem de prioridade do melhor encaixe entre perfil + demanda local.

            Perfil do usuário:
            $perfilUsuario

            Experiências do usuário:
            $historicoExperiencias

            Mapa de demanda das vagas próximas:
            $mapaDemanda

            Lista completa de vagas próximas:
            $listaVagas
        """.trimIndent()
    }

    fun construirPromptGeracaoCurriculo(
        usuario: UsuarioResult,
        experiencias: List<Experiencia>
    ): String {
        val nome = usuario.nome ?: "Não informado"
        val email = usuario.email
        val telefone = usuario.telefone ?: "Não informado"
        val escolaridade = usuario.escolaridade ?: "Não informada"
        val cidade = usuario.cidade ?: "Não informada"
        val estado = usuario.estado ?: "Não informado"
        val biografia = usuario.biografia ?: "Não informada"

        val experienciasTexto = if (experiencias.isEmpty()) {
            "Sem experiências cadastradas."
        } else {
            experiencias.joinToString(separator = "\n") {
                val cargo = it.cargo ?: "Não informado"
                val empresa = it.empresa ?: "Não informada"
                val dtInicio = it.dtInicio?.toString() ?: "?"
                val dtFim = it.dtFim?.toString() ?: "?"

                "- Cargo: $cargo; Empresa: $empresa; Início: $dtInicio; Fim: $dtFim"
            }
        }

        return """
            Gere um currículo profissional simples, direto e fácil de entender, em português do Brasil.

            Regras:
            - Use apenas informações fornecidas.
            - Não invente formação, cursos ou experiências.
            - Adapte a linguagem para ser acessível a pessoas com baixa escolaridade.
            - Estruture o texto com seções curtas: objetivo, dados pessoais, resumo profissional, experiências, habilidades e observações finais.
            - Destaque potencial de empregabilidade e aprendizados práticos.
            - Retorne SOMENTE o texto final do currículo.

            Dados do usuário:
            Nome: $nome
            Email: $email
            Telefone: $telefone
            Escolaridade: $escolaridade
            Cidade: $cidade
            Estado: $estado
            Biografia: $biografia

            Experiências:
            $experienciasTexto
        """.trimIndent()
    }

    fun construirPromptAvaliacaoCurriculo(
        curriculoTexto: String,
        usuario: UsuarioResult,
        experiencias: List<Experiencia>
    ): String {
        val nome = usuario.nome ?: "Não informado"
        val escolaridade = usuario.escolaridade ?: "Não informada"
        val cidade = usuario.cidade ?: "Não informada"
        val estado = usuario.estado ?: "Não informado"

        val experienciasTexto = if (experiencias.isEmpty()) {
            "Sem experiências cadastradas."
        } else {
            experiencias.joinToString(separator = "\n") {
                val cargo = it.cargo ?: "Não informado"
                val empresa = it.empresa ?: "Não informada"
                val dtInicio = it.dtInicio?.toString() ?: "?"
                val dtFim = it.dtFim?.toString() ?: "?"

                "- Cargo: $cargo; Empresa: $empresa; Início: $dtInicio; Fim: $dtFim"
            }
        }

        return """
            Avalie o currículo abaixo e sugira melhorias práticas.

            Regras:
            - Não altere os fatos do candidato.
            - Aponte pontos fortes, lacunas e sugestões objetivas.
            - Foque em clareza, organização, relevância profissional e aderência ao mercado.
            - Retorne SOMENTE um JSON válido com a estrutura:
              {
                "nota": 0,
                "pontosFortes": ["string"],
                "pontosMelhoria": ["string"],
                "sugestoes": ["string"],
                "observacaoFinal": "string"
              }

            Dados do usuário:
            Nome: $nome
            Escolaridade: $escolaridade
            Cidade: $cidade
            Estado: $estado

            Experiências:
            $experienciasTexto

            Currículo para análise:
            $curriculoTexto
        """.trimIndent()
    }
}