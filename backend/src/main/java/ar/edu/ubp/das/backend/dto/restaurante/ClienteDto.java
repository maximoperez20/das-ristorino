package ar.edu.ubp.das.backend.dto.restaurante;

/**
 * DTO genérico para datos de cliente en comunicaciones con restaurantes (REST/SOAP).
 * No tiene anotaciones específicas de protocolo ya que se usa tanto para JSON como para XML.
 */
public class ClienteDto {
    
    private String apellido;
    private String nombre;
    private String correo;
    private String telefonos;

    public ClienteDto() {}

    public ClienteDto(String apellido, String nombre, String correo, String telefonos) {
        this.apellido = apellido;
        this.nombre = nombre;
        this.correo = correo;
        this.telefonos = telefonos;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }
}

