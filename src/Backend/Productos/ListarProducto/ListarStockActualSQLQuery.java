package Backend.Productos.ListarProducto;

import Backend.Utils.dto.ComparadorSigno;
import Database.PGSQLClient;

import java.math.BigDecimal;
import java.sql.*;

public class ListarStockActualSQLQuery {
    private static final String LIST_BASE_SELECT =
            "SELECT p.id, p.nombre, p.descripcion, p.precio, s.cantidad, s.min, s.max " +
            "FROM producto p " +
            "LEFT JOIN stock s ON s.producto_id = p.id";


    public String executeListarProductos(PGSQLClient pgsqlClient, ComparadorSigno comparador) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {

            System.out.println("Connecting successfully to database");

            String sql;
            boolean esComparadorNulo = comparador == null;

            PreparedStatement ps;
            if (esComparadorNulo) {
                // Sin filtro: listar todos
                sql = LIST_BASE_SELECT + " ORDER BY p.id ASC";
                ps = connection.prepareStatement(sql);
            } else {
                String operador = mapOperadorSeguro(comparador.signo);
                if (operador == null) {
                    return "Error: operador inválido: " + comparador.signo;
                }
                sql = LIST_BASE_SELECT + " WHERE s.cantidad " + operador + " ? ORDER BY p.id ASC";
                ps = connection.prepareStatement(sql);
                ps.setBigDecimal(1, new java.math.BigDecimal(comparador.valor.toString()));
            }

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder result = new StringBuilder();

                while (rs.next()) {
                    result.append(formatearProducto(rs));
                }

                if (result.length() == 0) {
                    return "No se encontraron productos registrados.";
                }
                return result.toString();
            }

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
    public String executeListarProductosBetween(PGSQLClient pgsqlClient, int[] intervalo) {
        if (intervalo == null || intervalo.length != 2) {
            return "Error: se esperaban dos valores (mínimo y máximo)";
        }

        int min = intervalo[0];
        int max = intervalo[1];

        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        String sql = LIST_BASE_SELECT +
            " WHERE s.cantidad BETWEEN ? AND ? ORDER BY p.id ASC";

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword());
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, min);
            ps.setInt(2, max);

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder result = new StringBuilder();

                while (rs.next()) {
                    result.append(formatearProducto(rs));
                }

                if (result.length() == 0) {
                    return "Lista de Productos Vacia!";
                }

                return result.toString();
            }

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }

    private String mapOperadorSeguro(String signo) {
        if (signo == null) return null;
        switch (signo) {
            case ">":  return ">";
            case ">=": return ">=";
            case "<":  return "<";
            case "<=": return "<=";
            case "=":
            case "==": return "=";
            case "!=":
            case "<>": return "<>";
            default:   return null;
        }
    }

    private String formatearProducto(ResultSet rs) throws SQLException {
        return String.format(
                "✅ PRODUCTO\r\n" +
                        "--------------------------\r\n" +
                        "ID: %d\r\n" +
                        "Nombre: %s\r\n" +
                        "Descripción: %s\r\n" +
                "Precio: %s\r\n" +
                "Stock (cantidad): %d\r\n" +
                "Stock (min): %d\r\n" +
                "Stock (max): %d\r\n" +
                        "--------------------------\r\n",
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("descripcion"),
            rs.getBigDecimal("precio"),
            rs.getInt("cantidad"),
            rs.getInt("min"),
            rs.getInt("max")
        );
    }

}
