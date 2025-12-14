package ar.edu.ubp.das.backend.dto;

public class DatosCancelarReservaDto {

    private String nroRestaurante;
    private String codReservaSucursal;

    public String getNroRestaurante() {
        return nroRestaurante;
    }
    public void setNroRestaurante(String nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }
    public String getCodReservaSucursal() {
        return codReservaSucursal;
    }
    public void setCodReservaSucursal(String codReservaSucursal) {
        this.codReservaSucursal = codReservaSucursal;
    }
    
}
