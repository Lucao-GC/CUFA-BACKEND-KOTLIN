package cufa.conecta.com.domain.service.usuario.implementation

import cufa.conecta.com.config.UsuarioAutenticadoHelper
import cufa.conecta.com.domain.service.usuario.ExperienciaService
import cufa.conecta.com.model.data.Experiencia
import cufa.conecta.com.resources.usuario.ExperienciaRepository
import org.springframework.stereotype.Service

@Service
class ExperienciaServiceImpl(
    private val repository: ExperienciaRepository
): ExperienciaService {
    override fun criarExperiencia(data: Experiencia) {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        repository.criarExperiencia(data, email)
    }

    override fun listarPorUsuario(): List<Experiencia> {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        return repository.listarPorUsuario(email)
    }

    override fun atualizar(data: Experiencia) {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        repository.atualizar(data, email)
    }

    override fun deletarExperiencia(id: Long) {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        repository.deletarExperiencia(id, email)
    }
}