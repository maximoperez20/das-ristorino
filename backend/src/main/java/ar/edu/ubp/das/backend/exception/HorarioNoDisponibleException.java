package ar.edu.ubp.das.backend.exception;

import ar.edu.ubp.das.backend.dto.HorarioDisponibleDto;
import java.util.List;

/**
 * Excepción lanzada cuando un horario seleccionado ya no está disponible.
 * Incluye la lista de horarios disponibles actualizados para que el usuario pueda seleccionar otro.
 */
public class HorarioNoDisponibleException extends RuntimeException {
    
    private final List<HorarioDisponibleDto> horariosDisponibles;
    
    public HorarioNoDisponibleException(String message, List<HorarioDisponibleDto> horarios) {
        super(message);
        this.horariosDisponibles = horarios;
    }
    
    public HorarioNoDisponibleException(String message, Throwable cause, List<HorarioDisponibleDto> horarios) {
        super(message, cause);
        this.horariosDisponibles = horarios;
    }
    
    public List<HorarioDisponibleDto> getHorariosDisponibles() {
        return horariosDisponibles;
    }
}

