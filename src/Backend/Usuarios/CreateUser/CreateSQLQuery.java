package Backend.Usuarios.CreateUser;

import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.dto.CreateUsuarioDTO;
import Database.PGSQLClient;

import java.sql.*;

public class CreateSQLQuery {
    private static final String SQL_INSERT =
            "INSERT INTO \"user\" (rol_id, nombre, email, password) VALUES (?, ?, ?, ?)";
    public String executeInsertUserQuery(PGSQLClient pgsqlClient, CreateUsuarioDTO createUsuarioDTO) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Connecting successfully to database");
            if (GeneralUsuarioSQLUtils.existeUsuarioPorEmail(connection, createUsuarioDTO.email)) {
                return "Error: ya existe un usuario con el correo '" + createUsuarioDTO.email + "'.";
            }

            Integer rolId = GeneralUsuarioSQLUtils.findRolIdByNombre(connection, createUsuarioDTO.rol);
            if (rolId == null) {
                return "Error: rol inválido '" + createUsuarioDTO.rol + "'. Roles permitidos: propietario, vendedor, cliente.";
            }

            try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
                ps.setInt(1, rolId);
                ps.setString(2, createUsuarioDTO.nombre);
                ps.setString(3, createUsuarioDTO.email);
                ps.setString(4, createUsuarioDTO.password);

                int filas = ps.executeUpdate();

                if (filas == 0) {
                    return "Error: no se pudo insertar el usuario.";
                }
            }
            return String.format(
                    "Usuario creado exitosamente:\r\n" +
                            "--------------------------\r\n" +
                            "Nombre: %s\r\n" +
                            "Email: %s\r\n" +
                            "Rol: %s\r\n" +
                            "--------------------------\r\n",
                    createUsuarioDTO.nombre,
                    createUsuarioDTO.email,
                    createUsuarioDTO.rol
            );
        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }

}
