package cufa.conecta.com.domain.service.empresa.implementation

import cufa.conecta.com.config.UsuarioAutenticadoHelper
import cufa.conecta.com.application.exception.InvalidPageNumberException
import cufa.conecta.com.application.exception.InvalidSizeNumberException
import cufa.conecta.com.domain.service.empresa.PublicacaoService
import cufa.conecta.com.model.data.Publicacao
import cufa.conecta.com.model.data.result.PublicacaoResult
import cufa.conecta.com.resources.empresa.PublicacaoRepository
import cufa.conecta.config.RabbitConfig
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class PublicacaoServiceImpl(
    private val repository: PublicacaoRepository,
    private val rabbitTemplate: RabbitTemplate
): PublicacaoService {
    override fun criar(data: Publicacao) {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        repository.criar(data, email)
        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, data)
    }

    override fun buscarTodas(page: Int, size: Int): PublicacaoResult {
        validatePageAndSize(page, size)

        return repository.buscarTodas(page, size)
    }

    override fun buscarPublicacoesDaEmpresa(): List<Publicacao> {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        return repository.buscarPublicacoesPorEmpresaEmail(email)
    }

    override fun findById(id: Long): Publicacao = repository.findById(id)

    override fun editarPublicacao(data: Publicacao) {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        repository.atualizar(data, email)
    }

    override fun delete(id: Long) {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        repository.delete(id, email)
    }

    private fun validatePageAndSize(page: Int, size: Int) {
        if (page < 1) throw InvalidPageNumberException("A página $page não foi encontrada")

        if (size < 1) throw InvalidSizeNumberException("O tamanho da lista desejado é inválido")
    }
}