
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UsuarioRepositorio {

    private lateinit var jdbcTemplate: JdbcTemplate
    private lateinit var jdbcTemplateServer: JdbcTemplate

    fun iniciar() {

        jdbcTemplate = Conexao.jdbcTemplate!!
        jdbcTemplateServer = Conexao.jdbcTemplateServer!!
    }

    fun autenticarLogin(logarUsuarioEmail: String, logarUsuarioSenha: String): Boolean {
        val consulta = jdbcTemplateServer.queryForObject(
            "SELECT COUNT(*) AS count FROM Funcionario WHERE email = ? AND senha = ?",
            arrayOf(logarUsuarioEmail, logarUsuarioSenha),
            Int::class.java
        )

        return consulta == 1
    }

    fun logarFuncionario(logarUsuarioEmail: String, logarUsuarioSenha: String): Usuario? {
        return jdbcTemplateServer.queryForObject(
            "SELECT idFuncionario, nome, email, senha, fkEmpFunc, fkNivelAcesso FROM Funcionario WHERE email = ? AND senha = ?",
            arrayOf(logarUsuarioEmail, logarUsuarioSenha),
            BeanPropertyRowMapper(Usuario::class.java)
        )
    }
    fun registrarLogin(usuarioLogado: Usuario, idMaq: Int, maquinaSpecs:Maquina, horaLogin: LocalDateTime) {
        jdbcTemplate.update(
            """
        INSERT INTO Login (Email, Id_do_dispositivo, dataHoraEntrada)
        VALUES (?, ?, ?)
        """.trimIndent(),
            usuarioLogado.email,
            maquinaSpecs.idCPU,
            horaLogin
        )
        jdbcTemplateServer.update(
            """
        INSERT INTO Login (idFuncionario, idMaquina, idEmpresa, Email, Id_do_dispositivo, dataHoraEntrada)
        VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent(),
            usuarioLogado.idFuncionario,
            idMaq,
            usuarioLogado.fkEmpFunc,
            usuarioLogado.email,
            maquinaSpecs.idCPU,
            horaLogin
        )
    }
    fun atualizarAtividade(usuarioLogado: Usuario, idMaq: Int, atividade: String) {
        jdbcTemplateServer.update(
            """
                UPDATE Login
                SET Atividade = '${atividade}'
                WHERE idFuncionario = ${usuarioLogado.idFuncionario} and idMaquina = $idMaq 
                and idEmpresa = ${usuarioLogado.fkEmpFunc};
        """.trimIndent(),

            )
    }
    fun registrarSaida(usuarioLogado: Usuario, idMaquina: Int, horaLogout: LocalDateTime) {
        jdbcTemplate.update(
            """
            UPDATE Login
            SET dataHoraSaida = '${horaLogout.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}'
            WHERE Email = '${usuarioLogado.email}';
        """.trimIndent()
        )

        jdbcTemplateServer.update(
            """
            UPDATE Login
            SET dataHoraSaida = '${horaLogout.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}'
            WHERE idFuncionario = ${usuarioLogado.idFuncionario}
            AND idMaquina = $idMaquina
            AND idEmpresa = ${usuarioLogado.fkEmpFunc};
        """.trimIndent()
        )
    }

}