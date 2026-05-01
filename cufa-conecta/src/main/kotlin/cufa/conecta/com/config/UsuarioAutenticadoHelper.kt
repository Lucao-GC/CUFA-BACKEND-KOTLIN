package cufa.conecta.com.config

import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.server.ResponseStatusException

object UsuarioAutenticadoHelper {
    /** E-mail do usuário autenticado pelo JWT, ou 401 se não houver sessão válida. */
    fun emailObrigatorio(): String {
        val auth = SecurityContextHolder.getContext().authentication
            ?: throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Sessão expirada ou token inválido. Faça login novamente.",
            )
        val name = auth.name
        if (name.isNullOrBlank() || name == "anonymousUser") {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Sessão expirada ou token inválido. Faça login novamente.",
            )
        }
        return name
    }
}
