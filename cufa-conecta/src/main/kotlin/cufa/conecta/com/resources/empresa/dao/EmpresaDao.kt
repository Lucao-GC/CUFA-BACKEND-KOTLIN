package cufa.conecta.com.resources.empresa.dao

import cufa.conecta.com.resources.empresa.entity.EmpresaEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface EmpresaDao: JpaRepository<EmpresaEntity, Long> {
    fun findByEmail(email: String?): Optional<EmpresaEntity>

    fun existsByEmail(email: String?): Boolean

    @Transactional
    @Modifying
    @Query("""
            UPDATE cadastro_empresa e 
            SET e.biografia = :biografia
            WHERE e.idEmpresa = :idEmpresa
        """
    )
    fun atualizarBiografia(biografia: String, idEmpresa: Long)

    @Query("""
        SELECT c.nome
        FROM cadastro_empresa c
        WHERE c.idEmpresa = :idEmpresa
    """)
    fun findNameByEmpresaId(idEmpresa: Long): String

    @Modifying
    @Transactional
    @Query(
        """
            UPDATE cadastro_empresa e 
            SET e.latitude = :latitude,
            e.longitude = :longitude
            WHERE e.idEmpresa = :id
        """
    )
    fun adicionarLocalizacao(id: Long, latitude: Double?, longitude: Double?)
}