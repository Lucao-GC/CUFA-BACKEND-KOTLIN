package cufa.conecta.com.domain.service.usuario.implementation

import cufa.conecta.com.application.dto.response.usuario.UsuarioTokenDto
import cufa.conecta.com.domain.service.ai.IaGenerativaService
import cufa.conecta.com.domain.service.usuario.UsuarioService
import cufa.conecta.com.model.data.Login
import cufa.conecta.com.model.data.usuario.Usuario
import cufa.conecta.com.model.data.result.UsuarioResult
import cufa.conecta.com.model.data.usuario.Localizacao
import cufa.conecta.com.resources.usuario.UsuarioRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UsuarioServiceImpl(
    private val repository: UsuarioRepository,
    private val iaService: IaGenerativaService
): UsuarioService {
    override fun cadastrarUsuario(data: Usuario) = repository.cadastrarUsuario(data)

    override fun autenticar(data: Login): UsuarioTokenDto = repository.autenticar(data)

    override fun mostrarDados(): UsuarioResult {
        val auth = SecurityContextHolder.getContext().authentication
        val email = auth?.name

        val dadosUsuario = repository.mostrarDados(email!!)

        return dadosUsuario
    }

    override fun atualizar(data: Usuario) {
        val auth = SecurityContextHolder.getContext().authentication

        val email = auth?.name

        repository.atualizar(data, email!!)
    }

    override fun atualizarCurriculoUrl(curriculoUrl: String?) {
        val auth = SecurityContextHolder.getContext().authentication
        val email = auth?.name

        repository.atualizarCurriculoUrl(email!!, curriculoUrl)
    }

    override fun atualizarLocalizacao(data: Localizacao) {
        val auth = SecurityContextHolder.getContext().authentication
        val email = auth?.name

        repository.atualizarLocalizacao(email!!, data)
    }

    override fun recomendarVagas(latitude: Double, longitude: Double): String? {

        val vagas = repository.buscarVagasProximas(latitude, longitude)

        val resumoVagas = vagas.joinToString("\n") { it.toString() }

        val prompt = """
        Analise a lista de vagas abaixo e indique quais são
        mais recomendadas para o usuário com base na proximidade:

        $resumoVagas

        Retorne apenas o ranking das melhores vagas.
        """

        return iaService.gerarResposta(prompt)
    }
}
