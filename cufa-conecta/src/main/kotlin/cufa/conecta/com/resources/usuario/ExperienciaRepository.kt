package cufa.conecta.com.resources.usuario

import cufa.conecta.com.model.data.usuario.Experiencia

interface ExperienciaRepository {
    fun criarExperiencia(data: Experiencia, email: String)
    fun listarPorUsuario(email: String): List<Experiencia>
    fun atualizar(data: Experiencia, email: String)
    fun deletarExperiencia(id: Long, email: String)
}