package ar.edu.ubp.das.backend.dto;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResenaRequestDto {
    
    private int calificacion;
    private String comentario;
    private String nroReserva;
    
    // Constructor sin argumentos (requerido por Jackson)
    public ResenaRequestDto() {
    }
    
    // Constructor con argumentos (opcional, para facilitar creación)
    public ResenaRequestDto(
        int calificacion,
        String comentario,
        String nroReserva) {
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.nroReserva = nroReserva;
    }

    // Getters y Setters
    public int getCalificacion() {
        return calificacion;
    }
    
    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }
    
    public String getComentario() {
        return comentario;
    }
    
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    
    public String getNroReserva() {
        return nroReserva;
    }
    
    public void setNroReserva(String nroReserva) {
        this.nroReserva = nroReserva;
    }
}

