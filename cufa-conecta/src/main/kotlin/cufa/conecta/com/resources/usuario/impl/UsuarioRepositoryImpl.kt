package cufa.conecta.com.resources.usuario.impl

import cufa.conecta.com.application.dto.response.usuario.UsuarioTokenDto
import cufa.conecta.com.application.exception.CreateInternalServerError
import cufa.conecta.com.config.GerenciadorTokenJwt
import cufa.conecta.com.model.data.Login
import cufa.conecta.com.model.data.usuario.Usuario
import cufa.conecta.com.model.data.result.UsuarioResult
import cufa.conecta.com.model.data.usuario.Localizacao
import cufa.conecta.com.resources.empresa.exception.EmailExistenteException
import cufa.conecta.com.resources.empresa.exception.EmpresaNotFoundException
import cufa.conecta.com.resources.usuario.UsuarioRepository
import cufa.conecta.com.resources.usuario.dao.UsuarioDao
import cufa.conecta.com.resources.usuario.entity.UsuarioEntity
import cufa.conecta.com.resources.usuario.exception.AtualizarLocalizacaoException
import cufa.conecta.com.resources.usuario.exception.UpdateCurriculoException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.Period

@Repository
class UsuarioRepositoryImpl(
    private val dao: UsuarioDao,
    private val gerenciadorTokenJwt: GerenciadorTokenJwt,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder
): UsuarioRepository {
    override fun cadastrarUsuario(data: Usuario) {
        val email = data.email!!
        validarEmailExistente(email)

        val senhaCriptografada = passwordEncoder.encode(data.senha)

        val usuario = UsuarioEntity(
            nome = data.nome,
            email = email,
            senha = senhaCriptografada,
            cpf = data.cpf,
            telefone = data.telefone,
            escolaridade = data.escolaridade,
            dtNascimento = data.dtNascimento,
            estadoCivil = data.estadoCivil,
            estado = data.estado,
            cidade = data.cidade,
            biografia = data.biografia,
            curriculoUrl = data.curriculoUrl
        )

        runCatching {
            dao.save(usuario)
        }.getOrElse {
            throw CreateInternalServerError("Falha ao cadastrar o usuário ${data.nome}!!")
        }
    }

    override fun autenticar(data: Login): UsuarioTokenDto {
        validarUsuario(data.email, data.senha)

        val usuarioAutenticado = buscarUsuarioPorEmail(data.email)

        val authentication = UsernamePasswordAuthenticationToken(
            usuarioAutenticado.email,
            null,
            emptyList()
        )

        SecurityContextHolder.getContext().authentication = authentication

        val token = gerenciadorTokenJwt.generateToken(authentication)

        return UsuarioTokenDto(
            nome = usuarioAutenticado.nome!!,
            email = usuarioAutenticado.email!!,
            tokenJwt = token
        )
    }

    override fun obterUsuarioPorEmail(email: String): UsuarioEntity {
        return buscarUsuarioPorEmail(email)
    }

    override fun mostrarDados(email: String): UsuarioResult {
        val usuarioEntity = buscarUsuarioPorEmail(email)

        return mapearUsuario(usuarioEntity)
    }


    override fun atualizar(data: Usuario, email: String) {
        val usuarioExistente = buscarUsuarioPorEmail(email)

        val novoUsuario = UsuarioEntity(
            id = usuarioExistente.id,
            nome = data.nome ?: usuarioExistente.nome,
            email = usuarioExistente.email,
            senha = usuarioExistente.senha,
            cpf = data.cpf,
            telefone = data.telefone,
            escolaridade = data.escolaridade,
            dtNascimento = data.dtNascimento,
            estadoCivil = data.estadoCivil,
            estado = data.estado,
            cidade = data.cidade,
            biografia = data.biografia
        )

        runCatching {
            dao.save(novoUsuario)
        }.getOrElse {
            throw CreateInternalServerError("Falha ao atualizar o usuário ${data.nome}!!")
        }
    }

    override fun atualizarCurriculoUrl(email: String, curriculoUrl: String?) {
        val userId = buscarUsuarioPorEmail(email)
        runCatching {
            dao.atualizarCurriculoUrl(userId.id!!, curriculoUrl)
        }.getOrElse {
            throw UpdateCurriculoException("Falha ao atualizar o curriculo!!")
        }
    }

    override fun atualizarLocalizacao(email: String, data: Localizacao) {
        val usuario = buscarUsuarioPorEmail(email)

        val localizacaoUsuario = UsuarioEntity(
            latitude = data.latitude,
            longitude = data.longitude
        )

        runCatching {
            dao.adicionarLocalizacao(
                usuario.id!!,
                localizacaoUsuario.latitude,
                localizacaoUsuario.longitude
            )
        }.getOrElse {
            throw AtualizarLocalizacaoException(
                "Houve um erro ao tentar atualizar a localização do usuário!!"
            )
        }
    }

    private fun validarEmailExistente(email: String) {
        if (dao.findByEmail(email).isPresent)
            throw EmailExistenteException("O email inserido já existe!!")
    }

    private fun validarUsuario(email: String, senha: String) {
        val credentials: Authentication = UsernamePasswordAuthenticationToken(email, senha)

        val authResult = authenticationManager.authenticate(credentials)

        SecurityContextHolder.getContext().authentication = authResult
    }

    private fun buscarUsuarioPorEmail(email: String) = dao.findByEmail(email)
        .orElseThrow { EmpresaNotFoundException("Email do usuário não encontrado $email") }

    private fun mapearUsuario(usuarioEntity: UsuarioEntity): UsuarioResult {
            val dtNascUsuario = usuarioEntity.dtNascimento
            val idade = definirIdadeDoUsuario(dtNascUsuario)

            val usuario = UsuarioResult(
                nome = usuarioEntity.nome,
                email = usuarioEntity.email!!,
                cpf = usuarioEntity.cpf,
                telefone = usuarioEntity.telefone,
                escolaridade = usuarioEntity.escolaridade,
                idade = idade,
                estadoCivil = usuarioEntity.estadoCivil,
                estado = usuarioEntity.estado,
                cidade = usuarioEntity.cidade,
                biografia = usuarioEntity.biografia,
                curriculoUrl = usuarioEntity.curriculoUrl
            )

        return usuario
    }

    private fun definirIdadeDoUsuario(dtNasc: LocalDate?) =
        dtNasc?.let { Period.between(it, LocalDate.now()).years }
}