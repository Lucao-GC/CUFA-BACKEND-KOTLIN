package cufa.conecta.com.resources.usuario

import cufa.conecta.com.model.data.Candidatura
import cufa.conecta.com.model.data.empresa.Publicacao
import cufa.conecta.com.model.data.result.CandidaturaResult

interface CandidaturaRepository {
    fun criarCandidatura(data: Candidatura, email: String)
    fun listarCandidatosPorVaga(vagaId: Long, page: Int, size: Int, email: String): CandidaturaResult
    fun verificarCandidaturaExistente(vagaId: Long, email: String): Boolean
    fun listarPublicacoesCandidatadasPorUsuario(email: String): List<Publicacao>
}