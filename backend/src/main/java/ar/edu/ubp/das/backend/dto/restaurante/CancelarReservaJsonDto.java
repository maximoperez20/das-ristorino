package ar.edu.ubp.das.backend.dto.restaurante;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para serializar el JSON que se envía al servicio de restaurantes para cancelar una reserva.
 * Reemplaza el uso de HashMap genérico por un objeto tipado.
 */
public class CancelarReservaJsonDto {
    
    @JsonProperty("codReserva")
    private String codReserva;

    @JsonProperty("razonCancelacion")
    private String razonCancelacion;

    public String getRazonCancelacion() {
        return razonCancelacion;
    }

    public void setRazonCancelacion(String razonCancelacion) {
        this.razonCancelacion = razonCancelacion;
    }
    
    public CancelarReservaJsonDto() {
    }
    
    public CancelarReservaJsonDto(String codReserva, String razonCancelacion) {
        this.codReserva = codReserva;
        this.razonCancelacion = razonCancelacion;
    }
    
    public String getCodReserva() {
        return codReserva;
    }
    
    public void setCodReserva(String codReserva) {
        this.codReserva = codReserva;
    }
}
