package ar.edu.ubp.das.backend.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO interno para almacenar el contexto completo de un restaurante/sucursal
 * que será usado para generar el prompt de IA.
 */
public class RestauranteContextoDto {

    private String razonSocial;
    private String nombreSucursal;
    private String direccion;
    private String localidad;
    private String provincia;
    private Integer totalComensales;
    private List<String> tiposComida = new ArrayList<>();
    private List<String> ambientes = new ArrayList<>();
    private List<String> rangosPrecios = new ArrayList<>();
    private List<String> horarios = new ArrayList<>();
    private String observacionesAdicionales;
    
    // Identidad gastronómica y comunicacional
    private String tipoCocina;           // Tipo de cocina del restaurante
    private String estiloAtencion;        // Estilo de atención (formal, casual, etc.)
    private String platosEmblematicos;    // Platos emblemáticos (separados por comas)

    // Getters y Setters
    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public Integer getTotalComensales() {
        return totalComensales;
    }

    public void setTotalComensales(Integer totalComensales) {
        this.totalComensales = totalComensales;
    }

    public List<String> getTiposComida() {
        return tiposComida;
    }

    public void setTiposComida(List<String> tiposComida) {
        this.tiposComida = tiposComida;
    }

    public List<String> getAmbientes() {
        return ambientes;
    }

    public void setAmbientes(List<String> ambientes) {
        this.ambientes = ambientes;
    }

    public List<String> getRangosPrecios() {
        return rangosPrecios;
    }

    public void setRangosPrecios(List<String> rangosPrecios) {
        this.rangosPrecios = rangosPrecios;
    }

    public List<String> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<String> horarios) {
        this.horarios = horarios;
    }

    public String getObservacionesAdicionales() {
        return observacionesAdicionales;
    }

    public void setObservacionesAdicionales(String observacionesAdicionales) {
        this.observacionesAdicionales = observacionesAdicionales;
    }

    public String getTipoCocina() {
        return tipoCocina;
    }

    public void setTipoCocina(String tipoCocina) {
        this.tipoCocina = tipoCocina;
    }

    public String getEstiloAtencion() {
        return estiloAtencion;
    }

    public void setEstiloAtencion(String estiloAtencion) {
        this.estiloAtencion = estiloAtencion;
    }

    public String getPlatosEmblematicos() {
        return platosEmblematicos;
    }

    public void setPlatosEmblematicos(String platosEmblematicos) {
        this.platosEmblematicos = platosEmblematicos;
    }

    @Override
    public String toString() {
        return "RestauranteContextoDto{" +
                "razonSocial='" + razonSocial + '\'' +
                ", nombreSucursal='" + nombreSucursal + '\'' +
                ", direccion='" + direccion + '\'' +
                ", localidad='" + localidad + '\'' +
                ", provincia='" + provincia + '\'' +
                ", totalComensales=" + totalComensales +
                ", tiposComida=" + tiposComida +
                ", ambientes=" + ambientes +
                ", rangosPrecios=" + rangosPrecios +
                ", horarios=" + horarios +
                ", tipoCocina='" + tipoCocina + '\'' +
                ", estiloAtencion='" + estiloAtencion + '\'' +
                ", platosEmblematicos='" + platosEmblematicos + '\'' +
                '}';
    }
}

