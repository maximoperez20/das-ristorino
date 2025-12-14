package ar.edu.ubp.das.backend.client.soap;

import ar.edu.ubp.das.backend.client.RestauranteClient;
import ar.edu.ubp.das.backend.dto.HorarioDisponibleDto;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickRequest;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickResponse;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClicksBatchRequest;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClicksBatchResponse;
import ar.edu.ubp.das.backend.dto.restaurante.ObtenerMenuRequest;
import ar.edu.ubp.das.backend.dto.restaurante.ObtenerMenuResponse;
import ar.edu.ubp.das.backend.dto.restaurante.ClienteDto;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoRequest;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoResponse;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarReservaRequest;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarReservaResponse;
import ar.edu.ubp.das.backend.dto.restaurante.ObtenerMenuRequest;
import ar.edu.ubp.das.backend.dto.restaurante.ObtenerMenuResponse;
import ar.edu.ubp.das.backend.dto.soap.GetHorariosDisponiblesSoapDto;
import ar.edu.ubp.das.backend.dto.soap.NotificarClickSoapDto;
import ar.edu.ubp.das.backend.dto.soap.RegistrarContenidoSoapDto;
import ar.edu.ubp.das.backend.utils.SOAPClient;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.ArrayList;

import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoJsonDto;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickJsonDto;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClicksBatchJsonDto;
import ar.edu.ubp.das.backend.dto.restaurante.MarcarPublicadoJsonDto;
import java.util.HashMap; // Necesario para parámetros SOAP
import java.util.List;
import java.util.Map; // Necesario para parsear respuestas dinámicas

@Component
public class RestauranteSoapClientImpl implements RestauranteClient {

    private static final Logger logger = LoggerFactory.getLogger(RestauranteSoapClientImpl.class);
    //private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Value("${soap.restaurante.wsdl:http://localhost:8081/ws/restaurantes.wsdl}")
    private String defaultWsdlUrl;

    @Value("${soap.restaurante.namespace:http://das.ubp.edu.ar/restaurante}")
    private String namespace;

    @Value("${soap.restaurante.service:RestaurantePortService}")
    private String serviceName;

    @Value("${soap.restaurante.port:RestaurantePortSoap11}")
    private String portName;

    private final Gson gson;
    
    // URL dinámica por restaurante (se establece antes de cada llamada)
    private final ThreadLocal<String> dynamicWsdlUrl = new ThreadLocal<>();
    
    public RestauranteSoapClientImpl(Gson gson) {
        this.gson = gson;
    }
    
    /**
     * Establece la URL WSDL dinámica para el restaurante actual.
     * Si no se establece, se usa la URL por defecto.
     */
    public void setWsdlUrl(String wsdlUrl) {
        if (wsdlUrl != null && !wsdlUrl.trim().isEmpty()) {
            dynamicWsdlUrl.set(wsdlUrl);
        } else {
            dynamicWsdlUrl.remove();
        }
    }
    
    /**
     * Obtiene la URL WSDL a usar (dinámica o por defecto).
     */
    private String getWsdlUrl() {
        String url = dynamicWsdlUrl.get();
        return url != null ? url : defaultWsdlUrl;
    }

    @Override
    public RegistrarContenidoResponse registrarContenido(RegistrarContenidoRequest request) {
        try {
            // Usar DTO tipado en lugar de HashMap
            RegistrarContenidoJsonDto jsonDto = new RegistrarContenidoJsonDto(request);
            String jsonString = gson.toJson(jsonDto);

            // Crear cliente SOAP y enviar JSON
            SOAPClient soapClient = new SOAPClient.SOAPClientBuilder()
                    .wsdlUrl(getWsdlUrl())
                    .namespace(namespace)
                    .serviceName(serviceName)
                    .portName(portName)
                    .operationName("registrarContenidoRequest")
                    .build();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("jsonData", jsonString);

            RegistrarContenidoSoapDto soapResponse = soapClient.callServiceForObject(
                    RegistrarContenidoSoapDto.class,
                    "registrarContenidoResponse",
                    parameters
            );

            // Parsear respuesta JSON directamente al DTO genérico
            RegistrarContenidoResponse response = gson.fromJson(
                    soapResponse.getJsonResponse(),
                    RegistrarContenidoResponse.class
            );

            return response;
        } catch (Exception e) {
            logger.error("Error al llamar al servicio SOAP: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación SOAP: " + e.getMessage(), e);
        }
    }

    @Override
    public NotificarClickResponse notificarClick(NotificarClickRequest request) {
        logger.info("Notificando click vía SOAP - Restaurante: {}, Contenido: {}, Click: {}", 
                request.getNroRestaurante(), request.getNroContenido(), request.getNroClick());

        try {
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
            
            // Usar DTO tipado en lugar de HashMap
            NotificarClickJsonDto jsonDto = new NotificarClickJsonDto(request);
            String jsonString = gson.toJson(jsonDto);

            // Crear cliente SOAP y enviar JSON
            SOAPClient soapClient = new SOAPClient.SOAPClientBuilder()
                    .wsdlUrl(getWsdlUrl())
                    .namespace(namespace)
                    .serviceName(serviceName)
                    .portName(portName)
                    .operationName("notificarClickRequest")
                    .build();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("jsonData", jsonString);

            NotificarClickSoapDto soapResponse = soapClient.callServiceForObject(
                    NotificarClickSoapDto.class,
                    "notificarClickResponse",
                    parameters
            );

            // Parsear respuesta JSON directamente al DTO genérico
            NotificarClickResponse response = gson.fromJson(
                    soapResponse.getJsonResponse(),
                    NotificarClickResponse.class
            );

            return response;
        } catch (Exception e) {
            logger.error("Error al llamar al servicio SOAP para notificar click: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación SOAP: " + e.getMessage(), e);
        }
    }

    @Override
    public NotificarClicksBatchResponse notificarClicksBatch(NotificarClicksBatchRequest request) {
        try {
            // Usar DTO tipado en lugar de HashMap
            NotificarClicksBatchJsonDto jsonDto = new NotificarClicksBatchJsonDto(request);
            String jsonString = gson.toJson(jsonDto);

            SOAPClient soapClient = new SOAPClient.SOAPClientBuilder()
                    .wsdlUrl(getWsdlUrl())
                    .namespace(namespace)
                    .serviceName(serviceName)
                    .portName(portName)
                    .operationName("notificarClicksBatchRequest")
                    .build();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("jsonData", jsonString);

            String jsonResponseStr = soapClient.extractJsonResponse("notificarClicksBatchResponse", parameters);
            
            com.google.gson.reflect.TypeToken<Map<String, Object>> mapType = 
                new com.google.gson.reflect.TypeToken<Map<String, Object>>(){};
            Map<String, Object> jsonMap = gson.fromJson(jsonResponseStr, mapType.getType());
            
            NotificarClicksBatchResponse response = new NotificarClicksBatchResponse();
            response.setExitoso(jsonMap.get("exitoso") != null && (Boolean) jsonMap.get("exitoso"));
            response.setMensaje((String) jsonMap.get("mensaje"));
            response.setTotalClicks(jsonMap.get("totalClicks") != null ? 
                ((Number) jsonMap.get("totalClicks")).intValue() : 0);
            response.setClicksExitosos(jsonMap.get("clicksExitosos") != null ? 
                ((Number) jsonMap.get("clicksExitosos")).intValue() : 0);
            response.setClicksFallidos(jsonMap.get("clicksFallidos") != null ? 
                ((Number) jsonMap.get("clicksFallidos")).intValue() : 0);
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultadosMap = (List<Map<String, Object>>) jsonMap.get("resultados");
            if (resultadosMap != null) {
                List<NotificarClicksBatchResponse.ClickProcesadoDto> resultados = new ArrayList<>();
                for (Map<String, Object> resultadoMap : resultadosMap) {
                    NotificarClicksBatchResponse.ClickProcesadoDto dto = 
                        new NotificarClicksBatchResponse.ClickProcesadoDto();
                    dto.setNroClick((String) resultadoMap.get("nroClick"));
                    dto.setExitoso(resultadoMap.get("exitoso") != null && (Boolean) resultadoMap.get("exitoso"));
                    dto.setMensaje((String) resultadoMap.get("mensaje"));
                    resultados.add(dto);
                }
                response.setResultados(resultados);
            }

            return response;
        } catch (Exception e) {
            logger.error("Error al llamar al servicio SOAP para notificar clicks en bloque: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación SOAP: " + e.getMessage(), e);
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
            // Construir JSON a enviar
            Map<String, Object> jsonData = new HashMap<>();
            jsonData.put("nroRestaurante", nroRestaurante);
            jsonData.put("nroSucursal", nroSucursal);
            jsonData.put("codZona", codZona);
            jsonData.put("fecha", fecha.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE));
            jsonData.put("cantidad", cantidad);
            
            String jsonString = gson.toJson(jsonData);

            // Crear cliente SOAP y enviar JSON
            SOAPClient soapClient = new SOAPClient.SOAPClientBuilder()
                    .wsdlUrl(getWsdlUrl())
                    .namespace(namespace)
                    .serviceName(serviceName)
                    .portName(portName)
                    .operationName("getHorariosDisponiblesRequest")
                    .build();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("jsonData", jsonString);

            GetHorariosDisponiblesSoapDto soapResponse = soapClient.callServiceForObject(
                    GetHorariosDisponiblesSoapDto.class,
                    "getHorariosDisponiblesResponse",
                    parameters
            );

            // Parsear respuesta JSON que viene agrupada por zonas
            // La respuesta tiene formato: { "zonas": [...], "totalZonas": N, "fecha": "..." }
            // Necesitamos aplanar la estructura para devolver List<HorarioDisponibleDto>
            com.google.gson.reflect.TypeToken<Map<String, Object>> typeToken = new com.google.gson.reflect.TypeToken<Map<String, Object>>(){};
            Map<String, Object> jsonResponse = gson.fromJson(soapResponse.getJsonResponse(), typeToken.getType());
            
            List<HorarioDisponibleDto> horarios = new ArrayList<>();
            
            if (jsonResponse.containsKey("zonas")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> zonas = (List<Map<String, Object>>) jsonResponse.get("zonas");
                
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
                            
                            // Información de la zona
                            horario.setCodZona(codZonaResp);
                            horario.setNomZona(nomZona);
                            horario.setCapacidadZona(capacidadZona);
                            horario.setPermiteMenores(permiteMenores);
                            
                            // Información del turno
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
            }

            return horarios;

        } catch (Exception e) {
            logger.error("Error al consultar horarios disponibles vía SOAP: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación SOAP: " + e.getMessage(), e);
        }
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
            // Construir request usando DTO
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
            
            String jsonString = gson.toJson(request);

            SOAPClient soapClient = new SOAPClient.SOAPClientBuilder()
                    .wsdlUrl(getWsdlUrl())
                    .namespace(namespace)
                    .serviceName(serviceName)
                    .portName(portName)
                    .operationName("registrarReservaRequest")
                    .build();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("jsonData", jsonString);

            String jsonResponseStr = soapClient.extractJsonResponse("registrarReservaResponse", parameters);

            // Parsear respuesta JSON a DTO
            RegistrarReservaResponse response = gson.fromJson(jsonResponseStr, RegistrarReservaResponse.class);

            if (response != null && response.isConfirmada()) {
                return response.getCodReserva();
            } else {
                String mensaje = response != null ? response.getMensaje() : "Error desconocido";
                throw new RuntimeException("Error al registrar reserva en restaurante: " + mensaje);
            }

        } catch (Exception e) {
            logger.error("Error al registrar reserva vía SOAP: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación SOAP: " + e.getMessage(), e);
        }
    }

    @Override
    public java.util.Map<String, Object> obtenerContenidos(String nroRestaurante, String nroSucursal) {
        try {
            java.util.Map<String, Object> jsonData = new java.util.HashMap<>();
            jsonData.put("nroRestaurante", nroRestaurante);
            jsonData.put("nroSucursal", nroSucursal);

            SOAPClient soapClient = new SOAPClient.SOAPClientBuilder()
                    .wsdlUrl(getWsdlUrl())
                    .namespace(namespace)
                    .serviceName(serviceName)
                    .portName(portName)
                    .operationName("listarContenidosRequest")
                    .build();

            java.util.Map<String, Object> parameters = new java.util.HashMap<>();
            parameters.put("jsonData", gson.toJson(jsonData));

            String jsonResponseStr = soapClient.extractJsonResponse("listarContenidosResponse", parameters);

            // Si la respuesta es un objeto vacío {}, retornar null
            if (jsonResponseStr == null || jsonResponseStr.trim().isEmpty() || jsonResponseStr.trim().equals("{}")) {
                return null;
            }

            // Parsear como Map (no como List)
            com.google.gson.reflect.TypeToken<java.util.Map<String, Object>> typeToken =
                    new com.google.gson.reflect.TypeToken<java.util.Map<String, Object>>(){};

            java.util.Map<String, Object> contenido = gson.fromJson(jsonResponseStr, typeToken.getType());
            return contenido;
        } catch (Exception e) {
            logger.error("Error al obtener contenidos vía SOAP: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación SOAP: " + e.getMessage(), e);
        }
    }

    @Override
    public int marcarPublicado(String nroRestaurante, java.util.List<String> nroContenidos) {
        try {
            // Usar DTO tipado en lugar de HashMap
            MarcarPublicadoJsonDto jsonDto = new MarcarPublicadoJsonDto(nroContenidos);
            String jsonString = gson.toJson(jsonDto);
            
            SOAPClient soapClient = new SOAPClient.SOAPClientBuilder()
                    .wsdlUrl(getWsdlUrl())
                    .namespace(namespace)
                    .serviceName(serviceName)
                    .portName(portName)
                    .operationName("marcarPublicadoRequest")
                    .build();

            // Los parámetros SOAP deben ser Map para el cliente SOAP
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("jsonData", jsonString);

            String jsonResponseStr = soapClient.extractJsonResponse("marcarPublicadoResponse", parameters);

            com.google.gson.reflect.TypeToken<java.util.Map<String, Object>> mapType =
                    new com.google.gson.reflect.TypeToken<java.util.Map<String, Object>>(){};

            java.util.Map<String, Object> resp = gson.fromJson(jsonResponseStr, mapType.getType());
            if (resp != null && resp.get("actualizados") != null) {
                Number n = (Number) resp.get("actualizados");
                return n.intValue();
            }
            return 0;
        } catch (Exception e) {
            logger.error("Error al marcar publicados vía SOAP: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación SOAP: " + e.getMessage(), e);
        }
    }

    @Override
    public ObtenerMenuResponse obtenerMenu(ObtenerMenuRequest request) {
        return null;
    }
}
