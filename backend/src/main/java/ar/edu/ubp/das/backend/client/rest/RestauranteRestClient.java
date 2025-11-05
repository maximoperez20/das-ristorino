package ar.edu.ubp.das.backend.client.rest;

import ar.edu.ubp.das.backend.client.RestauranteClient;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickRequest;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickResponse;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoRequest;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoResponse;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación REST de RestauranteClient.
 * 
 * NOTA: Este cliente envía y recibe JSON usando Gson (igual que SOAP) para facilitar
 * modificaciones futuras sin necesidad de IA. Los DTOs genéricos se serializan
 * a JSON usando Gson, y solo se realizan conversiones especiales cuando es necesario
 * (imagen a base64, fecha a ISO string).
 */
@Component
public class RestauranteRestClient implements RestauranteClient {

    private static final Logger logger = LoggerFactory.getLogger(RestauranteRestClient.class);
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final RestTemplate restTemplate;
    private final Gson gson = new Gson();

    @Value("${rest.restaurante.baseUrl:http://localhost:8082/api}")
    private String baseUrl;

    @Value("${rest.restaurante.apiKey:}")
    private String apiKey;

    @Value("${rest.restaurante.username:}")
    private String username;

    @Value("${rest.restaurante.password:}")
    private String password;

    public RestauranteRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public RegistrarContenidoResponse registrarContenido(RegistrarContenidoRequest request) {
        logger.info("Registrando contenido vía REST - Restaurante: {}", request.getNroRestaurante());

        try {
            String url = baseUrl + "/restaurantes/" + request.getNroRestaurante() + "/contenidos";

            // ============================================
            // CONSTRUCCIÓN DEL JSON A ENVIAR
            // ============================================
            // Para agregar/modificar campos JSON, editar el Map jsonData abajo.
            // Los campos disponibles en request son:
            // - nroRestaurante (String)
            // - nroSucursal (String, opcional)
            // - contenidoAPublicar (String)
            // - imagenAPublicar (byte[], se convierte a base64)
            // - costoClick (BigDecimal, opcional)
            // ============================================
            
            Map<String, Object> jsonData = new HashMap<>();
            jsonData.put("nroRestaurante", request.getNroRestaurante());
            jsonData.put("nroSucursal", request.getNroSucursal());
            jsonData.put("contenidoAPublicar", request.getContenidoAPublicar());
            if (request.getImagenAPublicar() != null) {
                // Convertir imagen a base64 string para JSON
                jsonData.put("imagenAPublicar", Base64.getEncoder().encodeToString(request.getImagenAPublicar()));
            }
            jsonData.put("costoClick", request.getCostoClick());
            // AGREGAR NUEVOS CAMPOS AQUÍ: jsonData.put("nuevoCampo", valor);
            
            String jsonString = gson.toJson(jsonData);
            logger.info("JSON a enviar: {}", jsonString);

            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(jsonString, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Parsear respuesta JSON con GSON (consistente con SOAP)
            String responseBody = response.getBody();
            logger.info("Respuesta JSON recibida: {}", responseBody);
            
            RegistrarContenidoResponse result = gson.fromJson(
                    responseBody != null ? responseBody : "{}",
                    RegistrarContenidoResponse.class
            );
            if (result != null && result.isExitoso()) {
                logger.info("Contenido registrado vía REST exitosamente. ID: {}", result.getNroContenido());
            } else {
                logger.warn("El REST no pudo registrar el contenido: {}", 
                        result != null ? result.getMensaje() : "Respuesta nula");
            }

            return result != null ? result : new RegistrarContenidoResponse();
        } catch (Exception e) {
            logger.error("Error al llamar al servicio REST: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación REST: " + e.getMessage(), e);
        }
    }

    @Override
    public NotificarClickResponse notificarClick(NotificarClickRequest request) {
        logger.info("Notificando click vía REST - Restaurante: {}, Contenido: {}, Click: {}", 
                request.getNroRestaurante(), request.getNroContenido(), request.getNroClick());

        try {
            String url = baseUrl + "/restaurantes/" + request.getNroRestaurante() + 
                        "/contenidos/" + request.getNroContenido() + "/clicks";

            // ============================================
            // CONSTRUCCIÓN DEL JSON A ENVIAR
            // ============================================
            // Para agregar/modificar campos JSON, editar el Map jsonData abajo.
            // Los campos disponibles en request son:
            // - nroRestaurante (String)
            // - nroContenido (String)
            // - nroClick (String)
            // - fechaHoraRegistro (LocalDateTime, se convierte a ISO string)
            // - nroCliente (String, opcional)
            // - costoClick (BigDecimal, opcional)
            // ============================================
            
            Map<String, Object> jsonData = new HashMap<>();
            jsonData.put("nroRestaurante", request.getNroRestaurante());
            jsonData.put("nroContenido", request.getNroContenido());
            jsonData.put("nroClick", request.getNroClick());
            jsonData.put("fechaHoraRegistro", request.getFechaHoraRegistro().format(ISO_DATE_TIME));
            jsonData.put("nroCliente", request.getNroCliente());
            jsonData.put("costoClick", request.getCostoClick());
            // AGREGAR NUEVOS CAMPOS AQUÍ: jsonData.put("nuevoCampo", valor);
            
            String jsonString = gson.toJson(jsonData);
            logger.info("JSON a enviar: {}", jsonString);

            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(jsonString, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Parsear respuesta JSON con GSON (consistente con SOAP)
            String responseBody = response.getBody();
            logger.info("Respuesta JSON recibida: {}", responseBody);
            
            NotificarClickResponse result = gson.fromJson(
                    responseBody != null ? responseBody : "{}",
                    NotificarClickResponse.class
            );
            if (result != null && result.isExitoso()) {
                logger.info("Click notificado vía REST exitosamente");
            } else {
                logger.warn("El REST no pudo notificar el click: {}", 
                        result != null ? result.getMensaje() : "Respuesta nula");
            }

            return result != null ? result : new NotificarClickResponse();
        } catch (Exception e) {
            logger.error("Error al llamar al servicio REST para notificar click: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación REST: " + e.getMessage(), e);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (apiKey != null && !apiKey.trim().isEmpty()) {
            headers.set("X-API-Key", apiKey);
        }

        if (username != null && !username.trim().isEmpty() && 
            password != null && !password.trim().isEmpty()) {
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);
        }

        return headers;
    }
}

