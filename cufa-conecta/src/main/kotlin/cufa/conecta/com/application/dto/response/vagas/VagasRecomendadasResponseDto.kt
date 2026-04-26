package cufa.conecta.com.application.dto.response.vagas

data class VagasRecomendadasResponseDto(
    val recomendacoes: List<RecomendacaoDto>,
    val totalVagas: Int,
    val raioKm: Int
)