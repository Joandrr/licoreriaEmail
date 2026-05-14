package Backend.Usuarios.CambiarEstado;

import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.dto.UpdateUsuarioDTO;
import Backend.Usuarios.dto.UsuarioEstadoDTO;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CambiarEstadoUsuarioSQL {
    private static final String SQL_DELETE =
            "DELETE FROM \"user\" WHERE id = ?";

    public String executeUpdateEstadoUsuario(PGSQLClient pgsqlClient, UsuarioEstadoDTO dto){
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        try{
            Connection connection = DriverManager.getConnection(databaseUrl,pgsqlClient.getUser(),pgsqlClient.getPassword());
            System.out.println("Connecting successfully to database");
            UpdateUsuarioDTO usuarioDTODB = GeneralUsuarioSQLUtils.findUserById(connection,dto.id);
            //usuario no esta en la base de datos
            if (usuarioDTODB == null) {
                return "No existe un usuario con id=" + dto.id + ". No se realizó ninguna actualización.";
            }

            if (dto.estado.equalsIgnoreCase("activo")) {
                return "El usuario ya existe y se considera activo en este esquema (no hay columna estado).";
            }

            if (!dto.estado.equalsIgnoreCase("eliminado")) {
                return "Estado no soportado: " + dto.estado;
            }

            try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
                ps.setLong(1, dto.id);
                int filas = ps.executeUpdate();
                if (filas == 0) {
                    return "El usuario fue modificado/eliminado durante la operación. No se eliminó nada.";
                }

                return String.format(
                        "Usuario eliminado correctamente:\r\n" +
                                "--------------------------\r\n" +
                                "ID: %d\r\n" +
                                "Nombre: %s\r\n" +
                                "Email: %s\r\n" +
                                "Rol: %s\r\n" +
                                "--------------------------\r\n",
                        usuarioDTODB.id,
                        usuarioDTODB.nombre,
                        usuarioDTODB.email,
                        usuarioDTODB.rol
                );
            }
        }catch(Exception e){
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage() ;
        }
    }
}
