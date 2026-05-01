package cufa.conecta.com.application.dto.response.empresa

import cufa.conecta.com.model.data.Publicacao
import java.time.LocalDateTime

data class PublicacaoResponseDto(
    val publicacaoId: Long,
    val titulo: String,
    val descricao: String,
    val tipoContrato: String,
    val dtExpiracao: LocalDateTime,
    val dtPublicacao: LocalDateTime,
    val nomeEmpresa: String,
) {
    companion object {
        private val SEM_DATA = LocalDateTime.of(1970, 1, 1, 0, 0)

        fun listOf(listaDePublicacoes: List<Publicacao>): List<PublicacaoResponseDto> {
            return listaDePublicacoes.map { data ->
                PublicacaoResponseDto(
                    publicacaoId = data.publicacaoId ?: 0L,
                    nomeEmpresa = data.nomeEmpresa ?: "",
                    titulo = data.titulo ?: "",
                    descricao = data.descricao ?: "",
                    tipoContrato = data.tipoContrato ?: "",
                    dtExpiracao = data.dtExpiracao ?: data.dtPublicacao ?: SEM_DATA,
                    dtPublicacao = data.dtPublicacao ?: SEM_DATA,
                )
            }
        }
    }
}