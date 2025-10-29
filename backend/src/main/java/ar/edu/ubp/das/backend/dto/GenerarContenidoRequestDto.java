package ar.edu.ubp.das.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para solicitar la generación de contenido con IA.
 * Requiere el restaurante y el idioma; la sucursal es opcional.
 */
public class GenerarContenidoRequestDto {

    @NotBlank(message = "El número de restaurante es obligatorio")
    @Size(max = 36, message = "El número de restaurante no puede exceder 36 caracteres")
    private String nroRestaurante;

    @Size(max = 36, message = "El número de sucursal no puede exceder 36 caracteres")
    private String nroSucursal; // Opcional, si no se especifica se usará info general del restaurante

    @NotBlank(message = "El número de idioma es obligatorio")
    @Size(max = 36, message = "El número de idioma no puede exceder 36 caracteres")
    private String nroIdioma;

    @Size(max = 500, message = "El contexto adicional no puede exceder 500 caracteres")
    private String contextoAdicional; // Opcional: info extra que el usuario quiera agregar al prompt

    @Size(max = 100, message = "El ID del prompt no puede exceder 100 caracteres")
    private String promptId; // Opcional: si no se especifica, se usa el prompt por defecto configurado en application.properties

    // Getters y Setters
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

    public String getNroIdioma() {
        return nroIdioma;
    }

    public void setNroIdioma(String nroIdioma) {
        this.nroIdioma = nroIdioma;
    }

    public String getContextoAdicional() {
        return contextoAdicional;
    }

    public void setContextoAdicional(String contextoAdicional) {
        this.contextoAdicional = contextoAdicional;
    }

    public String getPromptId() {
        return promptId;
    }

    public void setPromptId(String promptId) {
        this.promptId = promptId;
    }
}

