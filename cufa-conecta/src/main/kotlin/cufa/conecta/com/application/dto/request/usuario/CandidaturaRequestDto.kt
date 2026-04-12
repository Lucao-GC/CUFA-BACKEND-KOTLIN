package cufa.conecta.com.application.dto.request.usuario

import cufa.conecta.com.model.data.Candidatura
import jakarta.validation.constraints.NotNull

data class CandidaturaRequestDto(
    @field:NotNull(message = "O id da publicação não pode ser nulo")
    val publicacaoId: Long,
    @field:NotNull(message = "O id da empresa não pode ser nulo")
    val empresaId: Long
) {
    fun toModel() = Candidatura(
        publicacaoId = publicacaoId,
        empresaId = empresaId
    )
}