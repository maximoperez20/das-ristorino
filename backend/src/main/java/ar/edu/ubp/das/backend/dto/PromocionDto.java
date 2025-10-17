package ar.edu.ubp.das.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PromocionDto {
    
    private Long id;
    
    @NotNull(message = "El ID del restaurante es obligatorio")
    private Long restauranteId;
    
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
    
    // Constructores
    public PromocionDto() {}
    
    public PromocionDto(Long restauranteId, String titulo, String descripcion, 
                       BigDecimal descuentoPorcentaje, LocalDateTime fechaInicio, 
                       LocalDateTime fechaFin) {
        this.restauranteId = restauranteId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.descuentoPorcentaje = descuentoPorcentaje;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getRestauranteId() {
        return restauranteId;
    }
    
    public void setRestauranteId(Long restauranteId) {
        this.restauranteId = restauranteId;
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
    
    @Override
    public String toString() {
        return "PromocionDto{" +
                "id=" + id +
                ", restauranteId=" + restauranteId +
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
                '}';
    }
}
