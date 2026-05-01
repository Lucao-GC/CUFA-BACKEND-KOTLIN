package cufa.conecta.com.domain.service.empresa.implementation

import cufa.conecta.com.application.dto.response.empresa.EmpresaTokenDto
import cufa.conecta.com.config.UsuarioAutenticadoHelper
import cufa.conecta.com.domain.service.empresa.EmpresaService
import cufa.conecta.com.model.data.Biografia
import cufa.conecta.com.model.data.Empresa
import cufa.conecta.com.model.data.Login
import cufa.conecta.com.model.data.result.EmpresaResult
import cufa.conecta.com.resources.empresa.EmpresaRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class EmpresaServiceImpl(
    private val repository: EmpresaRepository
): EmpresaService {
    override fun cadastrarEmpresa(data: Empresa) = repository.cadastrarEmpresa(data)

    override fun autenticar(dadosLogin: Login): EmpresaTokenDto = repository.autenticar(dadosLogin)

    @Cacheable("empresas_todas")
    override fun listarTodos(): List<EmpresaResult> = repository.listarTodos()

    @Cacheable(value = ["empresa_dados"], key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    override fun mostrarDados(): EmpresaResult {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        return repository.mostrarDados(email)
    }

    @CacheEvict(value = ["empresa_dados"], key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    override fun atualizarDados(data: Empresa) {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        repository.atualizarDados(data, email)
    }

    @CacheEvict(value = ["empresa_dados"], key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    override fun atualizarBiografia(texto: Biografia) {
        val email = UsuarioAutenticadoHelper.emailObrigatorio()
        repository.atualizarBiografia(texto, email)
    }
}