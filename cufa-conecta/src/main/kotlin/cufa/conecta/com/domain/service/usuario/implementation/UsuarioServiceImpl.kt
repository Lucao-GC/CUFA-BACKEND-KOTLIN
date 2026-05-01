package cufa.conecta.com.domain.service.usuario.implementation

import cufa.conecta.com.application.dto.request.usuario.UsuarioPerfilPatchDto
import cufa.conecta.com.application.dto.response.usuario.UsuarioTokenDto
import cufa.conecta.com.config.UsuarioAutenticadoHelper
import cufa.conecta.com.domain.service.usuario.UsuarioService
import cufa.conecta.com.model.data.Login
import cufa.conecta.com.model.data.Usuario
import cufa.conecta.com.model.data.result.UsuarioResult
import cufa.conecta.com.resources.usuario.UsuarioRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UsuarioServiceImpl(
    private val repository: UsuarioRepository
): UsuarioService {
    override fun cadastrarUsuario(data: Usuario) = repository.cadastrarUsuario(data)

    override fun autenticar(data: Login): UsuarioTokenDto = repository.autenticar(data)

    @Cacheable(value = ["usuario_dados"], key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    override fun mostrarDados(): UsuarioResult {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        return repository.mostrarDados(email)
    }
    @CacheEvict(value = ["usuario_dados"], key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    override fun atualizar(data: Usuario) {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        repository.atualizar(data, email)
    }

    @CacheEvict(value = ["usuario_dados"], key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    override fun atualizarCurriculoUrl(curriculoUrl: String?) {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        repository.atualizarCurriculoUrl(email, curriculoUrl)
    }

    @Transactional
    @CacheEvict(value = ["usuario_dados"], key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()", beforeInvocation = true)
    override fun patchPerfil(dto: UsuarioPerfilPatchDto): UsuarioResult {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        repository.patchPerfil(email, dto)
        return repository.mostrarDados(email)
    }

    @Transactional
    @CacheEvict(value = ["usuario_dados"], key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()", beforeInvocation = true)
    override fun atualizarFotoUrl(url: String): UsuarioResult {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        repository.atualizarFotoUrl(email, url)
        return repository.mostrarDados(email)
    }
}
