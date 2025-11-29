package ar.edu.ubp.das.backend.dto.response;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para respuesta de horarios disponibles agrupados por zona.
 */
public class HorariosDisponiblesResponse {
    
    private final List<ZonaConHorariosDto> zonas;
    private final Integer totalZonas;
    private final LocalDate fecha;
    
    public HorariosDisponiblesResponse(List<ZonaConHorariosDto> zonas, LocalDate fecha) {
        this.zonas = zonas;
        this.totalZonas = zonas != null ? zonas.size() : 0;
        this.fecha = fecha;
    }
    
    public List<ZonaConHorariosDto> getZonas() {
        return zonas;
    }
    
    public Integer getTotalZonas() {
        return totalZonas;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
}

