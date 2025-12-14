package ar.edu.ubp.das.backend.dto.restaurante;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ObtenerMenuRequest {
    
    @JsonProperty("nroRestaurante")
    private String nroRestaurante;
    
    @JsonProperty("nroSucursal")
    private String nroSucursal;

    public ObtenerMenuRequest() {}

    public ObtenerMenuRequest(String nroRestaurante, String nroSucursal) {
        this.nroRestaurante = nroRestaurante;
        this.nroSucursal = nroSucursal;
    }   

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
}
