package ar.edu.ubp.das.backend.dto.response;

import java.util.List;

/**
 * DTO para representar una zona con sus horarios disponibles.
 */
public class ZonaConHorariosDto {
    
    private String codZona;
    private String nomZona;
    private Integer capacidadZona;
    private Boolean permiteMenores;
    private List<HorarioTurnoDto> horarios;
    
    public ZonaConHorariosDto() {}
    
    public ZonaConHorariosDto(String codZona, String nomZona, Integer capacidadZona, 
                              Boolean permiteMenores, List<HorarioTurnoDto> horarios) {
        this.codZona = codZona;
        this.nomZona = nomZona;
        this.capacidadZona = capacidadZona;
        this.permiteMenores = permiteMenores;
        this.horarios = horarios;
    }
    
    public void setCodZona(String codZona) {
        this.codZona = codZona;
    }
    
    public void setNomZona(String nomZona) {
        this.nomZona = nomZona;
    }
    
    public void setCapacidadZona(Integer capacidadZona) {
        this.capacidadZona = capacidadZona;
    }
    
    public void setPermiteMenores(Boolean permiteMenores) {
        this.permiteMenores = permiteMenores;
    }
    
    public void setHorarios(List<HorarioTurnoDto> horarios) {
        this.horarios = horarios;
    }
    
    public String getCodZona() {
        return codZona;
    }
    
    public String getNomZona() {
        return nomZona;
    }
    
    public Integer getCapacidadZona() {
        return capacidadZona;
    }
    
    public Boolean getPermiteMenores() {
        return permiteMenores;
    }
    
    public List<HorarioTurnoDto> getHorarios() {
        return horarios;
    }
}

