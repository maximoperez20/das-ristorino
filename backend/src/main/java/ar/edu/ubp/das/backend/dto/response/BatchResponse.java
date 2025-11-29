package ar.edu.ubp.das.backend.dto.response;

/**
 * DTO para respuestas de operaciones batch.
 */
public class BatchResponse {
    
    private final String mensaje;
    private final String estado;
    
    public BatchResponse(String mensaje, String estado) {
        this.mensaje = mensaje;
        this.estado = estado;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public String getEstado() {
        return estado;
    }
}

