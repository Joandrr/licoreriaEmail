package Backend.Productos.ListarProducto;

import Backend.Utils.dto.IdentificadorStrDTO;
import Backend.Utils.GeneralMethods.Resultado;
import Database.PGSQLClient;
import SMTP.SMTPClient;
import Utils.SQLUtils;

public class ListarProductos {
    // Comando: listarProductos["*"]
    public static void executeListarProductosDemon(String emisor, String receptor, String server, String subject) {
        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER, SQLUtils.DB_GRUPO_PASSWORD, SQLUtils.DB_GRUPO_DB_NAME);
        SMTPClient smtpClientResponse = new SMTPClient(server, receptor, emisor);

        Resultado<IdentificadorStrDTO> resultadoMensajeDTO = IdentificadorStrDTO.createMensajePatronDTO(subject);
        if (!resultadoMensajeDTO.esExitoso()) {
            smtpClientResponse.sendDataToServer("Error", ("Error: " + resultadoMensajeDTO.getError() + "\r\n"));
            return;
        }

        String filtro = resultadoMensajeDTO.getValor().message;
        if (filtro == null || !filtro.equals("*")) {
            smtpClientResponse.sendDataToServer("Error", "Error: para listar productos use listarProductos[\"*\"]\r\n");
            return;
        }

        ListarStockActualSQLQuery query = new ListarStockActualSQLQuery();
        String resultado = query.executeListarProductos(pgsqlClient, null);

        System.out.println("[PRODUCTOS][LISTAR] RESULT:\n" + resultado);

        boolean esError = resultado != null && resultado.toLowerCase().startsWith("error");
        if (esError) {
            boolean enviado = smtpClientResponse.sendDataToServerWithStatus("Error", resultado + "\r\n");
            if (!enviado) {
                System.out.println("[SMTP][ERROR] No se pudo enviar la respuesta por correo (posible relay denied). Revisa permisos SMTP para enviar a dominios externos. ");
            }
            return;
        }
        boolean enviado = smtpClientResponse.sendDataToServerWithStatus("Listando los Productos", ("Listando los Productos\r\n" + resultado + "\r\n"));
        if (!enviado) {
            System.out.println("[SMTP][ERROR] No se pudo enviar la respuesta por correo (posible relay denied). Revisa permisos SMTP para enviar a dominios externos. ");
        }
    }
}
