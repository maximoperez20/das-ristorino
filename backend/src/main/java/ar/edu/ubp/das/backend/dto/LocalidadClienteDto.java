package ar.edu.ubp.das.backend.dto;

/**
 * DTO simple para obtener la localidad de un cliente
 * Usado por el stored procedure sp_ObtenerLocalidadPorNroCliente
 */
public class LocalidadClienteDto {
    
    private String localidad;
    
    public LocalidadClienteDto() {}
    
    public LocalidadClienteDto(String localidad) {
        this.localidad = localidad;
    }
    
    public String getLocalidad() {
        return localidad;
    }
    
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
}
