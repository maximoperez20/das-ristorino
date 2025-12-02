package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.BusquedaContextoDto;
import ar.edu.ubp.das.backend.dto.BusquedaNLPRequestDto;
import ar.edu.ubp.das.backend.dto.BusquedaNLPResponseDto;
import ar.edu.ubp.das.backend.dto.BusquedaNLPResultadoDto;
import ar.edu.ubp.das.backend.dto.BusquedaNLPParametrosDto;
import ar.edu.ubp.das.backend.dto.RestauranteDto;
import ar.edu.ubp.das.backend.repository.BusquedaRepository;
import ar.edu.ubp.das.backend.repository.ClienteRepository;
import ar.edu.ubp.das.backend.repository.RestauranteRepository;
import ar.edu.ubp.das.backend.service.ValidacionCatalogoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para búsqueda de restaurantes usando lenguaje natural (NLP)
 * Cumple Requerimientos 10 y 35: Buscar restaurantes con lenguaje natural
 */
@Service
public class BusquedaNLPService {

    private static final Logger logger = LoggerFactory.getLogger(BusquedaNLPService.class);

    private final BusquedaRepository busquedaRepository;
    private final OpenAIService openAIService;
    private final RestauranteRepository restauranteRepository;
    private final ClienteRepository clienteRepository;
    private final ValidacionCatalogoService validacionCatalogoService;
    
    @Value("${openai.prompt.busqueda.id:pmpt_68f9295bc1e48194b2e725a7b5df2b1c0a01e67130022025}")
    private String promptIdBusqueda;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public BusquedaNLPService(BusquedaRepository busquedaRepository,
                             OpenAIService openAIService,
                             RestauranteRepository restauranteRepository,
                             ClienteRepository clienteRepository,
                             ValidacionCatalogoService validacionCatalogoService) {
        this.busquedaRepository = busquedaRepository;
        this.openAIService = openAIService;
        this.restauranteRepository = restauranteRepository;
        this.clienteRepository = clienteRepository;
        this.validacionCatalogoService = validacionCatalogoService;
    }

    /**
     * Procesa una consulta en lenguaje natural y devuelve restaurantes relevantes con resultados exactos y sugerencias
     *
     * @param request Solicitud con la consulta del usuario
     * @param nroCliente UUID del cliente autenticado (opcional, puede ser null)
     * @return DTO con resultados exactos y sugerencias
     */
    public BusquedaNLPResultadoDto buscarRestaurantesPorNLP(BusquedaNLPRequestDto request, String nroCliente) {
        logger.info("🔍 ===== INICIO BÚSQUEDA NLP =====");
        logger.info("📝 Consulta del usuario: '{}'", request.getConsulta());
        logger.info("👤 Cliente autenticado: {}", nroCliente != null ? nroCliente : "No autenticado");

        // 1. Obtener catálogos de la BD
        BusquedaContextoDto contexto = construirContexto();
        logger.info("📚 Contexto enviado a IA:");
        BusquedaContextoDto.ContextoDto contextoDto = contexto.getContexto();
        logger.info("   - Tipos de comida disponibles: {}", contextoDto != null && contextoDto.getTiposComida() != null ? String.join(", ", contextoDto.getTiposComida()) : "null");
        logger.info("   - Barrios disponibles: {}", contextoDto != null && contextoDto.getBarrios() != null ? String.join(", ", contextoDto.getBarrios()) : "null");
        logger.info("   - Localidades disponibles: {}", contextoDto != null && contextoDto.getLocalidades() != null ? String.join(", ", contextoDto.getLocalidades()) : "null");
        logger.info("   - Ambientes disponibles: {}", contextoDto != null && contextoDto.getAmbientes() != null ? String.join(", ", contextoDto.getAmbientes()) : "null");
        logger.info("   - Rangos de precio disponibles: {}", contextoDto != null && contextoDto.getRangosPrecio() != null ? String.join(", ", contextoDto.getRangosPrecio()) : "null");

        // 2. Analizar consulta con OpenAI
        logger.info("🤖 Enviando consulta a OpenAI...");
        String respuestaJson = openAIService.analizarConsultaNLP(
            request.getConsulta(),
            contexto,
            promptIdBusqueda
        );
        logger.info("🤖 Respuesta JSON de OpenAI: {}", respuestaJson);

        // 3. Parsear respuesta JSON de OpenAI
        BusquedaNLPResponseDto respuestaNLP = parsearRespuestaOpenAI(respuestaJson);
        logger.info("📥 Respuesta parseada de IA (ANTES de validación):");
        logger.info("   - Tipo comida: {}", respuestaNLP.getTipoComida());
        logger.info("   - Barrio: {}", respuestaNLP.getBarrio());
        logger.info("   - Localidad: {}", respuestaNLP.getLocalidad());
        logger.info("   - Ambiente: {}", respuestaNLP.getAmbiente());
        logger.info("   - Rango precio: {}", respuestaNLP.getRangoPrecio());
        logger.info("   - Palabras clave: {}", respuestaNLP.getPalabrasClave());
        logger.info("   - Momento día: {}", respuestaNLP.getMomentoDia());
        logger.info("   - Intención: {}", respuestaNLP.getIntencion());
        
        // Guardar la localidad original de la IA antes de validar
        String localidadOriginalIA = respuestaNLP.getLocalidad();
        
        // 3.0. Validar y mapear valores de la IA a valores exactos del catálogo
        respuestaNLP = validacionCatalogoService.validarYMapar(respuestaNLP);
        logger.info("✅ Respuesta validada (DESPUÉS de validación):");
        logger.info("   - Tipo comida: {}", respuestaNLP.getTipoComida());
        logger.info("   - Barrio: {}", respuestaNLP.getBarrio());
        logger.info("   - Localidad: {}", respuestaNLP.getLocalidad());
        logger.info("   - Ambiente: {}", respuestaNLP.getAmbiente());
        logger.info("   - Rango precio: {}", respuestaNLP.getRangoPrecio());
        logger.info("   - Palabras clave: {}", respuestaNLP.getPalabrasClave());
        
        // 3.1. Si el usuario está autenticado, obtener su localidad PERO solo usarla si:
        // - OpenAI NO devolvió ninguna localidad (null o vacío en la respuesta original)
        // - NO usar la localidad del usuario si OpenAI devolvió una localidad genérica que no se mapeó
        // Esto evita filtrar incorrectamente cuando la IA devuelve algo genérico como "Córdoba"
        if (nroCliente != null && !nroCliente.isEmpty()) {
            try {
                String localidadUsuario = clienteRepository.obtenerLocalidadPorNroCliente(nroCliente);
                if (localidadUsuario != null && !localidadUsuario.isEmpty()) {
                    // Solo usar la localidad del usuario si OpenAI NO devolvió ninguna localidad en absoluto
                    // Si OpenAI devolvió algo (aunque no se haya mapeado), NO usar la del usuario
                    // para evitar filtrar incorrectamente
                    if (localidadOriginalIA == null || localidadOriginalIA.isEmpty()) {
                        respuestaNLP.setLocalidad(localidadUsuario);
                        logger.info("📍 Usando localidad del usuario autenticado: {} (OpenAI no devolvió localidad)", localidadUsuario);
                    } else {
                        logger.info("📍 OpenAI devolvió localidad '{}' (mapeada a '{}'), NO usando localidad del usuario para evitar filtrado incorrecto", 
                                   localidadOriginalIA, respuestaNLP.getLocalidad());
                    }
                }
            } catch (Exception e) {
                logger.error("Error al obtener localidad del usuario: {}", e.getMessage());
            }
        }
        
        // 4. Buscar restaurantes exactos usando stored procedure
        // IMPORTANTE: NO pasar nroCliente aquí - los resultados exactos deben ser estrictamente
        // basados en los criterios de búsqueda, no en preferencias del usuario
        // Las preferencias del usuario solo se usan en sugerencias
        logger.info("🔎 Llamando a stored procedure sp_BuscarRestaurantesPorNLP con parámetros:");
        logger.info("   - tiposComida: {}", respuestaNLP.getTipoComida());
        logger.info("   - barrio: {}", respuestaNLP.getBarrio());
        logger.info("   - localidad: {}", respuestaNLP.getLocalidad());
        logger.info("   - ambiente: {}", respuestaNLP.getAmbiente());
        logger.info("   - rangoPrecio: {}", respuestaNLP.getRangoPrecio());
        logger.info("   - palabrasClave: {}", respuestaNLP.getPalabrasClave());
        logger.info("   - nroCliente: null (NO usar preferencias del cliente en resultados exactos)");
        
        // Crear DTO tipado con los parámetros de búsqueda
        BusquedaNLPParametrosDto parametrosBusqueda = new BusquedaNLPParametrosDto(
            respuestaNLP.getTipoComida(),
            respuestaNLP.getBarrio(),
            respuestaNLP.getLocalidad(),
            respuestaNLP.getAmbiente(),
            respuestaNLP.getRangoPrecio(),
            respuestaNLP.getPalabrasClave(),
            null // NO usar preferencias del cliente en resultados exactos
        );
        
        List<RestauranteDto> resultadosExactos = restauranteRepository.buscarPorNLP(parametrosBusqueda);
        
        logger.info("📊 Resultados del SP (ANTES de eliminar duplicados): {} restaurantes", resultadosExactos.size());
        if (!resultadosExactos.isEmpty()) {
            logger.info("   Restaurantes encontrados:");
            for (int i = 0; i < Math.min(resultadosExactos.size(), 10); i++) {
                RestauranteDto r = resultadosExactos.get(i);
                logger.info("   {}. {} (ID: {})", i + 1, r.getNombre(), r.getNroRestaurante());
            }
            if (resultadosExactos.size() > 10) {
                logger.info("   ... y {} más", resultadosExactos.size() - 10);
            }
        }
        
        // Eliminar duplicados de resultados exactos (por nro_restaurante)
        int cantidadAntes = resultadosExactos.size();
        resultadosExactos = resultadosExactos.stream()
            .filter(r -> r.getNroRestaurante() != null)
            .collect(Collectors.toMap(
                RestauranteDto::getNroRestaurante,
                r -> r,
                (r1, r2) -> r1 // En caso de duplicados, mantener el primero
            ))
            .values()
            .stream()
            .collect(Collectors.toList());
        
        logger.info("✅ Resultados exactos (DESPUÉS de eliminar duplicados): {} restaurantes (eliminados: {})", 
                   resultadosExactos.size(), cantidadAntes - resultadosExactos.size());
        
        // 5. Obtener sugerencias (siempre, incluso si hay resultados exactos)
        // Las sugerencias se basan en preferencias del usuario si está autenticado,
        // o restaurantes populares/aleatorios si no lo está
        logger.info("💡 Obteniendo sugerencias...");
        logger.info("   - Excluir restaurantes: {} IDs", resultadosExactos.size());
        logger.info("   - nroCliente para preferencias: {}", nroCliente != null ? nroCliente : "null");
        logger.info("   - Límite: 10");
        
        List<RestauranteDto> sugerencias = restauranteRepository.obtenerSugerencias(
            resultadosExactos, // Excluir los que ya están en resultados exactos
            nroCliente,        // Para usar preferencias del usuario
            10                 // Límite de sugerencias
        );
        
        logger.info("📊 Sugerencias del SP (ANTES de eliminar duplicados): {} restaurantes", sugerencias.size());
        
        // Eliminar duplicados de sugerencias (por nro_restaurante)
        sugerencias = sugerencias.stream()
            .filter(r -> r.getNroRestaurante() != null)
            .collect(Collectors.toMap(
                RestauranteDto::getNroRestaurante,
                r -> r,
                (r1, r2) -> r1 // En caso de duplicados, mantener el primero
            ))
            .values()
            .stream()
            .collect(Collectors.toList());
        
        logger.info("   Después de eliminar duplicados: {} restaurantes", sugerencias.size());
        
        // Asegurar que las sugerencias no incluyan restaurantes de resultados exactos
        if (!resultadosExactos.isEmpty()) {
            Set<String> idsExactos = resultadosExactos.stream()
                .map(RestauranteDto::getNroRestaurante)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
            
            int cantidadAntesExclusion = sugerencias.size();
            sugerencias = sugerencias.stream()
                .filter(r -> !idsExactos.contains(r.getNroRestaurante()))
                .collect(Collectors.toList());
            
            logger.info("   Después de excluir resultados exactos: {} restaurantes (excluidos: {})", 
                       sugerencias.size(), cantidadAntesExclusion - sugerencias.size());
        }
        
        logger.info("✅ Sugerencias finales: {} restaurantes", sugerencias.size());
        if (!sugerencias.isEmpty()) {
            logger.info("   Restaurantes sugeridos:");
            for (int i = 0; i < Math.min(sugerencias.size(), 5); i++) {
                RestauranteDto r = sugerencias.get(i);
                logger.info("   {}. {} (ID: {})", i + 1, r.getNombre(), r.getNroRestaurante());
            }
        }
        
        logger.info("🔍 ===== FIN BÚSQUEDA NLP =====");
        logger.info("📈 Resumen: {} resultados exactos, {} sugerencias", resultadosExactos.size(), sugerencias.size());
        
        // Si hay muchos resultados sin filtros específicos, puede ser que OpenAI no extrajo correctamente
        if (resultadosExactos.size() > 10 && 
            (respuestaNLP.getTipoComida() == null || respuestaNLP.getTipoComida().isEmpty()) &&
            (respuestaNLP.getPalabrasClave() == null || respuestaNLP.getPalabrasClave().isEmpty())) {
            logger.warn("⚠️  Se encontraron {} restaurantes sin filtros específicos. OpenAI puede no haber extraído correctamente la intención.", resultadosExactos.size());
        }

        return new BusquedaNLPResultadoDto(resultadosExactos, sugerencias);
    }

    /**
     * Construye el contexto con catálogos de la BD
     */
    private BusquedaContextoDto construirContexto() {
        BusquedaContextoDto contexto = new BusquedaContextoDto();
        BusquedaContextoDto.ContextoDto ctx = new BusquedaContextoDto.ContextoDto();

        ctx.setTiposComida(busquedaRepository.obtenerTiposComida());
        ctx.setBarrios(busquedaRepository.obtenerBarrios());
        ctx.setLocalidades(busquedaRepository.obtenerLocalidades());
        ctx.setAmbientes(busquedaRepository.obtenerAmbientes());
        ctx.setRangosPrecio(busquedaRepository.obtenerRangosPrecio());

        contexto.setContexto(ctx);
        return contexto;
    }

    /**
     * Parsea la respuesta JSON de OpenAI a BusquedaNLPResponseDto
     * Maneja estructuras anidadas (como "criterios") si OpenAI las devuelve
     */
    private BusquedaNLPResponseDto parsearRespuestaOpenAI(String jsonResponse) {
        try {
            logger.debug("Respuesta raw de OpenAI ({} caracteres)", jsonResponse.length());
            
            // Limpiar JSON: extraer solo la parte JSON del texto
            String jsonLimpio = extraerJSONDeTexto(jsonResponse);
            

            // Intentar parsear directamente primero
            try {
                BusquedaNLPResponseDto dto = objectMapper.readValue(jsonLimpio, BusquedaNLPResponseDto.class);
                
                // Si tipoComida es null, intentar extraer de estructura anidada
                if (dto.getTipoComida() == null || dto.getTipoComida().isEmpty()) {
                    dto = extraerDeEstructuraAnidada(jsonLimpio, dto);
                }
                
                return dto;
            } catch (Exception e) {
                // Si falla el parsing directo, intentar con estructura anidada
                logger.warn("Error al parsear directamente, intentando con estructura anidada: {}", e.getMessage());
                return extraerDeEstructuraAnidada(jsonLimpio, new BusquedaNLPResponseDto());
            }
        } catch (Exception e) {
            logger.error("Error al parsear respuesta JSON de OpenAI", e);
            logger.error("Respuesta recibida (primeros 1000 caracteres): {}", 
                        jsonResponse.length() > 1000 ? jsonResponse.substring(0, 1000) : jsonResponse);
            throw new RuntimeException("Error al parsear respuesta de OpenAI: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extrae datos de una estructura anidada (ej: criterios.tiposComida)
     */
    @SuppressWarnings("unchecked")
    private BusquedaNLPResponseDto extraerDeEstructuraAnidada(String jsonLimpio, BusquedaNLPResponseDto dto) {
        try {
            // Parsear como Map genérico
            java.util.Map<String, Object> jsonMap = objectMapper.readValue(jsonLimpio, java.util.Map.class);
            
            // Buscar en el nivel superior primero
            if (jsonMap.containsKey("tipoComida")) {
                dto.setTipoComida((List<String>) jsonMap.get("tipoComida"));
            }
            if (jsonMap.containsKey("barrio")) {
                dto.setBarrio((String) jsonMap.get("barrio"));
            }
            if (jsonMap.containsKey("localidad")) {
                dto.setLocalidad((String) jsonMap.get("localidad"));
            }
            if (jsonMap.containsKey("ambiente")) {
                dto.setAmbiente((String) jsonMap.get("ambiente"));
            }
            if (jsonMap.containsKey("rangoPrecio")) {
                dto.setRangoPrecio((String) jsonMap.get("rangoPrecio"));
            }
            if (jsonMap.containsKey("momentoDia")) {
                dto.setMomentoDia((String) jsonMap.get("momentoDia"));
            }
            if (jsonMap.containsKey("intencion")) {
                dto.setIntencion((String) jsonMap.get("intencion"));
            }
            if (jsonMap.containsKey("palabrasClave")) {
                dto.setPalabrasClave((List<String>) jsonMap.get("palabrasClave"));
            }
            
            // Si hay un objeto anidado "criterios", extraer de ahí también
            if (jsonMap.containsKey("criterios")) {
                java.util.Map<String, Object> criterios = (java.util.Map<String, Object>) jsonMap.get("criterios");
                
                if (criterios != null) {
                    if (dto.getTipoComida() == null && criterios.containsKey("tiposComida")) {
                        Object tiposComidaObj = criterios.get("tiposComida");
                        if (tiposComidaObj instanceof List) {
                            dto.setTipoComida((List<String>) tiposComidaObj);
                        } else if (tiposComidaObj instanceof String) {
                            dto.setTipoComida(java.util.Arrays.asList((String) tiposComidaObj));
                        }
                    }
                    if (dto.getBarrio() == null && criterios.containsKey("barrios")) {
                        Object barriosObj = criterios.get("barrios");
                        if (barriosObj instanceof List) {
                            List<String> barrios = (List<String>) barriosObj;
                            if (!barrios.isEmpty()) {
                                dto.setBarrio(barrios.get(0)); // Tomar el primero
                            }
                        } else if (barriosObj instanceof String) {
                            dto.setBarrio((String) barriosObj);
                        }
                    }
                    if (dto.getLocalidad() == null && criterios.containsKey("localidades")) {
                        Object localidadesObj = criterios.get("localidades");
                        if (localidadesObj instanceof List) {
                            List<String> localidades = (List<String>) localidadesObj;
                            if (!localidades.isEmpty()) {
                                dto.setLocalidad(localidades.get(0)); // Tomar el primero
                            }
                        } else if (localidadesObj instanceof String) {
                            dto.setLocalidad((String) localidadesObj);
                        }
                    }
                    if (dto.getAmbiente() == null && criterios.containsKey("ambientes")) {
                        Object ambientesObj = criterios.get("ambientes");
                        if (ambientesObj instanceof List) {
                            List<String> ambientes = (List<String>) ambientesObj;
                            if (!ambientes.isEmpty()) {
                                dto.setAmbiente(ambientes.get(0)); // Tomar el primero
                            }
                        } else if (ambientesObj instanceof String) {
                            dto.setAmbiente((String) ambientesObj);
                        }
                    }
                    if (dto.getRangoPrecio() == null && criterios.containsKey("rangosPrecio")) {
                        Object rangosPrecioObj = criterios.get("rangosPrecio");
                        if (rangosPrecioObj instanceof List) {
                            List<String> rangosPrecio = (List<String>) rangosPrecioObj;
                            if (!rangosPrecio.isEmpty()) {
                                dto.setRangoPrecio(rangosPrecio.get(0)); // Tomar el primero
                            }
                        } else if (rangosPrecioObj instanceof String) {
                            dto.setRangoPrecio((String) rangosPrecioObj);
                        }
                    }
                }
            }
            
            // Si aún no hay tipoComida, buscar en palabras clave y extraer si coincide con catálogo
            if ((dto.getTipoComida() == null || dto.getTipoComida().isEmpty()) && 
                (dto.getPalabrasClave() != null && !dto.getPalabrasClave().isEmpty())) {
                List<String> tiposComidaCatalogo = busquedaRepository.obtenerTiposComida();
                List<String> tiposComidaEncontrados = new ArrayList<>();
                
                for (String palabra : dto.getPalabrasClave()) {
                    for (String tipo : tiposComidaCatalogo) {
                        if (tipo.equalsIgnoreCase(palabra) || 
                            palabra.toLowerCase().contains(tipo.toLowerCase()) ||
                            tipo.toLowerCase().contains(palabra.toLowerCase())) {
                            if (!tiposComidaEncontrados.contains(tipo)) {
                                tiposComidaEncontrados.add(tipo);
                            }
                        }
                    }
                }
                
                if (!tiposComidaEncontrados.isEmpty()) {
                    dto.setTipoComida(tiposComidaEncontrados);
                    logger.debug("Tipos de comida extraídos de palabras clave: {}", tiposComidaEncontrados);
                }
            }
            
            logger.debug("Datos extraídos: tipoComida={}, barrio={}, palabrasClave={}", 
                       dto.getTipoComida(), dto.getBarrio(), dto.getPalabrasClave());
            
            return dto;
        } catch (Exception e) {
            logger.error("Error al extraer de estructura anidada", e);
            return dto; // Devolver el DTO original si falla
        }
    }
    
    /**
     * Extrae el JSON del texto, incluso si hay texto explicativo antes o después
     */
    private String extraerJSONDeTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("La respuesta de OpenAI está vacía");
        }
        
        String textoLimpio = texto.trim();
        
        // 1. Eliminar markdown code blocks
        if (textoLimpio.startsWith("```json")) {
            textoLimpio = textoLimpio.substring(7).trim();
        }
        if (textoLimpio.startsWith("```")) {
            textoLimpio = textoLimpio.substring(3).trim();
        }
        if (textoLimpio.endsWith("```")) {
            textoLimpio = textoLimpio.substring(0, textoLimpio.length() - 3).trim();
        }
        
        // 2. Buscar el primer '{' que marca el inicio del JSON
        int inicioJson = textoLimpio.indexOf('{');
        if (inicioJson == -1) {
            throw new IllegalArgumentException("No se encontró JSON en la respuesta de OpenAI");
        }
        
        // 3. Buscar el último '}' que marca el final del JSON
        // Contar llaves balanceadas para encontrar el cierre correcto
        int nivelAnidacion = 0;
        int finJson = -1;
        
        for (int i = inicioJson; i < textoLimpio.length(); i++) {
            char c = textoLimpio.charAt(i);
            if (c == '{') {
                nivelAnidacion++;
            } else if (c == '}') {
                nivelAnidacion--;
                if (nivelAnidacion == 0) {
                    finJson = i + 1;
                    break;
                }
            }
        }
        
        if (finJson == -1) {
            throw new IllegalArgumentException("JSON incompleto en la respuesta de OpenAI");
        }
        
        // 4. Extraer solo la parte JSON
        String jsonExtraido = textoLimpio.substring(inicioJson, finJson).trim();
        
        
        return jsonExtraido;
    }
}
