package Backend.Usuarios.UpdateUser;

import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.dto.UpdateUsuarioDTO;
import Database.PGSQLClient;

import java.sql.*;

public class UpdateSQLQuery {
    private static final String SQL_UPDATE =
            "UPDATE \"user\" SET rol_id = ?, nombre = ?, email = ?, password = ? WHERE id = ?";

    public String executeUpdateUserQuery(PGSQLClient pgsqlClient, UpdateUsuarioDTO updateUsuarioDTO){
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        try{
            Connection connection = DriverManager.getConnection(databaseUrl,pgsqlClient.getUser(),pgsqlClient.getPassword());
            System.out.println("Connecting successfully to database");
            UpdateUsuarioDTO usuarioDTODB = GeneralUsuarioSQLUtils.findUserById(connection,updateUsuarioDTO.id);
            //usuario no esta en la base de datos
            if (usuarioDTODB == null) {
                return "No existe un usuario con id=" + updateUsuarioDTO.id + ". No se realizó ninguna actualización.";
            }
            //si el usuario si esta presente
            //emails diferentes
            if(!usuarioDTODB.email.equals(updateUsuarioDTO.email)){
                //entonces busca si existe algun email ya registrado en la bd
                if(GeneralUsuarioSQLUtils.existeUsuarioPorEmail(connection,updateUsuarioDTO.email)){
                    return "El usuario ya se encuentra registrado en el Sistema";
                }
                //si no existe entonces realiza el update
            }
            //si los emails son iguales igual que actualize
            Integer rolId = GeneralUsuarioSQLUtils.findRolIdByNombre(connection, updateUsuarioDTO.rol);
            if (rolId == null) {
                return "Error: rol inválido '" + updateUsuarioDTO.rol + "'. Roles permitidos: propietario, vendedor, cliente.";
            }
            try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
                ps.setInt(1, rolId);
                ps.setString(2, updateUsuarioDTO.nombre);
                ps.setString(3, updateUsuarioDTO.email);
                ps.setString(4, updateUsuarioDTO.password);
                ps.setLong(5, updateUsuarioDTO.id);
                int filas = ps.executeUpdate();
                if (filas == 0) {
                    return "El usuario fue modificado/eliminado durante la operación. No se actualizó nada.";
                }
                return String.format(
                        "Usuario actualizado correctamente:\r\n" +
                                "--------------------------\r\n" +
                                "ID: %d\r\n" +
                                "Nombre: %s\r\n" +
                                "Email: %s\r\n" +
                                "Rol: %s\r\n" +
                                "--------------------------\r\n",
                        updateUsuarioDTO.id,
                        updateUsuarioDTO.nombre,
                        updateUsuarioDTO.email,
                        updateUsuarioDTO.rol
                );
            }
        }catch(Exception e){
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage() ;
        }
    }
}
