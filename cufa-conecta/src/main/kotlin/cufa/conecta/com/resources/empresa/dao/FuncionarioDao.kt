package cufa.conecta.com.resources.empresa.dao

import cufa.conecta.com.resources.empresa.entity.FuncionarioEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FuncionarioDao: JpaRepository<FuncionarioEntity, Long> {
    fun findByEmail(email: String): Optional<FuncionarioEntity>

    fun existsByEmail(email: String): Boolean

    @Query("""
            SELECT f 
            FROM funcionarios f 
            WHERE f.empresaId = :empresaId
        """
    )
    fun findByEmpresaId(empresaId: Long): List<FuncionarioEntity>
}