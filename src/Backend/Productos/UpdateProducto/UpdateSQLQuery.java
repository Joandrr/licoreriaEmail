package Backend.Productos.UpdateProducto;

import Backend.Productos.GeneralProductoSQLUtils;
import Backend.Productos.dto.UpdateProductoDTO;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class UpdateSQLQuery {
    private static final String SQL_UPDATE_PRODUCTO =
        "UPDATE producto SET nombre = ?, descripcion = ?, precio = ? WHERE id = ?";

    private static final String SQL_UPDATE_STOCK =
        "UPDATE stock SET cantidad = ?, min = ? WHERE producto_id = ?";

    private static final String SQL_INSERT_STOCK =
        "INSERT INTO stock (producto_id, cantidad, min, max) VALUES (?, ?, ?, ?)";

    public String executeUpdateProductoQuery(PGSQLClient pgsqlClient, UpdateProductoDTO updateProductoDTO) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        try {
            Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword());
            System.out.println("Connecting successfully to database");
            connection.setAutoCommit(false);
            UpdateProductoDTO updateProductoDTDB = GeneralProductoSQLUtils.findProductoById(connection, updateProductoDTO.id);
            if (updateProductoDTDB == null) {
                connection.rollback();
                return "Error El producto no se encuentra en el sistema";
            }
            try (PreparedStatement psProducto = connection.prepareStatement(SQL_UPDATE_PRODUCTO)) {
                psProducto.setString(1, updateProductoDTO.nombre);
                if (updateProductoDTO.descripcion == null) {
                    psProducto.setNull(2, java.sql.Types.VARCHAR);
                } else {
                    psProducto.setString(2, updateProductoDTO.descripcion);
                }
                psProducto.setBigDecimal(3, java.math.BigDecimal.valueOf(updateProductoDTO.precioVenta));
                psProducto.setLong(4, updateProductoDTO.id);

                int filasProducto = psProducto.executeUpdate();
                if (filasProducto == 0) {
                    connection.rollback();
                    return "El producto fue modificado/eliminado durante la operación. No se actualizó nada.";
                }
            }

            // Actualiza stock; si no existe, lo inserta
            int filasStock;
            try (PreparedStatement psStock = connection.prepareStatement(SQL_UPDATE_STOCK)) {
                psStock.setInt(1, updateProductoDTO.stockActual);
                psStock.setInt(2, updateProductoDTO.stockMinimo);
                psStock.setLong(3, updateProductoDTO.id);
                filasStock = psStock.executeUpdate();
            }
            if (filasStock == 0) {
                int cantidad = updateProductoDTO.stockActual;
                int min = updateProductoDTO.stockMinimo;
                int max = Math.max(cantidad, min);
                try (PreparedStatement psInsertStock = connection.prepareStatement(SQL_INSERT_STOCK)) {
                    psInsertStock.setLong(1, updateProductoDTO.id);
                    psInsertStock.setInt(2, cantidad);
                    psInsertStock.setInt(3, min);
                    psInsertStock.setInt(4, max);
                    int filasInsert = psInsertStock.executeUpdate();
                    if (filasInsert == 0) {
                        connection.rollback();
                        return "Error: no se pudo registrar el stock del producto.";
                    }
                }
            }

            connection.commit();
            return String.format(
                    "Producto actualizado exitosamente:\r\n" +
                            "----------------------------------\r\n" +
                            "ID: %d\r\n" +
                            "Nombre: %s\r\n" +
                            "Descripción: %s\r\n" +
                            "Precio: %.2f\r\n" +
                            "Stock (cantidad): %d\r\n" +
                            "Stock (min): %d\r\n" +
                            "----------------------------------\r\n",
                    updateProductoDTO.id,
                    updateProductoDTO.nombre,
                    updateProductoDTO.descripcion,
                    updateProductoDTO.precioVenta,
                    updateProductoDTO.stockActual,
                    updateProductoDTO.stockMinimo
            );
        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
}

