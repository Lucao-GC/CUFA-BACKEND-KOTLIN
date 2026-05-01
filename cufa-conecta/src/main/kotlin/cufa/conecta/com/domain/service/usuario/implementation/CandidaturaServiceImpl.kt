package cufa.conecta.com.domain.service.usuario.implementation

import cufa.conecta.application.dto.response.usuario.CandidaturaFilaDto
import cufa.conecta.com.config.UsuarioAutenticadoHelper
import cufa.conecta.config.RabbitConfig
import cufa.conecta.com.application.exception.InvalidPageNumberException
import cufa.conecta.com.application.exception.InvalidSizeNumberException
import cufa.conecta.com.domain.service.usuario.CandidaturaService
import cufa.conecta.com.model.data.Candidatura
import cufa.conecta.com.model.data.Publicacao
import cufa.conecta.com.model.data.result.CandidaturaResult
import cufa.conecta.com.resources.empresa.PublicacaoRepository
import cufa.conecta.com.resources.usuario.CandidaturaRepository
import cufa.conecta.com.resources.usuario.UsuarioRepository
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class CandidaturaServiceImpl(
    private val repository: CandidaturaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val publicacaoRepository: PublicacaoRepository,
    private val rabbitTemplate: RabbitTemplate
) : CandidaturaService {

    companion object {
        private val log = LoggerFactory.getLogger(CandidaturaServiceImpl::class.java)
    }

    override fun criarCandidatura(data: Candidatura) {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()

        val usuario = usuarioRepository.obterUsuarioPorEmail(email)

        val nomeCandidato = usuario.nome

        val publicacao = publicacaoRepository.findById(data.publicacaoId)

        val tituloVaga = publicacao.titulo

        repository.criarCandidatura(data, email)

        val filaDto = CandidaturaFilaDto(
            nomeCandidato = nomeCandidato,
            tituloVaga = tituloVaga
        )

        runCatching {
            rabbitTemplate.convertAndSend("", RabbitConfig.QUEUE_CANDIDATURAS, filaDto)
        }.onFailure { e ->
            log.warn("Candidatura gravada, mas falha ao publicar na fila RabbitMQ: {}", e.message)
        }
    }

    override fun listarCandidatosPorVaga(vagaId: Long, page: Int, size: Int): CandidaturaResult {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        validatePageAndSize(page, size)

        return repository.listarCandidatosPorVaga(vagaId, page, size, email)
    }

    override fun verificarCandidaturaExistente(vagaId: Long): Boolean {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        return repository.verificarCandidaturaExistente(vagaId, email)
    }

    override fun listarPublicacoesCandidatadasPorUsuario(): List<Publicacao> {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        return repository.listarPublicacoesCandidatadasPorUsuario(email)
    }

    private fun validatePageAndSize(page: Int, size: Int) {
        if (page < 1) throw InvalidPageNumberException("A página $page não foi encontrada")

        if (size < 1) throw InvalidSizeNumberException("O tamanho da lista desejado é inválido")
    }
}