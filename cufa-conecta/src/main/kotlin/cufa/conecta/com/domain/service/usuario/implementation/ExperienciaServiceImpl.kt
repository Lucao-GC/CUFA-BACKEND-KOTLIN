package cufa.conecta.com.domain.service.usuario.implementation

import cufa.conecta.com.domain.service.usuario.ExperienciaService
import cufa.conecta.com.model.data.usuario.Experiencia
import cufa.conecta.com.resources.usuario.ExperienciaRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class ExperienciaServiceImpl(
    private val repository: ExperienciaRepository
): ExperienciaService {
    override fun criarExperiencia(data: Experiencia) {
        val email = autenticar()?.name

        repository.criarExperiencia(data, email!!)
    }

    override fun listarPorUsuario(): List<Experiencia> {
        val email = autenticar()?.name

        return repository.listarPorUsuario(email!!)
    }

    override fun atualizar(data: Experiencia) {
        val email = autenticar()?.name

        repository.atualizar(data, email!!)
    }

    override fun deletarExperiencia(id: Long) {
        val email = autenticar()?.name

        repository.deletarExperiencia(id, email!!)
    }

    private fun autenticar() = SecurityContextHolder.getContext().authentication
}