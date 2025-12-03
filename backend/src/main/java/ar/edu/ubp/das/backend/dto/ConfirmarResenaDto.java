package ar.edu.ubp.das.backend.dto;

import jakarta.validation.constraints.NotNull;

public class ConfirmarResenaDto {
    @NotNull(message = "El id de la reserva es obligatorio")
    private String idReserva;

    @NotNull(message = "El comentario es obligatorio")
    private String comentario;

    @NotNull(message = "La valoración es obligatoria")
    private Integer valoracion;

    public ConfirmarResenaDto() {}

    public String getIdReserva() {
        return idReserva;
    }
    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }
    public String getComentario() {
        return comentario;
    }
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    public Integer getValoracion() {
        return valoracion;
    }
    public void setValoracion(Integer valoracion) {
        this.valoracion = valoracion;
    }
    
}
