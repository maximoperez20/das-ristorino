package ar.edu.ubp.das.backend.dto;

import java.util.List;

public class MenusSucursalDto {
  private List<MenuDto> menus;

  public MenusSucursalDto() {
  }

  public MenusSucursalDto(List<MenuDto> menus) {
    this.menus = menus;
  }

  public List<MenuDto> getMenus() {
    return menus;
  }

  public void setMenus(List<MenuDto> menus) {
    this.menus = menus;
  }

}
