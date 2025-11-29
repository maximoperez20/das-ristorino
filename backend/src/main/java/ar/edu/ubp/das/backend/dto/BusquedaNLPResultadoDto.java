package ar.edu.ubp.das.backend.dto;

import java.util.List;

/**
 * DTO para respuesta de búsqueda NLP con resultados exactos y sugerencias.
 * 
 * - resultadosExactos: Restaurantes que coinciden exactamente con los criterios de búsqueda
 * - sugerencias: Restaurantes sugeridos basados en preferencias del usuario o populares
 */
public class BusquedaNLPResultadoDto {
    
    private List<RestauranteDto> resultadosExactos;
    private List<RestauranteDto> sugerencias;
    
    public BusquedaNLPResultadoDto() {}
    
    public BusquedaNLPResultadoDto(List<RestauranteDto> resultadosExactos, List<RestauranteDto> sugerencias) {
        this.resultadosExactos = resultadosExactos;
        this.sugerencias = sugerencias;
    }
    
    public List<RestauranteDto> getResultadosExactos() {
        return resultadosExactos;
    }
    
    public void setResultadosExactos(List<RestauranteDto> resultadosExactos) {
        this.resultadosExactos = resultadosExactos;
    }
    
    public List<RestauranteDto> getSugerencias() {
        return sugerencias;
    }
    
    public void setSugerencias(List<RestauranteDto> sugerencias) {
        this.sugerencias = sugerencias;
    }
}

