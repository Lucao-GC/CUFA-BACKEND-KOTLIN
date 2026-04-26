package cufa.conecta.com.resources.empresa.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import kotlin.jvm.Transient

@Entity(name = "publicacao")
data class PublicacaoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_publicacao")
    val publicacaoId: Long ?= null,

    @Column(name = "id_empresa")
    val empresaId: Long? = null,
    val titulo: String,
    val descricao: String,
    @Column(name= "tipo_contrato")
    val tipoContrato: String,
    @Column(name= "dt_expiracao")
    val dtExpiracao: LocalDateTime,
    @Column(name= "dt_publicacao")
    val dtPublicacao: LocalDateTime? = null,

    @Transient
    val distancia: Double? = null,

    @Transient
    val nomeEmpresa: String? = null
)