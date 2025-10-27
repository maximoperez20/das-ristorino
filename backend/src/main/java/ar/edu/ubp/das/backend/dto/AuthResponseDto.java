package ar.edu.ubp.das.backend.dto;

/**
 * DTO para respuesta de autenticación (login/registro)
 * Contiene el token JWT y datos básicos del usuario
 */
public class AuthResponseDto {
    
    private String token;
    private String nroCliente;
    private String nombre;
    private String apellido;
    private String correo;
    
    // Constructores
    public AuthResponseDto() {}
    
    public AuthResponseDto(String token, String nroCliente, String nombre, String apellido, String correo) {
        this.token = token;
        this.nroCliente = nroCliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
    }
    
    // Getters y Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getNroCliente() {
        return nroCliente;
    }
    
    public void setNroCliente(String nroCliente) {
        this.nroCliente = nroCliente;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    public void setCorreo(String correo) {
        this.correo = correo;
    }
}

