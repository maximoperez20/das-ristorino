package ar.edu.ubp.das.backend.dto.restaurante;

import java.math.BigDecimal;

/**
 * Request genérico para registrar contenido en restaurantes.
 * Compatible con SOAP y REST.
 */
public class RegistrarContenidoRequest {

    private String nroRestaurante;
    private String nroSucursal;
    private String contenidoAPublicar;
    private byte[] imagenAPublicar;
    private BigDecimal costoClick;

    public RegistrarContenidoRequest() {}

    public RegistrarContenidoRequest(String nroRestaurante, String nroSucursal, 
                                     String contenidoAPublicar, byte[] imagenAPublicar, 
                                     BigDecimal costoClick) {
        this.nroRestaurante = nroRestaurante;
        this.nroSucursal = nroSucursal;
        this.contenidoAPublicar = contenidoAPublicar;
        this.imagenAPublicar = imagenAPublicar;
        this.costoClick = costoClick;
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

    public String getContenidoAPublicar() {
        return contenidoAPublicar;
    }

    public void setContenidoAPublicar(String contenidoAPublicar) {
        this.contenidoAPublicar = contenidoAPublicar;
    }

    public byte[] getImagenAPublicar() {
        return imagenAPublicar;
    }

    public void setImagenAPublicar(byte[] imagenAPublicar) {
        this.imagenAPublicar = imagenAPublicar;
    }

    public BigDecimal getCostoClick() {
        return costoClick;
    }

    public void setCostoClick(BigDecimal costoClick) {
        this.costoClick = costoClick;
    }
}

