package ar.edu.ubp.das.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para representar una preferencia de un cliente
 */
public class PreferenciaClienteDto {
    
    @JsonProperty("codCategoria")
    private String codCategoria;
    
    @JsonProperty("nombreCategoria")
    private String nombreCategoria;
    
    @JsonProperty("nroValorDominio")
    private Integer nroValorDominio;
    
    @JsonProperty("nombreDominio")
    private String nombreDominio;
    
    @JsonProperty("observaciones")
    private String observaciones;
    
    public PreferenciaClienteDto() {
    }
    
    public PreferenciaClienteDto(String codCategoria, String nombreCategoria, 
                                 Integer nroValorDominio, String nombreDominio, 
                                 String observaciones) {
        this.codCategoria = codCategoria;
        this.nombreCategoria = nombreCategoria;
        this.nroValorDominio = nroValorDominio;
        this.nombreDominio = nombreDominio;
        this.observaciones = observaciones;
    }
    
    public String getCodCategoria() {
        return codCategoria;
    }
    
    public void setCodCategoria(String codCategoria) {
        this.codCategoria = codCategoria;
    }
    
    public String getNombreCategoria() {
        return nombreCategoria;
    }
    
    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }
    
    public Integer getNroValorDominio() {
        return nroValorDominio;
    }
    
    public void setNroValorDominio(Integer nroValorDominio) {
        this.nroValorDominio = nroValorDominio;
    }
    
    public String getNombreDominio() {
        return nombreDominio;
    }
    
    public void setNombreDominio(String nombreDominio) {
        this.nombreDominio = nombreDominio;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}

