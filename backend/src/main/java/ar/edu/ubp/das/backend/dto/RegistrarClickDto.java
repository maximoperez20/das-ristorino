package ar.edu.ubp.das.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para registrar un click en una promoción/contenido
 */
public class RegistrarClickDto {
    
    @NotBlank(message = "El número de restaurante es obligatorio")
    @Size(max = 36, message = "El número de restaurante no puede exceder 36 caracteres")
    private String nroRestaurante;
    
    @NotNull(message = "El número de idioma es obligatorio")
    private Integer nroIdioma;
    
    @NotBlank(message = "El número de contenido es obligatorio")
    @Size(max = 36, message = "El número de contenido no puede exceder 36 caracteres")
    private String nroContenido;
    
    // nroCliente es opcional - si el usuario está autenticado se puede incluir
    @Size(max = 36, message = "El número de cliente no puede exceder 36 caracteres")
    private String nroCliente;
    
    // Constructores
    public RegistrarClickDto() {}
    
    public RegistrarClickDto(String nroRestaurante, Integer nroIdioma, String nroContenido, String nroCliente) {
        this.nroRestaurante = nroRestaurante;
        this.nroIdioma = nroIdioma;
        this.nroContenido = nroContenido;
        this.nroCliente = nroCliente;
    }
    
    // Getters y Setters
    public String getNroRestaurante() {
        return nroRestaurante;
    }
    
    public void setNroRestaurante(String nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }
    
    public Integer getNroIdioma() {
        return nroIdioma;
    }

    public void setNroIdioma(Integer nroIdioma) {
        this.nroIdioma = nroIdioma;
    }
    
    public String getNroContenido() {
        return nroContenido;
    }
    
    public void setNroContenido(String nroContenido) {
        this.nroContenido = nroContenido;
    }
    
    public String getNroCliente() {
        return nroCliente;
    }
    
    public void setNroCliente(String nroCliente) {
        this.nroCliente = nroCliente;
    }
}

