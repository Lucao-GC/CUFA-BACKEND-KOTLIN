package cufa.conecta.com.domain.service.usuario

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile

interface UsuarioPerfilArquivoService {
    fun salvarImagem(file: MultipartFile): String
    fun download(filename: String): Resource
    fun urlPublica(filename: String): String
}
