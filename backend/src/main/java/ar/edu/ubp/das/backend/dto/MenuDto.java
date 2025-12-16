package ar.edu.ubp.das.backend.dto;

import java.util.List;

public class MenuDto {
  private String nroMenu;
  private String nomMenu;
  private List<PlatoDto> platos;

  public MenuDto() {
  }

  public MenuDto(String nroMenu, String nomMenu, List<PlatoDto> platos) {
    this.nroMenu = nroMenu;
    this.nomMenu = nomMenu;
    this.platos = platos;
  }

  public String getNroMenu() {
    return nroMenu;
  }

  public void setNroMenu(String nroMenu) {
    this.nroMenu = nroMenu;
  }
  
  public String getNomMenu() {
    return nomMenu;
  }

  public void setNomMenu(String nomMenu) {
    this.nomMenu = nomMenu;
  }
  
  public List<PlatoDto> getPlatos() {
    return platos;
  }

  public void setPlatos(List<PlatoDto> platos) {
    this.platos = platos;
  }
  
}

