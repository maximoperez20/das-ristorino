package ar.edu.ubp.das.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO para guardar preferencias de un cliente
 */
public class GuardarPreferenciasDto {
    
    @JsonProperty("preferencias")
    @NotEmpty(message = "Debe proporcionar al menos una preferencia")
    @Valid
    private List<PreferenciaItemDto> preferencias;
    
    public GuardarPreferenciasDto() {
    }
    
    public GuardarPreferenciasDto(List<PreferenciaItemDto> preferencias) {
        this.preferencias = preferencias;
    }
    
    public List<PreferenciaItemDto> getPreferencias() {
        return preferencias;
    }
    
    public void setPreferencias(List<PreferenciaItemDto> preferencias) {
        this.preferencias = preferencias;
    }
    
    /**
     * DTO interno para cada item de preferencia
     */
    public static class PreferenciaItemDto {
        
        @JsonProperty("codCategoria")
        @NotNull(message = "codCategoria es obligatorio")
        private String codCategoria;
        
        @JsonProperty("nroValorDominio")
        @NotNull(message = "nroValorDominio es obligatorio")
        private Integer nroValorDominio;
        
        @JsonProperty("observaciones")
        private String observaciones;
        
        public PreferenciaItemDto() {
        }
        
        public PreferenciaItemDto(String codCategoria, Integer nroValorDominio, String observaciones) {
            this.codCategoria = codCategoria;
            this.nroValorDominio = nroValorDominio;
            this.observaciones = observaciones;
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
        
        public String getObservaciones() {
            return observaciones;
        }
        
        public void setObservaciones(String observaciones) {
            this.observaciones = observaciones;
        }
    }
}

