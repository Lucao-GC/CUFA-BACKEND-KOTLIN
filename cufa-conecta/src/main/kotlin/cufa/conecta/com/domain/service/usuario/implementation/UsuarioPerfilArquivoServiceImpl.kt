package cufa.conecta.com.domain.service.usuario.implementation

import cufa.conecta.com.domain.service.usuario.UsuarioPerfilArquivoService
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID

@Service
class UsuarioPerfilArquivoServiceImpl : UsuarioPerfilArquivoService {

    private val uploadDir: Path = Paths.get("uploads/fotos-perfil").also {
        runCatching { Files.createDirectories(it) }
            .onFailure { throw RuntimeException("Não foi possível criar diretório de fotos: ${it.message}", it) }
    }

    override fun salvarImagem(file: MultipartFile): String {
        require(!file.isEmpty) { "Arquivo vazio." }
        val ext = file.originalFilename?.substringAfterLast('.', "")?.takeIf { it.isNotBlank() } ?: "jpg"
        val filename = "${UUID.randomUUID()}.$ext"
        val targetPath = uploadDir.resolve(filename)
        runCatching {
            Files.copy(file.inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)
        }.getOrElse { e ->
            throw RuntimeException("Erro ao salvar imagem: ${e.message}", e)
        }
        return filename
    }

    override fun download(filename: String): Resource {
        val filePath = uploadDir.resolve(filename).normalize()
        return runCatching {
            UrlResource(filePath.toUri()).apply {
                require(exists() && isReadable) { "Arquivo não encontrado" }
            }
        }.getOrElse { e ->
            throw RuntimeException("Erro ao carregar imagem: ${e.message}", e)
        }
    }

    /** Caminho relativo para o app prefixar com EXPO_PUBLIC_API_URL em qualquer host. */
    override fun urlPublica(filename: String): String =
        "/usuarios/fotos/download/$filename"
}
