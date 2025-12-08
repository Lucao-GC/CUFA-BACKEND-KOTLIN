package cufa.conecta.com.application.controller.usuarios

import cufa.conecta.com.application.dto.request.usuario.CandidaturaRequestDto
import cufa.conecta.com.application.dto.response.empresa.PublicacaoResponseDto
import cufa.conecta.com.application.dto.response.usuario.CandidaturaResponseDto
import cufa.conecta.com.domain.service.usuario.CandidaturaService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/candidaturas")
class CandidaturaController(
    private val service: CandidaturaService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun criarCandidatura(@RequestBody @Valid dto: CandidaturaRequestDto) {
        val data = dto.toModel()

        service.criarCandidatura(data)
    }

    @GetMapping("/{vagaId}")
    @ResponseStatus(HttpStatus.OK)
    fun listarCandidatosPorVaga(
        @PathVariable vagaId: Long,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): CandidaturaResponseDto {
        val candidatura = service.listarCandidatosPorVaga(vagaId, page, size)

        val result = CandidaturaResponseDto.of(candidatura)

        return result
    }

    @GetMapping("/verificar/{vagaId}")
    fun verificarCandidaturaExistente(@PathVariable vagaId: Long): Boolean {
        val candidaturaExistente = service.verificarCandidaturaExistente(vagaId)

        return candidaturaExistente
    }

    @GetMapping("/usuario")
    fun verCandidaturasPorUsuario(): List<PublicacaoResponseDto> {
        val listaDeVagasCandidatas = service.listarPublicacoesCandidatadasPorUsuario()

        val result = PublicacaoResponseDto.listOf(listaDeVagasCandidatas)

        return result
    }
}