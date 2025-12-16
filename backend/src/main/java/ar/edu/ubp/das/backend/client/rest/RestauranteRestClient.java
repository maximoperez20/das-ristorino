package ar.edu.ubp.das.backend.client.rest;

import ar.edu.ubp.das.backend.client.RestauranteClient;
import ar.edu.ubp.das.backend.dto.HorarioDisponibleDto;
import ar.edu.ubp.das.backend.dto.restaurante.ClienteDto;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickRequest;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickResponse;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClicksBatchRequest;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClicksBatchResponse;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoRequest;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoResponse;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarReservaRequest;
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
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoJsonDto;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickJsonDto;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClicksBatchJsonDto;
import ar.edu.ubp.das.backend.dto.restaurante.MarcarPublicadoJsonDto;
import java.util.List;
import java.util.Map; // Necesario para parsear respuestas dinámicas

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
    private final Gson gson;

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

    public RestauranteRestClient(RestTemplate restTemplate, Gson gson) {
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    @Override
    public RegistrarContenidoResponse registrarContenido(RegistrarContenidoRequest request) {
        try {
            String url = getBaseUrl() + "/restaurantes/" + request.getNroRestaurante() + "/contenidos";

            // Usar DTO tipado en lugar de HashMap
            RegistrarContenidoJsonDto jsonDto = new RegistrarContenidoJsonDto(request);
            String jsonString = gson.toJson(jsonDto);

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

            // Usar DTO tipado en lugar de HashMap
            NotificarClickJsonDto jsonDto = new NotificarClickJsonDto(request);
            String jsonString = gson.toJson(jsonDto);

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

            // Usar DTO tipado en lugar de HashMap
            NotificarClicksBatchJsonDto jsonDto = new NotificarClicksBatchJsonDto(request);
            String jsonString = gson.toJson(jsonDto);

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

            // Usar DTO tipado en lugar de HashMap
            MarcarPublicadoJsonDto jsonDto = new MarcarPublicadoJsonDto(nroContenidos);
            String jsonString = gson.toJson(jsonDto);

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
            Integer cantMenores,
            String observaciones) {
        try {
            String url = getBaseUrl() + "/restaurantes/" + nroRestaurante + "/reservas";

            // Construir request usando DTO (consistente con SOAP)
            RegistrarReservaRequest request = new RegistrarReservaRequest();
            request.setNroClienteRistorino(nroCliente);
            
            ClienteDto datosCliente = new ClienteDto(apellido, nombre, correo, telefonos);
            request.setDatosCliente(datosCliente);
            
            request.setNroRestaurante(nroRestaurante);
            request.setNroSucursal(nroSucursal);
            request.setCodZona(codZona);
            request.setFechaReserva(fechaReserva);
            request.setHoraDesde(horaDesde);
            request.setCantAdultos(cantAdultos);
            request.setCantMenores(cantMenores);
            request.setObservaciones(observaciones);

            String jsonBody = gson.toJson(request);

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

    @Override
    public int cancelarReserva(String nroRestaurante, String nroReserva, String motivoCancelacion) {
        try {
            String url = getBaseUrl() + "/restaurantes/" + nroRestaurante + "/reservas/" + nroReserva + "/cancelar";

            // Crear objeto con la propiedad motivoCancelacion (sin pre-serializar a String)
            Map<String, Object> body = new java.util.HashMap<>();
            if (motivoCancelacion != null && !motivoCancelacion.trim().isEmpty()) {
                body.put("motivoCancelacion", motivoCancelacion);
            }

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            

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
            logger.error("Error al cancelar reserva vía REST: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación REST: " + e.getMessage(), e);
        }
    }
}

