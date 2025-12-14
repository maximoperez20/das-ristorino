package ar.edu.ubp.das.backend.dto.restaurante;

public class CancelarReservaJsonDto {
    private String nroReserva;

    public CancelarReservaJsonDto(String nroReserva){}


    public String getNroReserva() {
        return nroReserva;
    }

    public void setNroReserva(String nroReserva) {
        this.nroReserva = nroReserva;
    }
    
}
