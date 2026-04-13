package cufa.conecta.com.model.data.usuario

import java.time.LocalDate

data class Usuario (
    val id: Long?= null,
    val nome: String?= null,
    val email: String?= null,
    val senha: String?= null,
    val cpf: String?= null,
    val telefone: String?= null,
    val escolaridade: String?= null,
    val dtNascimento: LocalDate?= null,
    val estadoCivil: String?= null,
    val estado: String?= null,
    val cidade: String?= null,
    val biografia: String?= null,
    val curriculoUrl: String?= null,
    val latitude: Double?= null,
    val longitude: Double?= null
)