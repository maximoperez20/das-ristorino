package ar.edu.ubp.das.backend.dto.restaurante;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para serializar el JSON que se envía al servicio de restaurantes para notificar clicks en batch.
 * Reemplaza el uso de HashMap genérico por un objeto tipado.
 */
public class NotificarClicksBatchJsonDto {
    
    @JsonProperty("nroRestaurante")
    private String nroRestaurante;
    
    @JsonProperty("clicks")
    private List<NotificarClickJsonDto> clicks;
    
    public NotificarClicksBatchJsonDto() {
        this.clicks = new ArrayList<>();
    }
    
    public NotificarClicksBatchJsonDto(NotificarClicksBatchRequest request) {
        this.nroRestaurante = request.getNroRestaurante();
        this.clicks = new ArrayList<>();
        if (request.getClicks() != null) {
            for (NotificarClickRequest clickRequest : request.getClicks()) {
                this.clicks.add(new NotificarClickJsonDto(clickRequest));
            }
        }
    }
    
    public String getNroRestaurante() {
        return nroRestaurante;
    }
    
    public void setNroRestaurante(String nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }
    
    public List<NotificarClickJsonDto> getClicks() {
        return clicks != null ? new ArrayList<>(clicks) : new ArrayList<>();
    }
    
    public void setClicks(List<NotificarClickJsonDto> clicks) {
        this.clicks = clicks != null ? new ArrayList<>(clicks) : new ArrayList<>();
    }
}

