package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.BusquedaContextoDto;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para interactuar con la API de OpenAI.
 * Genera contenido publicitario usando ChatGPT.
 */
@Service
public class OpenAIService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model:gpt-5-nano}")
    private String model;

    @Value("${openai.timeout:60}")
    private int timeoutSeconds;

    /**
     * Genera texto publicitario usando ChatGPT.
     *
     * @param prompt El prompt con el contexto del restaurante.
     * @param promptId ID del prompt guardado en OpenAI (opcional)
     * @return El texto generado por la IA.
     * @throws RuntimeException si hay error en la llamada a OpenAI.
     */
    public String generarContenidoPublicitario(String prompt, String promptId) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("not-configured") || apiKey.equals("${OPENAI_API_KEY:}")) {
            throw new RuntimeException("La API key de OpenAI no está configurada. Configure la variable de entorno OPENAI_API_KEY.");
        }

        try {
            logger.info("╔════════════════════════════════════════════════════════════════");
            logger.info("║ GENERACIÓN DE CONTENIDO CON OPENAI");
            logger.info("╠════════════════════════════════════════════════════════════════");
            logger.info("║ Modelo: {}", model);
            if (promptId != null && !promptId.isEmpty()) {
                logger.info("║ Prompt ID: {}", promptId);
            } else {
                logger.info("║ Prompt ID: (ninguno - usando prompt por defecto)");
            }
            logger.info("╠════════════════════════════════════════════════════════════════");
            logger.info("║ PROMPT ENVIADO:");
            logger.info("╠════════════════════════════════════════════════════════════════");
            
            // Crear cliente de OpenAI
            OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(timeoutSeconds));

            // Construir la lista de mensajes
            List<ChatMessage> messages = new ArrayList<>();
            
            // Si hay un promptId, usamos el prompt guardado en OpenAI
            if (promptId != null && !promptId.isEmpty()) {
                // El contexto del restaurante se pasa como JSON al prompt guardado
                ChatMessage userMessage = new ChatMessage(
                    ChatMessageRole.USER.value(),
                    prompt
                );
                messages.add(userMessage);
                
                // Loggear el prompt JSON
                logger.info("║ {}", prompt.replace("\n", "\n║ "));
            } else {
                // Si no hay promptId, usamos el sistema por defecto
                ChatMessage systemMessage = new ChatMessage(
                    ChatMessageRole.SYSTEM.value(),
                    "Eres un experto en marketing gastronómico. Tu tarea es crear textos publicitarios " +
                    "atractivos, convincentes y profesionales para restaurantes. Usa un lenguaje persuasivo " +
                    "pero natural, destacando las características únicas de cada establecimiento. " +
                    "Sigue las instrucciones del usuario sobre el idioma en el que debe escribirse el texto."
                );

                ChatMessage userMessage = new ChatMessage(
                    ChatMessageRole.USER.value(),
                    prompt
                );

                messages.add(systemMessage);
                messages.add(userMessage);
                
                // Loggear el prompt completo
                logger.info("║ SYSTEM: Eres un experto en marketing gastronómico...");
                logger.info("║ ");
                logger.info("║ USER:");
                String[] lines = prompt.split("\n");
                for (String line : lines) {
                    logger.info("║ {}", line);
                }
            }
            
            logger.info("╚════════════════════════════════════════════════════════════════");

            // Crear request (sin temperature ni maxTokens para compatibilidad con GPT-4)
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(messages)
                    .build();

            // Ejecutar request
            String contenidoGenerado = service.createChatCompletion(completionRequest)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            // Loggear la respuesta
            logger.info("");
            logger.info("╔════════════════════════════════════════════════════════════════");
            logger.info("║ RESPUESTA DE OPENAI");
            logger.info("╠════════════════════════════════════════════════════════════════");
            logger.info("║ Longitud: {} caracteres", contenidoGenerado.length());
            logger.info("╠════════════════════════════════════════════════════════════════");
            logger.info("║ CONTENIDO GENERADO:");
            logger.info("╠════════════════════════════════════════════════════════════════");
            
            // Loggear el contenido generado línea por línea
            String[] responseLines = contenidoGenerado.split("\n");
            for (String line : responseLines) {
                logger.info("║ {}", line);
            }
            
            logger.info("╚════════════════════════════════════════════════════════════════");
            logger.info("✅ Contenido generado exitosamente");
            
            // Cerrar servicio
            service.shutdownExecutor();
            
            return contenidoGenerado.trim();

        } catch (Exception e) {
            logger.error("Error al generar contenido con OpenAI: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar contenido con OpenAI: " + e.getMessage(), e);
        }
    }

    /**
     * Construye el prompt para ChatGPT basado en el contexto del restaurante.
     */
    public String construirPrompt(String razonSocial, String sucursal, String direccion, 
                                  String localidad, List<String> tiposComida, 
                                  List<String> ambientes, List<String> rangosPrecios,
                                  String observaciones, String contextoAdicional, String promptId,
                                  String codIdioma, String nomIdioma,
                                  String tipoCocina, String estiloAtencion, String platosEmblematicos) {
        
        if (promptId != null && !promptId.isEmpty()) {
            return construirVariablesParaPromptGuardado(razonSocial, sucursal, direccion, 
                                                       localidad, tiposComida, ambientes, 
                                                       rangosPrecios, observaciones, contextoAdicional,
                                                       codIdioma, nomIdioma,
                                                       tipoCocina, estiloAtencion, platosEmblematicos);
        }
        
        // Prompt por defecto
        StringBuilder prompt = new StringBuilder();
        prompt.append("Genera un texto publicitario atractivo para el siguiente restaurante:\n\n");
        
        prompt.append("📍 Restaurante: ").append(razonSocial).append("\n");
        if (sucursal != null && !sucursal.isEmpty()) {
            prompt.append("📍 Sucursal: ").append(sucursal).append("\n");
        }
        prompt.append("📍 Ubicación: ").append(direccion).append(", ").append(localidad).append("\n");
        
        if (!tiposComida.isEmpty()) {
            prompt.append("🍽️ Tipo de comida: ").append(String.join(", ", tiposComida)).append("\n");
        }
        
        if (!ambientes.isEmpty()) {
            prompt.append("🎭 Ambiente: ").append(String.join(", ", ambientes)).append("\n");
        }
        
        if (!rangosPrecios.isEmpty()) {
            prompt.append("💰 Rango de precio: ").append(String.join(", ", rangosPrecios)).append("\n");
        }
        
        // Identidad gastronómica y comunicacional
        if (tipoCocina != null && !tipoCocina.trim().isEmpty()) {
            prompt.append("🍳 Tipo de cocina: ").append(tipoCocina).append("\n");
        }
        
        if (estiloAtencion != null && !estiloAtencion.trim().isEmpty()) {
            prompt.append("👔 Estilo de atención: ").append(estiloAtencion).append("\n");
        }
        
        if (platosEmblematicos != null && !platosEmblematicos.trim().isEmpty()) {
            prompt.append("⭐ Platos emblemáticos: ").append(platosEmblematicos).append("\n");
        }
        
        if (observaciones != null && !observaciones.isEmpty()) {
            prompt.append("ℹ️ Detalles: ").append(observaciones).append("\n");
        }
        
        if (contextoAdicional != null && !contextoAdicional.isEmpty()) {
            prompt.append("💡 Información adicional: ").append(contextoAdicional).append("\n");
        }
        
        prompt.append("\n");
        prompt.append("Requisitos:\n");
        prompt.append("- Máximo 300 palabras\n");
        prompt.append("- Destaca las características únicas del restaurante\n");
        prompt.append("- Invita a los clientes a visitarlo\n");
        prompt.append("- Menciona la ubicación de forma natural\n");
        prompt.append("- Usa un tono ").append(determinarTono(ambientes)).append("\n");
        prompt.append("- NO uses emojis en el texto generado\n");
        prompt.append("- Escribe en ").append(nomIdioma != null ? nomIdioma : "español de Argentina").append("\n");

        return prompt.toString();
    }

    /**
     * Determina el tono del texto según el ambiente del restaurante.
     */
    private String determinarTono(List<String> ambientes) {
        if (ambientes.isEmpty()) {
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
     * Construye un JSON limpio con los datos del restaurante para el prompt guardado en OpenAI.
     * Omite campos null o vacíos.
     */
    private String construirVariablesParaPromptGuardado(String razonSocial, String sucursal,
                                                       String direccion, String localidad, 
                                                       List<String> tiposComida, List<String> ambientes,
                                                       List<String> rangosPrecios, String observaciones, 
                                                       String contextoAdicional, String codIdioma, String nomIdioma,
                                                       String tipoCocina, String estiloAtencion, String platosEmblematicos) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"restaurante\": \"").append(escaparJson(razonSocial != null ? razonSocial : "")).append("\"");
        
        if (sucursal != null && !sucursal.isEmpty()) {
            json.append(",\n  \"sucursal\": \"").append(escaparJson(sucursal)).append("\"");
        }
        
        if (direccion != null && !direccion.isEmpty()) {
            json.append(",\n  \"direccion\": \"").append(escaparJson(direccion)).append("\"");
        }
        
        if (localidad != null && !localidad.isEmpty()) {
            json.append(",\n  \"localidad\": \"").append(escaparJson(localidad)).append("\"");
        }
        
        if (tiposComida != null && !tiposComida.isEmpty()) {
            json.append(",\n  \"tipo_comida\": \"").append(escaparJson(String.join(", ", tiposComida))).append("\"");
        }
        
        if (ambientes != null && !ambientes.isEmpty()) {
            json.append(",\n  \"ambiente\": \"").append(escaparJson(String.join(", ", ambientes))).append("\"");
        }
        
        if (rangosPrecios != null && !rangosPrecios.isEmpty()) {
            json.append(",\n  \"rango_precio\": \"").append(escaparJson(String.join(", ", rangosPrecios))).append("\"");
        }
        
        // Identidad gastronómica y comunicacional
        if (tipoCocina != null && !tipoCocina.trim().isEmpty()) {
            json.append(",\n  \"tipo_cocina\": \"").append(escaparJson(tipoCocina)).append("\"");
        }
        
        if (estiloAtencion != null && !estiloAtencion.trim().isEmpty()) {
            json.append(",\n  \"estilo_atencion\": \"").append(escaparJson(estiloAtencion)).append("\"");
        }
        
        if (platosEmblematicos != null && !platosEmblematicos.trim().isEmpty()) {
            json.append(",\n  \"platos_emblematicos\": \"").append(escaparJson(platosEmblematicos)).append("\"");
        }
        
        if (observaciones != null && !observaciones.isEmpty()) {
            json.append(",\n  \"observaciones\": \"").append(escaparJson(observaciones)).append("\"");
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
        
        // Instrucción explícita para obtener solo el texto publicitario en el idioma especificado
        String instruccionIdioma = "";
        if (nomIdioma != null && !nomIdioma.isEmpty()) {
            instruccionIdioma = " Escribe el texto en " + nomIdioma + ".";
        } else if (codIdioma != null) {
            instruccionIdioma = " Escribe el texto en el idioma correspondiente al código " + codIdioma + ".";
        }
        
        return json.toString() + "\n\nGenera ÚNICAMENTE el texto publicitario listo para publicar." + instruccionIdioma + " NO incluyas explicaciones, títulos ni comentarios adicionales.";
    }
    
    /**
     * Analiza una consulta en lenguaje natural para búsqueda de restaurantes.
     * Usa un prompt guardado en OpenAI para extraer entidades.
     *
     * @param consultaUsuario Consulta del usuario en lenguaje natural
     * @param contexto Contexto con catálogos disponibles
     * @param promptId ID del prompt guardado en OpenAI
     * @return Respuesta JSON parseada con la intención extraída
     */
    public String analizarConsultaNLP(String consultaUsuario, BusquedaContextoDto contexto, String promptId) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("not-configured") || apiKey.equals("${OPENAI_API_KEY:}")) {
            throw new RuntimeException("La API key de OpenAI no está configurada. Configure la variable de entorno OPENAI_API_KEY.");
        }

        try {
            logger.info("╔════════════════════════════════════════════════════════════════");
            logger.info("║ ANÁLISIS NLP DE CONSULTA DE BÚSQUEDA");
            logger.info("╠════════════════════════════════════════════════════════════════");
            logger.info("║ Modelo: {}", model);
            logger.info("║ Prompt ID: {}", promptId);
            logger.info("╠════════════════════════════════════════════════════════════════");
            logger.info("║ CONSULTA DEL USUARIO:");
            logger.info("║ {}", consultaUsuario);
            logger.info("╠════════════════════════════════════════════════════════════════");
            logger.info("║ CONTEXTO ENVIADO A OPENAI:");
            logger.info("╠════════════════════════════════════════════════════════════════");
            
            // Construir JSON de contexto
            String jsonContexto = construirJSONParaBusquedaNLP(consultaUsuario, contexto);
            logger.info("║ {}", jsonContexto.replace("\n", "\n║ "));
            logger.info("╚════════════════════════════════════════════════════════════════");

            // Crear cliente de OpenAI
            OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(timeoutSeconds));

            // Construir mensaje para OpenAI
            List<ChatMessage> messages = new ArrayList<>();
            
            // Siempre usar system message para asegurar que devuelva JSON
            // El prompt guardado en OpenAI Platform se referencia pero se reforzará con instrucciones explícitas
            String systemPrompt = "Eres un asistente especializado en procesamiento de lenguaje natural para búsquedas gastronómicas. " +
                    "Tu tarea es analizar consultas de usuarios en lenguaje natural y extraer información estructurada " +
                    "para buscar restaurantes. " +
                    "\n\nREGLAS CRÍTICAS PARA TIPO DE COMIDA:" +
                    "\n- SIEMPRE intenta asociar la consulta a UNO O MÁS tipos de comida del catálogo proporcionado, incluso si no es una coincidencia exacta." +
                    "\n- Usa sinónimos y variaciones: 'parrillada' → 'Parrilla', 'sushi' → 'Sushi', 'pizza' → 'Pizzería', 'italiana' → 'Italiana', 'mexicana' → 'Mexicana', 'asiática' → 'Asiática', 'vegana' → 'Vegano'." +
                    "\n- Si la consulta menciona un tipo de comida aunque sea indirectamente (ej: 'quiero comer una parrillada'), DEBES incluir el tipo de comida correspondiente del catálogo." +
                    "\n- Si no hay coincidencia exacta, elige el tipo de comida MÁS CERCANO del catálogo que tenga sentido." +
                    "\n- Solo usa null para tipoComida si la consulta NO menciona NADA relacionado con tipos de comida." +
                    "\n\nIMPORTANTE: Tu respuesta DEBE ser ÚNICAMENTE un objeto JSON válido, sin explicaciones adicionales, " +
                    "sin markdown, sin comentarios. El JSON debe comenzar con '{' y terminar con '}'. " +
                    "NO incluyas texto antes ni después del JSON. NO hagas preguntas al usuario. " +
                    "Solo devuelve el JSON con la estructura especificada.";
            
            ChatMessage systemMessage = new ChatMessage(
                ChatMessageRole.SYSTEM.value(),
                systemPrompt
            );
            
            // Mensaje del usuario con JSON de contexto + instrucción reforzada con estructura esperada
            String mensajeUsuario = jsonContexto + 
                    "\n\nINSTRUCCIONES ESPECÍFICAS:" +
                    "\n1. TIPO DE COMIDA: SIEMPRE intenta asociar la consulta a uno o más tipos de comida del catálogo 'tiposComida' proporcionado." +
                    "\n   - Usa sinónimos: 'parrillada', 'asado', 'parrilla' → 'Parrilla'" +
                    "\n   - 'sushi', 'japonesa', 'comida japonesa', 'nikkei', 'peruano-japonés' → 'Sushi', 'Fusión japonesa-peruana' o 'Asiática'" +
                    "\n   - 'pizza', 'pizzería' → 'Pizzería'" +
                    "\n   - 'italiana', 'pasta', 'risotto' → 'Italiana' o 'Italiana tradicional'" +
                    "\n   - 'mexicana', 'tacos', 'burritos' → 'Mexicana'" +
                    "\n   - 'vegana', 'vegetariana' → 'Vegano'" +
                    "\n   - IMPORTANTE: Si el catálogo tiene 'Fusión japonesa-peruana', 'Sushi' o 'Asiática', y la consulta menciona 'japonesa', " +
                    "\n     DEBES incluir al menos uno de estos valores (prioriza 'Fusión japonesa-peruana' si existe en el catálogo)." +
                    "\n   - Si la consulta menciona un tipo de comida (aunque sea indirectamente), DEBES incluir el tipo correspondiente del catálogo." +
                    "\n   - Solo usa null si la consulta NO menciona NADA relacionado con tipos de comida." +
                    "\n2. PALABRAS CLAVE: Incluye palabras relevantes de la consulta que no se mapearon a otros campos." +
                    "\n3. PREFERENCIAS DEL USUARIO: Si el contexto incluye 'preferenciasUsuario', considera estas preferencias al interpretar la consulta. " +
                    "\n   Por ejemplo, si el usuario prefiere 'Italiana' y su consulta es genérica como 'quiero comer algo rico', " +
                    "\n   puedes inferir que probablemente busca restaurantes italianos. Sin embargo, SIEMPRE prioriza lo que el usuario menciona explícitamente." +
                    "\n4. Otros campos: Usa null si no se mencionan explícitamente o no son inferibles." +
                    "\n\nESTRUCTURA JSON REQUERIDA:\n" +
                    "{\n" +
                    "  \"tipoComida\": [\"Parrilla\"] o null (SIEMPRE intenta asociar si hay mención de comida),\n" +
                    "  \"barrio\": \"Centro\" o null,\n" +
                    "  \"localidad\": \"Córdoba\" o null,\n" +
                    "  \"ambiente\": \"Romántico\" o null,\n" +
                    "  \"rangoPrecio\": \"Económico\" o null,\n" +
                    "  \"momentoDia\": \"cena\" o null,\n" +
                    "  \"intencion\": \"comer\" o null,\n" +
                    "  \"palabrasClave\": [\"parrillada\"] o null\n" +
                    "}\n\n" +
                    "Los campos deben estar en el NIVEL SUPERIOR del JSON (no anidados en \"criterios\" u otro objeto). " +
                    "NO incluyas explicaciones, comentarios, preguntas ni texto adicional. " +
                    "NO uses markdown (sin ```json o ```). Solo devuelve el JSON puro comenzando con '{' y terminando con '}'.";
            
            ChatMessage userMessage = new ChatMessage(
                ChatMessageRole.USER.value(),
                mensajeUsuario
            );
            
            messages.add(systemMessage);
            messages.add(userMessage);
            

            // Crear request
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(messages)
                    .build();

            // Ejecutar request
            String respuestaJson = service.createChatCompletion(completionRequest)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            logger.info("╠════════════════════════════════════════════════════════════════");
            logger.info("║ RESPUESTA DE OPENAI:");
            logger.info("╠════════════════════════════════════════════════════════════════");
            logger.info("║ Longitud: {} caracteres", respuestaJson.length());
            logger.info("║ Contenido:");
            // Loggear la respuesta completa, dividida en líneas si es muy larga
            String respuestaLimpia = respuestaJson.trim();
            if (respuestaLimpia.length() > 500) {
                logger.info("║ (primeros 500 caracteres): {}", respuestaLimpia.substring(0, Math.min(500, respuestaLimpia.length())));
                logger.info("║ ... ({} caracteres más)", respuestaLimpia.length() - 500);
            } else {
                logger.info("║ {}", respuestaLimpia.replace("\n", "\n║ "));
            }
            logger.info("╚════════════════════════════════════════════════════════════════");
            
            // Cerrar servicio
            service.shutdownExecutor();
            
            return respuestaLimpia;

        } catch (Exception e) {
            logger.error("Error al analizar consulta NLP con OpenAI: {}", e.getMessage(), e);
            throw new RuntimeException("Error al analizar consulta NLP: " + e.getMessage(), e);
        }
    }

    /**
     * Construye el JSON de contexto para enviar a OpenAI
     */
    private String construirJSONParaBusquedaNLP(String consultaUsuario, BusquedaContextoDto contexto) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"consultaUsuario\": \"").append(escaparJson(consultaUsuario)).append("\",\n");
        json.append("  \"contexto\": {\n");
        
        // Tipos de comida
        if (contexto.getContexto().getTiposComida() != null && !contexto.getContexto().getTiposComida().isEmpty()) {
            json.append("    \"tiposComida\": [");
            for (int i = 0; i < contexto.getContexto().getTiposComida().size(); i++) {
                if (i > 0) json.append(", ");
                json.append("\"").append(escaparJson(contexto.getContexto().getTiposComida().get(i))).append("\"");
            }
            json.append("]");
        } else {
            json.append("    \"tiposComida\": []");
        }
        
        // Barrios
        if (contexto.getContexto().getBarrios() != null && !contexto.getContexto().getBarrios().isEmpty()) {
            json.append(",\n    \"barrios\": [");
            for (int i = 0; i < contexto.getContexto().getBarrios().size(); i++) {
                if (i > 0) json.append(", ");
                json.append("\"").append(escaparJson(contexto.getContexto().getBarrios().get(i))).append("\"");
            }
            json.append("]");
        } else {
            json.append(",\n    \"barrios\": []");
        }
        
        // Localidades
        if (contexto.getContexto().getLocalidades() != null && !contexto.getContexto().getLocalidades().isEmpty()) {
            json.append(",\n    \"localidades\": [");
            for (int i = 0; i < contexto.getContexto().getLocalidades().size(); i++) {
                if (i > 0) json.append(", ");
                json.append("\"").append(escaparJson(contexto.getContexto().getLocalidades().get(i))).append("\"");
            }
            json.append("]");
        } else {
            json.append(",\n    \"localidades\": []");
        }
        
        // Ambientes
        if (contexto.getContexto().getAmbientes() != null && !contexto.getContexto().getAmbientes().isEmpty()) {
            json.append(",\n    \"ambientes\": [");
            for (int i = 0; i < contexto.getContexto().getAmbientes().size(); i++) {
                if (i > 0) json.append(", ");
                json.append("\"").append(escaparJson(contexto.getContexto().getAmbientes().get(i))).append("\"");
            }
            json.append("]");
        } else {
            json.append(",\n    \"ambientes\": []");
        }
        
        // Rangos de precio
        if (contexto.getContexto().getRangosPrecio() != null && !contexto.getContexto().getRangosPrecio().isEmpty()) {
            json.append(",\n    \"rangosPrecio\": [");
            for (int i = 0; i < contexto.getContexto().getRangosPrecio().size(); i++) {
                if (i > 0) json.append(", ");
                json.append("\"").append(escaparJson(contexto.getContexto().getRangosPrecio().get(i))).append("\"");
            }
            json.append("]");
        } else {
            json.append(",\n    \"rangosPrecio\": []");
        }
        
        // Preferencias del usuario (si está autenticado y tiene preferencias)
        BusquedaContextoDto.PreferenciasUsuarioDto preferenciasUsuario = contexto.getContexto().getPreferenciasUsuario();
        if (preferenciasUsuario != null && preferenciasUsuario.tienePreferencias()) {
            json.append(",\n    \"preferenciasUsuario\": {");
            
            // Tipos de comida preferidos
            if (preferenciasUsuario.getTiposComida() != null && !preferenciasUsuario.getTiposComida().isEmpty()) {
                json.append("\n      \"tiposComida\": [");
                for (int i = 0; i < preferenciasUsuario.getTiposComida().size(); i++) {
                    if (i > 0) json.append(", ");
                    json.append("\"").append(escaparJson(preferenciasUsuario.getTiposComida().get(i))).append("\"");
                }
                json.append("]");
            } else {
                json.append("\n      \"tiposComida\": []");
            }
            
            // Ambientes preferidos
            if (preferenciasUsuario.getAmbientes() != null && !preferenciasUsuario.getAmbientes().isEmpty()) {
                json.append(",\n      \"ambientes\": [");
                for (int i = 0; i < preferenciasUsuario.getAmbientes().size(); i++) {
                    if (i > 0) json.append(", ");
                    json.append("\"").append(escaparJson(preferenciasUsuario.getAmbientes().get(i))).append("\"");
                }
                json.append("]");
            } else {
                json.append(",\n      \"ambientes\": []");
            }
            
            // Rangos de precio preferidos
            if (preferenciasUsuario.getRangosPrecio() != null && !preferenciasUsuario.getRangosPrecio().isEmpty()) {
                json.append(",\n      \"rangosPrecio\": [");
                for (int i = 0; i < preferenciasUsuario.getRangosPrecio().size(); i++) {
                    if (i > 0) json.append(", ");
                    json.append("\"").append(escaparJson(preferenciasUsuario.getRangosPrecio().get(i))).append("\"");
                }
                json.append("]");
            } else {
                json.append(",\n      \"rangosPrecio\": []");
            }
            
            json.append("\n    }");
        }
        
        json.append("\n  }\n}");
        
        return json.toString();
    }
    
    /**
     * Escapa caracteres especiales para JSON
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


