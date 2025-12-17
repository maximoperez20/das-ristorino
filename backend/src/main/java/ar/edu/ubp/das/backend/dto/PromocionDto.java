package ar.edu.ubp.das.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PromocionDto {
    
    // UUIDs de la base de datos (claves reales)
    private String nroRestaurante;
    private Integer nroIdioma;
    private String nroContenido;
    
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no puede exceder 100 caracteres")
    private String titulo;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
    
    @NotNull(message = "El descuento es obligatorio")
    @DecimalMin(value = "0.0", message = "El descuento debe ser al menos 0.0")
    @DecimalMax(value = "100.0", message = "El descuento no puede exceder 100.0")
    private BigDecimal descuentoPorcentaje;
    
    @DecimalMin(value = "0.0", message = "El descuento fijo debe ser al menos 0.0")
    private BigDecimal descuentoFijo;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;
    
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;
    
    @NotNull(message = "El estado es obligatorio")
    private String estado = "ACTIVA"; // ACTIVA, INACTIVA, EXPIRADA
    
    private String imagenUrl;
    
    @Min(value = 1, message = "El mínimo de personas debe ser al menos 1")
    private Integer minPersonas;
    
    @Min(value = 1, message = "El máximo de personas debe ser al menos 1")
    private Integer maxPersonas;
    
    private String codigoPromocion; // Código único para aplicar la promoción
    
    private Boolean requiereCodigo = false;

    private String propositoCorto; // Nuevo: propósito corto del contenido
    
    // Nuevo: costo por click y sucursal asociada (si aplica)
    private java.math.BigDecimal costoClick;
    private String nroSucursal;
    
    // Constructores
    public PromocionDto() {}
    
    // Getters y Setters
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
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public BigDecimal getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }
    
    public void setDescuentoPorcentaje(BigDecimal descuentoPorcentaje) {
        this.descuentoPorcentaje = descuentoPorcentaje;
    }
    
    public BigDecimal getDescuentoFijo() {
        return descuentoFijo;
    }
    
    public void setDescuentoFijo(BigDecimal descuentoFijo) {
        this.descuentoFijo = descuentoFijo;
    }
    
    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }
    
    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public LocalDateTime getFechaFin() {
        return fechaFin;
    }
    
    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getImagenUrl() {
        return imagenUrl;
    }
    
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
    
    public Integer getMinPersonas() {
        return minPersonas;
    }
    
    public void setMinPersonas(Integer minPersonas) {
        this.minPersonas = minPersonas;
    }
    
    public Integer getMaxPersonas() {
        return maxPersonas;
    }
    
    public void setMaxPersonas(Integer maxPersonas) {
        this.maxPersonas = maxPersonas;
    }
    
    public String getCodigoPromocion() {
        return codigoPromocion;
    }
    
    public void setCodigoPromocion(String codigoPromocion) {
        this.codigoPromocion = codigoPromocion;
    }
    
    public Boolean getRequiereCodigo() {
        return requiereCodigo;
    }
    
    public void setRequiereCodigo(Boolean requiereCodigo) {
        this.requiereCodigo = requiereCodigo;
    }

    public String getPropositoCorto() {
        return propositoCorto;
    }

    public void setPropositoCorto(String propositoCorto) {
        this.propositoCorto = propositoCorto;
    }

    public java.math.BigDecimal getCostoClick() {
        return costoClick;
    }

    public void setCostoClick(java.math.BigDecimal costoClick) {
        this.costoClick = costoClick;
    }

    public String getNroSucursal() {
        return nroSucursal;
    }

    public void setNroSucursal(String nroSucursal) {
        this.nroSucursal = nroSucursal;
    }
    
    @Override
    public String toString() {
        return "PromocionDto{" +
                "nroRestaurante='" + nroRestaurante + '\'' +
                ", nroIdioma='" + nroIdioma + '\'' +
                ", nroContenido='" + nroContenido + '\'' +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", descuentoPorcentaje=" + descuentoPorcentaje +
                ", descuentoFijo=" + descuentoFijo +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", estado='" + estado + '\'' +
                ", imagenUrl='" + imagenUrl + '\'' +
                ", minPersonas=" + minPersonas +
                ", maxPersonas=" + maxPersonas +
                ", codigoPromocion='" + codigoPromocion + '\'' +
                ", requiereCodigo=" + requiereCodigo +
                ", costoClick=" + costoClick +
                ", nroSucursal='" + nroSucursal + '\'' +
                '}';
    }
}
