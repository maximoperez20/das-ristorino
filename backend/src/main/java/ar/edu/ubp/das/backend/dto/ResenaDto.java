package ar.edu.ubp.das.backend.dto;
import java.util.Date;

public class ResenaDto {
    
  private String nombreCliente;
  private int calificacion;
  private String comentario;
  private Date fechaResena;
    
    public ResenaDto() {
        this.nombreCliente = "";
        this.calificacion = 0;
        this.comentario = "";
        this.fechaResena = new Date();
    }
    
    public ResenaDto(
            String nombreCliente,
            int calificacion,
            String comentario,
            Date fechaResena) {
        this.nombreCliente = nombreCliente;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fechaResena = fechaResena;
    }
    
    // Getters (retornan copias para inmutabilidad)
    public String getNombreCliente() {
        return nombreCliente;
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
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
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
}

