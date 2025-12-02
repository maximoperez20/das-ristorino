package ar.edu.ubp.das.backend.dto;

import java.util.List;

/**
 * DTO para encapsular los parámetros de búsqueda NLP.
 * Reemplaza 7 parámetros sueltos por un objeto tipado, mejorando la legibilidad y mantenibilidad.
 * 
 * Este DTO se usa para llamar al stored procedure sp_BuscarRestaurantesPorNLP.
 */
public class BusquedaNLPParametrosDto {
    
    private List<String> tiposComida;
    private String barrio;
    private String localidad;
    private String ambiente;
    private String rangoPrecio;
    private List<String> palabrasClave;
    private String nroCliente;
    
    public BusquedaNLPParametrosDto() {}
    
    public BusquedaNLPParametrosDto(
            List<String> tiposComida,
            String barrio,
            String localidad,
            String ambiente,
            String rangoPrecio,
            List<String> palabrasClave,
            String nroCliente) {
        this.tiposComida = tiposComida;
        this.barrio = barrio;
        this.localidad = localidad;
        this.ambiente = ambiente;
        this.rangoPrecio = rangoPrecio;
        this.palabrasClave = palabrasClave;
        this.nroCliente = nroCliente;
    }
    
    /**
     * Constructor desde BusquedaNLPResponseDto (respuesta validada de OpenAI)
     */
    public BusquedaNLPParametrosDto(BusquedaNLPResponseDto respuestaNLP, String nroCliente) {
        this.tiposComida = respuestaNLP.getTipoComida();
        this.barrio = respuestaNLP.getBarrio();
        this.localidad = respuestaNLP.getLocalidad();
        this.ambiente = respuestaNLP.getAmbiente();
        this.rangoPrecio = respuestaNLP.getRangoPrecio();
        this.palabrasClave = respuestaNLP.getPalabrasClave();
        this.nroCliente = nroCliente;
    }
    
    // Getters y setters
    public List<String> getTiposComida() {
        return tiposComida;
    }
    
    public void setTiposComida(List<String> tiposComida) {
        this.tiposComida = tiposComida;
    }
    
    public String getBarrio() {
        return barrio;
    }
    
    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }
    
    public String getLocalidad() {
        return localidad;
    }
    
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
    
    public String getAmbiente() {
        return ambiente;
    }
    
    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }
    
    public String getRangoPrecio() {
        return rangoPrecio;
    }
    
    public void setRangoPrecio(String rangoPrecio) {
        this.rangoPrecio = rangoPrecio;
    }
    
    public List<String> getPalabrasClave() {
        return palabrasClave;
    }
    
    public void setPalabrasClave(List<String> palabrasClave) {
        this.palabrasClave = palabrasClave;
    }
    
    public String getNroCliente() {
        return nroCliente;
    }
    
    public void setNroCliente(String nroCliente) {
        this.nroCliente = nroCliente;
    }
    
    /**
     * Convierte los parámetros a formato requerido por el stored procedure.
     * El SP espera strings separados por comas, no listas.
     * 
     * @return Array de objetos en el orden esperado por el SP
     */
    public Object[] toStoredProcedureParameters() {
        String tiposComidaStr = tiposComida != null && !tiposComida.isEmpty() 
            ? String.join(",", tiposComida) : null;
        String barrioStr = barrio != null && !barrio.isEmpty() ? barrio : null;
        String localidadStr = localidad != null && !localidad.isEmpty() ? localidad : null;
        String ambienteStr = ambiente != null && !ambiente.isEmpty() ? ambiente : null;
        String rangoPrecioStr = rangoPrecio != null && !rangoPrecio.isEmpty() ? rangoPrecio : null;
        String palabrasClaveStr = palabrasClave != null && !palabrasClave.isEmpty() 
            ? String.join(",", palabrasClave) : null;
        
        return new Object[] {
            tiposComidaStr,
            barrioStr,
            localidadStr,
            ambienteStr,
            rangoPrecioStr,
            palabrasClaveStr,
            nroCliente
        };
    }
    
    /**
     * Verifica si hay al menos un filtro activo.
     * Útil para validar antes de ejecutar la búsqueda.
     */
    public boolean tieneFiltros() {
        return (tiposComida != null && !tiposComida.isEmpty()) ||
               (barrio != null && !barrio.isEmpty()) ||
               (localidad != null && !localidad.isEmpty()) ||
               (ambiente != null && !ambiente.isEmpty()) ||
               (rangoPrecio != null && !rangoPrecio.isEmpty()) ||
               (palabrasClave != null && !palabrasClave.isEmpty());
    }
}

