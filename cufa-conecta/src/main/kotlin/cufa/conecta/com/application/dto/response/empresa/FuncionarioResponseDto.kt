package cufa.conecta.com.application.dto.response.empresa

import cufa.conecta.com.model.data.Funcionario

data class FuncionarioResponseDto(
    val id: Long,
    val nome: String,
    val email: String,
    val cargo: String
) {
    companion object {
        fun listOf(listaDeFuncionarios: List<Funcionario>): List<FuncionarioResponseDto> {
            return listaDeFuncionarios.map { data ->
                FuncionarioResponseDto(
                    id = data.id!!,
                    nome = data.nome,
                    email = data.email,
                    cargo = data.cargo.name
                )
            }
        }
    }
}