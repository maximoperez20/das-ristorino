package ar.edu.ubp.das.backend.client.rest;

import ar.edu.ubp.das.backend.client.RestauranteClient;
import ar.edu.ubp.das.backend.dto.HorarioDisponibleDto;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickRequest;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickResponse;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClicksBatchRequest;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClicksBatchResponse;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
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
    private String defaultBaseUrl;
    
    // URL dinámica por restaurante (se establece antes de cada llamada)
    private final ThreadLocal<String> dynamicBaseUrl = new ThreadLocal<>();
    
    /**
     * Establece la URL base dinámica para el restaurante actual.
     * Si no se establece, se usa la URL por defecto.
     */
    public void setBaseUrl(String baseUrl) {
        if (baseUrl != null && !baseUrl.trim().isEmpty()) {
            dynamicBaseUrl.set(baseUrl);
        } else {
            dynamicBaseUrl.remove();
        }
    }
    
    /**
     * Obtiene la URL base a usar (dinámica o por defecto).
     */
    private String getBaseUrl() {
        String url = dynamicBaseUrl.get();
        return url != null ? url : defaultBaseUrl;
    }

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
        try {
            String url = getBaseUrl() + "/restaurantes/" + request.getNroRestaurante() + "/contenidos";

            // Construir JSON a enviar
            Map<String, Object> jsonData = new HashMap<>();
            jsonData.put("nroRestaurante", request.getNroRestaurante());
            jsonData.put("nroSucursal", request.getNroSucursal());
            jsonData.put("contenidoAPublicar", request.getContenidoAPublicar());
            if (request.getImagenAPublicar() != null) {
                jsonData.put("imagenAPublicar", Base64.getEncoder().encodeToString(request.getImagenAPublicar()));
            }
            jsonData.put("costoClick", request.getCostoClick());
            
            String jsonString = gson.toJson(jsonData);

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
            
            RegistrarContenidoResponse result = gson.fromJson(
                    responseBody != null ? responseBody : "{}",
                    RegistrarContenidoResponse.class
            );
            
            if (result != null && !result.isExitoso()) {
                logger.warn("El REST no pudo registrar el contenido: {}", 
                        result.getMensaje() != null ? result.getMensaje() : "Respuesta nula");
            }

            return result != null ? result : new RegistrarContenidoResponse();
        } catch (Exception e) {
            logger.error("Error al llamar al servicio REST: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación REST: " + e.getMessage(), e);
        }
    }

    @Override
    public NotificarClickResponse notificarClick(NotificarClickRequest request) {
        try {
            String url = getBaseUrl() + "/restaurantes/" + request.getNroRestaurante() + 
                        "/contenidos/" + request.getNroContenido() + "/clicks";

            // Construir JSON a enviar
            Map<String, Object> jsonData = new HashMap<>();
            jsonData.put("nroRestaurante", request.getNroRestaurante());
            jsonData.put("nroContenido", request.getNroContenido());
            jsonData.put("nroClick", request.getNroClick());
            jsonData.put("fechaHoraRegistro", request.getFechaHoraRegistro().format(ISO_DATE_TIME));
            jsonData.put("nroCliente", request.getNroCliente());
            jsonData.put("costoClick", request.getCostoClick());
            
            String jsonString = gson.toJson(jsonData);

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
            
            NotificarClickResponse result = gson.fromJson(
                    responseBody != null ? responseBody : "{}",
                    NotificarClickResponse.class
            );
            
            if (result != null && !result.isExitoso()) {
                logger.warn("El REST no pudo notificar el click: {}", 
                        result.getMensaje() != null ? result.getMensaje() : "Respuesta nula");
            }

            return result != null ? result : new NotificarClickResponse();
        } catch (Exception e) {
            logger.error("Error al llamar al servicio REST para notificar click: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación REST: " + e.getMessage(), e);
        }
    }

    @Override
    public NotificarClicksBatchResponse notificarClicksBatch(NotificarClicksBatchRequest request) {
        try {
            String url = getBaseUrl() + "/restaurantes/" + request.getNroRestaurante() + "/clicks/batch";

            Map<String, Object> jsonData = new HashMap<>();
            jsonData.put("nroRestaurante", request.getNroRestaurante());
            
            List<Map<String, Object>> clicksJson = new ArrayList<>();
            if (request.getClicks() != null) {
                for (NotificarClickRequest click : request.getClicks()) {
                    Map<String, Object> clickJson = new HashMap<>();
                    clickJson.put("nroContenido", click.getNroContenido());
                    clickJson.put("nroClick", click.getNroClick());
                    clickJson.put("fechaHoraRegistro", click.getFechaHoraRegistro().format(ISO_DATE_TIME));
                    clickJson.put("nroCliente", click.getNroCliente());
                    clickJson.put("costoClick", click.getCostoClick());
                    clicksJson.add(clickJson);
                }
            }
            jsonData.put("clicks", clicksJson);
            
            String jsonString = gson.toJson(jsonData);

            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(jsonString, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String responseBody = response.getBody();
            
            NotificarClicksBatchResponse result = gson.fromJson(
                    responseBody != null ? responseBody : "{}",
                    NotificarClicksBatchResponse.class
            );
            
            if (result == null) {
                result = new NotificarClicksBatchResponse();
                result.setExitoso(false);
                result.setMensaje("Respuesta nula del servidor REST");
            }

            return result;
        } catch (Exception e) {
            logger.error("Error al llamar al servicio REST para notificar clicks en bloque: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación REST: " + e.getMessage(), e);
        }
    }

    @Override
    public List<HorarioDisponibleDto> getHorariosDisponibles(
            String nroRestaurante,
            String nroSucursal,
            String codZona,
            LocalDate fecha,
            Integer cantidad) {
        try {
            String url = getBaseUrl() + "/restaurantes/" + nroRestaurante + 
                        "/sucursales/" + nroSucursal + "/horarios-disponibles";
            
            // Construir query parameters
            StringBuilder urlBuilder = new StringBuilder(url);
            urlBuilder.append("?fecha=").append(fecha.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE));
            if (codZona != null) {
                urlBuilder.append("&codZona=").append(codZona);
            }
            if (cantidad != null) {
                urlBuilder.append("&cantidad=").append(cantidad);
            }
            
            url = urlBuilder.toString();

            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // Parsear respuesta JSON con GSON
            String responseBody = response.getBody();
            
            // La respuesta puede venir como lista plana o como objeto agrupado por zonas
            // Intentamos parsear como objeto primero
            try {
                com.google.gson.reflect.TypeToken<Map<String, Object>> typeToken = 
                    new com.google.gson.reflect.TypeToken<Map<String, Object>>(){};
                Map<String, Object> jsonResponse = gson.fromJson(responseBody, typeToken.getType());
                
                // Si tiene "zonas", es respuesta agrupada - aplanar
                if (jsonResponse.containsKey("zonas")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> zonas = (List<Map<String, Object>>) jsonResponse.get("zonas");
                    List<HorarioDisponibleDto> horarios = new ArrayList<>();
                    
                    for (Map<String, Object> zona : zonas) {
                        String codZonaResp = (String) zona.get("codZona");
                        String nomZona = (String) zona.get("nomZona");
                        Integer capacidadZona = zona.get("capacidadZona") != null 
                            ? ((Number) zona.get("capacidadZona")).intValue() : null;
                        Boolean permiteMenores = zona.get("permiteMenores") != null 
                            ? (Boolean) zona.get("permiteMenores") : null;
                        
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> turnos = (List<Map<String, Object>>) zona.get("horarios");
                        
                        if (turnos != null) {
                            for (Map<String, Object> turno : turnos) {
                                HorarioDisponibleDto horario = new HorarioDisponibleDto();
                                horario.setCodZona(codZonaResp);
                                horario.setNomZona(nomZona);
                                horario.setCapacidadZona(capacidadZona);
                                horario.setPermiteMenores(permiteMenores);
                                
                                if (turno.containsKey("horaDesde") && turno.get("horaDesde") != null) {
                                    horario.setHoraDesde(LocalTime.parse((String) turno.get("horaDesde")));
                                }
                                if (turno.containsKey("horaHasta") && turno.get("horaHasta") != null) {
                                    horario.setHoraHasta(LocalTime.parse((String) turno.get("horaHasta")));
                                }
                                if (turno.containsKey("yaReservados")) {
                                    horario.setYaReservados(turno.get("yaReservados") != null 
                                        ? ((Number) turno.get("yaReservados")).intValue() : 0);
                                }
                                if (turno.containsKey("disponibilidad")) {
                                    horario.setDisponibilidad(turno.get("disponibilidad") != null 
                                        ? ((Number) turno.get("disponibilidad")).intValue() : 0);
                                }
                                
                                horarios.add(horario);
                            }
                        }
                    }
                    
                    return horarios;
                }
            } catch (Exception e) {
                // No es respuesta agrupada, intentar como lista plana
            }
            
            // Si no es objeto agrupado, intentar como lista plana
            com.google.gson.reflect.TypeToken<List<HorarioDisponibleDto>> listTypeToken = 
                new com.google.gson.reflect.TypeToken<List<HorarioDisponibleDto>>(){};
            List<HorarioDisponibleDto> result = gson.fromJson(
                    responseBody != null ? responseBody : "[]",
                    listTypeToken.getType()
            );
            
            return result != null ? result : new ArrayList<>();

        } catch (Exception e) {
            logger.error("Error al llamar al servicio REST para obtener horarios: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación REST: " + e.getMessage(), e);
        }
    }

    @Override
    public java.util.Map<String, Object> obtenerContenidos(String nroRestaurante, String nroSucursal) {
        try {
            String url = getBaseUrl() + "/restaurantes/" + nroRestaurante + "/contenidos";
            if (nroSucursal != null && !nroSucursal.trim().isEmpty()) {
                url += "?nroSucursal=" + nroSucursal;
            }

            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String responseBody = response.getBody();

            // Si la respuesta es un objeto vacío {}, retornar null
            if (responseBody == null || responseBody.trim().isEmpty() || responseBody.trim().equals("{}")) {
                return null;
            }

            // Parsear como Map (no como List)
            com.google.gson.reflect.TypeToken<java.util.Map<String, Object>> typeToken =
                    new com.google.gson.reflect.TypeToken<java.util.Map<String, Object>>(){};

            java.util.Map<String, Object> contenido = gson.fromJson(responseBody, typeToken.getType());
            return contenido;
        } catch (Exception e) {
            logger.error("Error al obtener contenidos vía REST: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación REST: " + e.getMessage(), e);
        }
    }

    @Override
    public int marcarPublicado(String nroRestaurante, java.util.List<String> nroContenidos) {
        try {
            String url = getBaseUrl() + "/restaurantes/" + nroRestaurante + "/contenidos/publish";

            Map<String, Object> jsonData = new HashMap<>();
            jsonData.put("nroContenidos", nroContenidos);

            String jsonString = gson.toJson(jsonData);

            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(jsonString, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String responseBody = response.getBody();

            com.google.gson.reflect.TypeToken<Map<String, Object>> mapType =
                    new com.google.gson.reflect.TypeToken<Map<String, Object>>(){};

            Map<String, Object> resp = gson.fromJson(responseBody != null ? responseBody : "{}", mapType.getType());
            if (resp != null && resp.get("actualizados") != null) {
                Number n = (Number) resp.get("actualizados");
                return n.intValue();
            }
            return 0;
        } catch (Exception e) {
            logger.error("Error al marcar publicados vía REST: {}", e.getMessage(), e);
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

    @Override
    public String registrarReserva(
            String nroCliente,
            String apellido,
            String nombre,
            String correo,
            String telefonos,
            String nroRestaurante,
            String nroSucursal,
            String codZona,
            LocalDate fechaReserva,
            LocalTime horaDesde,
            Integer cantAdultos,
            Integer cantMenores) {
        try {
            String url = getBaseUrl() + "/restaurantes/" + nroRestaurante + "/reservas";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nroClienteRistorino", nroCliente);
            Map<String, Object> datosCliente = new HashMap<>();
            datosCliente.put("apellido", apellido);
            datosCliente.put("nombre", nombre);
            datosCliente.put("correo", correo);
            datosCliente.put("telefonos", telefonos != null ? telefonos : "");
            requestBody.put("datosCliente", datosCliente);
            requestBody.put("nroRestaurante", nroRestaurante);
            requestBody.put("nroSucursal", nroSucursal);
            requestBody.put("codZona", codZona);
            requestBody.put("fechaReserva", fechaReserva.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE));
            requestBody.put("horaDesde", horaDesde.format(java.time.format.DateTimeFormatter.ISO_LOCAL_TIME));
            requestBody.put("cantAdultos", cantAdultos);
            requestBody.put("cantMenores", cantMenores);

            String jsonBody = gson.toJson(requestBody);

            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String responseBody = response.getBody();
            if (responseBody != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = gson.fromJson(responseBody, Map.class);
                if (result != null && result.containsKey("codReserva")) {
                    return result.get("codReserva").toString();
                }
            }

            throw new RuntimeException("Respuesta inválida del servidor REST");

        } catch (Exception e) {
            logger.error("Error al registrar reserva vía REST: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación REST: " + e.getMessage(), e);
        }
    }
}

