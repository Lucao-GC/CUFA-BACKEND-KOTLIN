package cufa.conecta.com.domain.service.usuario.implementation


import com.fasterxml.jackson.databind.ObjectMapper
import cufa.conecta.com.application.dto.request.usuario.AnaliseCurriculoDto
import cufa.conecta.com.application.dto.response.usuario.AnaliseCurriculoResponseDto
import cufa.conecta.com.application.exception.CreateInternalServerError
import cufa.conecta.com.domain.service.ai.IaGenerativaService
import cufa.conecta.com.domain.service.usuario.CurriculoService
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

@Service
class CurriculoServiceImpl(
    private val iaService: IaGenerativaService,
    private val objectMapper: ObjectMapper
) : CurriculoService {

    private val uploadDir: Path = Paths.get("uploads/curriculos").also {
        runCatching { Files.createDirectories(it) }
            .onFailure { throw RuntimeException("Não foi possível criar diretório de uploads: ${it.message}", it) }
    }

    override fun salvarArquivoCurriculo(file: MultipartFile): String {
        require(!file.isEmpty) { "Arquivo vazio." }

        val filename = "${UUID.randomUUID()}-${file.originalFilename}"
        val targetPath = uploadDir.resolve(filename)

        runCatching {
            Files.copy(file.inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)
        }.getOrElse { e ->
            throw RuntimeException("Erro ao salvar arquivo: ${e.message}", e)
        }

        return filename
    }

    override fun downloadCurriculo(filename: String): Resource {
        val filePath = uploadDir.resolve(filename).normalize()

        return runCatching {
            UrlResource(filePath.toUri()).apply {
                require(exists() && isReadable) { "Arquivo não encontrado" }
            }
        }.getOrElse { e ->
            throw RuntimeException("Erro ao carregar arquivo: ${e.message}", e)
        }
    }

    override fun deletarArquivoFisico(filename: String) {
        val filePath = uploadDir.resolve(filename).normalize()

        runCatching {
            require(Files.exists(filePath)) { "Arquivo não encontrado para exclusão" }
            Files.delete(filePath)
        }.getOrElse { e ->
            throw RuntimeException("Erro ao excluir arquivo: ${e.message}", e)
        }
    }

    override fun gerarUrlArquivo(filename: String): String =  "http://localhost:8080/curriculos/download/$filename"

    override fun analisarCurriculo(file: MultipartFile): AnaliseCurriculoResponseDto {

        val textoCurriculo = extrairTexto(file)

        if (textoCurriculo.isBlank()) {
            throw CreateInternalServerError("Não foi possível extrair texto do currículo")
        }

        val prompt = """
        Você é um especialista em recrutamento e análise de currículos.

        Sua tarefa:
        - Analise o currículo abaixo
        - Identifique pontos de melhoria
        - Sugira melhorias práticas
        - Retorne SOMENTE um JSON

        Formato esperado:
        {
          "resumo": "Breve avaliação geral",
          "pontosFortes": ["..."],
          "pontosMelhoria": ["..."],
          "sugestoes": ["..."]
        }

        Currículo:
        $textoCurriculo
    """

        val resposta = iaService.gerarResposta(prompt) ?: throw CreateInternalServerError("IA indisponível")

        val respostaLimpa = extrairJson(resposta)

        val analise: AnaliseCurriculoDto = runCatching {
            objectMapper.readValue(respostaLimpa, AnaliseCurriculoDto::class.java)
        }.getOrElse {
            throw CreateInternalServerError("Erro ao interpretar JSON da IA: ${it.message}\nResposta: $resposta")
        }

        return AnaliseCurriculoResponseDto(analise)
    }

    private fun extrairTexto(file: MultipartFile): String {
        val nome = file.originalFilename ?: ""

        return when {
            nome.endsWith(".pdf") -> extrairPdf(file)
            else -> throw CreateInternalServerError("Formato não suportado")
        }
    }

    fun extrairPdf(file: MultipartFile): String {
        val document = PDDocument.load(file.inputStream)
        val stripper = PDFTextStripper()
        val texto = stripper.getText(document)
        document.close()
        return texto
    }

    private fun extrairJson(resposta: String): String {
        val inicio = resposta.indexOf("{")
        val fim = resposta.lastIndexOf("}")

        if (inicio == -1 || fim == -1) {
            throw CreateInternalServerError("Resposta da IA não contém JSON válido: $resposta")
        }

        return resposta.substring(inicio, fim + 1)
    }
}