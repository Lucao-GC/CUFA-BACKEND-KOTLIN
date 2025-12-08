package cufa.conecta.com.resources.usuario

import cufa.conecta.com.application.dto.response.usuario.UsuarioTokenDto
import cufa.conecta.com.model.data.Login
import cufa.conecta.com.model.data.Usuario
import cufa.conecta.com.model.data.result.UsuarioResult

interface UsuarioRepository {
    fun cadastrarUsuario(data: Usuario)
    fun autenticar(data: Login): UsuarioTokenDto
    fun mostrarDados(email: String): UsuarioResult
    fun atualizar(data: Usuario, email: String)
    fun atualizarCurriculoUrl(email: String, curriculoUrl: String?)
}