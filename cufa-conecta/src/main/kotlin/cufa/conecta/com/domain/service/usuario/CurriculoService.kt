package cufa.conecta.com.domain.service.usuario

import cufa.conecta.com.application.dto.response.usuario.AnaliseCurriculoResponseDto
import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile

interface CurriculoService {
    fun salvarArquivoCurriculo(file: MultipartFile): String
    fun downloadCurriculo(filename: String): Resource
    fun deletarArquivoFisico(filename: String)
    fun gerarUrlArquivo(filename: String): String
    fun analisarCurriculo(file: MultipartFile): AnaliseCurriculoResponseDto
}