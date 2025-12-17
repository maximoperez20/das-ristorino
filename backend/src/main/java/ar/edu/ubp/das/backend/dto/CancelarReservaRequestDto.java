package ar.edu.ubp.das.backend.dto;

public class CancelarReservaRequestDto {
  
  private String razonCancelacion;

  public CancelarReservaRequestDto() {}

  public String getRazonCancelacion() {
    return razonCancelacion;
  }

  public void setRazonCancelacion(String razonCancelacion) {
    this.razonCancelacion = razonCancelacion;
  }
}
