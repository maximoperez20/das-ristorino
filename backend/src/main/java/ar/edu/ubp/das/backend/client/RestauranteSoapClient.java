package ar.edu.ubp.das.backend.client;

import ar.edu.ubp.das.backend.dto.soap.NotificarClickSoapDto;
import ar.edu.ubp.das.backend.dto.soap.RegistrarContenidoSoapDto;
import ar.edu.ubp.das.backend.utils.SOAPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

@Component
public class RestauranteSoapClient {

    private static final Logger logger = LoggerFactory.getLogger(RestauranteSoapClient.class);

    @Value("${soap.restaurante.wsdl:http://localhost:8081/ws/restaurantes.wsdl}")
    private String wsdlUrl;

    @Value("${soap.restaurante.namespace:http://das.ubp.edu.ar/restaurante}")
    private String namespace;

    @Value("${soap.restaurante.service:RestaurantePortService}")
    private String serviceName;

    @Value("${soap.restaurante.port:RestaurantePortSoap11}")
    private String portName;

    public RegistrarContenidoSoapDto registrarContenido(
            String nroRestaurante,
            String nroSucursal,
            String contenidoAPublicar,
            byte[] imagenAPublicar,
            BigDecimal costoClick) {

        logger.info("Enviando contenido al SOAP - Restaurante: {}", nroRestaurante);

        try {
            SOAPClient soapClient = new SOAPClient.SOAPClientBuilder()
                    .wsdlUrl(wsdlUrl)
                    .namespace(namespace)
                    .serviceName(serviceName)
                    .portName(portName)
                    .operationName("registrarContenidoRequest")
                    .build();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("nroRestaurante", nroRestaurante);
            if (nroSucursal != null) {
                parameters.put("nroSucursal", nroSucursal);
            }
            parameters.put("contenidoAPublicar", contenidoAPublicar);
            if (imagenAPublicar != null) {
                parameters.put("imagenAPublicar", imagenAPublicar);
            }
            if (costoClick != null) {
                parameters.put("costoClick", costoClick);
            }

            RegistrarContenidoSoapDto response = soapClient.callServiceForObject(
                    RegistrarContenidoSoapDto.class,
                    "registrarContenidoResponse",
                    parameters
            );

            logger.info("Respuesta SOAP - Exitoso: {}, ID: {}, Mensaje: {}",
                    response.isExitoso(), response.getNroContenido(), response.getMensaje());

            return response;
        } catch (Exception e) {
            logger.error("Error al llamar al servicio SOAP: {}", e.getMessage(), e);
            throw new RuntimeException("Error en comunicación SOAP: " + e.getMessage(), e);
        }
    }

    public NotificarClickSoapDto notificarClick(
            String nroRestaurante,
            String nroContenido,
            String nroClick,
            LocalDateTime fechaHoraRegistro,
            String nroCliente,
            BigDecimal costoClick) {

        logger.info("Notificando click al SOAP - Restaurante: {}, Contenido: {}, Click: {}", 
                nroRestaurante, nroContenido, nroClick);

        try {
            SOAPClient soapClient = new SOAPClient.SOAPClientBuilder()
                    .wsdlUrl(wsdlUrl)
                    .namespace(namespace)
                    .serviceName(serviceName)
                    .portName(portName)
                    .operationName("notificarClickRequest")
                    .build();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("nroRestaurante", nroRestaurante);
            parameters.put("nroContenido", nroContenido);
            parameters.put("nroClick", nroClick);
            parameters.put("fechaHoraRegistro", convertToXMLGregorianCalendar(fechaHoraRegistro));
            if (nroCliente != null) {
                parameters.put("nroCliente", nroCliente);
            }
            if (costoClick != null) {
                parameters.put("costoClick", costoClick);
            }

            NotificarClickSoapDto response = soapClient.callServiceForObject(
                    NotificarClickSoapDto.class,
                    "notificarClickResponse",
                    parameters
            );

            logger.info("Respuesta SOAP - Exitoso: {}, Mensaje: {}",
                    response.isExitoso(), response.getMensaje());

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

