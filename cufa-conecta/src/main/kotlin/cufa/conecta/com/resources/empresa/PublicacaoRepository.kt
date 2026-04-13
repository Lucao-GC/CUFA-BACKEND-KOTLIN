package cufa.conecta.com.resources.empresa

import cufa.conecta.com.model.data.empresa.Publicacao
import cufa.conecta.com.model.data.result.PublicacaoResult

interface PublicacaoRepository {
    fun criar(data: Publicacao, email: String)
    fun buscarTodas(page: Int, size: Int): PublicacaoResult
    fun buscarPublicacoesPorEmpresaEmail(data: String): List<Publicacao>
    fun findById(id: Long): Publicacao
    fun delete(id: Long, email: String)
    fun atualizar(data: Publicacao, email: String)
}