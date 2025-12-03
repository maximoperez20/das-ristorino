package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.BusquedaContextoDto;
import ar.edu.ubp.das.backend.dto.BusquedaNLPRequestDto;
import ar.edu.ubp.das.backend.dto.BusquedaNLPResponseDto;
import ar.edu.ubp.das.backend.dto.BusquedaNLPResultadoDto;
import ar.edu.ubp.das.backend.dto.BusquedaNLPParametrosDto;
import ar.edu.ubp.das.backend.dto.CatalogosDto;
import ar.edu.ubp.das.backend.dto.PreferenciaClienteDto;
import ar.edu.ubp.das.backend.dto.RestauranteDto;
import ar.edu.ubp.das.backend.repository.BusquedaRepository;
import ar.edu.ubp.das.backend.repository.ClienteRepository;
import ar.edu.ubp.das.backend.repository.RestauranteRepository;
import ar.edu.ubp.das.backend.service.PreferenciaService;
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
    private final PreferenciaService preferenciaService;
    
    @Value("${openai.prompt.busqueda.id:pmpt_68f9295bc1e48194b2e725a7b5df2b1c0a01e67130022025}")
    private String promptIdBusqueda;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public BusquedaNLPService(BusquedaRepository busquedaRepository,
                             OpenAIService openAIService,
                             RestauranteRepository restauranteRepository,
                             ClienteRepository clienteRepository,
                             ValidacionCatalogoService validacionCatalogoService,
                             PreferenciaService preferenciaService) {
        this.busquedaRepository = busquedaRepository;
        this.openAIService = openAIService;
        this.restauranteRepository = restauranteRepository;
        this.clienteRepository = clienteRepository;
        this.validacionCatalogoService = validacionCatalogoService;
        this.preferenciaService = preferenciaService;
    }

    /**
     * Procesa una consulta en lenguaje natural y devuelve restaurantes relevantes con resultados exactos y sugerencias
     *
     * @param request Solicitud con la consulta del usuario
     * @param nroCliente UUID del cliente autenticado (opcional, puede ser null)
     * @return DTO con resultados exactos y sugerencias
     */
    public BusquedaNLPResultadoDto buscarRestaurantesPorNLP(BusquedaNLPRequestDto request, String nroCliente) {
        logger.info("Búsqueda NLP iniciada. Consulta: '{}', Cliente: {}", request.getConsulta(), nroCliente != null ? nroCliente : "No autenticado");

        // 1. Obtener catálogos de la BD (una sola vez)
        CatalogosDto catalogos = obtenerCatalogos();
        
        // 1.1. Obtener preferencias del usuario si está autenticado
        BusquedaContextoDto.PreferenciasUsuarioDto preferenciasUsuario = null;
        if (nroCliente != null && !nroCliente.isEmpty()) {
            try {
                preferenciasUsuario = obtenerPreferenciasUsuario(nroCliente);
                if (preferenciasUsuario != null && preferenciasUsuario.tienePreferencias()) {
                    logger.debug("Preferencias del usuario obtenidas - Tipos comida: {}, Ambientes: {}, Rangos precio: {}", 
                                preferenciasUsuario.getTiposComida(), preferenciasUsuario.getAmbientes(), preferenciasUsuario.getRangosPrecio());
                }
            } catch (Exception e) {
                logger.warn("Error al obtener preferencias del usuario: {}", e.getMessage());
            }
        }
        
        BusquedaContextoDto contexto = construirContexto(catalogos, preferenciasUsuario);

        // 2. Analizar consulta con OpenAI
        String respuestaJson = openAIService.analizarConsultaNLP(
            request.getConsulta(),
            contexto,
            promptIdBusqueda
        );
        logger.debug("Respuesta JSON de OpenAI: {}", respuestaJson);

        // 3. Parsear respuesta JSON de OpenAI
        BusquedaNLPResponseDto respuestaNLP = parsearRespuestaOpenAI(respuestaJson, catalogos);
        
        // Guardar la localidad original de la IA antes de validar
        String localidadOriginalIA = respuestaNLP.getLocalidad();
        
        // 3.0. Validar y mapear valores de la IA a valores exactos del catálogo
        respuestaNLP = validacionCatalogoService.validarYMapar(respuestaNLP, catalogos);
        logger.debug("Respuesta validada - Tipo comida: {}, Localidad: {}", respuestaNLP.getTipoComida(), respuestaNLP.getLocalidad());
        
        // 3.1. Si el usuario está autenticado, obtener su localidad solo si OpenAI no devolvió ninguna
        if (nroCliente != null && !nroCliente.isEmpty()) {
            try {
                String localidadUsuario = clienteRepository.obtenerLocalidadPorNroCliente(nroCliente);
                if (localidadUsuario != null && !localidadUsuario.isEmpty() && 
                    (localidadOriginalIA == null || localidadOriginalIA.isEmpty())) {
                    respuestaNLP.setLocalidad(localidadUsuario);
                    logger.debug("Usando localidad del usuario: {}", localidadUsuario);
                }
            } catch (Exception e) {
                logger.error("Error al obtener localidad del usuario: {}", e.getMessage());
            }
        }
        
        // 4. Buscar restaurantes exactos usando stored procedure
        // Pasar nroCliente para que el SP use las preferencias del usuario en el scoring
        BusquedaNLPParametrosDto parametrosBusqueda = new BusquedaNLPParametrosDto(
            respuestaNLP.getTipoComida(),
            respuestaNLP.getBarrio(),
            respuestaNLP.getLocalidad(),
            respuestaNLP.getAmbiente(),
            respuestaNLP.getRangoPrecio(),
            respuestaNLP.getPalabrasClave(),
            nroCliente // Usar preferencias del usuario en scoring
        );
        
        List<RestauranteDto> resultadosExactos = restauranteRepository.buscarPorNLP(parametrosBusqueda);
        
        // Eliminar duplicados de resultados exactos
        resultadosExactos = resultadosExactos.stream()
            .filter(r -> r.getNroRestaurante() != null)
            .collect(Collectors.toMap(
                RestauranteDto::getNroRestaurante,
                r -> r,
                (r1, r2) -> r1
            ))
            .values()
            .stream()
            .collect(Collectors.toList());
        
        logger.info("Resultados exactos: {} restaurantes", resultadosExactos.size());
        
        // 5. Obtener sugerencias
        List<RestauranteDto> sugerencias = restauranteRepository.obtenerSugerencias(
            resultadosExactos,
            nroCliente,
            10
        );
        
        // Eliminar duplicados de sugerencias
        sugerencias = sugerencias.stream()
            .filter(r -> r.getNroRestaurante() != null)
            .collect(Collectors.toMap(
                RestauranteDto::getNroRestaurante,
                r -> r,
                (r1, r2) -> r1
            ))
            .values()
            .stream()
            .collect(Collectors.toList());
        
        // Excluir restaurantes que ya están en resultados exactos
        if (!resultadosExactos.isEmpty()) {
            Set<String> idsExactos = resultadosExactos.stream()
                .map(RestauranteDto::getNroRestaurante)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
            
            sugerencias = sugerencias.stream()
                .filter(r -> !idsExactos.contains(r.getNroRestaurante()))
                .collect(Collectors.toList());
        }
        
        logger.info("Sugerencias: {} restaurantes", sugerencias.size());
        
        if (resultadosExactos.size() > 10 && 
            (respuestaNLP.getTipoComida() == null || respuestaNLP.getTipoComida().isEmpty()) &&
            (respuestaNLP.getPalabrasClave() == null || respuestaNLP.getPalabrasClave().isEmpty())) {
            logger.warn("Se encontraron {} restaurantes sin filtros específicos", resultadosExactos.size());
        }

        return new BusquedaNLPResultadoDto(resultadosExactos, sugerencias);
    }

    /**
     * Obtiene todos los catálogos de la BD una sola vez
     */
    private CatalogosDto obtenerCatalogos() {
        return new CatalogosDto(
            busquedaRepository.obtenerTiposComida(),
            busquedaRepository.obtenerBarrios(),
            busquedaRepository.obtenerLocalidades(),
            busquedaRepository.obtenerAmbientes(),
            busquedaRepository.obtenerRangosPrecio()
        );
    }

    /**
     * Obtiene las preferencias del usuario agrupadas por categoría
     */
    private BusquedaContextoDto.PreferenciasUsuarioDto obtenerPreferenciasUsuario(String nroCliente) {
        try {
            List<PreferenciaClienteDto> preferencias = preferenciaService.obtenerPreferenciasCliente(nroCliente, 0); // Usar idioma 0 (es-AR)
            
            if (preferencias == null || preferencias.isEmpty()) {
                return null;
            }
            
            BusquedaContextoDto.PreferenciasUsuarioDto preferenciasDto = new BusquedaContextoDto.PreferenciasUsuarioDto();
            
            for (PreferenciaClienteDto pref : preferencias) {
                String categoria = pref.getNombreCategoria();
                String dominio = pref.getNombreDominio();
                
                if (dominio == null || dominio.isEmpty()) {
                    continue;
                }
                
                if ("Tipo de comida".equals(categoria)) {
                    preferenciasDto.getTiposComida().add(dominio);
                } else if ("Ambiente".equals(categoria)) {
                    preferenciasDto.getAmbientes().add(dominio);
                } else if ("Rango de precio".equals(categoria)) {
                    preferenciasDto.getRangosPrecio().add(dominio);
                }
            }
            
            return preferenciasDto.tienePreferencias() ? preferenciasDto : null;
        } catch (Exception e) {
            logger.error("Error al obtener preferencias del usuario: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Construye el contexto con catálogos y preferencias del usuario para enviar a OpenAI
     */
    private BusquedaContextoDto construirContexto(CatalogosDto catalogos, BusquedaContextoDto.PreferenciasUsuarioDto preferenciasUsuario) {
        BusquedaContextoDto contexto = new BusquedaContextoDto();
        BusquedaContextoDto.ContextoDto contextoDto = catalogos.toContextoDto();
        
        // Agregar preferencias del usuario al contexto si existen
        if (preferenciasUsuario != null && preferenciasUsuario.tienePreferencias()) {
            contextoDto.setPreferenciasUsuario(preferenciasUsuario);
        }
        
        contexto.setContexto(contextoDto);
        return contexto;
    }

    /**
     * Parsea la respuesta JSON de OpenAI a BusquedaNLPResponseDto
     * Maneja estructuras anidadas (como "criterios") si OpenAI las devuelve
     */
    private BusquedaNLPResponseDto parsearRespuestaOpenAI(String jsonResponse, CatalogosDto catalogos) {
        try {
            logger.debug("Respuesta raw de OpenAI ({} caracteres)", jsonResponse.length());
            
            // Limpiar JSON: extraer solo la parte JSON del texto
            String jsonLimpio = extraerJSONDeTexto(jsonResponse);
            

            // Intentar parsear directamente primero
            try {
                BusquedaNLPResponseDto dto = objectMapper.readValue(jsonLimpio, BusquedaNLPResponseDto.class);
                
                // Si tipoComida es null, intentar extraer de estructura anidada
                if (dto.getTipoComida() == null || dto.getTipoComida().isEmpty()) {
                    dto = extraerDeEstructuraAnidada(jsonLimpio, dto, catalogos);
                }
                
                return dto;
            } catch (Exception e) {
                logger.warn("Error al parsear directamente, intentando con estructura anidada: {}", e.getMessage());
                return extraerDeEstructuraAnidada(jsonLimpio, new BusquedaNLPResponseDto(), catalogos);
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
    private BusquedaNLPResponseDto extraerDeEstructuraAnidada(String jsonLimpio, BusquedaNLPResponseDto dto, CatalogosDto catalogos) {
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
                List<String> tiposComidaCatalogo = catalogos.getTiposComida();
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
                }
            }
            
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
