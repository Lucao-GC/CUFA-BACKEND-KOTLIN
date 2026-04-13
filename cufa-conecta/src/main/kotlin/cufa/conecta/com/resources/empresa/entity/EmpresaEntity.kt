package cufa.conecta.com.resources.empresa.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity(name = "cadastro_empresa")
data class EmpresaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    val idEmpresa: Long ?= null,
    val nome: String ?= null,
    val email: String ?= null,
    val senha: String ?= null,
    val cep: String ?= null,
    val endereco: String ?= null,
    val numero: String ?= null,
    val cnpj: String ?= null,
    val area: String ?= null,
    val biografia: String ?= null,
    @Column(name = "dt_cadastro")
    val dtCadastro: LocalDate ?= null,
    val latitude: Double? = null,
    val longitude: Double? = null
)