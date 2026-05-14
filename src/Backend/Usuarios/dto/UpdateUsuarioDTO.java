package Backend.Usuarios.dto;

import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class UpdateUsuarioDTO extends CreateUsuarioDTO{
    public Long id;
    public UpdateUsuarioDTO(){
        super();
    }
    public UpdateUsuarioDTO(Long id, String nombre, String email, String password, String rol) {
        super(nombre, email, password, rol);
        this.id = id;
    }

    public static Resultado<UpdateUsuarioDTO> crearUpdateUsuarioMedianteSubject(String subject) throws InvalidDataException {
        String[] data = TecnoUtils.procesarString(subject);
        System.out.println(data.toString());
        System.out.println(data.length);
        if (data.length < 5) {
            return Resultado.error("Error: se esperaban al menos 5 campos (id, nombre, email, password, rol)");
        }
        String id = data[0];
        String subjectParaReutilizarCreate = String.format(
                "[\"%s\",\"%s\",\"%s\",\"%s\"]",
                data[1], data[2], data[3], data[4]
        );
        Resultado<CreateUsuarioDTO> resultadoCreateDTO = CreateUsuarioDTO.crearUsuarioMedianteSubject(subjectParaReutilizarCreate);
        if(!resultadoCreateDTO.esExitoso()){
            return Resultado.error(resultadoCreateDTO.getError());
        }
        if(GeneralMethods.esCampoNuloVacio(id)){
            return Resultado.error("Error..el campo id no puede ser nulo");
        }
        CreateUsuarioDTO createUsuarioDTO = resultadoCreateDTO.getValor();
        Long idDto;
        try {
            idDto = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return Resultado.error("Error: el campo 'id' debe ser numérico");
        }
        UpdateUsuarioDTO updateUsuarioDTO =  new UpdateUsuarioDTO(idDto, createUsuarioDTO.nombre, createUsuarioDTO.email, createUsuarioDTO.password, createUsuarioDTO.rol);
        return Resultado.ok(updateUsuarioDTO);
    }

    @Override
    public String toString() {
        return "Usuario actualizado {" +
                "id=" + id +
                ", nombre='" + super.nombre + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }

    public String toStringCorreo() {
        return "Usuario actualizado {\r\n" +
                "  id = " + id + "\r\n" +
                "  nombre = '" + nombre + "'\r\n" +
                "  email = '" + email + "'\r\n" +
                "  password = '" + password + "'\r\n" +
                "  rol = '" + rol + "'\r\n" +
                "}";
    }
}
