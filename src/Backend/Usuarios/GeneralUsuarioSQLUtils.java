package Backend.Usuarios;

import Backend.Usuarios.dto.UpdateUsuarioDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class GeneralUsuarioSQLUtils {
    private static final String SQL_EXISTS =
        "SELECT EXISTS(SELECT 1 FROM \"user\" WHERE id = ?)";
    private static final String SQL_EXISTS_EMAIL =
        "SELECT EXISTS(SELECT 1 FROM \"user\" WHERE email = ?)";

        private static final String[] ROLES_PERMITIDOS = {
            "propietario", "vendedor", "cliente"
        };

    public static boolean existsUser(Connection con, long id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_EXISTS)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }

    public static UpdateUsuarioDTO findUserById(Connection con, long id) throws SQLException {
        String SQL_FIND = "SELECT u.id, u.nombre, u.email, u.password, r.nombre AS rol " +
                "FROM \"user\" u " +
                "LEFT JOIN rol r ON u.rol_id = r.id " +
                "WHERE u.id = ?";

        try (PreparedStatement ps = con.prepareStatement(SQL_FIND)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UpdateUsuarioDTO usuario = new UpdateUsuarioDTO();
                    usuario.id = rs.getLong("id");
                    usuario.nombre = rs.getString("nombre");
                    usuario.email = rs.getString("email");
                    usuario.password = rs.getString("password");
                    usuario.rol = rs.getString("rol");
                    return usuario;
                }
                return null;
            }
        }
    }
    public static UpdateUsuarioDTO findUserByEmail(Connection con, String email) throws SQLException {
        String SQL_FIND = "SELECT u.id, u.nombre, u.email, u.password, r.nombre AS rol " +
                "FROM \"user\" u " +
                "LEFT JOIN rol r ON u.rol_id = r.id " +
                "WHERE u.email = ?";

        try (PreparedStatement ps = con.prepareStatement(SQL_FIND)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UpdateUsuarioDTO usuario = new UpdateUsuarioDTO();
                    usuario.id = rs.getLong("id");
                    usuario.nombre = rs.getString("nombre");
                    usuario.email = rs.getString("email");
                    usuario.password = rs.getString("password");
                    usuario.rol = rs.getString("rol");
                    return usuario;
                }
                return null; // No encontrado
            }
        }
    }

    public static Integer findRolIdByNombre(Connection con, String rolNombre) throws SQLException {
        String sql = "SELECT id FROM rol WHERE lower(nombre) = lower(?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rolNombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                return null;
            }
        }
    }

    public static boolean esRolPermitido(String rol) {
        return Arrays.asList(ROLES_PERMITIDOS).contains(rol.toLowerCase());
    }

    public static boolean existeUsuarioPorEmail(Connection connection, String email) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_EXISTS_EMAIL)) { // try() , para cerrar la sesion automatico
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }


}
