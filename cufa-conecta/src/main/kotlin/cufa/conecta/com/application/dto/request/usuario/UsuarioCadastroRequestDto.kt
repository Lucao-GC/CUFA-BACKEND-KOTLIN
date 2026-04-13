package cufa.conecta.com.application.dto.request.usuario

import cufa.conecta.com.model.data.usuario.Usuario
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UsuarioCadastroRequestDto(
    @field:NotBlank(message = "O campo nome não pode ser nulo, vazio ou branco")
    val nome: String,
    @field:Email(message = "email inválido")
    @field:NotBlank(message = "O campo email não pode ser nulo, vazio ou branco")
    val email: String,
    @field:Size(min = 8,max = 30, message = "A senha deve conter entre 8 e 30 caracteres")
    @field:NotBlank(message = "O campo cargo não pode ser nulo, vazio ou branco")
    val senha: String
) {
    fun toModel() = Usuario(
        nome = nome,
        email = email,
        senha = senha
    )
}