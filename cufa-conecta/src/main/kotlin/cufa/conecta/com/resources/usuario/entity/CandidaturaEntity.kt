package cufa.conecta.com.resources.usuario.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDate

@Entity(name = "candidatura")
data class CandidaturaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_candidatura")
    val id: Long? = null,

    @Column(name = "id_usuario")
    val usuarioId: Long,

    @Column(name = "id_publicacao")
    val publicacaoId: Long,

    @Column(name = "id_empresa")
    val empresaId: Long,

    @Column(name = "dt_candidatura")
    val candidatura: LocalDate,
)