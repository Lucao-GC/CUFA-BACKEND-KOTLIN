package cufa.conecta.com.model.data.usuario

import java.time.LocalDate

data class Experiencia (
    val id: Long?= null,
    val usuarioId: Long? = null,
    val cargo: String? = null,
    val empresa: String? = null,
    val dtInicio: LocalDate? = null,
    val dtFim: LocalDate? = null
)