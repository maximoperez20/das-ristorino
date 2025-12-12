package ar.edu.ubp.das.backend.dto;

// import com.fasterxml.jackson.annotation.JsonProperty;

public class DatosCancelarReservaDto {
    
    private String nroRestaurante;
    
    private String nroReservaRestaurante;

    public DatosCancelarReservaDto() {}

    public String getNroRestaurante() {
        return nroRestaurante;
    }

    public void setNroRestaurante(String nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }

    public String getNroReservaRestaurante() {
        return nroReservaRestaurante;
    }

    public void setNroReservaRestaurante(String nroReservaRestaurante) {
        this.nroReservaRestaurante = nroReservaRestaurante;
    }
}