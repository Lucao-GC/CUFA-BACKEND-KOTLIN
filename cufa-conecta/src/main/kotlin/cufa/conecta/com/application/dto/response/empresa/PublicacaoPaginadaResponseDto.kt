package cufa.conecta.com.application.dto.response.empresa

import cufa.conecta.com.model.data.empresa.Publicacao
import cufa.conecta.com.model.data.result.PublicacaoResult

data class PublicacaoPaginadaResponseDto(
    val paginaAtual: Int,
    val totalDePaginas: Int,
    val totalDePublicacoes: Long,
    val publicacoes: List<Publicacao>
) {
    companion object {
        fun listOfResult(publicacaoPaginada: PublicacaoResult): PublicacaoPaginadaResponseDto {
            return PublicacaoPaginadaResponseDto(
                paginaAtual = publicacaoPaginada.paginaAtual,
                totalDePaginas = publicacaoPaginada.totalDePaginas,
                totalDePublicacoes = publicacaoPaginada.totalDePublicacoes,
                publicacoes = publicacaoPaginada.publicacoes
            )
        }
    }
}