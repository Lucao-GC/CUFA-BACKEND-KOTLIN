package cufa.conecta.com.model.data.result

import cufa.conecta.com.model.data.empresa.Publicacao

data class PublicacaoResult(
    val paginaAtual: Int,
    val totalDePaginas: Int,
    val totalDePublicacoes: Long,
    val publicacoes: List<Publicacao>
)