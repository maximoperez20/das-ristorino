package ar.edu.ubp.das.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para representar una categoría con sus dominios
 */
public class CategoriaConDominiosDto {
    
    @JsonProperty("codCategoria")
    private String codCategoria;
    
    @JsonProperty("nombre")
    private String nombre;
    
    @JsonProperty("dominios")
    private List<DominioPreferenciaDto> dominios;
    
    public CategoriaConDominiosDto() {
        this.dominios = new ArrayList<>();
    }
    
    public CategoriaConDominiosDto(String codCategoria, String nombre) {
        this.codCategoria = codCategoria;
        this.nombre = nombre;
        this.dominios = new ArrayList<>();
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
    
    public List<DominioPreferenciaDto> getDominios() {
        return dominios;
    }
    
    public void setDominios(List<DominioPreferenciaDto> dominios) {
        this.dominios = dominios;
    }
}

