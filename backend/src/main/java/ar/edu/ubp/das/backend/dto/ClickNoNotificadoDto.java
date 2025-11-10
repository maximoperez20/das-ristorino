package ar.edu.ubp.das.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ClickNoNotificadoDto {
    
    private String nroRestaurante;
    private Integer nroIdioma;
    private String nroContenido;
    private String nroClick;
    private LocalDateTime fechaHoraRegistro;
    private String nroCliente;
    private BigDecimal costoClick;
    private String codContenidoRestaurante;
    
    public ClickNoNotificadoDto() {}
    
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
    
    public String getNroClick() {
        return nroClick;
    }
    
    public void setNroClick(String nroClick) {
        this.nroClick = nroClick;
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
    
    public BigDecimal getCostoClick() {
        return costoClick;
    }
    
    public void setCostoClick(BigDecimal costoClick) {
        this.costoClick = costoClick;
    }
    
    public String getCodContenidoRestaurante() {
        return codContenidoRestaurante;
    }
    
    public void setCodContenidoRestaurante(String codContenidoRestaurante) {
        this.codContenidoRestaurante = codContenidoRestaurante;
    }
}
