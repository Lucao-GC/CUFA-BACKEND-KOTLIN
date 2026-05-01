package cufa.conecta.com.domain.service.usuario

import cufa.conecta.com.application.dto.request.usuario.UsuarioPerfilPatchDto
import cufa.conecta.com.application.dto.response.usuario.UsuarioTokenDto
import cufa.conecta.com.model.data.Login
import cufa.conecta.com.model.data.Usuario
import cufa.conecta.com.model.data.result.UsuarioResult

interface UsuarioService {
    fun cadastrarUsuario(data: Usuario)
    fun autenticar(data: Login): UsuarioTokenDto
    fun mostrarDados(): UsuarioResult
    fun atualizar(data: Usuario)
    fun atualizarCurriculoUrl(curriculoUrl: String?)
    fun patchPerfil(dto: UsuarioPerfilPatchDto): UsuarioResult
    fun atualizarFotoUrl(url: String): UsuarioResult
}