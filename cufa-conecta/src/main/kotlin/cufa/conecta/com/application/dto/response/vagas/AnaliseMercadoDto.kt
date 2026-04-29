package cufa.conecta.com.application.dto.response.vagas

data class AnaliseMercadoDto(
    val resumo: String,
    val ocupacoesEmAlta: List<OcupacaoEmAltaDto>
) {
    data class OcupacaoEmAltaDto(
        val ocupacao: String,
        val quantidadeVagas: Int,
        val relacaoComPerfil: String
    )
}