package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.client.RestauranteClient;
import ar.edu.ubp.das.backend.client.RestauranteClientFactory;
import ar.edu.ubp.das.backend.dto.ContenidoGeneradoDto;
import ar.edu.ubp.das.backend.dto.GenerarContenidoRequestDto;
import ar.edu.ubp.das.backend.repository.ContenidoRepository;
import ar.edu.ubp.das.backend.repository.RestauranteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestión de contenidos generados con IA.
 * Orquesta la recopilación de datos, generación con OpenAI y almacenamiento.
 */
@Service
public class ContenidoService {

    private static final Logger logger = LoggerFactory.getLogger(ContenidoService.class);

    private final ContenidoRepository contenidoRepository;
    private final RestauranteRepository restauranteRepository;
    private final OpenAIService openAIService;
    private final RestauranteClientFactory restauranteClientFactory;
    
    @Value("${openai.prompt.id}")
    private String defaultPromptId;
    
    public ContenidoService(ContenidoRepository contenidoRepository,
                           RestauranteRepository restauranteRepository,
                           OpenAIService openAIService,
                           RestauranteClientFactory restauranteClientFactory) {
        this.contenidoRepository = contenidoRepository;
        this.restauranteRepository = restauranteRepository;
        this.openAIService = openAIService;
        this.restauranteClientFactory = restauranteClientFactory;
    }

    /**
     * Genera contenido publicitario con IA para un restaurante/sucursal.
     * 
     * FLUJO:
     * 1. Obtener el último contenido PUBLICADO (publicado = 1) de la tabla 'contenidos' 
     *    del sistema das_restaurantes_soap (vía SOAP)
     * 2. Obtener atributos y configuracion_restaurantes de das_ristorino
     * 3. Enviar todo al motor IA para generar contenido tipo promoción
     * 4. Guardar SOLO en la tabla 'contenidos_restaurantes' de das_ristorino (NO sincronizar con SOAP)
     * 
     * NOTA: Solo se consideran contenidos con publicado = 1. No es necesario notificar publicación.
     *
     * @param request Datos de la solicitud (restaurante, sucursal, idioma)
     * @return DTO con el contenido generado y guardado
     * @throws RuntimeException si no se encuentra el restaurante o hay error en la generación
     */
    public ContenidoGeneradoDto generarContenido(GenerarContenidoRequestDto request) {
        String promptId = (request.getPromptId() != null && !request.getPromptId().isEmpty())
                ? request.getPromptId()
                : defaultPromptId;

        // Validar que el restaurante existe en das-ristorino
        if (!restauranteRepository.existeRestaurante(request.getNroRestaurante())) {
            throw new RuntimeException("Restaurante no encontrado: " + request.getNroRestaurante());
        }

        // Obtener información del idioma
        String nomIdioma = contenidoRepository.obtenerNomIdioma(request.getNroIdioma());
        String codIdioma = contenidoRepository.obtenerCodIdioma(request.getNroIdioma());

        // Mapear nroSucursal interno a cod_sucursal_restaurante (ID en SOAP) si se proporcionó sucursal
        String codSucursalRestauranteParaSOAP = null;
        if (request.getNroSucursal() != null && !request.getNroSucursal().trim().isEmpty()) {
            // Validar que la sucursal existe y pertenece al restaurante (en das-ristorino)
            if (!restauranteRepository.existeSucursal(request.getNroRestaurante(), request.getNroSucursal())) {
                throw new RuntimeException("Sucursal no encontrada: " + request.getNroSucursal() + 
                                         " para el restaurante: " + request.getNroRestaurante());
            }
            
            // Obtener el cod_sucursal_restaurante (ID de la sucursal en das-restaurante-soap)
            codSucursalRestauranteParaSOAP = restauranteRepository.obtenerCodSucursalRestaurante(
                    request.getNroRestaurante(), 
                    request.getNroSucursal()
            );
            
            // Validar que la sucursal está sincronizada con el sistema del restaurante
            if (codSucursalRestauranteParaSOAP == null || codSucursalRestauranteParaSOAP.trim().isEmpty()) {
                throw new RuntimeException("La sucursal " + request.getNroSucursal() + 
                                         " no está sincronizada con el sistema del restaurante. " +
                                         "cod_sucursal_restaurante no está configurado.");
            }
        }

        RestauranteClient client = restauranteClientFactory.getClient(request.getNroRestaurante());
        java.util.Map<String, Object> contenidoSoap = client.obtenerContenidos(
                request.getNroRestaurante(), 
                codSucursalRestauranteParaSOAP
        );

        if (contenidoSoap == null || contenidoSoap.isEmpty()) {
            throw new RuntimeException("No se encontraron contenidos en el sistema SOAP para el restaurante: " + request.getNroRestaurante());
        }

        java.util.Map<String, String> atributosYConfiguracion = contenidoRepository.obtenerTodosLosAtributosYConfiguracion(
                request.getNroRestaurante()
        );

        ar.edu.ubp.das.backend.dto.RestauranteContextoDto contextoRestaurante = contenidoRepository
                .obtenerContextoRestaurante(request.getNroRestaurante(), request.getNroSucursal())
                .orElseThrow(() -> new RuntimeException("No se encontró información del restaurante: " + request.getNroRestaurante()));

        String contenidoFuente = contenidoSoap.get("contenidoAPublicar") != null 
            ? (String) contenidoSoap.get("contenidoAPublicar") 
            : "";

        // Extraer nroContenido del SOAP para guardarlo como cod_contenido_restaurante (necesario para notificar clicks)
        String codContenidoRestaurante = null;
        Object nroContenidoObj = contenidoSoap.get("nroContenido");
        if (nroContenidoObj == null) {
            nroContenidoObj = contenidoSoap.get("nro_contenido");
        }
        if (nroContenidoObj != null) {
            if (nroContenidoObj instanceof String) {
                codContenidoRestaurante = ((String) nroContenidoObj).trim();
                if (codContenidoRestaurante.isEmpty()) {
                    codContenidoRestaurante = null;
                }
            } else {
                codContenidoRestaurante = nroContenidoObj.toString().trim();
                if (codContenidoRestaurante.isEmpty()) {
                    codContenidoRestaurante = null;
                }
            }
        }

        if (codContenidoRestaurante == null) {
            logger.warn("No se encontró nroContenido en el contenido SOAP. Se generará un código AI_ automático.");
        }

        // Extraer proposito_corto del SOAP
        String propositoCorto = null;
        Object propositoObj = contenidoSoap.get("propositoCorto");
        if (propositoObj == null) {
            propositoObj = contenidoSoap.get("proposito_corto");
        }
        if (propositoObj != null) {
            if (propositoObj instanceof String) {
                propositoCorto = ((String) propositoObj).trim();
                if (propositoCorto.isEmpty()) {
                    propositoCorto = null;
                }
            } else {
                propositoCorto = propositoObj.toString().trim();
                if (propositoCorto.isEmpty()) {
                    propositoCorto = null;
                }
            }
        }

        String nroSucursalFinal = request.getNroSucursal();

        String prompt = construirPromptCompleto(
                contenidoFuente,
                contextoRestaurante,
                atributosYConfiguracion,
                nomIdioma,
                codIdioma,
                request.getContextoAdicional(),
                promptId
        );

        String contenidoGenerado = openAIService.generarContenidoPublicitario(prompt, promptId);

        // Guardar en das_ristorino. cod_contenido_restaurante permite notificar clicks al sistema SOAP
        // El costo de click se obtiene automáticamente en el stored procedure desde la tabla costos
        ContenidoGeneradoDto resultado = contenidoRepository.guardarContenidoGenerado(
                request.getNroRestaurante(),
                nroSucursalFinal,
                request.getNroIdioma(),
                contenidoGenerado,
                codContenidoRestaurante,
                propositoCorto
        ).orElseThrow(() -> new RuntimeException("Error al guardar el contenido generado en la base de datos"));

        logger.info("Contenido generado y guardado exitosamente. nroContenido: {}", resultado.getNroContenido());

        return resultado;
    }

    /**
     * Construye el prompt completo combinando:
     * - Contenido fuente del SOAP
     * - Contexto del restaurante (datos básicos, preferencias, horarios)
     * - Atributos y configuración del restaurante
     * 
     * @param contenidoFuente Contenido base del sistema SOAP
     * @param contexto Contexto del restaurante (datos básicos, preferencias, horarios)
     * @param atributosYConfiguracion Todos los atributos y configuración del restaurante
     * @param nomIdioma Nombre del idioma (ej: "Español de Argentina")
     * @param codIdioma Código del idioma (ej: "es-AR")
     * @param contextoAdicional Contexto adicional opcional del request
     * @param promptId ID del prompt guardado en OpenAI (opcional)
     * @return Prompt completo para enviar a OpenAI
     */
    private String construirPromptCompleto(
            String contenidoFuente,
            ar.edu.ubp.das.backend.dto.RestauranteContextoDto contexto,
            java.util.Map<String, String> atributosYConfiguracion,
            String nomIdioma,
            String codIdioma,
            String contextoAdicional,
            String promptId) {

        // Si hay promptId, construir JSON para prompt guardado
        if (promptId != null && !promptId.isEmpty()) {
            return construirJSONParaPromptGuardado(
                    contenidoFuente,
                    contexto,
                    atributosYConfiguracion,
                    nomIdioma,
                    codIdioma,
                    contextoAdicional
            );
        }

        // Prompt por defecto (texto estructurado)
        StringBuilder prompt = new StringBuilder();
        prompt.append("Genera un texto promocional breve en ").append(nomIdioma).append(" basado en el siguiente contenido:\n\n");
        prompt.append("=== CONTENIDO BASE ===\n");
        prompt.append(contenidoFuente).append("\n\n");

        prompt.append("=== INFORMACIÓN DEL RESTAURANTE ===\n");
        prompt.append("📍 Restaurante: ").append(contexto.getRazonSocial()).append("\n");
        if (contexto.getNombreSucursal() != null && !contexto.getNombreSucursal().isEmpty()) {
            prompt.append("📍 Sucursal: ").append(contexto.getNombreSucursal()).append("\n");
        }
        if (contexto.getDireccion() != null && !contexto.getDireccion().isEmpty()) {
            prompt.append("📍 Ubicación: ").append(contexto.getDireccion());
            if (contexto.getLocalidad() != null && !contexto.getLocalidad().isEmpty()) {
                prompt.append(", ").append(contexto.getLocalidad());
            }
            prompt.append("\n");
        }

        if (!contexto.getTiposComida().isEmpty()) {
            prompt.append("🍽️ Tipo de comida: ").append(String.join(", ", contexto.getTiposComida())).append("\n");
        }

        if (!contexto.getAmbientes().isEmpty()) {
            prompt.append("🎭 Ambiente: ").append(String.join(", ", contexto.getAmbientes())).append("\n");
        }

        if (!contexto.getRangosPrecios().isEmpty()) {
            prompt.append("💰 Rango de precio: ").append(String.join(", ", contexto.getRangosPrecios())).append("\n");
        }

        // Atributos y configuración
        if (!atributosYConfiguracion.isEmpty()) {
            prompt.append("\n=== ATRIBUTOS Y CONFIGURACIÓN ===\n");
            for (java.util.Map.Entry<String, String> entry : atributosYConfiguracion.entrySet()) {
                prompt.append("• ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        // Identidad gastronómica (si está en el contexto)
        if (contexto.getTipoCocina() != null && !contexto.getTipoCocina().isEmpty()) {
            prompt.append("🍳 Tipo de cocina: ").append(contexto.getTipoCocina()).append("\n");
        }
        if (contexto.getEstiloAtencion() != null && !contexto.getEstiloAtencion().isEmpty()) {
            prompt.append("👔 Estilo de atención: ").append(contexto.getEstiloAtencion()).append("\n");
        }
        if (contexto.getPlatosEmblematicos() != null && !contexto.getPlatosEmblematicos().isEmpty()) {
            prompt.append("⭐ Platos emblemáticos: ").append(contexto.getPlatosEmblematicos()).append("\n");
        }

        if (contexto.getObservacionesAdicionales() != null && !contexto.getObservacionesAdicionales().isEmpty()) {
            prompt.append("ℹ️ Detalles: ").append(contexto.getObservacionesAdicionales()).append("\n");
        }

        if (contextoAdicional != null && !contextoAdicional.isEmpty()) {
            prompt.append("💡 Información adicional: ").append(contextoAdicional).append("\n");
        }

        prompt.append("\n=== REQUISITOS ===\n");
        prompt.append("- Máximo 300 palabras\n");
        prompt.append("- Destaca las características únicas del restaurante\n");
        prompt.append("- Invita a los clientes a visitarlo\n");
        prompt.append("- Menciona la ubicación de forma natural\n");
        prompt.append("- Usa un tono ").append(determinarTono(contexto.getAmbientes())).append("\n");
        prompt.append("- NO uses emojis en el texto generado\n");
        prompt.append("- Escribe en ").append(nomIdioma).append("\n");

        return prompt.toString();
    }

    /**
     * Construye un JSON para usar con prompt guardado en OpenAI Platform.
     */
    private String construirJSONParaPromptGuardado(
            String contenidoFuente,
            ar.edu.ubp.das.backend.dto.RestauranteContextoDto contexto,
            java.util.Map<String, String> atributosYConfiguracion,
            String nomIdioma,
            String codIdioma,
            String contextoAdicional) {

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"contenidoFuente\": \"").append(escaparJson(contenidoFuente)).append("\",\n");
        json.append("  \"restaurante\": \"").append(escaparJson(contexto.getRazonSocial() != null ? contexto.getRazonSocial() : "")).append("\"");
        
        if (contexto.getNombreSucursal() != null && !contexto.getNombreSucursal().isEmpty()) {
            json.append(",\n  \"sucursal\": \"").append(escaparJson(contexto.getNombreSucursal())).append("\"");
        }
        
        if (contexto.getDireccion() != null && !contexto.getDireccion().isEmpty()) {
            json.append(",\n  \"direccion\": \"").append(escaparJson(contexto.getDireccion())).append("\"");
        }
        
        if (contexto.getLocalidad() != null && !contexto.getLocalidad().isEmpty()) {
            json.append(",\n  \"localidad\": \"").append(escaparJson(contexto.getLocalidad())).append("\"");
        }
        
        if (!contexto.getTiposComida().isEmpty()) {
            json.append(",\n  \"tipo_comida\": \"").append(escaparJson(String.join(", ", contexto.getTiposComida()))).append("\"");
        }
        
        if (!contexto.getAmbientes().isEmpty()) {
            json.append(",\n  \"ambiente\": \"").append(escaparJson(String.join(", ", contexto.getAmbientes()))).append("\"");
        }
        
        if (!contexto.getRangosPrecios().isEmpty()) {
            json.append(",\n  \"rango_precio\": \"").append(escaparJson(String.join(", ", contexto.getRangosPrecios()))).append("\"");
        }

        // Agregar todos los atributos y configuración
        if (!atributosYConfiguracion.isEmpty()) {
            for (java.util.Map.Entry<String, String> entry : atributosYConfiguracion.entrySet()) {
                String key = entry.getKey().toLowerCase().replace(" ", "_");
                json.append(",\n  \"").append(key).append("\": \"").append(escaparJson(entry.getValue())).append("\"");
            }
        }

        if (contexto.getTipoCocina() != null && !contexto.getTipoCocina().trim().isEmpty()) {
            json.append(",\n  \"tipo_cocina\": \"").append(escaparJson(contexto.getTipoCocina())).append("\"");
        }
        
        if (contexto.getEstiloAtencion() != null && !contexto.getEstiloAtencion().trim().isEmpty()) {
            json.append(",\n  \"estilo_atencion\": \"").append(escaparJson(contexto.getEstiloAtencion())).append("\"");
        }
        
        if (contexto.getPlatosEmblematicos() != null && !contexto.getPlatosEmblematicos().trim().isEmpty()) {
            json.append(",\n  \"platos_emblematicos\": \"").append(escaparJson(contexto.getPlatosEmblematicos())).append("\"");
        }
        
        if (contexto.getObservacionesAdicionales() != null && !contexto.getObservacionesAdicionales().isEmpty()) {
            json.append(",\n  \"observaciones\": \"").append(escaparJson(contexto.getObservacionesAdicionales())).append("\"");
        }
        
        if (contextoAdicional != null && !contextoAdicional.isEmpty()) {
            json.append(",\n  \"contexto_adicional\": \"").append(escaparJson(contextoAdicional)).append("\"");
        }
        
        if (codIdioma != null && !codIdioma.isEmpty()) {
            json.append(",\n  \"cod_idioma\": \"").append(escaparJson(codIdioma)).append("\"");
        }
        
        if (nomIdioma != null && !nomIdioma.isEmpty()) {
            json.append(",\n  \"nom_idioma\": \"").append(escaparJson(nomIdioma)).append("\"");
        }
        
        json.append("\n}");
        
        String instruccionIdioma = "";
        if (nomIdioma != null && !nomIdioma.isEmpty()) {
            instruccionIdioma = " Escribe el texto en " + nomIdioma + ".";
        } else if (codIdioma != null) {
            instruccionIdioma = " Escribe el texto en el idioma correspondiente al código " + codIdioma + ".";
        }
        
        return json.toString() + "\n\nGenera ÚNICAMENTE el texto publicitario listo para publicar." + instruccionIdioma + " NO incluyas explicaciones, títulos ni comentarios adicionales.";
    }

    /**
     * Determina el tono del texto según el ambiente del restaurante.
     */
    private String determinarTono(java.util.List<String> ambientes) {
        if (ambientes == null || ambientes.isEmpty()) {
            return "cálido y acogedor";
        }
        
        String primerAmbiente = ambientes.get(0).toLowerCase();
        if (primerAmbiente.contains("gourmet") || primerAmbiente.contains("premium")) {
            return "elegante y sofisticado";
        } else if (primerAmbiente.contains("romántico")) {
            return "romántico y cautivador";
        } else if (primerAmbiente.contains("familiar")) {
            return "cálido y familiar";
        } else if (primerAmbiente.contains("casual")) {
            return "casual y amigable";
        }
        
        return "cálido y acogedor";
    }

    /**
     * Escapa caracteres especiales para JSON.
     */
    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
