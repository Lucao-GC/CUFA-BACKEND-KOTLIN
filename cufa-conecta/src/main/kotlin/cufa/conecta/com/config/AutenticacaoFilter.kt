package cufa.conecta.com.config

import cufa.conecta.com.application.exception.UsuarioAutenticadoNotFound
import cufa.conecta.com.resources.AutenticacaoRepository
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

class AutenticacaoFilter(
    private val authentication: AutenticacaoRepository,
    private val jwtTokenManager: GerenciadorTokenJwt,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val jwtToken = recuperarToken(request)

        if (jwtToken != null) {
            try {
                val username = jwtTokenManager.getUsernameFromToken(jwtToken)
                if (SecurityContextHolder.getContext().authentication == null) {
                    val userDetails: UserDetails = authentication.loadUserByUsername(username)
                    if (!jwtTokenManager.validateToken(jwtToken, userDetails)) {
                        throw IllegalArgumentException("Token JWT inválido")
                    }
                    val authToken =
                        UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            } catch (ex: Exception) {
                if (rotaIgnoraTokenInvalido(request)) {
                    filterChain.doFilter(request, response)
                    return
                }
                when (ex) {
                    is JwtException,
                    is IllegalArgumentException,
                    is UsernameNotFoundException,
                    is UsuarioAutenticadoNotFound,
                    -> {
                        responderTokenInvalido(response)
                        return
                    }
                    else -> throw ex
                }
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun recuperarToken(request: HttpServletRequest): String? {
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7)
        }
        val cookieJwt: Cookie? = request.cookies?.find { it.name == "jwt" }
        return cookieJwt?.value
    }

    /**
     * Cadastro/login com um Bearer antigo no header não deve bloquear o fluxo (401).
     */
    private fun rotaIgnoraTokenInvalido(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        val method = request.method
        return (method == "POST" && path == "/usuarios/login") ||
            (method == "POST" && path == "/usuarios") ||
            (method == "POST" && path == "/empresas/login") ||
            (method == "POST" && path == "/empresas")
    }

    private fun responderTokenInvalido(response: HttpServletResponse) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.characterEncoding = Charsets.UTF_8.name()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write("{\"message\":\"Token expirado ou inválido. Faça login novamente.\"}")
    }
}
