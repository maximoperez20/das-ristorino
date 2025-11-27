package ar.edu.ubp.das.backend.client;

import ar.edu.ubp.das.backend.client.rest.RestauranteRestClient;
import ar.edu.ubp.das.backend.client.soap.RestauranteSoapClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Factory para obtener la implementación correcta de RestauranteClient
 * según el protocolo configurado para cada restaurante en la base de datos.
 */
@Component
public class RestauranteClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(RestauranteClientFactory.class);

    private final RestauranteSoapClientImpl soapClient;
    private final RestauranteRestClient restClient;
    private final JdbcTemplate jdbcTemplate;

    @Value("${restaurante.client.default.protocol:SOAP}")
    private String defaultProtocol;

    @Autowired
    public RestauranteClientFactory(RestauranteSoapClientImpl soapClient, 
                                   RestauranteRestClient restClient,
                                   JdbcTemplate jdbcTemplate) {
        this.soapClient = soapClient;
        this.restClient = restClient;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Obtiene el cliente adecuado para un restaurante específico.
     * Consulta el protocolo desde la base de datos (columna tipo_protocolo en restaurantes).
     * 
     * @param nroRestaurante ID del restaurante
     * @return Implementación de RestauranteClient adecuada
     */
    public RestauranteClient getClient(String nroRestaurante) {
        String protocolo = obtenerProtocoloDelRestaurante(nroRestaurante);

        if ("REST".equalsIgnoreCase(protocolo)) {
            logger.debug("Usando cliente REST para restaurante: {}", nroRestaurante);
            return restClient;
        } else {
            logger.debug("Usando cliente SOAP para restaurante: {}", nroRestaurante);
            return soapClient;
        }
    }

    /**
     * Obtiene el protocolo configurado para un restaurante desde la base de datos.
     * Si el restaurante no existe o no tiene protocolo configurado, usa el default.
     * 
     * @param nroRestaurante ID del restaurante
     * @return Protocolo configurado ('SOAP' o 'REST')
     */
    private String obtenerProtocoloDelRestaurante(String nroRestaurante) {
        try {
            String sql = "SELECT tipo_protocolo FROM restaurantes WHERE nro_restaurante = ?";
            String protocolo = jdbcTemplate.queryForObject(sql, String.class, nroRestaurante);
            
            if (protocolo != null && (protocolo.equalsIgnoreCase("SOAP") || protocolo.equalsIgnoreCase("REST"))) {
                return protocolo.toUpperCase();
            }
        } catch (Exception e) {
            logger.warn("No se pudo obtener protocolo para restaurante {}: {}. Usando default: {}", 
                       nroRestaurante, e.getMessage(), defaultProtocol);
        }
        
        // Fallback al protocolo por defecto
        return defaultProtocol;
    }
}
