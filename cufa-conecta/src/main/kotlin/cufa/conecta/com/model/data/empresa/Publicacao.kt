package cufa.conecta.com.model.data.empresa

import java.time.LocalDateTime

data class Publicacao(
    val publicacaoId: Long? = null,
    val empresaId: Long? = null,
    val nomeEmpresa: String? = null,
    val titulo: String?,
    val descricao: String?,
    val tipoContrato: String?,
    val dtExpiracao: LocalDateTime?,
    val dtPublicacao: LocalDateTime? = null,
)