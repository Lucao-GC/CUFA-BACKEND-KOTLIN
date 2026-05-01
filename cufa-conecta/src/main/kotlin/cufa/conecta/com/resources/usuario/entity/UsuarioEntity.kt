package cufa.conecta.com.resources.usuario.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDate

@Entity(name = "cadastro_usuario")
data class UsuarioEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    val id: Long? = null,
    val nome: String ?= null,
    val email: String ?= null,
    val senha: String ?= null,
    val cpf: String ?= null,
    val telefone: String ?= null,
    val escolaridade: String ?= null,
    @Column(name = "dt_nascimento")
    val dtNascimento: LocalDate ?= null,
    @Column(name = "estado_civil")
    val estadoCivil: String ?= null,
    val estado: String ?= null,
    val cidade: String ?= null,
    val biografia: String ?= null,
    @Column(name = "curriculo_url")
    val curriculoUrl: String ?= null,
    @Column(name = "foto_url")
    val fotoUrl: String? = null,
)