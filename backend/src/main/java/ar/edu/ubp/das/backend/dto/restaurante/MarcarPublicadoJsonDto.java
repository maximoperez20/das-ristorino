package ar.edu.ubp.das.backend.dto.restaurante;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para serializar el JSON que se envía al servicio de restaurantes para marcar contenidos como publicados.
 * Reemplaza el uso de HashMap genérico por un objeto tipado.
 */
public class MarcarPublicadoJsonDto {
    
    @JsonProperty("nroContenidos")
    private List<String> nroContenidos;
    
    public MarcarPublicadoJsonDto() {
        this.nroContenidos = new ArrayList<>();
    }
    
    public MarcarPublicadoJsonDto(List<String> nroContenidos) {
        this.nroContenidos = nroContenidos != null ? new ArrayList<>(nroContenidos) : new ArrayList<>();
    }
    
    public List<String> getNroContenidos() {
        return new ArrayList<>(nroContenidos); // Retornar copia para inmutabilidad
    }
    
    public void setNroContenidos(List<String> nroContenidos) {
        this.nroContenidos = nroContenidos != null ? new ArrayList<>(nroContenidos) : new ArrayList<>();
    }
}

