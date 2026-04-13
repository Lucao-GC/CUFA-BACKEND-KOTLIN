package cufa.conecta.com.domain.service.empresa

import cufa.conecta.com.application.dto.response.empresa.EmpresaTokenDto
import cufa.conecta.com.model.data.empresa.Biografia
import cufa.conecta.com.model.data.empresa.Empresa
import cufa.conecta.com.model.data.Login
import cufa.conecta.com.model.data.result.EmpresaResult
import cufa.conecta.com.model.data.usuario.Localizacao

interface EmpresaService {
    fun cadastrarEmpresa(data: Empresa)
    fun autenticar(dadosLogin: Login): EmpresaTokenDto
    fun listarTodos(): List<EmpresaResult>
    fun mostrarDados(): EmpresaResult
    fun atualizarDados(data: Empresa)
    fun atualizarBiografia(texto: Biografia)
    fun atualizarLocalizacao(data: Localizacao)
}