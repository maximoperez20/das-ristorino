package ar.edu.ubp.das.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;

/**
 * DTO para representar un horario disponible para reservas
 */
public class HorarioDisponibleDto {
    
    @JsonProperty("hora_desde")
    private LocalTime horaDesde;
    
    @JsonProperty("hora_hasta")
    private LocalTime horaHasta;
    
    @JsonProperty("capacidad_zona")
    private Integer capacidadZona;
    
    @JsonProperty("ya_reservados")
    private Integer yaReservados;
    
    @JsonProperty("disponibilidad")
    private Integer disponibilidad;
    
    @JsonProperty("cod_zona")
    private String codZona;
    
    @JsonProperty("nom_zona")
    private String nomZona;
    
    @JsonProperty("permite_menores")
    private Boolean permiteMenores;

    // Constructores
    public HorarioDisponibleDto() {}

    public HorarioDisponibleDto(LocalTime horaDesde, LocalTime horaHasta, 
                                Integer capacidadZona, Integer yaReservados, 
                                Integer disponibilidad) {
        this.horaDesde = horaDesde;
        this.horaHasta = horaHasta;
        this.capacidadZona = capacidadZona;
        this.yaReservados = yaReservados;
        this.disponibilidad = disponibilidad;
    }

    // Getters y Setters
    public LocalTime getHoraDesde() {
        return horaDesde;
    }

    public void setHoraDesde(LocalTime horaDesde) {
        this.horaDesde = horaDesde;
    }

    public LocalTime getHoraHasta() {
        return horaHasta;
    }

    public void setHoraHasta(LocalTime horaHasta) {
        this.horaHasta = horaHasta;
    }

    public Integer getCapacidadZona() {
        return capacidadZona;
    }

    public void setCapacidadZona(Integer capacidadZona) {
        this.capacidadZona = capacidadZona;
    }

    public Integer getYaReservados() {
        return yaReservados;
    }

    public void setYaReservados(Integer yaReservados) {
        this.yaReservados = yaReservados;
    }

    public Integer getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(Integer disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public String getCodZona() {
        return codZona;
    }

    public void setCodZona(String codZona) {
        this.codZona = codZona;
    }

    public String getNomZona() {
        return nomZona;
    }

    public void setNomZona(String nomZona) {
        this.nomZona = nomZona;
    }

    public Boolean getPermiteMenores() {
        return permiteMenores;
    }

    public void setPermiteMenores(Boolean permiteMenores) {
        this.permiteMenores = permiteMenores;
    }

    @Override
    public String toString() {
        return "HorarioDisponibleDto{" +
                "horaDesde=" + horaDesde +
                ", horaHasta=" + horaHasta +
                ", capacidadZona=" + capacidadZona +
                ", yaReservados=" + yaReservados +
                ", disponibilidad=" + disponibilidad +
                ", codZona='" + codZona + '\'' +
                ", nomZona='" + nomZona + '\'' +
                ", permiteMenores=" + permiteMenores +
                '}';
    }
}

