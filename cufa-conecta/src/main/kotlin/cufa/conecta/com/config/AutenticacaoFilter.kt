package cufa.conecta.com.config

import cufa.conecta.com.resources.AutenticacaoRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

class AutenticacaoFilter(
    private val authentication: AutenticacaoRepository,
    private val jwtTokenManager: GerenciadorTokenJwt
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwtToken = recuperarToken(request)

        if (jwtToken != null) {
            val username = runCatching {
                jwtTokenManager.getUsernameFromToken(jwtToken)
            }.getOrElse {
                filterChain.doFilter(request, response)
                return
            }

            if (SecurityContextHolder.getContext().authentication == null)
                addUsernameInContext(request, username, jwtToken)
        }

        filterChain.doFilter(request, response)
    }

    private fun addUsernameInContext(
        request: HttpServletRequest,
        username: String,
        jwtToken: String
    ) {
        val userDetails: UserDetails = authentication.loadUserByUsername(username)

        if (jwtTokenManager.validateToken(jwtToken, userDetails)) {
            val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authToken
        }
    }

    private fun recuperarToken(request: HttpServletRequest): String? {
        // 1️⃣ Tenta pegar do Header Authorization
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7)
        }

        // 2️⃣ Se não tiver no header, tenta pegar do cookie "jwt"
        val cookieJwt: Cookie? = request.cookies?.find { it.name == "jwt" }
        return cookieJwt?.value
    }
}
