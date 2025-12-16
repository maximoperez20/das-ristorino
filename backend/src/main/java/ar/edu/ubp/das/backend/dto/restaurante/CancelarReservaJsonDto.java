package ar.edu.ubp.das.backend.dto.restaurante;

public class CancelarReservaJsonDto {
    private String nroReserva;
    private String motivoCancelacion;

    public CancelarReservaJsonDto(String nroReserva, String motivoCancelacion){
        this.nroReserva = nroReserva;
        this.motivoCancelacion = motivoCancelacion;
    }


    public String getNroReserva() {
        return nroReserva;
    }

    public void setNroReserva(String nroReserva) {
        this.nroReserva = nroReserva;
    }
    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }
    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }
    
}
