package cufa.conecta.com.application.dto.response.usuario

import cufa.conecta.com.model.data.result.UsuarioResult

data class UsuarioResponseDto(
    val nome: String?,
    val email: String,
    val cpf: String?,
    val telefone: String?,
    val escolaridade: String?,
    val idade: Int?,
    val estadoCivil: String?,
    val estado: String?,
    val cidade: String?,
    val biografia: String?,
    val curriculoUrl: String?
) {
    companion object {
        fun of(usuarioResult: UsuarioResult) = UsuarioResponseDto(
            nome = usuarioResult.nome,
            email = usuarioResult.email,
            cpf = usuarioResult.cpf,
            telefone = usuarioResult.telefone,
            escolaridade = usuarioResult.escolaridade,
            idade = usuarioResult.idade,
            estadoCivil = usuarioResult.estadoCivil,
            estado = usuarioResult.estado,
            cidade = usuarioResult.cidade,
            biografia = usuarioResult.biografia,
            curriculoUrl = usuarioResult.curriculoUrl
        )
    }
}