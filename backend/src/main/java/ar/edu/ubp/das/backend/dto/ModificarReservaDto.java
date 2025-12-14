package ar.edu.ubp.das.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ModificarReservaDto {
  
  @JsonProperty("nroRestaurante")
  private String nroRestaurante;

  @JsonProperty("nroSucursal")
  private String nroSucursal;

  @JsonProperty("codZona")
  private String codZona;

  @JsonProperty("fechaReserva")
  private String fechaReserva;
  
  @JsonProperty("horaDesde")
  private String horaDesde;

  @JsonProperty("cantAdultos")
  private Integer cantAdultos;
  
  @JsonProperty("cantMenores")
  private Integer cantMenores;

  @JsonProperty("preferenciasReserva")
  private List<Integer> preferenciasReserva;

  
  public ModificarReservaDto() {
  }

  public ModificarReservaDto(String nroRestaurante, String nroSucursal, String codZona, String fechaReserva, String horaDesde, Integer cantAdultos, Integer cantMenores, List<Integer> preferenciasReserva) {
    this.nroRestaurante = nroRestaurante;
    this.nroSucursal = nroSucursal;
    this.codZona = codZona;
    this.horaDesde = horaDesde;
    this.cantAdultos = cantAdultos;
    this.cantMenores = cantMenores;
    this.preferenciasReserva = preferenciasReserva;
    this.fechaReserva = fechaReserva;
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

  public String getHoraDesde() {
    return horaDesde;
  }

  public void setHoraDesde(String horaDesde) {
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

  public List<Integer> getPreferenciasReserva() {
    return preferenciasReserva;
  }

  public void setPreferenciasReserva(List<Integer> preferenciasReserva) {
    this.preferenciasReserva = preferenciasReserva;
  }

  public String getFechaReserva() {
    return fechaReserva;
  }

  public void setFechaReserva(String fechaReserva) {
    this.fechaReserva = fechaReserva;
  }
}
