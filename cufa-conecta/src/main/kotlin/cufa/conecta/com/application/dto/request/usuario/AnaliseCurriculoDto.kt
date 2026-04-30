package cufa.conecta.com.application.dto.request.usuario

data class AnaliseCurriculoDto(
    val resumo: String,
    val pontosFortes: List<String>,
    val pontosMelhoria: List<String>,
    val sugestoes: List<String>
)