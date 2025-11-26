package ar.edu.ubp.das.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO para la ficha completa de un restaurante según Requerimiento 11.
 * Incluye información detallada, sucursales y promociones vigentes.
 */
public class RestauranteDetalleDto {
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private Integer capacidad;
    private LocalTime horarioApertura;
    private LocalTime horarioCierre;
    private List<String> tipoCocina;  // Lista de tipos de comida (ej: ["Sushi", "Japonés"])
    private Double calificacion;
    private Boolean activo;
    private List<String> imagenes;    // URLs de imágenes
    private List<String> diasAtencion;
    
    // Relaciones
    private List<SucursalDto> sucursales;
    private List<PromocionDto> promociones;
    
    // Constructores
    public RestauranteDetalleDto() {}
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Integer getCapacidad() {
        return capacidad;
    }
    
    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }
    
    public LocalTime getHorarioApertura() {
        return horarioApertura;
    }
    
    public void setHorarioApertura(LocalTime horarioApertura) {
        this.horarioApertura = horarioApertura;
    }
    
    public LocalTime getHorarioCierre() {
        return horarioCierre;
    }
    
    public void setHorarioCierre(LocalTime horarioCierre) {
        this.horarioCierre = horarioCierre;
    }
    
    public List<String> getTipoCocina() {
        return tipoCocina;
    }
    
    public void setTipoCocina(List<String> tipoCocina) {
        this.tipoCocina = tipoCocina;
    }
    
    public Double getCalificacion() {
        return calificacion;
    }
    
    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public List<String> getImagenes() {
        return imagenes;
    }
    
    public void setImagenes(List<String> imagenes) {
        this.imagenes = imagenes;
    }
    
    public List<String> getDiasAtencion() {
        return diasAtencion;
    }
    
    public void setDiasAtencion(List<String> diasAtencion) {
        this.diasAtencion = diasAtencion;
    }
    
    public List<SucursalDto> getSucursales() {
        return sucursales;
    }
    
    public void setSucursales(List<SucursalDto> sucursales) {
        this.sucursales = sucursales;
    }
    
    public List<PromocionDto> getPromociones() {
        return promociones;
    }
    
    public void setPromociones(List<PromocionDto> promociones) {
        this.promociones = promociones;
    }
}

