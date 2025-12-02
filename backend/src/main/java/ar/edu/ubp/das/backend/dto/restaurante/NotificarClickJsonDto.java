package ar.edu.ubp.das.backend.dto.restaurante;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * DTO para serializar el JSON que se envía al servicio de restaurantes para notificar clicks.
 * Reemplaza el uso de HashMap genérico por un objeto tipado.
 */
public class NotificarClickJsonDto {
    
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    @JsonProperty("nroRestaurante")
    private String nroRestaurante;
    
    @JsonProperty("nroContenido")
    private String nroContenido;
    
    @JsonProperty("nroClick")
    private String nroClick;
    
    @JsonProperty("fechaHoraRegistro")
    private String fechaHoraRegistro;
    
    @JsonProperty("nroCliente")
    private String nroCliente;
    
    @JsonProperty("costoClick")
    private BigDecimal costoClick;
    
    public NotificarClickJsonDto() {}
    
    public NotificarClickJsonDto(NotificarClickRequest request) {
        this.nroRestaurante = request.getNroRestaurante();
        this.nroContenido = request.getNroContenido();
        this.nroClick = request.getNroClick();
        this.fechaHoraRegistro = request.getFechaHoraRegistro().format(ISO_DATE_TIME);
        this.nroCliente = request.getNroCliente();
        this.costoClick = request.getCostoClick();
    }
    
    // Getters y setters
    public String getNroRestaurante() {
        return nroRestaurante;
    }
    
    public void setNroRestaurante(String nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }
    
    public String getNroContenido() {
        return nroContenido;
    }
    
    public void setNroContenido(String nroContenido) {
        this.nroContenido = nroContenido;
    }
    
    public String getNroClick() {
        return nroClick;
    }
    
    public void setNroClick(String nroClick) {
        this.nroClick = nroClick;
    }
    
    public String getFechaHoraRegistro() {
        return fechaHoraRegistro;
    }
    
    public void setFechaHoraRegistro(String fechaHoraRegistro) {
        this.fechaHoraRegistro = fechaHoraRegistro;
    }
    
    public String getNroCliente() {
        return nroCliente;
    }
    
    public void setNroCliente(String nroCliente) {
        this.nroCliente = nroCliente;
    }
    
    public BigDecimal getCostoClick() {
        return costoClick;
    }
    
    public void setCostoClick(BigDecimal costoClick) {
        this.costoClick = costoClick;
    }
}

