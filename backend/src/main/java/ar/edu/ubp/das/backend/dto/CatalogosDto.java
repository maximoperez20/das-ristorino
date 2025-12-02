package ar.edu.ubp.das.backend.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DTO para encapsular todos los catálogos del sistema.
 * Proporciona métodos de utilidad para búsqueda y validación.
 * 
 * Reemplaza múltiples List<String> sueltos por un objeto tipado.
 */
public class CatalogosDto {
    
    private List<String> tiposComida;
    private List<String> barrios;
    private List<String> localidades;
    private List<String> ambientes;
    private List<String> rangosPrecio;
    
    public CatalogosDto() {
        this.tiposComida = new ArrayList<>();
        this.barrios = new ArrayList<>();
        this.localidades = new ArrayList<>();
        this.ambientes = new ArrayList<>();
        this.rangosPrecio = new ArrayList<>();
    }
    
    public CatalogosDto(
            List<String> tiposComida,
            List<String> barrios,
            List<String> localidades,
            List<String> ambientes,
            List<String> rangosPrecio) {
        this.tiposComida = tiposComida != null ? new ArrayList<>(tiposComida) : new ArrayList<>();
        this.barrios = barrios != null ? new ArrayList<>(barrios) : new ArrayList<>();
        this.localidades = localidades != null ? new ArrayList<>(localidades) : new ArrayList<>();
        this.ambientes = ambientes != null ? new ArrayList<>(ambientes) : new ArrayList<>();
        this.rangosPrecio = rangosPrecio != null ? new ArrayList<>(rangosPrecio) : new ArrayList<>();
    }
    
    // Getters (retornan copias para inmutabilidad)
    public List<String> getTiposComida() {
        return new ArrayList<>(tiposComida);
    }
    
    public void setTiposComida(List<String> tiposComida) {
        this.tiposComida = tiposComida != null ? new ArrayList<>(tiposComida) : new ArrayList<>();
    }
    
    public List<String> getBarrios() {
        return new ArrayList<>(barrios);
    }
    
    public void setBarrios(List<String> barrios) {
        this.barrios = barrios != null ? new ArrayList<>(barrios) : new ArrayList<>();
    }
    
    public List<String> getLocalidades() {
        return new ArrayList<>(localidades);
    }
    
    public void setLocalidades(List<String> localidades) {
        this.localidades = localidades != null ? new ArrayList<>(localidades) : new ArrayList<>();
    }
    
    public List<String> getAmbientes() {
        return new ArrayList<>(ambientes);
    }
    
    public void setAmbientes(List<String> ambientes) {
        this.ambientes = ambientes != null ? new ArrayList<>(ambientes) : new ArrayList<>();
    }
    
    public List<String> getRangosPrecio() {
        return new ArrayList<>(rangosPrecio);
    }
    
    public void setRangosPrecio(List<String> rangosPrecio) {
        this.rangosPrecio = rangosPrecio != null ? new ArrayList<>(rangosPrecio) : new ArrayList<>();
    }
    
    // Métodos de utilidad
    
    /**
     * Verifica si un tipo de comida existe en el catálogo (case-insensitive)
     */
    public boolean tieneTipoComida(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return false;
        }
        return tiposComida.stream()
            .anyMatch(t -> t.equalsIgnoreCase(tipo.trim()));
    }
    
    /**
     * Busca un tipo de comida similar en el catálogo (case-insensitive)
     */
    public Optional<String> buscarTipoComida(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return Optional.empty();
        }
        return tiposComida.stream()
            .filter(t -> t.equalsIgnoreCase(tipo.trim()))
            .findFirst();
    }
    
    /**
     * Verifica si un barrio existe en el catálogo (case-insensitive)
     */
    public boolean tieneBarrio(String barrio) {
        if (barrio == null || barrio.trim().isEmpty()) {
            return false;
        }
        return barrios.stream()
            .anyMatch(b -> b.equalsIgnoreCase(barrio.trim()));
    }
    
    /**
     * Verifica si una localidad existe en el catálogo (case-insensitive)
     */
    public boolean tieneLocalidad(String localidad) {
        if (localidad == null || localidad.trim().isEmpty()) {
            return false;
        }
        return localidades.stream()
            .anyMatch(l -> l.equalsIgnoreCase(localidad.trim()));
    }
    
    /**
     * Verifica si un ambiente existe en el catálogo (case-insensitive)
     */
    public boolean tieneAmbiente(String ambiente) {
        if (ambiente == null || ambiente.trim().isEmpty()) {
            return false;
        }
        return ambientes.stream()
            .anyMatch(a -> a.equalsIgnoreCase(ambiente.trim()));
    }
    
    /**
     * Verifica si un rango de precio existe en el catálogo (case-insensitive)
     */
    public boolean tieneRangoPrecio(String rangoPrecio) {
        if (rangoPrecio == null || rangoPrecio.trim().isEmpty()) {
            return false;
        }
        return rangosPrecio.stream()
            .anyMatch(r -> r.equalsIgnoreCase(rangoPrecio.trim()));
    }
    
    /**
     * Convierte este DTO a BusquedaContextoDto.ContextoDto para enviar a OpenAI
     */
    public BusquedaContextoDto.ContextoDto toContextoDto() {
        BusquedaContextoDto.ContextoDto contexto = new BusquedaContextoDto.ContextoDto();
        contexto.setTiposComida(getTiposComida());
        contexto.setBarrios(getBarrios());
        contexto.setLocalidades(getLocalidades());
        contexto.setAmbientes(getAmbientes());
        contexto.setRangosPrecio(getRangosPrecio());
        return contexto;
    }
}

