package ar.edu.ubp.das.backend.dto;

import jakarta.validation.constraints.*;

/**
 * DTO para crear un nuevo cliente/usuario
 */
public class CrearClienteDto {
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 120, message = "El apellido no puede exceder 120 caracteres")
    private String apellido;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede exceder 120 caracteres")
    private String nombre;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String password;
    
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato del correo no es válido")
    @Size(max = 150, message = "El correo no puede exceder 150 caracteres")
    private String correo;
    
    @Size(max = 120, message = "El teléfono no puede exceder 120 caracteres")
    private String telefonos;
    
    @NotBlank(message = "La localidad es obligatoria")
    private String nroLocalidad; // UUID de la localidad
    
    // Constructores
    public CrearClienteDto() {}
    
    public CrearClienteDto(String apellido, String nombre, String password, String correo, String telefonos, String nroLocalidad) {
        this.apellido = apellido;
        this.nombre = nombre;
        this.password = password;
        this.correo = correo;
        this.telefonos = telefonos;
        this.nroLocalidad = nroLocalidad;
    }
    
    // Getters y Setters
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
    
    @Override
    public String toString() {
        return "CrearClienteDto{" +
                "apellido='" + apellido + '\'' +
                ", nombre='" + nombre + '\'' +
                ", password='[PROTECTED]'" +
                ", correo='" + correo + '\'' +
                ", telefonos='" + telefonos + '\'' +
                ", nroLocalidad='" + nroLocalidad + '\'' +
                '}';
    }
}

