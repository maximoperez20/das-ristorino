package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.BusquedaNLPResponseDto;
import ar.edu.ubp.das.backend.repository.BusquedaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ValidacionCatalogoService.class);
    
    private final BusquedaRepository busquedaRepository;
    
    public ValidacionCatalogoService(BusquedaRepository busquedaRepository) {
        this.busquedaRepository = busquedaRepository;
    }
    
    /**
     * Valida y mapea los valores de la respuesta de la IA a valores exactos del catálogo.
     * 
     * @param respuestaIA Respuesta de la IA (puede contener valores aproximados)
     * @return Respuesta validada con valores exactos del catálogo
     */
    public BusquedaNLPResponseDto validarYMapar(BusquedaNLPResponseDto respuestaIA) {
        logger.info("🔍 ===== VALIDACIÓN DE CATÁLOGO =====");
        logger.info("📥 Valores recibidos de IA:");
        logger.info("   - Tipo comida: {}", respuestaIA.getTipoComida());
        logger.info("   - Barrio: {}", respuestaIA.getBarrio());
        logger.info("   - Localidad: {}", respuestaIA.getLocalidad());
        logger.info("   - Ambiente: {}", respuestaIA.getAmbiente());
        logger.info("   - Rango precio: {}", respuestaIA.getRangoPrecio());
        logger.info("   - Palabras clave: {}", respuestaIA.getPalabrasClave());
        
        BusquedaNLPResponseDto respuestaValidada = new BusquedaNLPResponseDto();
        
        // Obtener catálogos de la BD
        List<String> tiposComidaCatalogo = busquedaRepository.obtenerTiposComida();
        List<String> barriosCatalogo = busquedaRepository.obtenerBarrios();
        List<String> localidadesCatalogo = busquedaRepository.obtenerLocalidades();
        List<String> ambientesCatalogo = busquedaRepository.obtenerAmbientes();
        List<String> rangosPrecioCatalogo = busquedaRepository.obtenerRangosPrecio();
        
        logger.info("📚 Catálogos disponibles:");
        logger.info("   - Tipos comida: {}", tiposComidaCatalogo);
        logger.info("   - Barrios: {}", barriosCatalogo);
        logger.info("   - Localidades: {}", localidadesCatalogo);
        logger.info("   - Ambientes: {}", ambientesCatalogo);
        logger.info("   - Rangos precio: {}", rangosPrecioCatalogo);
        
        // Validar y mapear tipoComida (lista)
        // Para tipos de comida, usamos una validación más inteligente que maneja sinónimos
        if (respuestaIA.getTipoComida() != null && !respuestaIA.getTipoComida().isEmpty()) {
            List<String> tiposComidaValidos = new ArrayList<>();
            for (String tipoIA : respuestaIA.getTipoComida()) {
                String tipoValidado = buscarValorExactoTipoComida(tipoIA, tiposComidaCatalogo);
                if (tipoValidado != null && !tiposComidaValidos.contains(tipoValidado)) {
                    tiposComidaValidos.add(tipoValidado);
                    logger.info("   ✅ Tipo comida '{}' -> mapeado a '{}'", tipoIA, tipoValidado);
                } else if (tipoValidado == null) {
                    logger.warn("   ⚠️ Tipo de comida '{}' NO encontrado en catálogo, se omite", tipoIA);
                }
            }
            respuestaValidada.setTipoComida(tiposComidaValidos.isEmpty() ? null : tiposComidaValidos);
        } else {
            respuestaValidada.setTipoComida(null);
            logger.info("   ℹ️ Tipo comida: null (no especificado por IA)");
        }
        
        // Validar y mapear barrio
        if (respuestaIA.getBarrio() != null && !respuestaIA.getBarrio().isEmpty()) {
            String barrioValidado = buscarValorExacto(respuestaIA.getBarrio(), barriosCatalogo);
            respuestaValidada.setBarrio(barrioValidado);
            if (barrioValidado != null) {
                logger.info("   ✅ Barrio '{}' -> mapeado a '{}'", respuestaIA.getBarrio(), barrioValidado);
            } else {
                logger.warn("   ⚠️ Barrio '{}' NO encontrado en catálogo", respuestaIA.getBarrio());
            }
        } else {
            respuestaValidada.setBarrio(null);
            logger.info("   ℹ️ Barrio: null (no especificado por IA)");
        }
        
        // Validar y mapear localidad
        // IMPORTANTE: Para localidades, solo mapear si hay coincidencia exacta o muy cercana
        // No usar coincidencia parcial porque "Córdoba" podría mapearse incorrectamente a "Alta Córdoba"
        if (respuestaIA.getLocalidad() != null && !respuestaIA.getLocalidad().isEmpty()) {
            String localidadValidada = buscarValorExactoLocalidad(respuestaIA.getLocalidad(), localidadesCatalogo);
            respuestaValidada.setLocalidad(localidadValidada);
            if (localidadValidada != null) {
                logger.info("   ✅ Localidad '{}' -> mapeada a '{}'", respuestaIA.getLocalidad(), localidadValidada);
            } else {
                logger.warn("   ⚠️ Localidad '{}' NO encontrada en catálogo (no se mapea)", respuestaIA.getLocalidad());
            }
        } else {
            respuestaValidada.setLocalidad(null);
            logger.info("   ℹ️ Localidad: null (no especificada por IA)");
        }
        
        // Validar y mapear ambiente
        if (respuestaIA.getAmbiente() != null && !respuestaIA.getAmbiente().isEmpty()) {
            String ambienteValidado = buscarValorExacto(respuestaIA.getAmbiente(), ambientesCatalogo);
            respuestaValidada.setAmbiente(ambienteValidado);
            if (ambienteValidado != null) {
                logger.info("   ✅ Ambiente '{}' -> mapeado a '{}'", respuestaIA.getAmbiente(), ambienteValidado);
            } else {
                logger.warn("   ⚠️ Ambiente '{}' NO encontrado en catálogo", respuestaIA.getAmbiente());
            }
        } else {
            respuestaValidada.setAmbiente(null);
            logger.info("   ℹ️ Ambiente: null (no especificado por IA)");
        }
        
        // Validar y mapear rangoPrecio
        if (respuestaIA.getRangoPrecio() != null && !respuestaIA.getRangoPrecio().isEmpty()) {
            String rangoPrecioValidado = buscarValorExacto(respuestaIA.getRangoPrecio(), rangosPrecioCatalogo);
            respuestaValidada.setRangoPrecio(rangoPrecioValidado);
            if (rangoPrecioValidado != null) {
                logger.info("   ✅ Rango precio '{}' -> mapeado a '{}'", respuestaIA.getRangoPrecio(), rangoPrecioValidado);
            } else {
                logger.warn("   ⚠️ Rango precio '{}' NO encontrado en catálogo", respuestaIA.getRangoPrecio());
            }
        } else {
            respuestaValidada.setRangoPrecio(null);
            logger.info("   ℹ️ Rango precio: null (no especificado por IA)");
        }
        
        // Campos que no requieren validación (se copian tal cual)
        respuestaValidada.setMomentoDia(respuestaIA.getMomentoDia());
        respuestaValidada.setIntencion(respuestaIA.getIntencion());
        respuestaValidada.setPalabrasClave(respuestaIA.getPalabrasClave());
        
        logger.info("📤 Valores validados finales:");
        logger.info("   - Tipo comida: {}", respuestaValidada.getTipoComida());
        logger.info("   - Barrio: {}", respuestaValidada.getBarrio());
        logger.info("   - Localidad: {}", respuestaValidada.getLocalidad());
        logger.info("   - Ambiente: {}", respuestaValidada.getAmbiente());
        logger.info("   - Rango precio: {}", respuestaValidada.getRangoPrecio());
        logger.info("   - Palabras clave: {}", respuestaValidada.getPalabrasClave());
        logger.info("🔍 ===== FIN VALIDACIÓN =====");
        
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
                logger.debug("Coincidencia exacta (localidad): '{}' → '{}'", valorIALimpio, valorCatalogo);
                return valorCatalogo;
            }
        }
        
        // 2. Fuzzy matching estricto (solo si la distancia es muy pequeña)
        String mejorCoincidencia = null;
        int mejorDistancia = Integer.MAX_VALUE;
        int umbralMaximo = 2; // Umbral muy estricto para localidades (máximo 2 caracteres de diferencia)
        
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
        
        if (mejorCoincidencia != null) {
            logger.debug("Fuzzy matching (localidad): '{}' → '{}' (distancia: {})", 
                        valorIALimpio, mejorCoincidencia, mejorDistancia);
            return mejorCoincidencia;
        }
        
        // 3. Si no hay coincidencia, retornar null (NO mapear)
        logger.debug("No se encontró coincidencia para localidad '{}'", valorIALimpio);
        return null;
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
                    logger.debug("Mapeo por sinónimo (japonesa): '{}' → '{}'", valorIA, tipo);
                    return tipo;
                }
            }
            for (String tipo : catalogo) {
                if (tipo.equalsIgnoreCase("Sushi")) {
                    logger.debug("Mapeo por sinónimo (japonesa): '{}' → '{}'", valorIA, tipo);
                    return tipo;
                }
            }
            for (String tipo : catalogo) {
                if (tipo.equalsIgnoreCase("Asiática")) {
                    logger.debug("Mapeo por sinónimo (japonesa): '{}' → '{}'", valorIA, tipo);
                    return tipo;
                }
            }
        }
        
        // Caso especial: "italiana" o variaciones
        if (valorIALimpio.contains("italian")) {
            // Prioridad: 1) Italiana tradicional, 2) Italiana
            for (String tipo : catalogo) {
                String tipoLower = tipo.toLowerCase();
                if (tipoLower.contains("italian") && tipoLower.contains("tradicional")) {
                    logger.debug("Mapeo por sinónimo (italiana): '{}' → '{}'", valorIA, tipo);
                    return tipo;
                }
            }
            for (String tipo : catalogo) {
                if (tipo.equalsIgnoreCase("Italiana")) {
                    logger.debug("Mapeo por sinónimo (italiana): '{}' → '{}'", valorIA, tipo);
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
                logger.debug("Coincidencia exacta: '{}' → '{}'", valorIALimpio, valorCatalogo);
                return valorCatalogo;
            }
        }
        
        // 2. Coincidencia parcial (contiene)
        for (String valorCatalogo : catalogo) {
            if (valorCatalogo.toLowerCase().contains(valorIALimpio.toLowerCase()) ||
                valorIALimpio.toLowerCase().contains(valorCatalogo.toLowerCase())) {
                logger.debug("Coincidencia parcial: '{}' → '{}'", valorIALimpio, valorCatalogo);
                return valorCatalogo;
            }
        }
        
        // 3. Fuzzy matching (Levenshtein distance)
        String mejorCoincidencia = null;
        int mejorDistancia = Integer.MAX_VALUE;
        int umbralMaximo = Math.max(3, valorIALimpio.length() / 3); // Umbral dinámico
        
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
        
        if (mejorCoincidencia != null) {
            logger.debug("Fuzzy matching: '{}' → '{}' (distancia: {})", 
                        valorIALimpio, mejorCoincidencia, mejorDistancia);
            return mejorCoincidencia;
        }
        
        logger.debug("No se encontró coincidencia para '{}' en el catálogo", valorIALimpio);
        return null;
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

