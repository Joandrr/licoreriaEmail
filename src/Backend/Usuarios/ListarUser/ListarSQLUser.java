package Backend.Usuarios.ListarUser;
import Database.PGSQLClient;
import java.sql.*;

public class ListarSQLUser {

    private static final String SQL_LISTAR_TODOS =
        "SELECT u.id, u.nombre, u.email, r.nombre AS rol " +
        "FROM \"user\" u " +
        "LEFT JOIN rol r ON u.rol_id = r.id " +
        "ORDER BY u.id ASC";

    private static final String SQL_LISTAR_POR_ROL =
        "SELECT u.id, u.nombre, u.email, r.nombre AS rol " +
        "FROM \"user\" u " +
        "LEFT JOIN rol r ON u.rol_id = r.id " +
        "WHERE lower(r.nombre) = lower(?) " +
        "ORDER BY u.id ASC";


    public String executeListarUsuarios(PGSQLClient pgsqlClient, String filtroRol) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {

            System.out.println("Connecting successfully to database");

            PreparedStatement ps;
            if (filtroRol.equals("*")) {
                ps = connection.prepareStatement(SQL_LISTAR_TODOS);
            } else {
                ps = connection.prepareStatement(SQL_LISTAR_POR_ROL);
                ps.setString(1, filtroRol);
            }

            try (ResultSet rs = ps.executeQuery()) {
                //StringBuilder result = new StringBuilder();
                StringBuilder result = new StringBuilder();

                int contador = 1;

                while (rs.next()) {
                    result.append(formatearUsuario(rs, contador++));
                }

                if (result.length() == 0) {
                    return "No se encontraron usuarios registrados.";
                }
                ///result.append(".");
                return result.toString();
            }

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }



    private String formatearUsuario(ResultSet rs, int numero) throws SQLException {
        long id = rs.getLong("id");
        String nombre = rs.getString("nombre");
        String email = rs.getString("email");
        String rol = rs.getString("rol");

        return String.format(
                "----------------------------------------------------\r\n" +
                "Usuario %d:\r\n" +
                        "ID: %d\r\n" +
                        "Nombre: %s\r\n" +
                        "Email: %s\r\n" +
                        "Rol: %s\r\n" +
                        "----------------------------------------------------\r\n",
            numero, id, nombre, email, rol
        );
    }
}
