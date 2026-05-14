package Backend.Usuarios.dto;

import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class CreateUsuarioDTO {
    public String nombre;
    public String email;
    public String password;
    public String rol;

    public CreateUsuarioDTO(){}
    public CreateUsuarioDTO(String nombre, String email, String password, String rol){
          this.nombre = nombre;
          this.email = email;
          this.password = password;
          this.rol = rol;
    }
    public static Resultado<CreateUsuarioDTO> crearUsuarioMedianteSubject(String subject) throws InvalidDataException {
        String[] data = TecnoUtils.procesarString(subject);
        // Verificamos cantidad mínima de campos
        if (data.length < 4) {
            return Resultado.error("Error: se esperaba que el usuario introduzca al menos 4 campos (nombre, email, password, rol)");
        }
        // createUser["Juan","juan@gmail.com","12345678","vendedor"]
        String nombre = data[0];
        String email = data[1];
        String password = data[2];
        String rol = data[3];

        if (GeneralMethods.esCampoNuloVacio(nombre)) {
            return Resultado.error("Error: el campo 'nombre' no puede ser nulo o vacio");
        }
        if (GeneralMethods.esCampoNuloVacio(email)) {
            return Resultado.error("Error: el campo 'email' no puede ser nulo o vacio");
        }
        if (GeneralMethods.esCampoNuloVacio(password)) {
            return Resultado.error("Error: el campo 'password' no puede ser nulo o vacio");
        }
        if (GeneralMethods.esCampoNuloVacio(rol)) {
            return Resultado.error("Error: el campo 'rol' no puede ser nulo o vacio");
        }

        if(!GeneralUsuarioSQLUtils.esRolPermitido(rol)){
            return Resultado.error("Error...el valor de rol es un valor diferente de lo esperado...");
        }
        if (password.length() < 6) {
            return Resultado.error("Error: la contraseña debe tener al menos 6 caracteres");
        }

        CreateUsuarioDTO usuario = new CreateUsuarioDTO(nombre, email, password, rol);
        return Resultado.ok(usuario);
    }


    @Override
    public String toString() {
        return "Usuario creado {" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }

    public String toStringCorreo() {
        return "Usuario creado {\r\n" +
                "  nombre = '" + nombre + "'\r\n" +
                "  email = '" + email + "'\r\n" +
                "  password = '" + password + "'\r\n" +
                "  rol = '" + rol + "'\r\n" +
                "}";
    }
    public String toStringCorreoHTML() {
        String html = """
<html>\r
  <body style="font-family: Arial, sans-serif; padding: 10px;">\r
    <h2 style="color:#4CAF50;">✅ Usuario creado exitosamente</h2>\r
        <p><b>Nombre:</b> %s</p>\r
    <p><b>Email:</b> %s</p>\r
    <p><b>Contraseña:</b> %s</p>\r
    <p><b>Rol:</b> %s</p>\r
    <br>\r
    <p>Bienvenido al sistema 🎉</p>\r
  </body>\r
</html>\r
""".formatted(nombre, email, password, rol);
        return html;
    }


}
