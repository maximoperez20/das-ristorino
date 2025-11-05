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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación SOAP de RestauranteClient.
 * 
 * NOTA: Este cliente envía y recibe JSON usando Gson para facilitar
 * modificaciones futuras sin necesidad de IA. Los DTOs genéricos se
 * serializan directamente a JSON, y solo se realizan conversiones
 * especiales cuando es necesario (imagen a base64, fecha a ISO string).
 */
@Component
public class RestauranteSoapClientImpl implements RestauranteClient {

    private static final Logger logger = LoggerFactory.getLogger(RestauranteSoapClientImpl.class);
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

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
            
            // Construir Map manualmente para tener control total sobre el JSON
            // Esto facilita agregar/modificar campos sin tocar el DTO
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

            // Crear cliente SOAP y enviar JSON
            SOAPClient soapClient = new SOAPClient.SOAPClientBuilder()
                    .wsdlUrl(wsdlUrl)
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

            logger.info("Respuesta JSON recibida: {}", soapResponse.getJsonResponse());

            // Parsear respuesta JSON directamente al DTO genérico
            RegistrarContenidoResponse response = gson.fromJson(
                    soapResponse.getJsonResponse(),
                    RegistrarContenidoResponse.class
            );

            logger.info("Respuesta parseada - Exitoso: {}, ID: {}, Mensaje: {}",
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

            // Crear cliente SOAP y enviar JSON
            SOAPClient soapClient = new SOAPClient.SOAPClientBuilder()
                    .wsdlUrl(wsdlUrl)
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

            logger.info("Respuesta JSON recibida: {}", soapResponse.getJsonResponse());

            // Parsear respuesta JSON directamente al DTO genérico
            NotificarClickResponse response = gson.fromJson(
                    soapResponse.getJsonResponse(),
                    NotificarClickResponse.class
            );

            logger.info("Respuesta parseada - Exitoso: {}, Mensaje: {}",
                    response.isExitoso(), response.getMensaje());

            return response;
        } catch (Exception e) {
            logger.error("Error al llamar al servicio SOAP para notificar click: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación SOAP: " + e.getMessage(), e);
        }
    }
}
