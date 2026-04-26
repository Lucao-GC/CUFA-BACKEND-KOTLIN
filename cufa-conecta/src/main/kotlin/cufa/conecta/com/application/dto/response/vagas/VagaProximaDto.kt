package cufa.conecta.com.application.dto.response.vagas

import java.time.LocalDateTime

data class VagaProximaDto(
    val publicacaoId: Long,
    val empresaId: Long,
    val nomeEmpresa: String,
    val titulo: String,
    val tipoContrato: String,
    val dtExpiracao: LocalDateTime,
    val distancia: Double
)