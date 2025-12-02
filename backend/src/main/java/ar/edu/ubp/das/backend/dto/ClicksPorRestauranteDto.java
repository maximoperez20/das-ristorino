package ar.edu.ubp.das.backend.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para agrupar clicks por restaurante.
 * Reemplaza el uso de Map<String, List<ClickNoNotificadoDto>> por un objeto tipado.
 */
public class ClicksPorRestauranteDto {
    
    private String nroRestaurante;
    private List<ClickNoNotificadoDto> clicks;
    
    public ClicksPorRestauranteDto() {
        this.clicks = new ArrayList<>();
    }
    
    public ClicksPorRestauranteDto(String nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
        this.clicks = new ArrayList<>();
    }
    
    public ClicksPorRestauranteDto(String nroRestaurante, List<ClickNoNotificadoDto> clicks) {
        this.nroRestaurante = nroRestaurante;
        this.clicks = clicks != null ? new ArrayList<>(clicks) : new ArrayList<>();
    }
    
    public void agregarClick(ClickNoNotificadoDto click) {
        if (click != null) {
            clicks.add(click);
        }
    }
    
    public String getNroRestaurante() {
        return nroRestaurante;
    }
    
    public void setNroRestaurante(String nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }
    
    public List<ClickNoNotificadoDto> getClicks() {
        return new ArrayList<>(clicks); // Retornar copia para inmutabilidad
    }
    
    public void setClicks(List<ClickNoNotificadoDto> clicks) {
        this.clicks = clicks != null ? new ArrayList<>(clicks) : new ArrayList<>();
    }
    
    public boolean isEmpty() {
        return clicks.isEmpty();
    }
    
    public int size() {
        return clicks.size();
    }
}

