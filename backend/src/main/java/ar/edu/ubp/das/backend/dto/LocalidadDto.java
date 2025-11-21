package ar.edu.ubp.das.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para representar una localidad
 */
public class LocalidadDto {
    
    @JsonProperty("nroLocalidad")
    private String nroLocalidad; // UUID
    
    @JsonProperty("nombre")
    private String nombre;
    
    @JsonProperty("provincia")
    private String provincia;
    
    // Constructores
    public LocalidadDto() {}
    
    public LocalidadDto(String nroLocalidad, String nombre, String provincia) {
        this.nroLocalidad = nroLocalidad;
        this.nombre = nombre;
        this.provincia = provincia;
    }
    
    // Getters y Setters
    public String getNroLocalidad() {
        return nroLocalidad;
    }
    
    public void setNroLocalidad(String nroLocalidad) {
        this.nroLocalidad = nroLocalidad;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getProvincia() {
        return provincia;
    }
    
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
}
