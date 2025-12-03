package ar.edu.ubp.das.backend.dto;

/**
 * DTO para representar una resena de sucursal de un restaurante
 */
public class ResenasSucursalesDto {
    
    private String nroResena;
    private String nroRestaurante;
    private String nroSucursal;
    private String comentario;
    private Integer valoracion;  
    private String nroCliente;
    private String nombreCliente;
    
    public ResenasSucursalesDto() {}

    public ResenasSucursalesDto(String nroResena, String nroRestaurante, String nroSucursal, String comentario, Integer valoracion, String nroCliente, String nombreCliente) {
        this.nroResena = nroResena;
        this.nroRestaurante = nroRestaurante;
        this.nroSucursal = nroSucursal;
        this.comentario = comentario;
        this.valoracion = valoracion;
        this.nroCliente = nroCliente;
        this.nombreCliente = nombreCliente;
    }

    public String getNroResena() {
        return nroResena;
    }

    public void setNroResena(String nroResena) {
        this.nroResena = nroResena;
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

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Integer getValoracion() {
        return valoracion;
    }

    public void setValoracion(Integer valoracion) {
        this.valoracion = valoracion;
    }
    public String getNroCliente() {
        return nroCliente;
    }

    public void setNroCliente(String nroCliente) {
        this.nroCliente = nroCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }
    
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
}

