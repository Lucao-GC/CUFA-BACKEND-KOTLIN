package cufa.conecta.com.domain.service.usuario.implementation

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.JsonNode
import cufa.conecta.com.application.dto.response.vagas.AnaliseMercadoDto
import cufa.conecta.com.application.dto.response.vagas.VagasRecomendadasResponseDto
import cufa.conecta.com.application.dto.response.usuario.UsuarioTokenDto
import cufa.conecta.com.application.dto.response.vagas.RecomendacaoDto
import cufa.conecta.com.application.exception.CreateInternalServerError
import cufa.conecta.com.domain.service.ai.IaGenerativaService
import cufa.conecta.com.domain.service.usuario.ExperienciaService
import cufa.conecta.com.domain.service.usuario.UsuarioService
import cufa.conecta.com.model.data.Login
import cufa.conecta.com.model.data.usuario.Usuario
import cufa.conecta.com.model.data.result.UsuarioResult
import cufa.conecta.com.model.data.usuario.Localizacao
import cufa.conecta.com.model.data.usuario.Experiencia
import cufa.conecta.com.resources.usuario.UsuarioRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UsuarioServiceImpl(
    private val repository: UsuarioRepository,
    private val iaService: IaGenerativaService,
    private val experienciaService: ExperienciaService,
    private val objectMapper: ObjectMapper
): UsuarioService {
    override fun cadastrarUsuario(data: Usuario) = repository.cadastrarUsuario(data)

    override fun autenticar(data: Login): UsuarioTokenDto = repository.autenticar(data)

    override fun mostrarDados(): UsuarioResult {
        val auth = SecurityContextHolder.getContext().authentication
        val email = auth?.name

        val dadosUsuario = repository.mostrarDados(email!!)

        return dadosUsuario
    }

    override fun atualizar(data: Usuario) {
        val auth = SecurityContextHolder.getContext().authentication

        val email = auth?.name

        repository.atualizar(data, email!!)
    }

    override fun atualizarCurriculoUrl(curriculoUrl: String?) {
        val auth = SecurityContextHolder.getContext().authentication
        val email = auth?.name

        repository.atualizarCurriculoUrl(email!!, curriculoUrl)
    }

    override fun atualizarLocalizacao(data: Localizacao) {
        val auth = SecurityContextHolder.getContext().authentication
        val email = auth?.name

        repository.atualizarLocalizacao(email!!, data)
    }

    override fun recomendarVagas(latitude: Double, longitude: Double): VagasRecomendadasResponseDto {
        val vagas = repository.buscarVagasProximas(latitude, longitude)

        if (vagas.isEmpty()) throw CreateInternalServerError("Nenhuma vaga encontrada próxima às suas coordenadas")

        val usuario = mostrarDados()
        val experiencias = runCatching { experienciaService.listarPorUsuario() }
            .getOrElse { emptyList() }

        val prompt = iaService.construirPromptRecomendacaoLocal(usuario, experiencias, vagas)

        val respostaString = runCatching { iaService.gerarResposta(prompt, respostaEmJson = true) }.getOrNull()

        if (respostaString.isNullOrBlank()) {
            return montarRespostaPadrao(vagas)
        }

        return runCatching {
            mapearRespostaDaIa(respostaString, vagas)
        }.getOrElse {
            montarRespostaPadrao(vagas, it.message)
        }
    }

    private fun mapearRespostaDaIa(
        respostaString: String,
        vagasOriginais: List<cufa.conecta.com.application.dto.response.vagas.VagaProximaDto>
    ): VagasRecomendadasResponseDto {
        val root = objectMapper.readTree(respostaString)

        val analiseMercado = root.path("analiseMercado")
            .takeIf { !it.isMissingNode && !it.isNull }
            ?.let { mapearAnaliseMercado(it) }

        val recomendacoes = root.path("recomendacoes")
            .takeIf { it.isArray }
            ?.mapNotNull { node -> mapearRecomendacao(node, vagasOriginais) }
            ?.distinctBy { it.id }
            .orEmpty()

        if (recomendacoes.isEmpty()) {
            throw CreateInternalServerError("A IA não retornou recomendações válidas")
        }

        return VagasRecomendadasResponseDto(
            recomendacoes = recomendacoes,
            totalVagas = vagasOriginais.size,
            raioKm = 5,
            analiseMercado = analiseMercado ?: criarAnaliseFallback(vagasOriginais)
        )
    }

    private fun mapearAnaliseMercado(node: JsonNode): AnaliseMercadoDto {
        val ocupacoesEmAlta = node.path("ocupacoesEmAlta")
            .takeIf { it.isArray }
            ?.mapNotNull { ocupacao ->
                val nomeOcupacao = ocupacao.path("ocupacao").asText("")
                if (nomeOcupacao.isBlank()) return@mapNotNull null

                AnaliseMercadoDto.OcupacaoEmAltaDto(
                    ocupacao = nomeOcupacao,
                    quantidadeVagas = ocupacao.path("quantidadeVagas").asInt(0),
                    relacaoComPerfil = ocupacao.path("relacaoComPerfil").asText("")
                )
            }
            .orEmpty()

        return AnaliseMercadoDto(
            resumo = node.path("resumo").asText(""),
            ocupacoesEmAlta = ocupacoesEmAlta
        )
    }

    private fun mapearRecomendacao(
        node: JsonNode,
        vagasOriginais: List<cufa.conecta.com.application.dto.response.vagas.VagaProximaDto>
    ): RecomendacaoDto? {
        val id = node.path("id").asLong(0)
        val vaga = vagasOriginais.firstOrNull { it.publicacaoId == id } ?: return null

        return RecomendacaoDto(
            id = vaga.publicacaoId,
            nomeEmpresa = node.path("nomeEmpresa").asText(vaga.nomeEmpresa),
            idEmpresa = node.path("idEmpresa").asLong(vaga.empresaId),
            titulo = node.path("titulo").asText(vaga.titulo),
            tipoContrato = node.path("tipoContrato").asText(vaga.tipoContrato)
        )
    }

    private fun montarRespostaPadrao(
        vagas: List<cufa.conecta.com.application.dto.response.vagas.VagaProximaDto>,
        motivoFallback: String? = null
    ): VagasRecomendadasResponseDto {
        val recomendacoes = vagas.take(5).map {
            RecomendacaoDto(
                id = it.publicacaoId,
                nomeEmpresa = it.nomeEmpresa,
                idEmpresa = it.empresaId,
                titulo = it.titulo,
                tipoContrato = it.tipoContrato
            )
        }

        return VagasRecomendadasResponseDto(
            recomendacoes = recomendacoes,
            totalVagas = vagas.size,
            raioKm = 5,
            analiseMercado = criarAnaliseFallback(vagas, motivoFallback)
        )
    }

    private fun criarAnaliseFallback(
        vagas: List<cufa.conecta.com.application.dto.response.vagas.VagaProximaDto>,
        motivoFallback: String? = null
    ): AnaliseMercadoDto {
        val ocupacoesEmAlta = vagas
            .groupBy { it.titulo.trim() }
            .entries
            .sortedByDescending { it.value.size }
            .take(3)
            .map { entrada ->
                AnaliseMercadoDto.OcupacaoEmAltaDto(
                    ocupacao = entrada.key,
                    quantidadeVagas = entrada.value.size,
                    relacaoComPerfil = "Demanda local detectada no raio pesquisado."
                )
            }

        val resumoBase = if (vagas.isNotEmpty()) {
            "Foram encontradas ${vagas.size} vagas próximas, com destaque para ocupações repetidas no mercado local."
        } else {
            "Não foi possível identificar demanda local suficiente para gerar um resumo confiável."
        }

        val resumo = listOfNotNull(resumoBase, motivoFallback?.let { "Fallback aplicado: $it" })
            .joinToString(" ")

        return AnaliseMercadoDto(
            resumo = resumo,
            ocupacoesEmAlta = ocupacoesEmAlta
        )
    }
}
