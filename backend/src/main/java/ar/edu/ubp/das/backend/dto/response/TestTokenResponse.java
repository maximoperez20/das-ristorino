package ar.edu.ubp.das.backend.dto.response;

/**
 * DTO para respuesta de token de testing.
 */
public class TestTokenResponse {
    
    private final String token;
    private final String nroCliente;
    private final String correo;
    private final String nombre;
    private final String apellido;
    private final String note;
    
    public TestTokenResponse(String token, String nroCliente, String correo, 
                            String nombre, String apellido, String note) {
        this.token = token;
        this.nroCliente = nroCliente;
        this.correo = correo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.note = note;
    }
    
    public String getToken() {
        return token;
    }
    
    public String getNroCliente() {
        return nroCliente;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public String getNote() {
        return note;
    }
}

