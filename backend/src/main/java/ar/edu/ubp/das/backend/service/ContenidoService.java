package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.client.RestauranteClient;
import ar.edu.ubp.das.backend.client.RestauranteClientFactory;
import ar.edu.ubp.das.backend.dto.ContenidoGeneradoDto;
import ar.edu.ubp.das.backend.dto.GenerarContenidoRequestDto;
// removed unused imports after refactor
import ar.edu.ubp.das.backend.repository.ContenidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestión de contenidos generados con IA.
 * Orquesta la recopilación de datos, generación con OpenAI y almacenamiento.
 */
@Service
public class ContenidoService {

    private static final Logger logger = LoggerFactory.getLogger(ContenidoService.class);

    @Autowired
    private ContenidoRepository contenidoRepository;

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private RestauranteClientFactory restauranteClientFactory;

    @Value("${openai.prompt.id}")
    private String defaultPromptId;

    /**
     * Genera contenido publicitario con IA para un restaurante/sucursal.
     *
     * @param request Datos de la solicitud (restaurante, sucursal, idioma)
     * @return DTO con el contenido generado y guardado
     * @throws RuntimeException si no se encuentra el restaurante o hay error en la generación
     */
    public ContenidoGeneradoDto generarContenido(GenerarContenidoRequestDto request) {
        // Nuevo flujo: obtener los contenidos desde el sistema legacy, generar promociones con IA
        // y guardar solo en das_ristorino. Finalmente marcar como publicados en legacy.

        String promptId = (request.getPromptId() != null && !request.getPromptId().isEmpty())
                ? request.getPromptId()
                : defaultPromptId;

        // Obtener información del idioma
        String nomIdioma = contenidoRepository.obtenerNomIdioma(request.getNroIdioma());

        RestauranteClient client = restauranteClientFactory.getClient(request.getNroRestaurante());

        java.util.List<java.util.Map<String, Object>> legacyContenidos = client.obtenerContenidos(request.getNroRestaurante(), request.getNroSucursal());

        if (legacyContenidos == null || legacyContenidos.isEmpty()) {
            throw new RuntimeException("No se encontraron contenidos en el sistema legacy para el restaurante: " + request.getNroRestaurante());
        }

        ContenidoGeneradoDto ultimoResultado = null;
        java.util.List<String> contenidosMarcados = new java.util.ArrayList<>();

        for (java.util.Map<String, Object> legacy : legacyContenidos) {
            try {
                String contenidoFuente = legacy.get("contenidoAPublicar") != null ? (String) legacy.get("contenidoAPublicar") : "";
                String legacyNro = legacy.get("nroContenido") != null ? (String) legacy.get("nroContenido") : null;

                StringBuilder promptBuilder = new StringBuilder();
                promptBuilder.append("Generar un texto promocional breve en ").append(nomIdioma).append(" basado en el siguiente contenido:\n\n");
                promptBuilder.append(contenidoFuente);
                if (request.getContextoAdicional() != null && !request.getContextoAdicional().isEmpty()) {
                    promptBuilder.append("\n\nContexto adicional: ").append(request.getContextoAdicional());
                }

                String prompt = promptBuilder.toString();

                String contenidoGenerado = openAIService.generarContenidoPublicitario(prompt, promptId);

                // Guardar en das_ristorino
                // Obtener nroSucursal y costoClick desde el contenido legacy si están presentes
                String legacyNroSucursal = null;
                Object nroSucursalObj = legacy.get("nroSucursal");
                if (nroSucursalObj == null) {
                    nroSucursalObj = legacy.get("nro_sucursal");
                }
                if (nroSucursalObj != null) {
                    if (nroSucursalObj instanceof String) {
                        legacyNroSucursal = ((String) nroSucursalObj).trim();
                        if (legacyNroSucursal.isEmpty()) {
                            legacyNroSucursal = null;
                        }
                    } else if (nroSucursalObj instanceof Number) {
                        legacyNroSucursal = String.valueOf(nroSucursalObj);
                    } else {
                        // Fallback: use toString()
                        legacyNroSucursal = nroSucursalObj.toString();
                        if (legacyNroSucursal != null && legacyNroSucursal.trim().isEmpty()) {
                            legacyNroSucursal = null;
                        }
                    }
                }

                java.math.BigDecimal legacyCostoClick = null;
                Object costoObj = legacy.get("costoClick");
                if (costoObj == null) {
                    costoObj = legacy.get("costo_click");
                }
                if (costoObj != null) {
                    try {
                        if (costoObj instanceof Number) {
                            legacyCostoClick = java.math.BigDecimal.valueOf(((Number) costoObj).doubleValue());
                        } else if (costoObj instanceof String && !((String) costoObj).isEmpty()) {
                            legacyCostoClick = new java.math.BigDecimal((String) costoObj);
                        }
                    } catch (Exception ex) {
                        logger.warn("No se pudo parsear costoClick del contenido legacy: {}", costoObj);
                    }
                }

                ContenidoGeneradoDto resultado = contenidoRepository.guardarContenidoGenerado(
                        request.getNroRestaurante(),
                        legacyNroSucursal,
                        request.getNroIdioma(),
                        contenidoGenerado,
                        legacyCostoClick
                ).orElseThrow(() -> new RuntimeException("Error al guardar el contenido generado en la base de datos"));

                ultimoResultado = resultado;

                // Si guardó ok, marcar para publicar en legacy
                if (legacyNro != null) {
                    contenidosMarcados.add(legacyNro);
                }

            } catch (Exception e) {
                logger.error("Error procesando contenido legacy: {}", e.getMessage(), e);
                // Continuar con siguientes contenidos
            }
        }

        // Marcar como publicados en legacy aquellos que se generaron correctamente
        if (!contenidosMarcados.isEmpty()) {
            try {
                int updated = client.marcarPublicado(request.getNroRestaurante(), contenidosMarcados);
                logger.info("Contenidos marcados como publicados en legacy: {} (actualizados={})", contenidosMarcados.size(), updated);
            } catch (Exception e) {
                logger.error("Error al marcar publicados en legacy: {}", e.getMessage(), e);
            }
        }

        if (ultimoResultado == null) {
            throw new RuntimeException("No se generó ni guardó ningún contenido correctamente.");
        }

        return ultimoResultado;
    }
}

