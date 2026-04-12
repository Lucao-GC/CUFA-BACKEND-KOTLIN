package cufa.conecta.com.application.controller.empresas

import cufa.conecta.com.application.dto.request.empresa.PublicacaoRequestDto
import cufa.conecta.com.application.dto.response.empresa.PublicacaoPaginadaResponseDto
import cufa.conecta.com.application.dto.response.empresa.PublicacaoResponseDto
import cufa.conecta.com.domain.service.empresa.PublicacaoService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/publicacoes")
class PublicacaoController(
    private val service: PublicacaoService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun criar(@RequestBody dto: PublicacaoRequestDto) {
        val data = dto.toModel()

        service.criar(data)
    }

    @GetMapping
    fun listarTodos(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): PublicacaoPaginadaResponseDto {
        val publicacoesEncontradas = service.buscarTodas(page, size)

        val result = PublicacaoPaginadaResponseDto.listOfResult(publicacoesEncontradas)

        return result
    }

    @GetMapping("/empresa")
    fun listarPorEmpresa(): List<PublicacaoResponseDto> {
        val publicacoesEncontradas = service.buscarPublicacoesDaEmpresa()

        val result = PublicacaoResponseDto.listOf(publicacoesEncontradas)

        return result
    }

    @PutMapping("/{id}")
    fun editar(@PathVariable id: Long, @RequestBody dto: PublicacaoRequestDto) {
        val data = dto.toUpdateModel(id)

        service.editarPublicacao(data)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletar(@PathVariable id: Long) = service.delete(id)
}