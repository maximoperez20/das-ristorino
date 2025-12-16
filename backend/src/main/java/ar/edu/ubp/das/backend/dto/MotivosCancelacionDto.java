package ar.edu.ubp.das.backend.dto;

public class MotivosCancelacionDto {
    private String cod_motivo_cancelacion;
    private String descripcion;

    public MotivosCancelacionDto() {}

    public MotivosCancelacionDto(String cod_motivo_cancelacion, String descripcion) {
        this.cod_motivo_cancelacion = cod_motivo_cancelacion;
        this.descripcion = descripcion;
    }

    public String getCod_motivo_cancelacion() {
        return cod_motivo_cancelacion;
    }

    public void setCod_motivo_cancelacion(String cod_motivo_cancelacion) {
        this.cod_motivo_cancelacion = cod_motivo_cancelacion    ;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }    
}
