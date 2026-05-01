package cufa.conecta.com.application.dto.request.usuario

/** Campos opcionais: só os não-nulos são aplicados ao perfil (PATCH). */
data class UsuarioPerfilPatchDto(
    val nome: String? = null,
    val biografia: String? = null,
    val cidade: String? = null,
    val estado: String? = null,
    val fotoUrl: String? = null,
)
