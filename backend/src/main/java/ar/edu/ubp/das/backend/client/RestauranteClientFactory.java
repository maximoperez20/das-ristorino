package ar.edu.ubp.das.backend.client;

import ar.edu.ubp.das.backend.client.rest.RestauranteRestClient;
import ar.edu.ubp.das.backend.client.soap.RestauranteSoapClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Factory para obtener la implementación correcta de RestauranteClient
 * según el protocolo configurado para cada restaurante.
 * 
 * Por defecto usa SOAP para mantener compatibilidad con el código existente.
 * En el futuro se puede extender para consultar el protocolo desde la BD.
 */
@Component
public class RestauranteClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(RestauranteClientFactory.class);

    private final RestauranteSoapClientImpl soapClient;
    private final RestauranteRestClient restClient;

    @Value("${restaurante.client.default.protocol:SOAP}")
    private String defaultProtocol;

    @Autowired
    public RestauranteClientFactory(RestauranteSoapClientImpl soapClient, 
                                   RestauranteRestClient restClient) {
        this.soapClient = soapClient;
        this.restClient = restClient;
    }

    /**
     * Obtiene el cliente adecuado para un restaurante específico.
     * 
     * Por ahora retorna SOAP por defecto.
     * En el futuro se puede consultar desde la BD el protocolo configurado.
     * 
     * @param nroRestaurante ID del restaurante
     * @return Implementación de RestauranteClient adecuada
     */
    public RestauranteClient getClient(String nroRestaurante) {
        // TODO: En el futuro, consultar desde BD el protocolo del restaurante
        // Por ahora, usar el protocolo por defecto
        String protocolo = obtenerProtocoloDelRestaurante(nroRestaurante);

        if ("REST".equalsIgnoreCase(protocolo)) {
            logger.debug("Usando cliente REST para restaurante: {}", nroRestaurante);
            return restClient;
        } else {
            logger.debug("Usando cliente SOAP para restaurante: {} (default)", nroRestaurante);
            return soapClient;
        }
    }

    /**
     * Obtiene el protocolo configurado para un restaurante.
     * Por ahora retorna el protocolo por defecto.
     * 
     * En el futuro se puede implementar consultando desde la BD:
     * - Tabla configuracion_restaurantes con atributo 'tipo_protocolo'
     * - O una tabla dedicada para configuración de comunicación
     * 
     * @param nroRestaurante ID del restaurante
     * @return Protocolo configurado ('SOAP' o 'REST')
     */
    private String obtenerProtocoloDelRestaurante(String nroRestaurante) {
        // TODO: Consultar desde BD
        // SELECT valor FROM configuracion_restaurantes cr
        // JOIN atributos a ON cr.cod_atributo = a.cod_atributo
        // WHERE cr.nro_restaurante = @nroRestaurante
        //   AND a.nom_atributo = 'tipo_protocolo'
        
        // Por ahora, retornar el protocolo por defecto
        return defaultProtocol;
    }
}

