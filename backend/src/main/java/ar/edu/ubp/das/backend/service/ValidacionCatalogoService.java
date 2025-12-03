package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.BusquedaNLPResponseDto;
import ar.edu.ubp.das.backend.dto.CatalogosDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para validar y mapear valores devueltos por la IA a valores exactos del catálogo.
 * 
 * Estrategia:
 * - La IA puede devolver valores aproximados o sinónimos
 * - Este servicio valida y mapea esos valores a los valores exactos del catálogo
 * - Usa fuzzy matching para encontrar el valor más cercano
 * - Si no hay coincidencia razonable, usa null
 */
@Service
public class ValidacionCatalogoService {

    
    public ValidacionCatalogoService() {
    }
    
    /**
     * Valida y mapea los valores de la respuesta de la IA a valores exactos del catálogo.
     * 
     * @param respuestaIA Respuesta de la IA (puede contener valores aproximados)
     * @param catalogos Catálogos del sistema (obtenidos una sola vez)
     * @return Respuesta validada con valores exactos del catálogo
     */
    public BusquedaNLPResponseDto validarYMapar(BusquedaNLPResponseDto respuestaIA, CatalogosDto catalogos) {
        BusquedaNLPResponseDto respuestaValidada = new BusquedaNLPResponseDto();
        
        List<String> tiposComidaCatalogo = catalogos.getTiposComida();
        List<String> barriosCatalogo = catalogos.getBarrios();
        List<String> localidadesCatalogo = catalogos.getLocalidades();
        List<String> ambientesCatalogo = catalogos.getAmbientes();
        List<String> rangosPrecioCatalogo = catalogos.getRangosPrecio();
        
        // Validar y mapear tipoComida (lista)
        // Para tipos de comida, usamos una validación más inteligente que maneja sinónimos
        if (respuestaIA.getTipoComida() != null && !respuestaIA.getTipoComida().isEmpty()) {
            List<String> tiposComidaValidos = new ArrayList<>();
            for (String tipoIA : respuestaIA.getTipoComida()) {
                String tipoValidado = buscarValorExactoTipoComida(tipoIA, tiposComidaCatalogo);
                if (tipoValidado != null && !tiposComidaValidos.contains(tipoValidado)) {
                    tiposComidaValidos.add(tipoValidado);
                }
            }
            respuestaValidada.setTipoComida(tiposComidaValidos.isEmpty() ? null : tiposComidaValidos);
        }
        
        // Validar y mapear barrio
        if (respuestaIA.getBarrio() != null && !respuestaIA.getBarrio().isEmpty()) {
            respuestaValidada.setBarrio(buscarValorExacto(respuestaIA.getBarrio(), barriosCatalogo));
        }
        
        // Validar y mapear localidad (solo coincidencia exacta o muy cercana)
        if (respuestaIA.getLocalidad() != null && !respuestaIA.getLocalidad().isEmpty()) {
            respuestaValidada.setLocalidad(buscarValorExactoLocalidad(respuestaIA.getLocalidad(), localidadesCatalogo));
        }
        
        // Validar y mapear ambiente
        if (respuestaIA.getAmbiente() != null && !respuestaIA.getAmbiente().isEmpty()) {
            respuestaValidada.setAmbiente(buscarValorExacto(respuestaIA.getAmbiente(), ambientesCatalogo));
        }
        
        // Validar y mapear rangoPrecio
        if (respuestaIA.getRangoPrecio() != null && !respuestaIA.getRangoPrecio().isEmpty()) {
            respuestaValidada.setRangoPrecio(buscarValorExacto(respuestaIA.getRangoPrecio(), rangosPrecioCatalogo));
        }
        
        // Campos que no requieren validación (se copian tal cual)
        respuestaValidada.setMomentoDia(respuestaIA.getMomentoDia());
        respuestaValidada.setIntencion(respuestaIA.getIntencion());
        respuestaValidada.setPalabrasClave(respuestaIA.getPalabrasClave());
        
        return respuestaValidada;
    }
    
    /**
     * Busca una localidad en el catálogo usando solo coincidencia exacta o fuzzy matching estricto.
     * NO usa coincidencia parcial para evitar mapear "Córdoba" a "Alta Córdoba".
     * 
     * @param valorIA Valor devuelto por la IA
     * @param catalogo Lista de localidades válidas del catálogo
     * @return Localidad exacta del catálogo o null si no hay coincidencia razonable
     */
    private String buscarValorExactoLocalidad(String valorIA, List<String> catalogo) {
        if (valorIA == null || valorIA.trim().isEmpty() || catalogo == null || catalogo.isEmpty()) {
            return null;
        }
        
        String valorIALimpio = valorIA.trim();
        
        // 1. Coincidencia exacta (case-insensitive)
        for (String valorCatalogo : catalogo) {
            if (valorCatalogo.equalsIgnoreCase(valorIALimpio)) {
                return valorCatalogo;
            }
        }
        
        // 2. Fuzzy matching estricto (solo si la distancia es muy pequeña)
        String mejorCoincidencia = null;
        int mejorDistancia = Integer.MAX_VALUE;
        int umbralMaximo = 2;
        
        for (String valorCatalogo : catalogo) {
            int distancia = calcularDistanciaLevenshtein(
                valorIALimpio.toLowerCase(), 
                valorCatalogo.toLowerCase()
            );
            
            if (distancia < mejorDistancia && distancia <= umbralMaximo) {
                mejorDistancia = distancia;
                mejorCoincidencia = valorCatalogo;
            }
        }
        
        return mejorCoincidencia;
    }
    
    /**
     * Busca un tipo de comida en el catálogo usando estrategias inteligentes con sinónimos.
     * Maneja casos especiales como "japonesa" → "Fusión japonesa-peruana", "Sushi" o "Asiática"
     * 
     * @param valorIA Valor devuelto por la IA (ej: "japonesa", "comida japonesa")
     * @param catalogo Lista de tipos de comida válidos del catálogo
     * @return Tipo de comida del catálogo o null si no hay coincidencia razonable
     */
    private String buscarValorExactoTipoComida(String valorIA, List<String> catalogo) {
        if (valorIA == null || valorIA.trim().isEmpty() || catalogo == null || catalogo.isEmpty()) {
            return null;
        }
        
        String valorIALimpio = valorIA.trim().toLowerCase();
        
        // Mapeo de sinónimos específicos para tipos de comida
        // Caso especial: "japonesa" o variaciones
        if (valorIALimpio.contains("japon") || valorIALimpio.contains("nikkei") || valorIALimpio.contains("peruano-japon")) {
            // Prioridad: 1) Fusión japonesa-peruana, 2) Sushi, 3) Asiática
            for (String tipo : catalogo) {
                String tipoLower = tipo.toLowerCase();
                if (tipoLower.contains("fusión") && (tipoLower.contains("japon") || tipoLower.contains("peru"))) {
                    return tipo;
                }
            }
            for (String tipo : catalogo) {
                if (tipo.equalsIgnoreCase("Sushi")) {
                    return tipo;
                }
            }
            for (String tipo : catalogo) {
                if (tipo.equalsIgnoreCase("Asiática")) {
                    return tipo;
                }
            }
        }
        
        // Caso especial: "italiana" o variaciones
        if (valorIALimpio.contains("italian")) {
            for (String tipo : catalogo) {
                String tipoLower = tipo.toLowerCase();
                if (tipoLower.contains("italian") && tipoLower.contains("tradicional")) {
                    return tipo;
                }
            }
            for (String tipo : catalogo) {
                if (tipo.equalsIgnoreCase("Italiana")) {
                    return tipo;
                }
            }
        }
        
        // Para otros casos, usar la búsqueda normal
        return buscarValorExacto(valorIA, catalogo);
    }
    
    /**
     * Busca un valor exacto en el catálogo usando diferentes estrategias:
     * 1. Coincidencia exacta (case-insensitive)
     * 2. Coincidencia parcial (contiene)
     * 3. Fuzzy matching (Levenshtein distance)
     * 
     * @param valorIA Valor devuelto por la IA
     * @param catalogo Lista de valores válidos del catálogo
     * @return Valor exacto del catálogo o null si no hay coincidencia razonable
     */
    private String buscarValorExacto(String valorIA, List<String> catalogo) {
        if (valorIA == null || valorIA.trim().isEmpty() || catalogo == null || catalogo.isEmpty()) {
            return null;
        }
        
        String valorIALimpio = valorIA.trim();
        
        // 1. Coincidencia exacta (case-insensitive)
        for (String valorCatalogo : catalogo) {
            if (valorCatalogo.equalsIgnoreCase(valorIALimpio)) {
                return valorCatalogo;
            }
        }
        
        // 2. Coincidencia parcial (contiene)
        for (String valorCatalogo : catalogo) {
            if (valorCatalogo.toLowerCase().contains(valorIALimpio.toLowerCase()) ||
                valorIALimpio.toLowerCase().contains(valorCatalogo.toLowerCase())) {
                return valorCatalogo;
            }
        }
        
        // 3. Fuzzy matching (Levenshtein distance)
        String mejorCoincidencia = null;
        int mejorDistancia = Integer.MAX_VALUE;
        int umbralMaximo = Math.max(3, valorIALimpio.length() / 3);
        
        for (String valorCatalogo : catalogo) {
            int distancia = calcularDistanciaLevenshtein(
                valorIALimpio.toLowerCase(), 
                valorCatalogo.toLowerCase()
            );
            
            if (distancia < mejorDistancia && distancia <= umbralMaximo) {
                mejorDistancia = distancia;
                mejorCoincidencia = valorCatalogo;
            }
        }
        
        return mejorCoincidencia;
    }
    
    /**
     * Calcula la distancia de Levenshtein entre dos strings.
     * Mide la cantidad mínima de ediciones (inserción, eliminación, sustitución)
     * necesarias para transformar un string en otro.
     * 
     * @param s1 Primer string
     * @param s2 Segundo string
     * @return Distancia de Levenshtein
     */
    private int calcularDistanciaLevenshtein(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return Integer.MAX_VALUE;
        }
        
        int len1 = s1.length();
        int len2 = s2.length();
        
        // Optimización: si la diferencia de longitud es muy grande, no vale la pena calcular
        if (Math.abs(len1 - len2) > Math.max(len1, len2) / 2) {
            return Integer.MAX_VALUE;
        }
        
        // Matriz de distancias
        int[][] dp = new int[len1 + 1][len2 + 1];
        
        // Inicializar primera fila y columna
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }
        
        // Calcular distancias
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1,     // Eliminación
                                dp[i][j - 1] + 1),      // Inserción
                        dp[i - 1][j - 1] + 1           // Sustitución
                    );
                }
            }
        }
        
        return dp[len1][len2];
    }
}

