package Backend.Commands;


import Backend.Utils.GeneralMethods.GeneralMethods;
import SMTP.SMTPClient;

public class Comando {
    public static void executeComandoDemon(String emisor,String receptor,String server,String subject){

        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        String comandos = """
USUARIOS

createUser["nombre","email","password","rol: propietario|vendedor|cliente"]

updateUser["idUsuario","nombre","email","password","rol: propietario|vendedor|cliente"]

listarUsuarios["*"] | listarUsuarios["rol"]

cambiarEstadoUsuario["idUsuario","estado: eliminado"]


PRODUCTOS
createProducto["nombre","descripcion","precioVenta","stockMinimo","stockActual"]
updateProducto["productoId","nombre","descripcion","precioVenta","stockMinimo","stockActual"]
listarProductoSimple[">5"] | listarProductoSimple["*"]
listarProductoIntervalo["5","10"]
cambiarEstadoProducto["productoId","activo|eliminado"]


SERVICIOS
createServicio["nombre","descripcion","precio","duracion"]
updateServicio["servicioId","nombre","descripcion","precio","duracion"]
listarServicioSimple[">15"] | listarServicioSimple["*"]
listarServicioIntervalo["5","10"]
cambiarEstadoServicio["servicioId","activo|eliminado"]


PAGOS
createPago["ventaId","tipoPago","monto"]
deletePago["ventaId","pagoId"]
listarPagosDeVenta["ventaId"]


CITAS
cita_create["clienteId","barberoId","servicios","fechaInicio","fechaFin","observaciones","pagoInicial"]
cita_update["citaId","usuarioId","barberoId","fechaInicio","fechaFin"]
cita_list["clienteId","barberoId","estado","fechaInicio","fechaFin","servicioId","limit"]
cita_cancel["citaId","usuarioId","estado"]


REPORTES
FECHAS FORMATO YYYY-MM-DD

reporte_ventas_totales["fechaInicio","fechaFin"]
reporte_ventas_producto["fechaInicio","fechaFin"]
reporte_citas_barbero["fechaInicio","fechaFin"]
reporte_detalle_venta_cita["citaId"]
reporte_movimientos_inventario["productoId","fechaInicio","fechaFin"]


MOVIMIENTOS_DE_INVENTARIO
movimiento_inventario_create["productoId","usuarioId","tipo","cantidad","motivo"]
movimiento_inventario_update["movimientoId","cantidad","motivo","fecha"]
movimiento_inventario_list["productoId","usuarioId","fechaInicio","fechaFin","tipo","motivo","limit"]

Fechas Formato YYYY-MM-DD
""";
        comandos = GeneralMethods.parsearSubjectComillaTriplev2(comandos);
        smtpClientResponse.sendDataToServer("Faceritos Commands",comandos+ "\r\n");


    }
}
