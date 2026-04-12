package cufa.conecta.com.application.dto.request.empresa

import cufa.conecta.com.model.data.Publicacao
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class PublicacaoRequestDto(
    @field:NotBlank(message = "O campo do titulo não pode ser nulo, vazio ou branco")
    val titulo: String,

    @field:NotBlank(message = "O campo da descrição não pode ser nul, vazio ou branco")
    val descricao: String,

    @field:NotBlank(message = "O cargo não pode ser nulo, vazio ou branco")
    val tipoContrato: String,

    @field:NotBlank(message = "O cargo não pode ser nulo, vazio ou branco")
    val dtExpiracao: LocalDateTime
) {
    fun toModel() = Publicacao(
        titulo = titulo,
        descricao = descricao,
        tipoContrato = tipoContrato,
        dtExpiracao = dtExpiracao
    )

    fun toUpdateModel(id: Long) = Publicacao(
    publicacaoId = id,
    titulo = titulo,
    descricao = descricao,
    tipoContrato = tipoContrato,
    dtExpiracao = dtExpiracao
    )
}
