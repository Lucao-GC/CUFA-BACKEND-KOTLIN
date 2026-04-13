package cufa.conecta.com.application.controller

import cufa.conecta.com.application.dto.response.ApiExceptionDto
import cufa.conecta.com.application.exception.CreateInternalServerError
import cufa.conecta.com.application.exception.DadosInvalidosException
import cufa.conecta.com.application.exception.EmpresaNotExistsException
import cufa.conecta.com.application.exception.FuncionarioNotExistsException
import cufa.conecta.com.application.exception.InvalidSizeNumberException
import cufa.conecta.com.application.exception.PageNotFoundException
import cufa.conecta.com.application.exception.PublicacaoNotFoundException
import cufa.conecta.com.application.exception.UsuarioAutenticadoNotFound
import cufa.conecta.com.application.exception.UsuarioNotFoundException
import cufa.conecta.com.resources.empresa.exception.EmailExistenteException
import cufa.conecta.com.resources.empresa.exception.EmpresaNotFoundException
import cufa.conecta.com.resources.usuario.exception.AtualizarLocalizacaoException
import cufa.conecta.com.resources.usuario.exception.UpdateCurriculoException
import cufa.conecta.com.resources.usuario.exception.UsuarioExistenteException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ControllerExceptionHandler: ResponseEntityExceptionHandler() {
    @ExceptionHandler(
        UpdateCurriculoException::class,
        AtualizarLocalizacaoException::class,
        CreateInternalServerError::class,
        Exception::class
    )
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    fun handleInternalServerError(ex: Exception): ApiExceptionDto {
        return ApiExceptionDto(
            status = INTERNAL_SERVER_ERROR,
            message = ex.message ?: "Internal Server Error",
            type = ex.javaClass.typeName
        )
    }

    @ExceptionHandler(
        DadosInvalidosException::class,
        InvalidSizeNumberException::class,
        InvalidSizeNumberException::class
    )
    @ResponseStatus(BAD_REQUEST)
    fun handleBadRequest(ex: Exception): ApiExceptionDto {
        return ApiExceptionDto(
            status = BAD_REQUEST,
            message = ex.message ?: "Bad Request",
            type = ex.javaClass.typeName
        )
    }

    @ExceptionHandler(
        EmpresaNotExistsException::class,
        EmpresaNotFoundException::class,
        PublicacaoNotFoundException::class,
        UsuarioNotFoundException::class,
        FuncionarioNotExistsException::class,
        UsuarioAutenticadoNotFound::class,
        PageNotFoundException::class
    )
    @ResponseStatus(NOT_FOUND)
    fun handleNotFound(ex: Exception): ApiExceptionDto {
        return ApiExceptionDto(
            status = NOT_FOUND,
            message = ex.message ?: "Not Found",
            type = ex.javaClass.typeName
        )
    }

    @ExceptionHandler(
        EmailExistenteException::class,
        UsuarioExistenteException::class
    )
    @ResponseStatus(CONFLICT)
    fun handleConflict(ex: Exception): ApiExceptionDto {
        return ApiExceptionDto(
            status = CONFLICT,
            message = ex.message ?: "Conflict",
            type = ex.javaClass.typeName
        )
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> =
        ex.bindingResult.allErrors
            .map(ObjectError::getDefaultMessage)
            .let {
                ResponseEntity(
                    ApiExceptionDto(
                        status = HttpStatus.resolve(status.value()) ?: INTERNAL_SERVER_ERROR,
                        message = it.toString(),
                        type = ex.javaClass.typeName
                    ),
                    status
                )
            }
}