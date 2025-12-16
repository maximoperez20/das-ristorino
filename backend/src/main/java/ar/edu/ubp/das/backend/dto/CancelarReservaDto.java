package ar.edu.ubp.das.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CancelarReservaDto {
    
    @NotBlank(message = "El código de motivo de cancelación es obligatorio")
    private String codMotivoCancelacion;
    
    @Size(max = 400, message = "Las notas no pueden exceder los 400 caracteres")
    private String notas;

    public CancelarReservaDto() {
    }

    public CancelarReservaDto(String codMotivoCancelacion, String notas) {
        this.codMotivoCancelacion = codMotivoCancelacion;
        this.notas = notas;
    }

    public String getCodMotivoCancelacion() {
        return codMotivoCancelacion;
    }

    public void setCodMotivoCancelacion(String codMotivoCancelacion) {
        this.codMotivoCancelacion = codMotivoCancelacion;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
