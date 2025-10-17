package ar.edu.ubp.das.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CambiarEstadoDto {
    
    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 20, message = "El estado no puede exceder 20 caracteres")
    private String estado;
    
    // Constructores
    public CambiarEstadoDto() {}
    
    public CambiarEstadoDto(String estado) {
        this.estado = estado;
    }
    
    // Getters y Setters
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    @Override
    public String toString() {
        return "CambiarEstadoDto{" +
                "estado='" + estado + '\'' +
                '}';
    }
}
