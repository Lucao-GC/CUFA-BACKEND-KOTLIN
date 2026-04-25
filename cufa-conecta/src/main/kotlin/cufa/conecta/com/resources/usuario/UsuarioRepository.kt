package cufa.conecta.com.resources.usuario

import cufa.conecta.com.application.dto.response.usuario.UsuarioTokenDto
import cufa.conecta.com.model.data.Login
import cufa.conecta.com.model.data.usuario.Usuario
import cufa.conecta.com.model.data.result.UsuarioResult
import cufa.conecta.com.model.data.usuario.Localizacao
import cufa.conecta.com.resources.empresa.entity.PublicacaoEntity
import cufa.conecta.com.resources.usuario.entity.UsuarioEntity

interface UsuarioRepository {
    fun obterUsuarioPorEmail(email: String): UsuarioEntity
    fun cadastrarUsuario(data: Usuario)
    fun autenticar(data: Login): UsuarioTokenDto
    fun mostrarDados(email: String): UsuarioResult
    fun atualizar(data: Usuario, email: String)
    fun atualizarCurriculoUrl(email: String, curriculoUrl: String?)
    fun atualizarLocalizacao(email: String, data: Localizacao)
    fun buscarVagasProximas(latitude: Double, longitude: Double): List<PublicacaoEntity>
}