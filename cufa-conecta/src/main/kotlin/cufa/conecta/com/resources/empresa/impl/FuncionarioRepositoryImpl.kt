package cufa.conecta.com.resources.empresa.impl

import cufa.conecta.com.application.exception.CreateInternalServerError
import cufa.conecta.com.application.exception.FuncionarioNotExistsException
import cufa.conecta.com.model.data.Funcionario
import cufa.conecta.com.resources.empresa.FuncionarioRepository
import cufa.conecta.com.resources.empresa.dao.EmpresaDao
import cufa.conecta.com.resources.empresa.dao.FuncionarioDao
import cufa.conecta.com.resources.empresa.entity.FuncionarioEntity
import cufa.conecta.com.resources.empresa.exception.EmailExistenteException
import cufa.conecta.com.resources.empresa.exception.EmpresaNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository

@Repository
class FuncionarioRepositoryImpl(
    private val dao: FuncionarioDao,
    private val empresaDao: EmpresaDao,
    private val passwordEncoder: PasswordEncoder
): FuncionarioRepository {

    override fun criarFuncionario(data: Funcionario, empresaEmail: String) {
        val empresa = buscarEmpresaPorEmail(empresaEmail)

        validarEmailExistente(data.email)

        val senhaCriptografada = passwordEncoder.encode(data.senha)

        val funcionario = FuncionarioEntity(
            empresaId = empresa.idEmpresa!!,
            nome = data.nome,
            email = data.email,
            senha = senhaCriptografada,
            cargo = data.cargo,
        )

        runCatching {
            dao.save(funcionario)
        }.getOrElse {
            throw CreateInternalServerError("Falha ao cadastrar o funcionário!!")
        }
    }

    override fun existsByEmail(email: String): Boolean = dao.findByEmail(email).isPresent

    override fun buscarFuncionarios(email: String): List<Funcionario> {
        val empresa = buscarEmpresaPorEmail(email)

        val listaDeFuncionariosEntity = dao.findByEmpresaId(empresa.idEmpresa!!)

        return mapearFuncionarios(listaDeFuncionariosEntity)
    }

    override fun mostrarDados(id: Long): Funcionario {
        val funcionarioEntity = buscarFuncionarioPorId(id)

        val funcionario = Funcionario(
            empresaId = funcionarioEntity.empresaId,
            nome = funcionarioEntity.nome,
            email = funcionarioEntity.email,
            senha = funcionarioEntity.senha,
            cargo = funcionarioEntity.cargo,
        )

        return funcionario
    }

    override fun delete(id: Long) {
        val funcionario = buscarFuncionarioPorId(id)

        dao.delete(funcionario)
    }

    private final fun buscarFuncionarioPorId(id: Long): FuncionarioEntity =
        dao.findById(id)
            .orElseThrow { FuncionarioNotExistsException("Funcionário não foi encontrado") }

    private fun buscarEmpresaPorEmail(email: String) =
        empresaDao.findByEmail(email)
            .orElseThrow { EmpresaNotFoundException("Empresa não encontrada!") }

    private final fun mapearFuncionarios(funcionariosEntity: List<FuncionarioEntity>): List<Funcionario> {
        return funcionariosEntity.map { funcionarioEntity ->
            Funcionario(
                id = funcionarioEntity.funcionarioId,
                nome = funcionarioEntity.nome,
                email = funcionarioEntity.email,
                cargo = funcionarioEntity.cargo,
            )
        }
    }

    private fun validarEmailExistente(email: String) {
        if (dao.existsByEmail(email)) {
            throw EmailExistenteException("O email inserido já existe!!")
        }
    }
}