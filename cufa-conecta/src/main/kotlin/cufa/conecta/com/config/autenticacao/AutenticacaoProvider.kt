package cufa.conecta.com.config.autenticacao

import cufa.conecta.com.application.exception.DadosInvalidosException
import cufa.conecta.com.resources.AutenticacaoRepository
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class AutenticacaoProvider(
    private val usuarioAutorizacao: AutenticacaoRepository,
    private val passwordEncoder: BCryptPasswordEncoder
): AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.name
        val password = authentication.credentials.toString()

        val userDetails = usuarioAutorizacao.loadUserByUsername(username)

        return userDetails.takeIf { passwordEncoder.matches(
            password,
            it.password
        ) }
            ?.let { UsernamePasswordAuthenticationToken(
                it,
                null,
                it.authorities)
            } ?: throw DadosInvalidosException("Usuário ou senha inválidos")

    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}