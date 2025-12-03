package ar.edu.ubp.das.backend.dto;
import java.util.Date;

public class ResenaRequestDto {
    
  private int calificacion;
  private String comentario;
  private Date fechaResena;
  private String nroRestaurante;
  private String nroSucursal;
  private String nroCliente;
    
    public ResenaRequestDto(
        int calificacion,
        String comentario,
        Date fechaResena,
        String nroRestaurante,
        String nroSucursal,
        String nroCliente) {
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fechaResena = fechaResena;
        this.nroRestaurante = nroRestaurante;
        this.nroSucursal = nroSucursal;
        this.nroCliente = nroCliente;
    }

    public int getCalificacion() {
        return calificacion;
    }
    public String getComentario() {
        return comentario;
    }
    public Date getFechaResena() {
        return fechaResena;
    }
    public String getNroRestaurante() {
        return nroRestaurante;
    }
    public String getNroSucursal() {
        return nroSucursal;
    }
    public String getNroCliente() {
        return nroCliente;
    }
    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    public void setFechaResena(Date fechaResena) {
        this.fechaResena = fechaResena;
    }
    public void setNroRestaurante(String nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }
    public void setNroSucursal(String nroSucursal) {
        this.nroSucursal = nroSucursal;
    }
    public void setNroCliente(String nroCliente) {
        this.nroCliente = nroCliente;
    }
}

