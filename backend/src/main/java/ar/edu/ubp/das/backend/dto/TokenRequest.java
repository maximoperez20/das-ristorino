package ar.edu.ubp.das.backend.dto;

/**
 * DTO para encapsular los datos necesarios para generar un token JWT.
 * Reemplaza múltiples parámetros String sueltos por un objeto tipado.
 */
public class TokenRequest {
    
    private String nroCliente;
    private String correo;
    private String nombre;
    private String apellido;
    
    public TokenRequest() {}
    
    public TokenRequest(String nroCliente, String correo, String nombre, String apellido) {
        this.nroCliente = nroCliente;
        this.correo = correo;
        this.nombre = nombre;
        this.apellido = apellido;
    }
    
    public String getNroCliente() {
        return nroCliente;
    }
    
    public void setNroCliente(String nroCliente) {
        this.nroCliente = nroCliente;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    public void setCorreo(String correo) {
        this.correo = correo;
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
}

