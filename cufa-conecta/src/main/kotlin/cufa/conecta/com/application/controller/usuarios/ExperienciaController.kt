package cufa.conecta.com.application.controller.usuarios

import cufa.conecta.com.application.dto.request.usuario.ExperienciaRequestDto
import cufa.conecta.com.application.dto.response.usuario.ExperienciaResponseDto
import cufa.conecta.com.domain.service.usuario.ExperienciaService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/experiencias")
class ExperienciaController(
    private val service: ExperienciaService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun criarExperiencia(@RequestBody dto: ExperienciaRequestDto) {
        val data = dto.toModel()

        service.criarExperiencia(data)
    }

    @GetMapping
    fun listarExperienciasPorUsuario(): List<ExperienciaResponseDto> {
        val listaDeExperiencias = service.listarPorUsuario()

        val result = ExperienciaResponseDto.listOf(listaDeExperiencias)

        return result
    }

    @PutMapping("/{id}")
    fun atualizarExperiencia(
        @PathVariable id: Long,
        @RequestBody dto: ExperienciaRequestDto
    ) {
        val empresaAtualizada = dto.toUpdateModel(id)

        service.atualizar(empresaAtualizada)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletarExperiencia(@PathVariable id: Long) = service.deletarExperiencia(id)
}