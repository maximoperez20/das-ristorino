package ar.edu.ubp.das.backend.client;

import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickRequest;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickResponse;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoRequest;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoResponse;

/**
 * Interfaz común para comunicación con restaurantes.
 * Permite implementaciones para diferentes protocolos (SOAP, REST, etc.).
 */
public interface RestauranteClient {

    /**
     * Registra contenido generado en el sistema del restaurante.
     *
     * @param request Datos del contenido a registrar
     * @return Respuesta con el resultado del registro
     */
    RegistrarContenidoResponse registrarContenido(RegistrarContenidoRequest request);

    /**
     * Notifica un click sobre un contenido al sistema del restaurante.
     *
     * @param request Datos del click a notificar
     * @return Respuesta con el resultado de la notificación
     */
    NotificarClickResponse notificarClick(NotificarClickRequest request);
}

