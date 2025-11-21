package ar.edu.ubp.das.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para representar un dominio (valor) de una categoría de preferencia
 */
public class DominioPreferenciaDto {
    
    @JsonProperty("codCategoria")
    private String codCategoria;
    
    @JsonProperty("nroValorDominio")
    private Integer nroValorDominio;
    
    @JsonProperty("nombre")
    private String nombre;
    
    public DominioPreferenciaDto() {
    }
    
    public DominioPreferenciaDto(String codCategoria, Integer nroValorDominio, String nombre) {
        this.codCategoria = codCategoria;
        this.nroValorDominio = nroValorDominio;
        this.nombre = nombre;
    }
    
    public String getCodCategoria() {
        return codCategoria;
    }
    
    public void setCodCategoria(String codCategoria) {
        this.codCategoria = codCategoria;
    }
    
    public Integer getNroValorDominio() {
        return nroValorDominio;
    }
    
    public void setNroValorDominio(Integer nroValorDominio) {
        this.nroValorDominio = nroValorDominio;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

