package cufa.conecta.com.model.data.empresa

data class Empresa(
    val id: Long? = null,
    val nome: String,
    val email: String?= null,
    val senha: String?= null,
    val cep: String,
    val endereco: String,
    val numero: String,
    val cnpj: String,
    val area: String,
    val biografia: String? = null
)