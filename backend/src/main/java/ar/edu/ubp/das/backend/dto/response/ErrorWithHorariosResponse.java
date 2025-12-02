package ar.edu.ubp.das.backend.dto.response;

/**
 * DTO para respuestas de error que incluyen horarios alternativos agrupados por zona.
 * Usado cuando una reserva falla por falta de disponibilidad.
 * Mantiene la misma estructura que la respuesta normal de horarios disponibles.
 */
public class ErrorWithHorariosResponse {
    
    private final String error;
    private final HorariosDisponiblesResponse horarios;
    
    public ErrorWithHorariosResponse(String error, HorariosDisponiblesResponse horarios) {
        this.error = error;
        this.horarios = horarios;
    }
    
    public String getError() {
        return error;
    }
    
    public HorariosDisponiblesResponse getHorarios() {
        return horarios;
    }
}
