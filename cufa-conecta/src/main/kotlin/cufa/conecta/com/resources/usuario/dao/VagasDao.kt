package cufa.conecta.com.resources.usuario.dao

import cufa.conecta.com.resources.empresa.entity.PublicacaoEntity
import cufa.conecta.com.resources.usuario.entity.ExperienciaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface VagasDao : JpaRepository<ExperienciaEntity, Long> {

    @Query("""
    SELECT 
        p.id_publicacao, 
        p.id_empresa, 
        p.titulo, 
        p.descricao, 
        p.tipo_contrato, 
        p.dt_expiracao, 
        p.dt_publicacao,
        e.nome as nomeEmpresa,
        (
            6371 * acos(
                cos(radians(:latitude)) * cos(radians(e.latitude)) *
                cos(radians(e.longitude) - radians(:longitude)) +
                sin(radians(:latitude)) * sin(radians(e.latitude))
            )
        ) AS distancia
    FROM publicacao p
    INNER JOIN cadastro_empresa e ON p.id_empresa = e.id_empresa
    WHERE p.dt_expiracao > NOW()
    AND e.latitude IS NOT NULL 
    AND e.longitude IS NOT NULL
    HAVING distancia <= 50
    ORDER BY distancia ASC
    LIMIT 10
""", nativeQuery = true)
    fun buscarVagasProximas(latitude: Double, longitude: Double): List<PublicacaoEntity>
}
