package cufa.conecta.com.application.controller.usuarios

import cufa.conecta.com.application.dto.request.LoginDto
import cufa.conecta.com.application.dto.request.AtualizarLocalizacaoDto
import cufa.conecta.com.application.dto.request.usuario.UsuarioCadastroRequestDto
import cufa.conecta.com.application.dto.request.usuario.UsuarioUpdateRequestDto
import cufa.conecta.com.application.dto.response.vagas.VagasRecomendadasResponseDto
import cufa.conecta.com.application.dto.response.usuario.UsuarioResponseDto
import cufa.conecta.com.application.dto.response.usuario.UsuarioTokenDto
import cufa.conecta.com.application.exception.CreateInternalServerError
import cufa.conecta.com.domain.service.usuario.UsuarioService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/usuarios")
class UsuarioController(
    private val service: UsuarioService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun cadastrarUsuario(@RequestBody @Valid dto: UsuarioCadastroRequestDto) {
        val data = dto.toModel()

        service.cadastrarUsuario(data)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid dto: LoginDto,
        response: HttpServletResponse
    ): UsuarioTokenDto {
        val data = dto.toModel()
        val usuarioToken = service.autenticar(data)

        val token = usuarioToken.tokenJwt

        val cookie = "jwt=$token; HttpOnly; Path=/; Max-Age=${60 * 60 * 24 * 7}; SameSite=Lax"

        response.addHeader("Set-Cookie", cookie)

        return UsuarioTokenDto(
            nome = usuarioToken.nome,
            email = usuarioToken.email,
            tokenJwt = null
        )
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(response: HttpServletResponse) {
        val cookie = Cookie("jwt", null)
        cookie.isHttpOnly = true
        cookie.path = "/"
        cookie.maxAge = 0

        response.addCookie(cookie)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun mostrarDadosUsuario(): UsuarioResponseDto {
        val usuarioData = service.mostrarDados()

        val result = UsuarioResponseDto.of(usuarioData)

        return result
    }

    @PutMapping
    fun incrementarDadosDoUsuarios(@RequestBody @Valid dto: UsuarioUpdateRequestDto) {
        val usuarioAtualizado = dto.toModel()

        service.atualizar(usuarioAtualizado)
    }

    @PatchMapping("/localizacao")
    @ResponseStatus(HttpStatus.OK)
    fun atualizarLocalizacao(@RequestBody @Valid dto: AtualizarLocalizacaoDto) {

        service.atualizarLocalizacao(dto.toModel())
    }

    @GetMapping("/recomendar")
    @ResponseStatus(HttpStatus.OK)
    fun recomendar(@RequestParam latitude: Double, @RequestParam longitude: Double): VagasRecomendadasResponseDto {
        val auth = SecurityContextHolder.getContext().authentication

        if (!auth.isAuthenticated) throw CreateInternalServerError("Usuário não autenticado")

        return service.recomendarVagas(latitude, longitude)
    }
}