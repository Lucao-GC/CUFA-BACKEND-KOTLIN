package cufa.conecta.com.resources.usuario.impl

import cufa.conecta.com.application.exception.CreateInternalServerError
import cufa.conecta.com.application.exception.PageNotFoundException
import cufa.conecta.com.application.exception.PublicacaoNotFoundException
import cufa.conecta.com.application.exception.UsuarioNotFoundException
import cufa.conecta.com.model.data.Candidato
import cufa.conecta.com.model.data.Candidatura
import cufa.conecta.com.model.data.Experiencia
import cufa.conecta.com.model.data.Publicacao
import cufa.conecta.com.model.data.result.CandidaturaResult
import cufa.conecta.com.resources.empresa.dao.EmpresaDao
import cufa.conecta.com.resources.empresa.dao.PublicacaoDao
import cufa.conecta.com.resources.empresa.entity.EmpresaEntity
import cufa.conecta.com.resources.empresa.entity.PublicacaoEntity
import cufa.conecta.com.resources.empresa.exception.EmpresaNotFoundException
import cufa.conecta.com.resources.usuario.CandidaturaRepository
import cufa.conecta.com.resources.usuario.dao.CandidaturaDao
import cufa.conecta.com.resources.usuario.dao.ExperienciaDao
import cufa.conecta.com.resources.usuario.dao.UsuarioDao
import cufa.conecta.com.resources.usuario.entity.CandidaturaEntity
import cufa.conecta.com.resources.usuario.entity.UsuarioEntity
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.Period
import kotlin.math.ceil

@Repository
class CandidaturaRepositoryImpl(
    private val dao: CandidaturaDao,
    private val usuarioDao: UsuarioDao,
    private val publicacaoDao: PublicacaoDao,
    private val empresaDao: EmpresaDao,
    private val experienciaDao: ExperienciaDao
): CandidaturaRepository {

    override fun criarCandidatura(data: Candidatura, email: String) {
        val usuario = buscarUsuarioPeloEmail(email)
        val publicacao = buscarPublicacaoPeloId(data.publicacaoId)
        val empresa = buscarEmpresaPeloId(data.empresaId)
        
        val candidaturaEntity = CandidaturaEntity(
            usuarioId = usuario.id!!,
            publicacaoId = publicacao.publicacaoId!!,
            empresaId = empresa.idEmpresa!!,
            candidatura = LocalDate.now()
        )

        runCatching {
            dao.save(candidaturaEntity)
        }.getOrElse {
            throw CreateInternalServerError("Falha ao criar a candidatura!!")
        }
    }

    override fun listarCandidatosPorVaga(vagaId: Long, page: Int, size: Int, email: String): CandidaturaResult {
        val empresa = buscarEmpresaPeloEmail(email)
        val totalOfUsers = dao.countByPublicacaoIdAndEmpresaId(vagaId, empresa.idEmpresa!!)

        val totalOfPages = if (totalOfUsers == 0L) 1 else ceil(totalOfUsers.toDouble() / size).toInt()

        if (page < 1 || page > totalOfPages)
            throw PageNotFoundException("A página $page não foi encontrada")

        val candidatos = listarCandidatosPorPublicacao(vagaId, page, size, empresa.idEmpresa!!)
        val publicacao = buscarPublicacaoPeloId(vagaId)

        val nomeEmpresa = empresaDao.findNameByEmpresaId(publicacao.empresaId!!)

        val vaga = CandidaturaResult(
            titulo = publicacao.titulo,
            nomeEmpresa = nomeEmpresa,
            tipoContrato = publicacao.tipoContrato,
            dtPublicacao = publicacao.dtPublicacao!!,
            dtExpiracao = publicacao.dtExpiracao,
            qtdCandidatos = candidatos.size,
            candidatos = candidatos,
            paginaAtual = page,
            totalDePaginas = totalOfPages,
            totalDeCandidatos = totalOfUsers
        )

        return vaga
    }

    override fun verificarCandidaturaExistente(vagaId: Long, email: String): Boolean {
        val usuario = buscarUsuarioPeloEmail(email)

        return dao.existsByUsuarioIdAndPublicacaoId(usuario.id!!, vagaId)
    }

    override fun listarPublicacoesCandidatadasPorUsuario(email: String): List<Publicacao> {
        val usuario: UsuarioEntity = buscarUsuarioPeloEmail(email)
        val listaDeCandidaturasEntity: List<CandidaturaEntity> = dao.findByUsuarioId(usuario.id!!)

        return mapearPublicacoes(listaDeCandidaturasEntity)
    }

    private fun buscarUsuarioPeloEmail(email: String): UsuarioEntity =
        usuarioDao.findByEmail(email)
            .orElseThrow { UsuarioNotFoundException("O usuário com o email $email não existe") }

    private fun buscarEmpresaPeloEmail(email: String): EmpresaEntity =
        empresaDao.findByEmail(email)
            .orElseThrow { EmpresaNotFoundException("A empresa com o email $email não existe") }

    private fun buscarPublicacaoPeloId(id: Long): PublicacaoEntity =
        publicacaoDao.findById(id)
            .orElseThrow { PublicacaoNotFoundException("A publicação com o ID:$id não existe") }

    private fun buscarEmpresaPeloId(id: Long): EmpresaEntity =
        empresaDao.findById(id)
            .orElseThrow { EmpresaNotFoundException("A empresa com o ID:$id não existe") }

    private fun listarCandidatosPorPublicacao(publicacaoId: Long, page: Int, size: Int, empresaId: Long): List<Candidato> {
        val offset = (page - 1) * size

        val listaDeUsuariosEntity = usuarioDao.dadosPaginados(publicacaoId, empresaId, size, offset)

        return mapearUsuarios(listaDeUsuariosEntity)
    }

    private fun buscarExperienciasDoUsuario(id: Long): List<Experiencia>? {
        val experiencias = experienciaDao.findByUsuarioId(id)
            .map { exp ->
                Experiencia(
                    cargo = exp.cargo,
                    empresa = exp.empresa,
                    dtInicio = exp.dtInicio,
                    dtFim = exp.dtFim
                )
            }
        
        return experiencias
    }

    private fun mapearUsuarios(usuariosEntity: List<UsuarioEntity>): List<Candidato> {
        return usuariosEntity.map { usuarioEntity ->
            val experiencias = buscarExperienciasDoUsuario(usuarioEntity.id!!) ?: emptyList()

            val dtNascUsuario = usuarioEntity.dtNascimento
            val idade = definirIdadeDoUsuario(dtNascUsuario!!)

            Candidato(
                id = usuarioEntity.id!!,
                nome = usuarioEntity.nome!!,
                idade = idade,
                biografia = usuarioEntity.biografia!!,
                email = usuarioEntity.email!!,
                telefone = usuarioEntity.telefone!!,
                curriculoUrl = usuarioEntity.curriculoUrl,
                experiencias = experiencias
            )
        }
    }

    private fun mapearPublicacoes(candidaturasEntity: List<CandidaturaEntity>): List<Publicacao> {
        return candidaturasEntity.map { candidaturaEntity ->
            val publicacaoEntity = buscarPublicacaoPeloId(candidaturaEntity.publicacaoId)
            val nomeEmpresa = empresaDao.findNameByEmpresaId(publicacaoEntity.empresaId!!
            )

            Publicacao(
                publicacaoId = publicacaoEntity.publicacaoId,
                empresaId = publicacaoEntity.empresaId,
                nomeEmpresa = nomeEmpresa,
                titulo = publicacaoEntity.titulo,
                descricao = publicacaoEntity.descricao,
                tipoContrato = publicacaoEntity.tipoContrato,
                dtExpiracao = publicacaoEntity.dtExpiracao,
                dtPublicacao = publicacaoEntity.dtPublicacao
            )
        }
    }

    private fun definirIdadeDoUsuario(dtNasc: LocalDate) = Period.between(dtNasc, LocalDate.now()).years
}
