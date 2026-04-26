package cufa.conecta.com.application.dto.response.vagas

data class RecomendacaoDto(
    val id: Long,
    val nomeEmpresa: String,
    val idEmpresa: Long,
    val titulo: String,
    val tipoContrato: String
)