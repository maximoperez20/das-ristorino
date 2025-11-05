package ar.edu.ubp.das.backend.client.soap;

import ar.edu.ubp.das.backend.client.RestauranteClient;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickRequest;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickResponse;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoRequest;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoResponse;
import ar.edu.ubp.das.backend.dto.soap.NotificarClickSoapDto;
import ar.edu.ubp.das.backend.dto.soap.RegistrarContenidoSoapDto;
import ar.edu.ubp.das.backend.utils.SOAPClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación SOAP de RestauranteClient.
 * Adapta los DTOs genéricos a los DTOs SOAP específicos.
 */
@Component
public class RestauranteSoapClientImpl implements RestauranteClient {

    private static final Logger logger = LoggerFactory.getLogger(RestauranteSoapClientImpl.class);

    @Value("${soap.restaurante.wsdl:http://localhost:8081/ws/restaurantes.wsdl}")
    private String wsdlUrl;

    @Value("${soap.restaurante.namespace:http://das.ubp.edu.ar/restaurante}")
    private String namespace;

    @Value("${soap.restaurante.service:RestaurantePortService}")
    private String serviceName;

    @Value("${soap.restaurante.port:RestaurantePortSoap11}")
    private String portName;

    private final Gson gson = new Gson();

    @Override
    public RegistrarContenidoResponse registrarContenido(RegistrarContenidoRequest request) {
        logger.info("Registrando contenido vía SOAP - Restaurante: {}", request.getNroRestaurante());

        try {
            // Construir JSON con los datos del request
            Map<String, Object> jsonData = new HashMap<>();
            jsonData.put("nroRestaurante", request.getNroRestaurante());
            if (request.getNroSucursal() != null) {
                jsonData.put("nroSucursal", request.getNroSucursal());
            }
            jsonData.put("contenidoAPublicar", request.getContenidoAPublicar());
            if (request.getImagenAPublicar() != null) {
                // Convertir imagen a base64 string
                String imagenBase64 = Base64.getEncoder().encodeToString(request.getImagenAPublicar());
                jsonData.put("imagenAPublicar", imagenBase64);
            }
            if (request.getCostoClick() != null) {
                jsonData.put("costoClick", request.getCostoClick());
            }

            String jsonString = gson.toJson(jsonData);
            logger.info("JSON a enviar: {}", jsonString);

            // Crear cliente SOAP
            SOAPClient soapClient = new SOAPClient.SOAPClientBuilder()
                    .wsdlUrl(wsdlUrl)
                    .namespace(namespace)
                    .serviceName(serviceName)
                    .portName(portName)
                    .operationName("registrarContenidoRequest")
                    .build();

            // Enviar JSON como string en el parámetro jsonData
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("jsonData", jsonString);

            // Llamar al servicio y recibir respuesta JSON
            RegistrarContenidoSoapDto soapResponse = soapClient.callServiceForObject(
                    RegistrarContenidoSoapDto.class,
                    "registrarContenidoResponse",
                    parameters
            );

            logger.info("Respuesta JSON recibida: {}", soapResponse.getJsonResponse());

            // Parsear respuesta JSON con GSON
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> responseMap = gson.fromJson(soapResponse.getJsonResponse(), mapType);

            // Mapear respuesta JSON a respuesta genérica
            RegistrarContenidoResponse response = new RegistrarContenidoResponse();
            response.setNroContenido((String) responseMap.get("nroContenido"));
            response.setExitoso((Boolean) responseMap.get("exitoso"));
            response.setMensaje((String) responseMap.get("mensaje"));

            logger.info("Respuesta SOAP parseada - Exitoso: {}, ID: {}, Mensaje: {}",
                    response.isExitoso(), response.getNroContenido(), response.getMensaje());

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
            // Construir JSON con los datos del request
            Map<String, Object> jsonData = new HashMap<>();
            jsonData.put("nroRestaurante", request.getNroRestaurante());
            jsonData.put("nroContenido", request.getNroContenido());
            jsonData.put("nroClick", request.getNroClick());
            // Convertir fecha a string ISO
            jsonData.put("fechaHoraRegistro", request.getFechaHoraRegistro().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            if (request.getNroCliente() != null) {
                jsonData.put("nroCliente", request.getNroCliente());
            }
            if (request.getCostoClick() != null) {
                jsonData.put("costoClick", request.getCostoClick());
            }

            String jsonString = gson.toJson(jsonData);
            logger.info("JSON a enviar: {}", jsonString);

            // Crear cliente SOAP
            SOAPClient soapClient = new SOAPClient.SOAPClientBuilder()
                    .wsdlUrl(wsdlUrl)
                    .namespace(namespace)
                    .serviceName(serviceName)
                    .portName(portName)
                    .operationName("notificarClickRequest")
                    .build();

            // Enviar JSON como string en el parámetro jsonData
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("jsonData", jsonString);

            // Llamar al servicio y recibir respuesta JSON
            NotificarClickSoapDto soapResponse = soapClient.callServiceForObject(
                    NotificarClickSoapDto.class,
                    "notificarClickResponse",
                    parameters
            );

            logger.info("Respuesta JSON recibida: {}", soapResponse.getJsonResponse());

            // Parsear respuesta JSON con GSON
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> responseMap = gson.fromJson(soapResponse.getJsonResponse(), mapType);

            // Mapear respuesta JSON a respuesta genérica
            NotificarClickResponse response = new NotificarClickResponse();
            response.setExitoso((Boolean) responseMap.get("exitoso"));
            response.setMensaje((String) responseMap.get("mensaje"));

            logger.info("Respuesta SOAP parseada - Exitoso: {}, Mensaje: {}",
                    response.isExitoso(), response.getMensaje());

            return response;
        } catch (Exception e) {
            logger.error("Error al llamar al servicio SOAP para notificar click: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación SOAP: " + e.getMessage(), e);
        }
    }
}

