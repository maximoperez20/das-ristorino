package ar.edu.ubp.das.backend.dto.restaurante;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class RegistrarReservaRequest {
    
    private String nroClienteRistorino;
    private ClienteDto datosCliente;
    private String nroRestaurante;
    private String nroSucursal;
    private String codZona;
    private LocalDate fechaReserva;
    private LocalTime horaDesde;
    private Integer cantAdultos;
    private Integer cantMenores;
    private BigDecimal costoReserva;
    private String observacionesReserva; //nuevo campo para observaciones del cliente

    public RegistrarReservaRequest() {}

    public String getNroClienteRistorino() {
        return nroClienteRistorino;
    }

    public void setNroClienteRistorino(String nroClienteRistorino) {
        this.nroClienteRistorino = nroClienteRistorino;
    }

    public ClienteDto getDatosCliente() {
        return datosCliente;
    }

    public void setDatosCliente(ClienteDto datosCliente) {
        this.datosCliente = datosCliente;
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

    public String getObservacionesReserva() {
        return observacionesReserva;
    }
    public void setObservacionesReserva(String observacionesReserva) {
        this.observacionesReserva = observacionesReserva;
    }
}

