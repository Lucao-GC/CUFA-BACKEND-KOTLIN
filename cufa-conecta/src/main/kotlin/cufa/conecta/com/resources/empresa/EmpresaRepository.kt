package cufa.conecta.com.resources.empresa

import cufa.conecta.com.application.dto.response.empresa.EmpresaTokenDto
import cufa.conecta.com.model.data.empresa.Biografia
import cufa.conecta.com.model.data.empresa.Empresa
import cufa.conecta.com.model.data.Login
import cufa.conecta.com.model.data.result.EmpresaResult
import cufa.conecta.com.model.data.usuario.Localizacao

interface EmpresaRepository {
    fun cadastrarEmpresa(data: Empresa)
    fun autenticar(dto: Login): EmpresaTokenDto
    fun listarTodos(): List<EmpresaResult>
    fun mostrarDados(email: String): EmpresaResult
    fun atualizarDados(data: Empresa, email: String)
    fun atualizarBiografia(data: Biografia, email: String)
    fun atualizarLocalizacao(email: String, data: Localizacao)
}