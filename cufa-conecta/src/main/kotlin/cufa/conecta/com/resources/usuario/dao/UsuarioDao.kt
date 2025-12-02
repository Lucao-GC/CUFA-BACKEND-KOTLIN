package cufa.conecta.com.resources.usuario.dao

import cufa.conecta.com.resources.usuario.entity.UsuarioEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UsuarioDao : JpaRepository<UsuarioEntity, Long> {
    fun findByEmail(email: String?): Optional<UsuarioEntity>

    @Modifying
    @Query("UPDATE cadastro_usuario user SET user.curriculoUrl = :curriculoUrl WHERE user.id = :id")
    fun atualizarCurriculoUrl(id: Long, curriculoUrl: String?)

    @Query(
        """
        SELECT * 
        FROM cadastro_usuario u
        WHERE u.id_usuario IN (
            SELECT c.id_usuario
            FROM candidatura c
            WHERE c.id_publicacao = :publicacaoId
            AND c.id_empresa = :empresaId
        )
        ORDER BY u.id_usuario
        LIMIT :size
        OFFSET :offset
    """,
        nativeQuery = true
    )
    fun dadosPaginados(publicacaoId: Long, empresaId: Long, size: Int, offset: Int): List<UsuarioEntity>
}
