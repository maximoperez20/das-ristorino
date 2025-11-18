package ar.edu.ubp.das.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para representar una categoría de preferencia gastronómica
 */
public class CategoriaPreferenciaDto {
    
    @JsonProperty("codCategoria")
    private String codCategoria;
    
    @JsonProperty("nombre")
    private String nombre;
    
    public CategoriaPreferenciaDto() {
    }
    
    public CategoriaPreferenciaDto(String codCategoria, String nombre) {
        this.codCategoria = codCategoria;
        this.nombre = nombre;
    }
    
    public String getCodCategoria() {
        return codCategoria;
    }
    
    public void setCodCategoria(String codCategoria) {
        this.codCategoria = codCategoria;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

