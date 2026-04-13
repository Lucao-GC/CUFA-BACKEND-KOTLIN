package cufa.conecta.com.application.dto.request

import cufa.conecta.com.model.data.usuario.Localizacao
import jakarta.validation.constraints.NotBlank

data class AtualizarLocalizacaoDto(
    @field:NotBlank(message = "O campo latitude não pode ser nulo, vazio ou branco")
    val latitude: String,
    @field:NotBlank(message = "O campo longitude não pode ser nulo, vazio ou branco")
    val longitude: String
) {
    fun toModel() = Localizacao(
        latitude = latitude.toDouble(),
        longitude = longitude.toDouble()
    )
}