package ar.edu.ubp.das.backend.dto;

/**
 * DTO para representar una sucursal de un restaurante
 */
public class SucursalDto {
    
    private String nroRestaurante;
    private String nroSucursal;
    private String nombre;
    private String direccion;
    private String localidad;
    private String provincia;
    private String codigoPostal;
    private String telefonos;
    private Integer capacidad;
    private Integer minToleranciaReserva; // en minutos
    
    // Constructores
    public SucursalDto() {}
    
    // Getters y Setters
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
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getLocalidad() {
        return localidad;
    }
    
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
    
    public String getProvincia() {
        return provincia;
    }
    
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    
    public String getCodigoPostal() {
        return codigoPostal;
    }
    
    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }
    
    public String getTelefonos() {
        return telefonos;
    }
    
    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }
    
    public Integer getCapacidad() {
        return capacidad;
    }
    
    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }
    
    public Integer getMinToleranciaReserva() {
        return minToleranciaReserva;
    }
    
    public void setMinToleranciaReserva(Integer minToleranciaReserva) {
        this.minToleranciaReserva = minToleranciaReserva;
    }
}

