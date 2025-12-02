package ar.edu.ubp.das.backend.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO para encapsular los atributos de configuración de un restaurante.
 * Aunque internamente usa un Map, proporciona una interfaz tipada y métodos de utilidad.
 * 
 * NOTA: En este caso específico, mantener un Map es aceptable porque los atributos
 * son dinámicos y no conocemos todas las claves posibles en tiempo de compilación.
 * Sin embargo, encapsulamos el Map en un objeto para mejor OOP.
 */
public class AtributosRestauranteDto {
    
    private final Map<String, String> atributos;
    
    public AtributosRestauranteDto() {
        this.atributos = new HashMap<>();
    }
    
    public AtributosRestauranteDto(Map<String, String> atributos) {
        this.atributos = atributos != null ? new HashMap<>(atributos) : new HashMap<>();
    }
    
    public void agregarAtributo(String nombre, String valor) {
        if (nombre != null && valor != null && !valor.trim().isEmpty()) {
            atributos.put(nombre, valor.trim());
        }
    }
    
    public String obtenerAtributo(String nombre) {
        return atributos.get(nombre);
    }
    
    public boolean tieneAtributo(String nombre) {
        return atributos.containsKey(nombre);
    }
    
    public Map<String, String> getAtributos() {
        return new HashMap<>(atributos); // Retornar copia para inmutabilidad
    }
    
    public boolean isEmpty() {
        return atributos.isEmpty();
    }
    
    public int size() {
        return atributos.size();
    }
}

