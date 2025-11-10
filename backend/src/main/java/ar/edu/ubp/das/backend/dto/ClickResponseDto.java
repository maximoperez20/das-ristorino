package ar.edu.ubp.das.backend.dto;

import java.time.LocalDateTime;

/**
 * DTO para respuesta después de registrar un click
 */
public class ClickResponseDto {
    
    private String nroClick;
    private String nroRestaurante;
    private Integer nroIdioma;
    private String nroContenido;
    private LocalDateTime fechaHoraRegistro;
    private String nroCliente;
    private Double costoClick;
    private Boolean notificado;
    
    // Constructores
    public ClickResponseDto() {}
    
    // Getters y Setters
    public String getNroClick() {
        return nroClick;
    }
    
    public void setNroClick(String nroClick) {
        this.nroClick = nroClick;
    }
    
    public String getNroRestaurante() {
        return nroRestaurante;
    }
    
    public void setNroRestaurante(String nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }
    
    public Integer getNroIdioma() {
        return nroIdioma;
    }

    public void setNroIdioma(Integer nroIdioma) {
        this.nroIdioma = nroIdioma;
    }
    
    public String getNroContenido() {
        return nroContenido;
    }
    
    public void setNroContenido(String nroContenido) {
        this.nroContenido = nroContenido;
    }
    
    public LocalDateTime getFechaHoraRegistro() {
        return fechaHoraRegistro;
    }
    
    public void setFechaHoraRegistro(LocalDateTime fechaHoraRegistro) {
        this.fechaHoraRegistro = fechaHoraRegistro;
    }
    
    public String getNroCliente() {
        return nroCliente;
    }
    
    public void setNroCliente(String nroCliente) {
        this.nroCliente = nroCliente;
    }
    
    public Double getCostoClick() {
        return costoClick;
    }
    
    public void setCostoClick(Double costoClick) {
        this.costoClick = costoClick;
    }
    
    public Boolean getNotificado() {
        return notificado;
    }
    
    public void setNotificado(Boolean notificado) {
        this.notificado = notificado;
    }
}

