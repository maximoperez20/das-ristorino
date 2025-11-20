package ar.edu.ubp.das.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para la respuesta de contenido generado por IA.
 * Contiene los UUIDs, el texto generado y las fechas de vigencia.
 * Documentación detallada en: openapi-docs.yaml
 */
public class ContenidoGeneradoDto {

    private String nroRestaurante;
    private String nroSucursal;
    private Integer nroIdioma;
    private String nroContenido;
    private String contenidoGenerado;
    private LocalDate fechaIniVigencia;
    private LocalDate fechaFinVigencia;
    private String nombreRestaurante;
    private String nombreSucursal;
    private BigDecimal costoClick;


    // Constructors
    public ContenidoGeneradoDto() {}

    public ContenidoGeneradoDto(String nroRestaurante, Integer nroIdioma, String nroContenido, 
                                String contenidoGenerado, LocalDate fechaIniVigencia, 
                                LocalDate fechaFinVigencia, String nroSucursal, BigDecimal costoClick) {
        this.nroRestaurante = nroRestaurante;
        this.nroIdioma = nroIdioma;
        this.nroContenido = nroContenido;
        this.contenidoGenerado = contenidoGenerado;
        this.fechaIniVigencia = fechaIniVigencia;
        this.fechaFinVigencia = fechaFinVigencia;
        this.nroSucursal = nroSucursal;
        this.costoClick = costoClick;
    }

    // Getters and Setters
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

    public String getContenidoGenerado() {
        return contenidoGenerado;
    }

    public void setContenidoGenerado(String contenidoGenerado) {
        this.contenidoGenerado = contenidoGenerado;
    }

    public LocalDate getFechaIniVigencia() {
        return fechaIniVigencia;
    }

    public void setFechaIniVigencia(LocalDate fechaIniVigencia) {
        this.fechaIniVigencia = fechaIniVigencia;
    }

    public LocalDate getFechaFinVigencia() {
        return fechaFinVigencia;
    }

    public void setFechaFinVigencia(LocalDate fechaFinVigencia) {
        this.fechaFinVigencia = fechaFinVigencia;
    }

    public BigDecimal getCostoClick() {
        return costoClick;
    }

    public void setCostoClick(BigDecimal costoClick) {
        this.costoClick = costoClick;
    }


    public String getNombreRestaurante() {
        return nombreRestaurante;
    }

    public void setNombreRestaurante(String nombreRestaurante) {
        this.nombreRestaurante = nombreRestaurante;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    @Override
    public String toString() {
        return "ContenidoGeneradoDto{" +
                "nroRestaurante='" + nroRestaurante + '\'' +
                ", nroSucursal='" + nroSucursal + '\'' +
                ", nroIdioma='" + nroIdioma + '\'' +
                ", nroContenido='" + nroContenido + '\'' +
                ", nombreRestaurante='" + nombreRestaurante + '\'' +
                ", nombreSucursal='" + nombreSucursal + '\'' +
                ", fechaIniVigencia=" + fechaIniVigencia +
                ", fechaFinVigencia=" + fechaFinVigencia +
                ", contenidoGenerado='" + (contenidoGenerado != null ? contenidoGenerado.substring(0, Math.min(50, contenidoGenerado.length())) + "..." : "null") + '\'' +
                '}';
    }
}

