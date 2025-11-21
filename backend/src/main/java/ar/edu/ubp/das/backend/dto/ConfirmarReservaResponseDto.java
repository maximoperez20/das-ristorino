package ar.edu.ubp.das.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class ConfirmarReservaResponseDto {
    
    private String codigoReserva;
    private String nroRestaurante;
    private String nroSucursal;
    private String codZona;
    private LocalDate fechaReserva;
    private LocalTime horaDesde;
    private Integer cantAdultos;
    private Integer cantMenores;
    private BigDecimal costoReserva;
    private String mensaje;
    private String urlMapa;

    public ConfirmarReservaResponseDto() {}

    public String getCodigoReserva() {
        return codigoReserva;
    }

    public void setCodigoReserva(String codigoReserva) {
        this.codigoReserva = codigoReserva;
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

    public String getCodZona() {
        return codZona;
    }

    public void setCodZona(String codZona) {
        this.codZona = codZona;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalTime getHoraDesde() {
        return horaDesde;
    }

    public void setHoraDesde(LocalTime horaDesde) {
        this.horaDesde = horaDesde;
    }

    public Integer getCantAdultos() {
        return cantAdultos;
    }

    public void setCantAdultos(Integer cantAdultos) {
        this.cantAdultos = cantAdultos;
    }

    public Integer getCantMenores() {
        return cantMenores;
    }

    public void setCantMenores(Integer cantMenores) {
        this.cantMenores = cantMenores;
    }

    public BigDecimal getCostoReserva() {
        return costoReserva;
    }

    public void setCostoReserva(BigDecimal costoReserva) {
        this.costoReserva = costoReserva;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getUrlMapa() {
        return urlMapa;
    }

    public void setUrlMapa(String urlMapa) {
        this.urlMapa = urlMapa;
    }
}

