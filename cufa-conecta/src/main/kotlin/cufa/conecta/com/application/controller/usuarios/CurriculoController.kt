package cufa.conecta.com.application.controller.usuarios

import cufa.conecta.com.application.dto.request.usuario.CurriculoRequestDto
import cufa.conecta.com.application.dto.response.usuario.CurriculoResponseDto
import cufa.conecta.com.domain.service.usuario.CurriculoService
import cufa.conecta.com.domain.service.usuario.UsuarioService
import jakarta.validation.Valid
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/curriculos")
class CurriculoController(
    private val service: CurriculoService,
    private val usuarioService: UsuarioService
) {

    @PostMapping("/upload")
    fun uploadCurriculo(@ModelAttribute @Valid dto: CurriculoRequestDto): CurriculoResponseDto {
        val filename = service.salvarArquivoCurriculo(dto.file)

        return CurriculoResponseDto(filename)
    }

    @GetMapping
    fun getCurriculoUsuario(): CurriculoResponseDto {
        val usuario = usuarioService.mostrarDados()
        val url = usuario.curriculoUrl

        if (url.isNullOrBlank()) {
            return CurriculoResponseDto("")
        }

        val nomeArquivo = url.substringAfterLast("/")
        return CurriculoResponseDto(nomeArquivo)
    }

    @GetMapping("/download/{filename:.+}",
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    fun downloadCurriculo(@PathVariable filename: String): Resource {
        return service.downloadCurriculo(filename)
    }

    @PostMapping("/update")
    fun updateCurriculoUsuario(@RequestParam("file") file: MultipartFile): String {
        val filename = service.salvarArquivoCurriculo(file)
        val curriculoUrl = service.gerarUrlArquivo(filename)

        usuarioService.atualizarCurriculoUrl(curriculoUrl)

        return curriculoUrl
    }

    @DeleteMapping
    fun deletarCurriculoUsuario(): String {
        val usuario = usuarioService.mostrarDados()
        val urlAntiga = usuario.curriculoUrl

        urlAntiga?.takeIf { it.isNotEmpty() }?.let {
            val nomeArquivo = it.substringAfterLast('/')
            service.deletarArquivoFisico(nomeArquivo)
        }

        usuarioService.atualizarCurriculoUrl(null)

        return "Currículo deletado com sucesso."
    }
}
