package cufa.conecta.com.model.data.result

import java.time.LocalDate

data class UsuarioResult (
    val nome: String?,
    val email: String,
    val cpf: String?,
    val telefone: String?,
    val escolaridade: String?,
    val dtNascimento: LocalDate?,
    val idade: Int?,
    val estadoCivil: String?,
    val estado: String?,
    val cidade: String?,
    val biografia: String?,
    val curriculoUrl: String?,
)