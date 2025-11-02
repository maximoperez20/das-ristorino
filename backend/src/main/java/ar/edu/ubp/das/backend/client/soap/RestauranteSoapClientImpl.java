package ar.edu.ubp.das.backend.client.soap;

import ar.edu.ubp.das.backend.client.RestauranteClient;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickRequest;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickResponse;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoRequest;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoResponse;
import ar.edu.ubp.das.backend.dto.soap.NotificarClickSoapDto;
import ar.edu.ubp.das.backend.dto.soap.RegistrarContenidoSoapDto;
import ar.edu.ubp.das.backend.utils.SOAPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

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

    @Override
    public RegistrarContenidoResponse registrarContenido(RegistrarContenidoRequest request) {
        logger.info("Registrando contenido vía SOAP - Restaurante: {}", request.getNroRestaurante());

        try {
            SOAPClient soapClient = new SOAPClient.SOAPClientBuilder()
                    .wsdlUrl(wsdlUrl)
                    .namespace(namespace)
                    .serviceName(serviceName)
                    .portName(portName)
                    .operationName("registrarContenidoRequest")
                    .build();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("nroRestaurante", request.getNroRestaurante());
            if (request.getNroSucursal() != null) {
                parameters.put("nroSucursal", request.getNroSucursal());
            }
            parameters.put("contenidoAPublicar", request.getContenidoAPublicar());
            if (request.getImagenAPublicar() != null) {
                parameters.put("imagenAPublicar", request.getImagenAPublicar());
            }
            if (request.getCostoClick() != null) {
                parameters.put("costoClick", request.getCostoClick());
            }

            RegistrarContenidoSoapDto soapResponse = soapClient.callServiceForObject(
                    RegistrarContenidoSoapDto.class,
                    "registrarContenidoResponse",
                    parameters
            );

            logger.info("Respuesta SOAP - Exitoso: {}, ID: {}, Mensaje: {}",
                    soapResponse.isExitoso(), soapResponse.getNroContenido(), soapResponse.getMensaje());

            // Mapear respuesta SOAP a respuesta genérica
            RegistrarContenidoResponse response = new RegistrarContenidoResponse();
            response.setNroContenido(soapResponse.getNroContenido());
            response.setExitoso(soapResponse.isExitoso());
            response.setMensaje(soapResponse.getMensaje());

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
            SOAPClient soapClient = new SOAPClient.SOAPClientBuilder()
                    .wsdlUrl(wsdlUrl)
                    .namespace(namespace)
                    .serviceName(serviceName)
                    .portName(portName)
                    .operationName("notificarClickRequest")
                    .build();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("nroRestaurante", request.getNroRestaurante());
            parameters.put("nroContenido", request.getNroContenido());
            parameters.put("nroClick", request.getNroClick());
            parameters.put("fechaHoraRegistro", convertToXMLGregorianCalendar(request.getFechaHoraRegistro()));
            if (request.getNroCliente() != null) {
                parameters.put("nroCliente", request.getNroCliente());
            }
            if (request.getCostoClick() != null) {
                parameters.put("costoClick", request.getCostoClick());
            }

            NotificarClickSoapDto soapResponse = soapClient.callServiceForObject(
                    NotificarClickSoapDto.class,
                    "notificarClickResponse",
                    parameters
            );

            logger.info("Respuesta SOAP - Exitoso: {}, Mensaje: {}",
                    soapResponse.isExitoso(), soapResponse.getMensaje());

            // Mapear respuesta SOAP a respuesta genérica
            NotificarClickResponse response = new NotificarClickResponse();
            response.setExitoso(soapResponse.isExitoso());
            response.setMensaje(soapResponse.getMensaje());

            return response;
        } catch (Exception e) {
            logger.error("Error al llamar al servicio SOAP para notificar click: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación SOAP: " + e.getMessage(), e);
        }
    }

    private XMLGregorianCalendar convertToXMLGregorianCalendar(LocalDateTime localDateTime) {
        try {
            ZonedDateTime zonedDateTime = localDateTime.atZone(java.time.ZoneId.systemDefault());
            GregorianCalendar gregorianCalendar = GregorianCalendar.from(zonedDateTime);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (Exception e) {
            logger.error("Error al convertir LocalDateTime a XMLGregorianCalendar: {}", e.getMessage(), e);
            throw new RuntimeException("Error al convertir fecha: " + e.getMessage(), e);
        }
    }
}

