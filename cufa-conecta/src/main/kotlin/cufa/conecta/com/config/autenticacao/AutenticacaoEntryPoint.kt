package cufa.conecta.com.config.autenticacao

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class AutenticacaoEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        when (authException) {
            is BadCredentialsException,
            is InsufficientAuthenticationException -> { response.sendError(HttpServletResponse.SC_UNAUTHORIZED) }
            else -> {
                response.sendError(HttpServletResponse.SC_FORBIDDEN)
            }
        }
    }
}