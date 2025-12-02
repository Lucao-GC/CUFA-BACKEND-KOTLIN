package cufa.conecta.com.application.controller.empresas

import cufa.conecta.com.application.dto.request.empresa.FuncionarioRequestDto
import cufa.conecta.com.application.dto.response.empresa.FuncionarioResponseDto
import cufa.conecta.com.domain.service.empresa.FuncionarioService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/funcionarios")
class FuncionarioController(
    private val service: FuncionarioService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun criar(@RequestBody @Valid dto: FuncionarioRequestDto) {
        val funcionarioData = dto.toModel()

        service.criarFuncionario(funcionarioData)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun listarPorEmpresa(): List<FuncionarioResponseDto> {
        val funcionariosEncontrados = service.buscarFuncionarios()

        if (funcionariosEncontrados.isEmpty()) return emptyList()

        val result = FuncionarioResponseDto.listOf(funcionariosEncontrados)

        return result
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remover(@PathVariable id: Long) = service.deletar(id)
}