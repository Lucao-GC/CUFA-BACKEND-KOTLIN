package cufa.conecta.com.resources.usuario.impl

import cufa.conecta.com.application.exception.CreateInternalServerError
import cufa.conecta.com.application.exception.UsuarioNotFoundException
import cufa.conecta.com.model.data.Experiencia
import cufa.conecta.com.resources.usuario.ExperienciaRepository
import cufa.conecta.com.resources.usuario.dao.ExperienciaDao
import cufa.conecta.com.resources.usuario.dao.UsuarioDao
import cufa.conecta.com.resources.usuario.entity.ExperienciaEntity
import cufa.conecta.com.resources.usuario.entity.UsuarioEntity
import org.springframework.stereotype.Repository

@Repository
class ExperienciaRepositoryImpl(
    private val dao: ExperienciaDao,
    private val usuarioDao: UsuarioDao
): ExperienciaRepository {
    override fun criarExperiencia(data: Experiencia, email: String) {
        val usuario = buscarUsuarioPeloEmail(email)

        val experienciaEntity = ExperienciaEntity(
            usuarioId = usuario.id!!,
            cargo = data.cargo!!,
            empresa = data.empresa!!,
            dtInicio = data.dtInicio!!,
            dtFim = data.dtFim!!
        )

        runCatching {
            dao.save(experienciaEntity)
        }.getOrElse {
            throw CreateInternalServerError("Falha ao cadastrar a experiência!!")
        }
    }

    override fun listarPorUsuario(email: String): List<Experiencia> {
        val usuario = buscarUsuarioPeloEmail(email)

        val listaDeExperienciasEntity = dao.findByUsuarioId(usuario.id!!)

        val listaDeExperiencias = mutableListOf<Experiencia>()

        for (experienciaEntity in listaDeExperienciasEntity) {
            val experiencia = Experiencia(
                id = experienciaEntity.id!!,
                cargo = experienciaEntity.cargo,
                empresa = experienciaEntity.empresa,
                dtInicio = experienciaEntity.dtInicio,
                dtFim = experienciaEntity.dtFim
            )

            listaDeExperiencias.add(experiencia)
        }

        return listaDeExperiencias
    }

    override fun atualizar(data : Experiencia, email: String) {
        val usuario = buscarUsuarioPeloEmail(email)
        val experienciaAntiga = buscarExperienciaPeloId(data.id!!)

        val novaExperiencia = ExperienciaEntity(
            cargo = data.cargo ?: experienciaAntiga.cargo,
            empresa = data.empresa ?: experienciaAntiga.empresa,
            dtInicio = data.dtInicio ?: experienciaAntiga.dtInicio,
            dtFim = data.dtFim ?: experienciaAntiga.dtFim
        )

        dao.atualizarExperiencia(
            data.id,
            usuario.id!!,
            novaExperiencia.cargo,
            novaExperiencia.empresa,
            novaExperiencia.dtInicio,
            novaExperiencia.dtFim
        )
    }

    override fun deletarExperiencia(id: Long, email: String) {
        val usuario = buscarUsuarioPeloEmail(email)
        val experiencia = buscarExperienciaPeloId(id)

        dao.deletarExperiencia(experiencia.id!!, usuario.id!!)
    }

    private fun buscarExperienciaPeloId(id: Long): ExperienciaEntity =
        dao.findById(id)
            .orElseThrow { RuntimeException("Experiencia pelo ID: $id") }

    private fun buscarUsuarioPeloEmail(email: String): UsuarioEntity =
        usuarioDao.findByEmail(email)
            .orElseThrow { UsuarioNotFoundException("O usuário com o email $email não existe") }
}