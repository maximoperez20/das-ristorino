package ar.edu.ubp.das.backend.dto.response;

import ar.edu.ubp.das.backend.dto.HorarioDisponibleDto;
import java.util.List;

/**
 * DTO para respuestas de error que incluyen horarios alternativos.
 * Usado cuando una reserva falla por falta de disponibilidad.
 */
public class ErrorWithHorariosResponse {
    
    private final String error;
    private final List<HorarioDisponibleDto> horarios;
    
    public ErrorWithHorariosResponse(String error, List<HorarioDisponibleDto> horarios) {
        this.error = error;
        this.horarios = horarios;
    }
    
    public String getError() {
        return error;
    }
    
    public List<HorarioDisponibleDto> getHorarios() {
        return horarios;
    }
}
