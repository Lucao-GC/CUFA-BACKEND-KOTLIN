package cufa.conecta.com.domain.service.empresa

import cufa.conecta.com.model.data.empresa.Funcionario

interface FuncionarioService {
    fun criarFuncionario(data: Funcionario)
    fun existsByEmail(email: String): Boolean
    fun buscarFuncionarios(): List<Funcionario>
    fun mostrarDados(id: Long): Funcionario
    fun deletar(id: Long)
}