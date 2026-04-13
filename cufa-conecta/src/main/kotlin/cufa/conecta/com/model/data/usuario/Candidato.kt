package cufa.conecta.com.model.data.usuario

data class Candidato(
    val id: Long,
    val nome: String,
    val idade: Int,
    val biografia: String,
    val email: String,
    val telefone: String,
    val curriculoUrl: String? = null,
    val experiencias: List<Experiencia>
)