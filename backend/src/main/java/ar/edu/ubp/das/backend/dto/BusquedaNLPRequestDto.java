package ar.edu.ubp.das.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para solicitud de búsqueda con lenguaje natural (NLP)
 */
public class BusquedaNLPRequestDto {

    @NotBlank(message = "La consulta es obligatoria")
    @Size(max = 500, message = "La consulta no puede exceder 500 caracteres")
    private String consulta;

    // Constructores
    public BusquedaNLPRequestDto() {}

    public BusquedaNLPRequestDto(String consulta) {
        this.consulta = consulta;
    }

    // Getters y Setters
    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }
}

