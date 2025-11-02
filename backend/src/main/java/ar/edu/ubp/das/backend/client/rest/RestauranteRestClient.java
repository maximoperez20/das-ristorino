package ar.edu.ubp.das.backend.client.rest;

import ar.edu.ubp.das.backend.client.RestauranteClient;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickRequest;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickResponse;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoRequest;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoResponse;
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

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación REST de RestauranteClient.
 * Realiza llamadas HTTP REST al sistema del restaurante.
 */
@Component
public class RestauranteRestClient implements RestauranteClient {

    private static final Logger logger = LoggerFactory.getLogger(RestauranteRestClient.class);

    private final RestTemplate restTemplate;

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

            Map<String, Object> body = new HashMap<>();
            if (request.getNroSucursal() != null) {
                body.put("nroSucursal", request.getNroSucursal());
            }
            body.put("contenidoAPublicar", request.getContenidoAPublicar());
            if (request.getImagenAPublicar() != null) {
                body.put("imagenAPublicar", Base64.getEncoder().encodeToString(request.getImagenAPublicar()));
            }
            if (request.getCostoClick() != null) {
                body.put("costoClick", request.getCostoClick());
            }

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<RegistrarContenidoResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    RegistrarContenidoResponse.class
            );

            RegistrarContenidoResponse result = response.getBody();
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

            Map<String, Object> body = new HashMap<>();
            body.put("nroClick", request.getNroClick());
            body.put("fechaHoraRegistro", request.getFechaHoraRegistro());
            if (request.getNroCliente() != null) {
                body.put("nroCliente", request.getNroCliente());
            }
            if (request.getCostoClick() != null) {
                body.put("costoClick", request.getCostoClick());
            }

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<NotificarClickResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    NotificarClickResponse.class
            );

            NotificarClickResponse result = response.getBody();
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

