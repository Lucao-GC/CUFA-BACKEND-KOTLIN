package cufa.conecta.com.resources.empresa.impl

import cufa.conecta.com.application.exception.CreateInternalServerError
import cufa.conecta.com.application.exception.PageNotFoundException
import cufa.conecta.com.application.exception.PublicacaoNotFoundException
import cufa.conecta.com.model.data.empresa.Publicacao
import cufa.conecta.com.model.data.result.PublicacaoResult
import cufa.conecta.com.resources.empresa.PublicacaoRepository
import cufa.conecta.com.resources.empresa.dao.EmpresaDao
import cufa.conecta.com.resources.empresa.dao.PublicacaoDao
import cufa.conecta.com.resources.empresa.entity.PublicacaoEntity
import cufa.conecta.com.resources.empresa.exception.EmpresaNotFoundException
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import kotlin.math.ceil

@Repository
class PublicacaoRepositoryImpl(
    private val dao: PublicacaoDao,
    private val empresaDao: EmpresaDao
): PublicacaoRepository {

    override fun criar(data: Publicacao, email: String) {
        val empresa = buscarEmpresaPorEmail(email)

        val publicacao = PublicacaoEntity(
            empresaId = empresa.idEmpresa!!,
            titulo = data.titulo!!,
            descricao = data.descricao!!,
            tipoContrato = data.tipoContrato!!,
            dtExpiracao = data.dtExpiracao!!,
            dtPublicacao = LocalDateTime.now(),
        )

        runCatching {
            dao.save(publicacao)
        }.getOrElse {
            throw CreateInternalServerError("Falha ao cadastrar a publicação!!")
        }
    }

    override fun buscarTodas(page: Int, size: Int): PublicacaoResult {
        val totalOfPublishes = dao.count()

        val totalOfPages = ceil(totalOfPublishes.toDouble() / size).toInt()

        if (page > totalOfPages && totalOfPublishes >= 0)
            throw PageNotFoundException("A página $page não foi encontrada")

        val publicacoes = listarPublicacoes(page, size)

        val dadosPaginados = PublicacaoResult(
            paginaAtual = page,
            totalDePaginas = totalOfPages,
            totalDePublicacoes = totalOfPublishes,
            publicacoes = publicacoes
        )

        return dadosPaginados
    }

    override fun buscarPublicacoesPorEmpresaEmail(data: String): List<Publicacao> {
        val empresa = buscarEmpresaPorEmail(data)

        val listaDePublicacoesEntity = dao.findByEmpresaId(empresa.idEmpresa!!)

        return mapearPublicacoes(listaDePublicacoesEntity)
    }

    override fun findById(id: Long): Publicacao {
        val entity = buscarPublicacaoPorId(id)
        val nomeEmpresa = empresaDao.findNameByEmpresaId(entity.empresaId!!)

        val publicacao = Publicacao(
            publicacaoId = entity.publicacaoId,
            empresaId = entity.empresaId,
            nomeEmpresa = nomeEmpresa,
            titulo = entity.titulo,
            descricao = entity.descricao,
            tipoContrato = entity.tipoContrato,
            dtExpiracao = entity.dtExpiracao,
            dtPublicacao = entity.dtPublicacao
        )

        return publicacao
    }

    override fun delete(id: Long, email: String) {
        val empresa = buscarEmpresaPorEmail(email)

        val publicacao = buscarPublicacaoPorId(id)

        runCatching {
            dao.deletePublicacao(
            publicacao.publicacaoId!!,
            empresa.idEmpresa!!
            )
        }.getOrElse {
            throw CreateInternalServerError("Falha ao deletar a publicação!!")
        }
    }

    override fun atualizar(data: Publicacao, email: String) {
        val empresa = buscarEmpresaPorEmail(email)

        val publicacaoAntiga = buscarPublicacaoPorId(data.publicacaoId!!)

        val publicacaoEditada = PublicacaoEntity(
            publicacaoId = publicacaoAntiga.publicacaoId,
            titulo = data.titulo ?: publicacaoAntiga.titulo,
            descricao = data.descricao ?: publicacaoAntiga.descricao,
            tipoContrato = data.tipoContrato ?: publicacaoAntiga.tipoContrato,
            dtExpiracao = data.dtExpiracao ?: publicacaoAntiga.dtExpiracao
        )

        runCatching {
            dao.atualizarPublicacao(
                publicacaoEditada.publicacaoId!!,
                empresa.idEmpresa!!,
                publicacaoEditada.titulo,
                publicacaoEditada.descricao,
                publicacaoEditada.tipoContrato,
                publicacaoEditada.dtExpiracao
            )
        }.getOrElse {
            throw CreateInternalServerError("Falha ao deletar a publicação!!")
        }
    }

    private fun mapearPublicacoes(publicacoesEntity: List<PublicacaoEntity>): List<Publicacao> {
        return publicacoesEntity.map { entity ->
            val nomeEmpresa = empresaDao.findNameByEmpresaId(entity.empresaId!!)

            Publicacao(
                publicacaoId = entity.publicacaoId,
                empresaId = entity.empresaId,
                nomeEmpresa = nomeEmpresa,
                titulo = entity.titulo,
                descricao = entity.descricao,
                tipoContrato = entity.tipoContrato,
                dtExpiracao = entity.dtExpiracao,
                dtPublicacao = entity.dtPublicacao
            )
        }
    }

    private fun buscarEmpresaPorEmail(email: String) =
        empresaDao.findByEmail(email)
            .orElseThrow { EmpresaNotFoundException("Empresa não encontrada!") }

    private fun buscarPublicacaoPorId(id: Long) =
        dao.findById(id)
            .orElseThrow { PublicacaoNotFoundException("Publicação não foi encontrada") }

    private fun listarPublicacoes(page: Int, size: Int): List<Publicacao> {
        val offset = (page - 1) * size

        val listaDeUsuariosEntity = dao.dadosPaginados(offset, size)

        return mapearPublicacoes(listaDeUsuariosEntity)
    }
}