package ar.edu.ubp.das.backend.dto;

/**
 * DTO para representar una sucursal de un restaurante
 */
public class ResenasSucursalesDto {
    
    private String nroResena;
    private String nroRestaurante;
    private String nroSucursal;
    private String comentario;
    private Integer valoracion;   
    
    public ResenasSucursalesDto() {}

    public ResenasSucursalesDto(String nroResena, String nroRestaurante, String nroSucursal, String comentario, Integer valoracion) {
        this.nroResena = nroResena;
        this.nroRestaurante = nroRestaurante;
        this.nroSucursal = nroSucursal;
        this.comentario = comentario;
        this.valoracion = valoracion;
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
}

