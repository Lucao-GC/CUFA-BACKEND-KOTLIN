package cufa.conecta.com.domain.service.empresa

import cufa.conecta.com.model.data.Publicacao
import cufa.conecta.com.model.data.result.PublicacaoResult

interface PublicacaoService {
    fun criar(data: Publicacao)
    fun buscarTodas(page: Int, size: Int): PublicacaoResult
    fun buscarPublicacoesDaEmpresa(): List<Publicacao>
    fun findById(id: Long): Publicacao
    fun editarPublicacao(data: Publicacao)
    fun delete(id: Long)
}