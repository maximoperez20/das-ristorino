package ar.edu.ubp.das.backend.dto.response;

/**
 * DTO para respuesta de preferencias guardadas.
 */
public class PreferenciasGuardadasResponse {
    
    private final String mensaje;
    private final Integer preferenciasGuardadas;
    
    public PreferenciasGuardadasResponse(String mensaje, Integer preferenciasGuardadas) {
        this.mensaje = mensaje;
        this.preferenciasGuardadas = preferenciasGuardadas;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public Integer getPreferenciasGuardadas() {
        return preferenciasGuardadas;
    }
}

