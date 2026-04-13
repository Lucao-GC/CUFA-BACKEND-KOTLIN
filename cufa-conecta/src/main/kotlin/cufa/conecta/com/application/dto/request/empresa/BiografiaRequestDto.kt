package cufa.conecta.com.application.dto.request.empresa

import cufa.conecta.com.model.data.empresa.Biografia
import jakarta.validation.constraints.NotBlank

data class BiografiaRequestDto(
    @field:NotBlank(message = "A biografia não pode ser nulo, vazio ou branco")
    val biografia: String
) {
    fun toModel() = Biografia(texto = biografia)
}