package cufa.conecta.com.domain.service.usuario

import cufa.conecta.com.model.data.Candidatura
import cufa.conecta.com.model.data.empresa.Publicacao
import cufa.conecta.com.model.data.result.CandidaturaResult

interface CandidaturaService {
    fun criarCandidatura(data: Candidatura)
    fun listarCandidatosPorVaga(vagaId: Long, page: Int, size: Int): CandidaturaResult
    fun verificarCandidaturaExistente(vagaId: Long): Boolean
    fun listarPublicacoesCandidatadasPorUsuario(): List<Publicacao>
}