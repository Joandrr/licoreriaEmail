package Backend.Productos.CreateProducto;

import Backend.Productos.dto.CreateProductoDTO;
import Database.PGSQLClient;

import java.math.BigDecimal;
import java.sql.*;

public class CreateSQLQuery {
    private static final String SQL_INSERT_PRODUCTO =
            "INSERT INTO producto (nombre, descripcion, precio) VALUES (?, ?, ?) RETURNING id";

    private static final String SQL_INSERT_STOCK =
            "INSERT INTO stock (producto_id, cantidad, min, max) VALUES (?, ?, ?, ?)";

    public String executeInsertProductoQuery(PGSQLClient pgsqlClient, CreateProductoDTO createProductoDTO) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Connecting successfully to database");
            connection.setAutoCommit(false);

            Long productoId;
            try (PreparedStatement psProducto = connection.prepareStatement(SQL_INSERT_PRODUCTO)) {
                psProducto.setString(1, createProductoDTO.nombre);
                if (createProductoDTO.descripcion == null) {
                    psProducto.setNull(2, Types.VARCHAR);
                } else {
                    psProducto.setString(2, createProductoDTO.descripcion);
                }
                psProducto.setBigDecimal(3, BigDecimal.valueOf(createProductoDTO.precioVenta));

                try (ResultSet rs = psProducto.executeQuery()) {
                    if (!rs.next()) {
                        connection.rollback();
                        return "Error: no se pudo insertar el producto.";
                    }
                    productoId = rs.getLong(1);
                }
            }

            int cantidad = createProductoDTO.stockActual;
            int min = createProductoDTO.stockMinimo;
            int max = Math.max(cantidad, min);

            try (PreparedStatement psStock = connection.prepareStatement(SQL_INSERT_STOCK)) {
                psStock.setLong(1, productoId);
                psStock.setInt(2, cantidad);
                psStock.setInt(3, min);
                psStock.setInt(4, max);
                int filas = psStock.executeUpdate();
                if (filas == 0) {
                    connection.rollback();
                    return "Error: no se pudo insertar el stock del producto.";
                }
            }

            connection.commit();
            return String.format(
                    "Producto creado exitosamente:\r\n" +
                            "-----------------------------\r\n" +
                            "ID: %d\r\n" +
                            "Nombre: %s\r\n" +
                            "Descripción: %s\r\n" +
                            "Precio: %.2f\r\n" +
                            "Stock (cantidad): %d\r\n" +
                            "Stock (min): %d\r\n" +
                            "Stock (max): %d\r\n" +
                            "-----------------------------\r\n",
                    productoId,
                    createProductoDTO.nombre,
                    createProductoDTO.descripcion,
                    createProductoDTO.precioVenta,
                    cantidad,
                    min,
                    max
            );

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
}
