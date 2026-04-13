package cufa.conecta.com.resources.usuario.dao

import cufa.conecta.com.resources.usuario.entity.ExperienciaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface VagasDao : JpaRepository<ExperienciaEntity, Long> {

@Query(
    """ 
            SELECT *
            FROM (
            SELECT v.*, (
                6371 * acos( 
                cos(radians(:lat)) *
                cos(radians(v.latitude)) *
                cos(radians(v.longitude) -
                radians(:lng)) + sin(radians(:lat)) *
                sin(radians(v.latitude))
                )
            ) AS distancia
            FROM cadastro_empresa v
            )
            HAVING distancia < 10
            ORDER BY distancia
            
        """, nativeQuery = true
)
fun buscarVagasProximas(latitude: Double, longitude: Double)
}
