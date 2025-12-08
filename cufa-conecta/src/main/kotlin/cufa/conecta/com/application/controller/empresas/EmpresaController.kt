package cufa.conecta.com.application.controller.empresas

import cufa.conecta.com.application.dto.request.LoginDto
import cufa.conecta.com.application.dto.request.empresa.BiografiaRequestDto
import cufa.conecta.com.application.dto.request.empresa.EmpresaRequestDto
import cufa.conecta.com.application.dto.request.empresa.EmpresaUpdateRequestDto
import cufa.conecta.com.application.dto.response.empresa.EmpresaResponseDto
import cufa.conecta.com.application.dto.response.empresa.EmpresaTokenDto
import cufa.conecta.com.application.exception.EmpresaNotExistsException
import cufa.conecta.com.domain.service.empresa.EmpresaService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/empresas")
class EmpresaController(
    private val service: EmpresaService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun cadastrar(@RequestBody @Valid dto: EmpresaRequestDto) {
        val empresaData = dto.toModel()

        service.cadastrarEmpresa(empresaData)
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    fun login(
        @RequestBody @Valid loginDto: LoginDto,
        response: HttpServletResponse
    ): EmpresaTokenDto {
        val empresaData = loginDto.toModel()
        val empresaToken = service.autenticar(empresaData)

        val token = empresaToken.tokenJwt

        val cookie = "jwt=$token; HttpOnly; Path=/; Max-Age=${60 * 60 * 24 * 7}; SameSite=Lax"

        response.addHeader("Set-Cookie", cookie)

        return EmpresaTokenDto(
            nome = empresaToken.nome,
            email = empresaToken.email,
            tokenJwt = null
        )
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(response: HttpServletResponse) {
        val cookie = Cookie("jwt", null)
        cookie.isHttpOnly = true
        cookie.secure = false
        cookie.path = "/"
        cookie.maxAge = 0

        response.addCookie(cookie)
    }

    @GetMapping("/all")
    fun listarEmpresas(): List<EmpresaResponseDto> {
        val empresasEncontradas = service.listarTodos()

        if (empresasEncontradas.isEmpty()) {
            throw EmpresaNotExistsException("Não há nenhuma empresa cadastrada!")
        }

        val result = EmpresaResponseDto.listOf(empresasEncontradas)

        return result
    }

    @GetMapping
    fun buscarDados(): EmpresaResponseDto {
        val empresaData = service.mostrarDados()

        val result = EmpresaResponseDto.of(empresaData)

        return result
    }

    @PutMapping
    fun atualizar(@RequestBody @Valid dto: EmpresaUpdateRequestDto) {
        val empresaAtualizada = dto.toModel()

        service.atualizarDados(empresaAtualizada)
    }

    @PatchMapping("/biografia")
    fun adicionarBiografia(@RequestBody @Valid dto: BiografiaRequestDto) {

        val data = dto.toModel()

        service.atualizarBiografia(data)
    }
}