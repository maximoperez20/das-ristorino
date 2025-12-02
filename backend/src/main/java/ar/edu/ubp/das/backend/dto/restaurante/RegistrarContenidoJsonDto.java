package ar.edu.ubp.das.backend.dto.restaurante;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * DTO para serializar el JSON que se envía al servicio de restaurantes.
 * Reemplaza el uso de HashMap genérico por un objeto tipado.
 */
public class RegistrarContenidoJsonDto {
    
    @JsonProperty("nroRestaurante")
    private String nroRestaurante;
    
    @JsonProperty("nroSucursal")
    private String nroSucursal;
    
    @JsonProperty("contenidoAPublicar")
    private String contenidoAPublicar;
    
    @JsonProperty("imagenAPublicar")
    private String imagenAPublicar; // Base64 encoded
    
    @JsonProperty("costoClick")
    private BigDecimal costoClick;
    
    public RegistrarContenidoJsonDto() {}
    
    public RegistrarContenidoJsonDto(RegistrarContenidoRequest request) {
        this.nroRestaurante = request.getNroRestaurante();
        this.nroSucursal = request.getNroSucursal();
        this.contenidoAPublicar = request.getContenidoAPublicar();
        if (request.getImagenAPublicar() != null) {
            this.imagenAPublicar = java.util.Base64.getEncoder().encodeToString(request.getImagenAPublicar());
        }
        this.costoClick = request.getCostoClick();
    }
    
    // Getters y setters
    public String getNroRestaurante() {
        return nroRestaurante;
    }
    
    public void setNroRestaurante(String nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }
    
    public String getNroSucursal() {
        return nroSucursal;
    }
    
    public void setNroSucursal(String nroSucursal) {
        this.nroSucursal = nroSucursal;
    }
    
    public String getContenidoAPublicar() {
        return contenidoAPublicar;
    }
    
    public void setContenidoAPublicar(String contenidoAPublicar) {
        this.contenidoAPublicar = contenidoAPublicar;
    }
    
    public String getImagenAPublicar() {
        return imagenAPublicar;
    }
    
    public void setImagenAPublicar(String imagenAPublicar) {
        this.imagenAPublicar = imagenAPublicar;
    }
    
    public BigDecimal getCostoClick() {
        return costoClick;
    }
    
    public void setCostoClick(BigDecimal costoClick) {
        this.costoClick = costoClick;
    }
}

