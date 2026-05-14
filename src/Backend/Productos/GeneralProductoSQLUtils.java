package Backend.Productos;

import Backend.Productos.dto.UpdateProductoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GeneralProductoSQLUtils {
    public static UpdateProductoDTO findProductoById(Connection con, long id) throws SQLException {
        String SQL_FIND =
                "SELECT p.id, p.nombre, p.descripcion, p.precio, s.cantidad, s.min, s.max " +
                "FROM producto p " +
                "LEFT JOIN stock s ON s.producto_id = p.id " +
                "WHERE p.id = ?";

        try (PreparedStatement ps = con.prepareStatement(SQL_FIND)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UpdateProductoDTO productoDTO = new UpdateProductoDTO();
                    productoDTO.id = rs.getLong("id");
                    productoDTO.nombre = rs.getString("nombre");
                    productoDTO.descripcion = rs.getString("descripcion");
                    productoDTO.precioVenta = rs.getBigDecimal("precio").floatValue();
                    productoDTO.stockActual = rs.getInt("cantidad");
                    productoDTO.stockMinimo = rs.getInt("min");
                    productoDTO.estado = null;
                    productoDTO.deleteAt = null;
                    return productoDTO;
                }
                return null;
            }
        }
    }
}
