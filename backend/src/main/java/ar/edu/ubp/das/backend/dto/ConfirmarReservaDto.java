package ar.edu.ubp.das.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ConfirmarReservaDto {
    
    @NotNull(message = "El número de restaurante es obligatorio")
    private String nroRestaurante;
    
    @NotNull(message = "El número de sucursal es obligatorio")
    private String nroSucursal;
    
    @NotNull(message = "El código de zona es obligatorio")
    private String codZona;
    
    @NotNull(message = "La fecha de reserva es obligatoria")
    private LocalDate fechaReserva;
    
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaDesde;
    
    @NotNull(message = "La cantidad de adultos es obligatoria")
    @Min(value = 1, message = "Debe haber al menos 1 adulto")
    private Integer cantAdultos;
    
    @NotNull(message = "La cantidad de menores es obligatoria")
    @Min(value = 0, message = "La cantidad de menores no puede ser negativa")
    private Integer cantMenores;

    private String observaciones;

    @NotNull(message = "Las preferencias de reserva son obligatorias")
    private List<Integer> preferenciasReserva;

    public ConfirmarReservaDto() {}

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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<Integer> getPreferenciasReserva() {
        return preferenciasReserva;
    }

    public void setPreferenciasReserva(List<Integer> preferenciasReserva) {
        this.preferenciasReserva = preferenciasReserva;
    }
}

