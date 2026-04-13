package cufa.conecta.com.domain.service.usuario

import cufa.conecta.com.model.data.usuario.Experiencia

interface ExperienciaService {
    fun criarExperiencia(data: Experiencia)
    fun listarPorUsuario(): List<Experiencia>
    fun atualizar(data: Experiencia)
    fun deletarExperiencia(id: Long)
}