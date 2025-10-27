package ar.edu.ubp.das.backend.dto;

/**
 * DTO para respuesta de cliente creado (sin exponer la contraseña)
 */
public class ClienteResponseDto {
    
    private String nroCliente;
    private String apellido;
    private String nombre;
    private String correo;
    private String telefonos;
    private String nroLocalidad;
    private Boolean habilitado;
    
    // Constructores
    public ClienteResponseDto() {}
    
    public ClienteResponseDto(String nroCliente, String apellido, String nombre, String correo, String telefonos, String nroLocalidad, Boolean habilitado) {
        this.nroCliente = nroCliente;
        this.apellido = apellido;
        this.nombre = nombre;
        this.correo = correo;
        this.telefonos = telefonos;
        this.nroLocalidad = nroLocalidad;
        this.habilitado = habilitado;
    }
    
    // Getters y Setters
    public String getNroCliente() {
        return nroCliente;
    }
    
    public void setNroCliente(String nroCliente) {
        this.nroCliente = nroCliente;
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
    
    public String getNroLocalidad() {
        return nroLocalidad;
    }
    
    public void setNroLocalidad(String nroLocalidad) {
        this.nroLocalidad = nroLocalidad;
    }
    
    public Boolean getHabilitado() {
        return habilitado;
    }
    
    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }
    
    @Override
    public String toString() {
        return "ClienteResponseDto{" +
                "nroCliente='" + nroCliente + '\'' +
                ", apellido='" + apellido + '\'' +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", telefonos='" + telefonos + '\'' +
                ", nroLocalidad='" + nroLocalidad + '\'' +
                ", habilitado=" + habilitado +
                '}';
    }
}

