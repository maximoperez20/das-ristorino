package ar.edu.ubp.das.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import java.time.LocalTime;
import java.util.List;

public class RestauranteDto {
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private String nroRestaurante; // UUID del restaurante (identificador real)
    
    @NotBlank(message = "El nombre del restaurante es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccion;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;
    
    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Max(value = 1000, message = "La capacidad no puede exceder 1000")
    private Integer capacidad;
    
    @NotNull(message = "El horario de apertura es obligatorio")
    private LocalTime horarioApertura;
    
    @NotNull(message = "El horario de cierre es obligatorio")
    private LocalTime horarioCierre;
    
    @Size(max = 100, message = "La categoría no puede exceder 100 caracteres")
    private String categoria;
    
    @DecimalMin(value = "0.0", message = "La calificación debe ser al menos 0.0")
    @DecimalMax(value = "5.0", message = "La calificación no puede exceder 5.0")
    private Double calificacion;
    
    private Boolean activo = true;
    
    private String imagenUrl;
    
    private List<String> diasAtencion; // ["LUNES", "MARTES", "MIERCOLES", etc.]
    
    // Constructores
    public RestauranteDto() {}
    
    public RestauranteDto(String nombre, String direccion, String telefono, String email, 
                         Integer capacidad, LocalTime horarioApertura, LocalTime horarioCierre, 
                         String categoria) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.capacidad = capacidad;
        this.horarioApertura = horarioApertura;
        this.horarioCierre = horarioCierre;
        this.categoria = categoria;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNroRestaurante() {
        return nroRestaurante;
    }
    
    public void setNroRestaurante(String nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
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
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
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
    
    public String getImagenUrl() {
        return imagenUrl;
    }
    
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
    
    public List<String> getDiasAtencion() {
        return diasAtencion;
    }
    
    public void setDiasAtencion(List<String> diasAtencion) {
        this.diasAtencion = diasAtencion;
    }
    
    @Override
    public String toString() {
        return "RestauranteDto{" +
                "id=" + id +
                ", nroRestaurante='" + nroRestaurante + '\'' +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", capacidad=" + capacidad +
                ", horarioApertura=" + horarioApertura +
                ", horarioCierre=" + horarioCierre +
                ", categoria='" + categoria + '\'' +
                ", calificacion=" + calificacion +
                ", activo=" + activo +
                ", imagenUrl='" + imagenUrl + '\'' +
                ", diasAtencion=" + diasAtencion +
                '}';
    }
}
