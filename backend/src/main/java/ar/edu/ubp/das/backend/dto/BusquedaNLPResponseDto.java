package ar.edu.ubp.das.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * DTO para respuesta de análisis NLP de OpenAI
 * Representa la intención extraída de la consulta del usuario
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusquedaNLPResponseDto {

    private List<String> tipoComida;
    private String barrio;
    private String localidad;
    private String ambiente;
    private String rangoPrecio;
    private String momentoDia; // "almuerzo", "cena", "desayuno"
    private String intencion; // "comer", "cenar", "almorzar", "tomar"
    private List<String> palabrasClave;

    // Constructores
    public BusquedaNLPResponseDto() {}

    // Getters y Setters
    public List<String> getTipoComida() {
        return tipoComida;
    }

    public void setTipoComida(List<String> tipoComida) {
        this.tipoComida = tipoComida;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public String getRangoPrecio() {
        return rangoPrecio;
    }

    public void setRangoPrecio(String rangoPrecio) {
        this.rangoPrecio = rangoPrecio;
    }

    public String getMomentoDia() {
        return momentoDia;
    }

    public void setMomentoDia(String momentoDia) {
        this.momentoDia = momentoDia;
    }

    public String getIntencion() {
        return intencion;
    }

    public void setIntencion(String intencion) {
        this.intencion = intencion;
    }

    public List<String> getPalabrasClave() {
        return palabrasClave;
    }

    public void setPalabrasClave(List<String> palabrasClave) {
        this.palabrasClave = palabrasClave;
    }
}
