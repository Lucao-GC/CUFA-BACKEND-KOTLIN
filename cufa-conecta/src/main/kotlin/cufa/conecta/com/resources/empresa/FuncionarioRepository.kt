package cufa.conecta.com.resources.empresa

import cufa.conecta.com.model.data.empresa.Funcionario

interface FuncionarioRepository {
    fun criarFuncionario(data: Funcionario, empresaEmail: String)
    fun existsByEmail(email: String): Boolean
    fun buscarFuncionarios(email: String): List<Funcionario>
    fun mostrarDados(id: Long): Funcionario
    fun delete(id: Long)
}