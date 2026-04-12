package cufa.conecta.com.resources.usuario.dao

import cufa.conecta.com.resources.usuario.entity.CandidaturaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CandidaturaDao : JpaRepository<CandidaturaEntity, Long> {
    fun existsByUsuarioIdAndPublicacaoId(usuarioId: Long, vaga: Long): Boolean

    fun findByUsuarioId(usuarioId: Long): List<CandidaturaEntity>

    @Query(
        """
        SELECT COUNT(c)
        FROM candidatura c
        WHERE c.publicacaoId = :publicacaoId
          AND c.empresaId = :empresaId
    """
    )
    fun countByPublicacaoIdAndEmpresaId(publicacaoId: Long, empresaId: Long): Long
}