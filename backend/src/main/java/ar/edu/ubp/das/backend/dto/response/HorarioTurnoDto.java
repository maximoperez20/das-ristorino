package ar.edu.ubp.das.backend.dto.response;

import java.time.LocalTime;

/**
 * DTO para representar un turno/horario disponible.
 * Usado en respuestas agrupadas por zona.
 */
public class HorarioTurnoDto {
    
    private final LocalTime horaDesde;
    private final LocalTime horaHasta;
    private final Integer yaReservados;
    private final Integer disponibilidad;
    
    public HorarioTurnoDto(LocalTime horaDesde, LocalTime horaHasta, Integer yaReservados, Integer disponibilidad) {
        this.horaDesde = horaDesde;
        this.horaHasta = horaHasta;
        this.yaReservados = yaReservados;
        this.disponibilidad = disponibilidad;
    }
    
    public LocalTime getHoraDesde() {
        return horaDesde;
    }
    
    public LocalTime getHoraHasta() {
        return horaHasta;
    }
    
    public Integer getYaReservados() {
        return yaReservados;
    }
    
    public Integer getDisponibilidad() {
        return disponibilidad;
    }
}

