package cufa.conecta.com.resources.empresa.entity

import cufa.conecta.com.domain.enum.Cargo
import jakarta.persistence.*

@Entity(name = "funcionarios")
data class FuncionarioEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_funcionario")
    val funcionarioId: Long? = null,

    @Column(name = "id_empresa")
    val empresaId: Long,
    val nome: String,
    val email: String,
    val senha: String,
    @Enumerated(EnumType.STRING)
    val cargo: Cargo,
)