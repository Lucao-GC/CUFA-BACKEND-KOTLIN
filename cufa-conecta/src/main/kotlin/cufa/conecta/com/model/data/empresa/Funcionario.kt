package cufa.conecta.com.model.data.empresa

import cufa.conecta.com.domain.enum.Cargo

data class Funcionario (
    val id: Long?= null,
    val empresaId: Long ?= null,
    val nome: String,
    val email: String,
    val senha: String?= null,
    val cargo: Cargo
)