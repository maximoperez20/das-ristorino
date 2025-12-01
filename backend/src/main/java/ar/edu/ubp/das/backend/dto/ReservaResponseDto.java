package ar.edu.ubp.das.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class ReservaResponseDto {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("nombre_cliente")
    private String nombreCliente;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("telefono")
    private String telefono;
    
    @JsonProperty("fecha_hora")
    private LocalDateTime fechaHora;
    
    @JsonProperty("cantidad_personas")
    private Integer cantidadPersonas;
    
    @JsonProperty("estado")
    private String estado;
    
    @JsonProperty("observaciones")
    private String observaciones;
    
    @JsonProperty("fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @JsonProperty("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @JsonProperty("nombre_restaurante")
    private String nombreRestaurante;
    
    @JsonProperty("nombre_sucursal")
    private String nombreSucursal;
    
    @JsonProperty("nombre_zona")
    private String nombreZona;
    
    @JsonProperty("cant_adultos")
    private Integer cantAdultos;
    
    @JsonProperty("cant_menores")
    private Integer cantMenores;
    
    @JsonProperty("preferencias")
    private String preferencias;
    
    // Constructores
    public ReservaResponseDto() {}
    
    public ReservaResponseDto(String id, String nombreCliente, String email, String telefono, 
                             LocalDateTime fechaHora, Integer cantidadPersonas, String estado, 
                             String observaciones, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.email = email;
        this.telefono = telefono;
        this.fechaHora = fechaHora;
        this.cantidadPersonas = cantidadPersonas;
        this.estado = estado;
        this.observaciones = observaciones;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }
    
    // Getters y Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNombreCliente() {
        return nombreCliente;
    }
    
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
    
    public Integer getCantidadPersonas() {
        return cantidadPersonas;
    }
    
    public void setCantidadPersonas(Integer cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
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
    
    public String getNombreZona() {
        return nombreZona;
    }
    
    public void setNombreZona(String nombreZona) {
        this.nombreZona = nombreZona;
    }
    
    public Integer getCantAdultos() {
        return cantAdultos;
    }
    
    public void setCantAdultos(Integer cantAdultos) {
        this.cantAdultos = cantAdultos;
    }
    
    public Integer getCantMenores() {
        return cantMenores;
    }
    
    public void setCantMenores(Integer cantMenores) {
        this.cantMenores = cantMenores;
    }
    
    public String getPreferencias() {
        return preferencias;
    }
    
    public void setPreferencias(String preferencias) {
        this.preferencias = preferencias;
    }
    
    @Override
    public String toString() {
        return "ReservaResponseDto{" +
                "id=" + id +
                ", nombreCliente='" + nombreCliente + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", fechaHora=" + fechaHora +
                ", cantidadPersonas=" + cantidadPersonas +
                ", estado='" + estado + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }
}
