package cufa.conecta.com.application.dto.request.empresa

import cufa.conecta.com.domain.enum.Cargo
import cufa.conecta.com.model.data.empresa.Funcionario
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class FuncionarioRequestDto(
    @field:NotBlank(message = "O campo do nome não pode ser nulo, vazio ou branco")
    val nome: String,

    @field:Email
    @field:NotBlank(message = "O campo do email não pode ser nulo, vazio ou branco")
    val email: String,

    @field:Size(min = 8,max = 30, message = "A senha deve conter entre 8 e 30 caracteres")
    @field:NotBlank(message = "O campo da senha não pode ser nulo, vazio ou branco")
    val senha: String,

    @field:NotBlank(message = "O campo do cargo não pode ser nulo, vazio ou branco")
    val cargo: String
) {
    fun toModel() = Funcionario(
        nome = nome,
        email = email,
        senha = senha,
        cargo = Cargo.fromString(cargo.uppercase())
    )
}