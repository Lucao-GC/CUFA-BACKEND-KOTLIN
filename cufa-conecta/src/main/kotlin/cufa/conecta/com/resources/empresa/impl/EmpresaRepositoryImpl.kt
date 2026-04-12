package cufa.conecta.com.resources.empresa.impl

import cufa.conecta.com.application.dto.response.empresa.EmpresaTokenDto
import cufa.conecta.com.application.exception.CreateInternalServerError
import cufa.conecta.com.config.GerenciadorTokenJwt
import cufa.conecta.com.model.data.Biografia
import cufa.conecta.com.model.data.Empresa
import cufa.conecta.com.model.data.Login
import cufa.conecta.com.model.data.result.EmpresaResult
import cufa.conecta.com.resources.empresa.EmpresaRepository
import cufa.conecta.com.resources.empresa.dao.EmpresaDao
import cufa.conecta.com.resources.empresa.entity.EmpresaEntity
import cufa.conecta.com.resources.empresa.exception.EmailExistenteException
import cufa.conecta.com.resources.empresa.exception.EmpresaNotFoundException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class EmpresaRepositoryImpl (
    private val dao: EmpresaDao,
    private val gerenciadorTokenJwt : GerenciadorTokenJwt,
    private val authenticationManager : AuthenticationManager,
    private val passwordEncoder: PasswordEncoder
): EmpresaRepository {

    override fun cadastrarEmpresa(data: Empresa) {
        val email = data.email!!
        validarEmailExistente(email)

        val senhaCriptografada = passwordEncoder.encode(data.senha)

        val empresaEntity = EmpresaEntity(
            nome = data.nome,
            email = email,
            senha = senhaCriptografada,
            cep = data.cep,
            numero = data.numero,
            endereco = data.endereco,
            cnpj = data.cnpj,
            area = data.area,
            dtCadastro = LocalDate.now()
        )

        runCatching {
            dao.save(empresaEntity)
        }.getOrElse {
            throw CreateInternalServerError("Falha ao cadastrar a empresa!!")
        }
    }

    override fun autenticar(dto: Login): EmpresaTokenDto {
        validarEmpresa(dto.email, dto.senha)

        val empresaAutenticada = buscarEmpresaPorEmail(dto.email)

        val dadosAutenticados = UsernamePasswordAuthenticationToken(
            empresaAutenticada.email,
            null,
            emptyList()
        )

        SecurityContextHolder.getContext().authentication = dadosAutenticados

        val tokenJwt = gerenciadorTokenJwt.generateToken(dadosAutenticados)

        return EmpresaTokenDto(
            empresaAutenticada.email,
            empresaAutenticada.nome,
            tokenJwt
        )
    }

    override fun listarTodos(): List<EmpresaResult> {
        val listaDeEmpresasEntity = dao.findAll()

        return mapearEmpresas(listaDeEmpresasEntity)
    }

    override fun mostrarDados(email: String): EmpresaResult {
        val empresaEntity = buscarEmpresaPorEmail(email)

        return mapearEmpresa(empresaEntity)
    }

    override fun atualizarDados(data: Empresa, email: String) {
        val empresaExistente = buscarEmpresaPorEmail(email)

        val empresaAtualizada = EmpresaEntity(
            idEmpresa = empresaExistente.idEmpresa,
            nome = data.nome,
            email = empresaExistente.email,
            senha = empresaExistente.senha,
            cep = data.cep,
            numero = data.numero,
            endereco = data.endereco,
            cnpj = data.cnpj,
            area = data.area
        )

        runCatching {
            dao.save(empresaAtualizada)
        }.getOrElse {
            throw CreateInternalServerError("Falha ao atualizar os dados da empresa: ${data.nome}!!")
        }
    }

    override fun atualizarBiografia(data: Biografia, email: String) {
        val empresa = buscarEmpresaPorEmail(email)

        runCatching {
            dao.atualizarBiografia(data.texto, empresa.idEmpresa!!)
        }.getOrElse {
            throw CreateInternalServerError("Falha ao tentar inserir a biografia!!")
        }
    }


    private fun validarEmpresa(email: String, senha: String) {
        val credentials: Authentication = UsernamePasswordAuthenticationToken(email, senha)

        val authResult = authenticationManager.authenticate(credentials)

        SecurityContextHolder.getContext().authentication = authResult
    }

    private fun buscarEmpresaPorEmail(email: String): EmpresaEntity =
        dao.findByEmail(email)
            .orElseThrow { EmpresaNotFoundException("Email do usuário não encontrado") }


    private fun validarEmailExistente(email: String) {
        if (dao.existsByEmail(email)) {
            throw EmailExistenteException("O email inserido já existe!!")
        }
    }

    private fun mapearEmpresa(entity: EmpresaEntity): EmpresaResult {
        val empresa = EmpresaResult(
            nome = entity.nome,
            email = entity.email,
            cep = entity.cep,
            endereco = entity.endereco,
            numero = entity.numero,
            cnpj = entity.cnpj,
            area = entity.area,
            biografia = entity.biografia
        )

        return empresa
    }

    private fun mapearEmpresas(empresasEntity: List<EmpresaEntity>): List<EmpresaResult> {
        return empresasEntity.map { entity ->
            EmpresaResult(
                nome = entity.nome,
                email = entity.email,
                cep = entity.cep,
                endereco = entity.endereco,
                numero = entity.numero,
                cnpj = entity.cnpj,
                area = entity.area,
                biografia = entity.biografia
            )
        }
    }
}
