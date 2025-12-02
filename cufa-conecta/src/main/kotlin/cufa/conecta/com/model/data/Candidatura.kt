package cufa.conecta.com.model.data

import java.time.LocalDate

data class Candidatura (
    val publicacaoId: Long,
    val empresaId: Long,
    val dtCandidatura: LocalDate? = LocalDate.now()
)