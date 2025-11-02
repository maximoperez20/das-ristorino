package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.BusquedaContextoDto;
import ar.edu.ubp.das.backend.dto.BusquedaNLPRequestDto;
import ar.edu.ubp.das.backend.dto.BusquedaNLPResponseDto;
import ar.edu.ubp.das.backend.dto.RestauranteDto;
import ar.edu.ubp.das.backend.repository.BusquedaRepository;
import ar.edu.ubp.das.backend.repository.RestauranteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    @Value("${openai.prompt.busqueda.id:pmpt_68f9295bc1e48194b2e725a7b5df2b1c0a01e67130022025}")
    private String promptIdBusqueda;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Procesa una consulta en lenguaje natural y devuelve restaurantes relevantes
     *
     * @param request Solicitud con la consulta del usuario
     * @return Lista de restaurantes que coinciden con la intención del usuario
     */
    public List<RestauranteDto> buscarRestaurantesPorNLP(BusquedaNLPRequestDto request) {
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
        logger.info("Intención extraída - Tipo comida: {}, Barrio: {}, Ambiente: {}, Rango precio: {}",
                    respuestaNLP.getTipoComida(),
                    respuestaNLP.getBarrio(),
                    respuestaNLP.getAmbiente(),
                    respuestaNLP.getRangoPrecio());

        // 4. Buscar restaurantes usando stored procedure
        List<RestauranteDto> restaurantes = restauranteRepository.buscarPorNLP(
            respuestaNLP.getTipoComida(),
            respuestaNLP.getBarrio(),
            respuestaNLP.getLocalidad(),
            respuestaNLP.getAmbiente(),
            respuestaNLP.getRangoPrecio(),
            respuestaNLP.getPalabrasClave()
        );
        logger.info("Encontrados {} restaurantes", restaurantes.size());

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
     */
    private BusquedaNLPResponseDto parsearRespuestaOpenAI(String jsonResponse) {
        try {
            logger.info("Respuesta raw de OpenAI (primeros 500 caracteres): {}", 
                        jsonResponse.length() > 500 ? jsonResponse.substring(0, 500) : jsonResponse);
            
            // Limpiar JSON: extraer solo la parte JSON del texto
            String jsonLimpio = extraerJSONDeTexto(jsonResponse);
            
            logger.info("JSON limpio extraído: {}", jsonLimpio);

            // Configurar ObjectMapper para ignorar campos desconocidos
            return objectMapper.readValue(jsonLimpio, BusquedaNLPResponseDto.class);
        } catch (Exception e) {
            logger.error("Error al parsear respuesta JSON de OpenAI", e);
            logger.error("Respuesta recibida (primeros 1000 caracteres): {}", 
                        jsonResponse.length() > 1000 ? jsonResponse.substring(0, 1000) : jsonResponse);
            throw new RuntimeException("Error al parsear respuesta de OpenAI: " + e.getMessage(), e);
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

