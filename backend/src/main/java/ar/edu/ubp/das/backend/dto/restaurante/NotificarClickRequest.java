package ar.edu.ubp.das.backend.dto.restaurante;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request genérico para notificar clicks en restaurantes.
 * Compatible con SOAP y REST.
 */
public class NotificarClickRequest {

    private String nroRestaurante;
    private String nroContenido;
    private String nroClick;
    private LocalDateTime fechaHoraRegistro;
    private String nroCliente;
    private BigDecimal costoClick;

    public NotificarClickRequest() {}

    public NotificarClickRequest(String nroRestaurante, String nroContenido, 
                                String nroClick, LocalDateTime fechaHoraRegistro,
                                String nroCliente, BigDecimal costoClick) {
        this.nroRestaurante = nroRestaurante;
        this.nroContenido = nroContenido;
        this.nroClick = nroClick;
        this.fechaHoraRegistro = fechaHoraRegistro;
        this.nroCliente = nroCliente;
        this.costoClick = costoClick;
    }

    public String getNroRestaurante() {
        return nroRestaurante;
    }

    public void setNroRestaurante(String nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }

    public String getNroContenido() {
        return nroContenido;
    }

    public void setNroContenido(String nroContenido) {
        this.nroContenido = nroContenido;
    }

    public String getNroClick() {
        return nroClick;
    }

    public void setNroClick(String nroClick) {
        this.nroClick = nroClick;
    }

    public LocalDateTime getFechaHoraRegistro() {
        return fechaHoraRegistro;
    }

    public void setFechaHoraRegistro(LocalDateTime fechaHoraRegistro) {
        this.fechaHoraRegistro = fechaHoraRegistro;
    }

    public String getNroCliente() {
        return nroCliente;
    }

    public void setNroCliente(String nroCliente) {
        this.nroCliente = nroCliente;
    }

    public BigDecimal getCostoClick() {
        return costoClick;
    }

    public void setCostoClick(BigDecimal costoClick) {
        this.costoClick = costoClick;
    }
}

