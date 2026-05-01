package cufa.conecta.com.application.controller.usuarios

import cufa.conecta.com.application.dto.request.LoginDto
import cufa.conecta.com.application.dto.request.usuario.UsuarioCadastroRequestDto
import cufa.conecta.com.application.dto.request.usuario.UsuarioPerfilPatchDto
import cufa.conecta.com.application.dto.request.usuario.UsuarioUpdateRequestDto
import cufa.conecta.com.application.dto.response.usuario.UsuarioResponseDto
import cufa.conecta.com.application.dto.response.usuario.UsuarioTokenDto
import cufa.conecta.com.domain.service.usuario.UsuarioPerfilArquivoService
import cufa.conecta.com.domain.service.usuario.UsuarioService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/usuarios")
class UsuarioController(
    private val service: UsuarioService,
    private val perfilArquivoService: UsuarioPerfilArquivoService,
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
            tokenJwt = token
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

    /**
     * Aceita PATCH, PUT e POST com `application/json`.
     * Alguns ambientes não encaminham PATCH corretamente; o DispatcherServlet não encontra handler
     * e o fallback de recurso estático devolve 404 ("No static resource usuarios/perfil").
     */
    @RequestMapping(
        path = ["/perfil"],
        method = [RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.POST],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
    )
    @ResponseStatus(HttpStatus.OK)
    fun patchPerfil(@RequestBody dto: UsuarioPerfilPatchDto): UsuarioResponseDto {
        val data = service.patchPerfil(dto)
        return UsuarioResponseDto.of(data)
    }

    @PostMapping("/foto", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFotoPerfil(@RequestParam("file") file: MultipartFile): UsuarioResponseDto {
        val filename = perfilArquivoService.salvarImagem(file)
        val url = perfilArquivoService.urlPublica(filename)
        val data = service.atualizarFotoUrl(url)
        return UsuarioResponseDto.of(data)
    }

    @GetMapping("/fotos/download/{filename:.+}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadFotoPerfil(@PathVariable filename: String): Resource =
        perfilArquivoService.download(filename)
}