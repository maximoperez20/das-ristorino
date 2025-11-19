package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.BusquedaContextoDto;
import ar.edu.ubp.das.backend.dto.BusquedaNLPRequestDto;
import ar.edu.ubp.das.backend.dto.BusquedaNLPResponseDto;
import ar.edu.ubp.das.backend.dto.RestauranteDto;
import ar.edu.ubp.das.backend.repository.BusquedaRepository;
import ar.edu.ubp.das.backend.repository.ClienteRepository;
import ar.edu.ubp.das.backend.repository.RestauranteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para búsqueda de restaurantes usando lenguaje natural (NLP)
 * Cumple Requerimientos 10 y 35: Buscar restaurantes con lenguaje natural
 */
@Service
public class BusquedaNLPService {

    private static final Logger logger = LoggerFactory.getLogger(BusquedaNLPService.class);

    @Autowired
    private BusquedaRepository busquedaRepository;

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Value("${openai.prompt.busqueda.id:pmpt_68f9295bc1e48194b2e725a7b5df2b1c0a01e67130022025}")
    private String promptIdBusqueda;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Procesa una consulta en lenguaje natural y devuelve restaurantes relevantes
     *
     * @param request Solicitud con la consulta del usuario
     * @param nroCliente UUID del cliente autenticado (opcional, puede ser null)
     * @return Lista de restaurantes que coinciden con la intención del usuario
     */
    public List<RestauranteDto> buscarRestaurantesPorNLP(BusquedaNLPRequestDto request, String nroCliente) {
        logger.info("Iniciando búsqueda NLP para consulta: {}", request.getConsulta());

        // 1. Obtener catálogos de la BD
        BusquedaContextoDto contexto = construirContexto();
        logger.info("Contexto construido con {} tipos de comida, {} barrios, {} localidades",
                    contexto.getContexto().getTiposComida().size(),
                    contexto.getContexto().getBarrios().size(),
                    contexto.getContexto().getLocalidades().size());

        // 2. Analizar consulta con OpenAI
        String respuestaJson = openAIService.analizarConsultaNLP(
            request.getConsulta(),
            contexto,
            promptIdBusqueda
        );
        logger.info("Respuesta JSON de OpenAI obtenida");

        // 3. Parsear respuesta JSON de OpenAI
        BusquedaNLPResponseDto respuestaNLP = parsearRespuestaOpenAI(respuestaJson);
        
        // 3.1. Si el usuario está autenticado, obtener su localidad y usarla si OpenAI no devolvió una
        if (nroCliente != null && !nroCliente.isEmpty()) {
            try {
                String localidadUsuario = clienteRepository.obtenerLocalidadPorNroCliente(nroCliente);
                if (localidadUsuario != null && !localidadUsuario.isEmpty()) {
                    // Si OpenAI no devolvió una localidad específica, usar la del usuario
                    if (respuestaNLP.getLocalidad() == null || respuestaNLP.getLocalidad().isEmpty()) {
                        respuestaNLP.setLocalidad(localidadUsuario);
                        logger.info("📍 Usando localidad del usuario autenticado: {}", localidadUsuario);
                    } else if (!localidadUsuario.equalsIgnoreCase(respuestaNLP.getLocalidad())) {
                        // Si OpenAI devolvió una localidad diferente, priorizar la del usuario
                        respuestaNLP.setLocalidad(localidadUsuario);
                        logger.info("📍 Priorizando localidad del usuario ({}) sobre la de OpenAI ({})", 
                                   localidadUsuario, respuestaNLP.getLocalidad());
                    }
                }
            } catch (Exception e) {
                logger.error("Error al obtener localidad del usuario: {}", e.getMessage());
            }
        }
        
        logger.info("Intención extraída - Tipo: {}, Localidad: {}, Barrio: {}", 
                   respuestaNLP.getTipoComida(), respuestaNLP.getLocalidad(), respuestaNLP.getBarrio());

        // 4. Buscar restaurantes usando stored procedure
        // Si nroCliente está presente, se usará para hacer match con preferencias_clientes
        List<RestauranteDto> restaurantes = restauranteRepository.buscarPorNLP(
            respuestaNLP.getTipoComida(),
            respuestaNLP.getBarrio(),
            respuestaNLP.getLocalidad(),
            respuestaNLP.getAmbiente(),
            respuestaNLP.getRangoPrecio(),
            respuestaNLP.getPalabrasClave(),
            nroCliente // Puede ser null si el usuario no está autenticado
        );
        logger.info("Encontrados {} restaurantes", restaurantes.size());
        
        // Si hay muchos resultados sin filtros específicos, puede ser que OpenAI no extrajo correctamente
        if (restaurantes.size() > 10 && 
            (respuestaNLP.getTipoComida() == null || respuestaNLP.getTipoComida().isEmpty()) &&
            (respuestaNLP.getPalabrasClave() == null || respuestaNLP.getPalabrasClave().isEmpty())) {
            logger.warn("⚠️  Se encontraron {} restaurantes sin filtros específicos. OpenAI puede no haber extraído correctamente la intención.", restaurantes.size());
        }

        return restaurantes;
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
            logger.info("Respuesta raw de OpenAI (primeros 500 caracteres): {}", 
                        jsonResponse.length() > 500 ? jsonResponse.substring(0, 500) : jsonResponse);
            
            // Limpiar JSON: extraer solo la parte JSON del texto
            String jsonLimpio = extraerJSONDeTexto(jsonResponse);
            
            logger.info("JSON limpio extraído: {}", jsonLimpio);

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
                    logger.info("Tipos de comida extraídos de palabras clave: {}", tiposComidaEncontrados);
                }
            }
            
            logger.info("Datos extraídos (puede incluir estructura anidada): tipoComida={}, barrio={}, palabrasClave={}", 
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
        
        logger.debug("JSON extraído: inicio={}, fin={}, longitud={}", 
                    inicioJson, finJson, jsonExtraido.length());
        
        return jsonExtraido;
    }
}
