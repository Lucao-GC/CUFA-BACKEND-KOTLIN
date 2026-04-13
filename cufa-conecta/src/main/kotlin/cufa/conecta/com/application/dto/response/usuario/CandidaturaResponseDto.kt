package cufa.conecta.com.application.dto.response.usuario

import cufa.conecta.com.model.data.usuario.Candidato
import cufa.conecta.com.model.data.result.CandidaturaResult
import java.time.LocalDateTime

data class CandidaturaResponseDto(
    val titulo: String,
    val nomeEmpresa: String,
    val tipoContrato: String,
    val dtPublicacao: LocalDateTime,
    val dtExpiracao: LocalDateTime,
    val qtdCandidatos: Int,
    val candidatos: List<Candidato>,
) {
    companion object {
        fun of(data: CandidaturaResult) = CandidaturaResponseDto(
            titulo = data.titulo,
            nomeEmpresa = data.nomeEmpresa,
            tipoContrato = data.tipoContrato,
            dtPublicacao = data.dtPublicacao,
            dtExpiracao = data.dtExpiracao,
            qtdCandidatos = data.qtdCandidatos,
            candidatos = data.candidatos
        )
    }
}