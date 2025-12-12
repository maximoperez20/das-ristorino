package ar.edu.ubp.das.backend.dto.restaurante;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para serializar el JSON que se envía al servicio de restaurantes para cancelar una reserva.
 * Reemplaza el uso de HashMap genérico por un objeto tipado.
 */
public class CancelarReservaJsonDto {
    
    @JsonProperty("codReserva")
    private String codReserva;
    
    public CancelarReservaJsonDto() {
    }
    
    public CancelarReservaJsonDto(String codReserva) {
        this.codReserva = codReserva;
    }
    
    public String getCodReserva() {
        return codReserva;
    }
    
    public void setCodReserva(String codReserva) {
        this.codReserva = codReserva;
    }
}
