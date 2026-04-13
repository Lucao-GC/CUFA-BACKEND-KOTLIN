package cufa.conecta.com.application.dto.request.empresa

import cufa.conecta.com.model.data.empresa.Empresa
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.br.CNPJ

data class EmpresaRequestDto(
    @field:NotBlank(message = "O campo do nome não pode ser nulo, vazio ou branco")
    val nome: String,

    @field:Email(message = "O email inserido é inválido")
    @field:NotBlank(message = "O campo do email não pode ser nulo, vazio ou branco")
    val email: String,

    @field:Size(min = 8,max = 30, message = "A senha deve conter entre 8 e 30 caracteres")
    @field:NotBlank(message = "O campo da senha não pode ser nulo, vazio ou branco")
    val senha: String,

    @field:Size(min = 8,max = 8, message = "O CEP deve conter 8 dígitos")
    @field:Digits(message = "O CEP deve conter apenas números", integer = 8, fraction = 0)
    @field:NotBlank(message = "O campo do CEP não pode ser nulo, vazio ou branco")
    val cep: String,

    @field:NotBlank(message = "O campo do endereço não pode ser nulo, vazio ou branco")
    val endereco: String,

    @field:NotBlank(message = "O campo do número não pode ser nulo, vazio ou branco")
    val numero: String,

    @field:CNPJ(message = "CNPJ inválido")
    @field:Size(message = "O CNPJ deve conter 14 dígitos", min = 14, max = 14)
    @field:Digits(message = "O CNPJ deve conter apenas números", integer = 14, fraction = 0)
    @field:NotBlank(message = "O campo do CNPJ não pode ser nulo, vazio ou branco")
    val cnpj: String,

    @field:NotBlank(message = "O campo da área não pode ser nulo, vazio ou branco")
    val area: String
) {
    fun toModel(): Empresa =
        Empresa(
            nome = nome,
            email = email,
            senha = senha,
            cep = cep,
            endereco = endereco,
            numero = numero,
            cnpj = cnpj,
            area = area
        )
}