package ar.edu.ubp.das.backend.client;

import ar.edu.ubp.das.backend.dto.HorarioDisponibleDto;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickRequest;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickResponse;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClicksBatchRequest;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClicksBatchResponse;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoRequest;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoResponse;

import java.time.LocalDate;
import java.util.List;

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

    /**
     * Notifica múltiples clicks en bloque al sistema del restaurante.
     * Permite minimizar la comunicación entre aplicaciones.
     *
     * @param request Request con lista de clicks a notificar
     * @return Respuesta con el resultado del procesamiento en bloque
     */
    NotificarClicksBatchResponse notificarClicksBatch(NotificarClicksBatchRequest request);

    /**
     * Obtiene los horarios disponibles para una sucursal, zona y fecha específica.
     *
     * @param nroRestaurante UUID del restaurante
     * @param nroSucursal UUID de la sucursal
     * @param codZona UUID de la zona
     * @param fecha Fecha para consultar disponibilidad
     * @param cantidad Cantidad de personas (opcional, null para todos los horarios)
     * @return Lista de horarios disponibles con capacidad y disponibilidad
     */
    List<HorarioDisponibleDto> getHorariosDisponibles(
            String nroRestaurante,
            String nroSucursal,
            String codZona,
            LocalDate fecha,
            Integer cantidad
    );

    /**
     * Obtiene el último contenido NO publicado del restaurante.
     * Si todos están publicados, retorna el más nuevo.
     * Retorna: nroContenido, contenidoAPublicar, imagenAPublicar (URL|null), costoClick, nroSucursal, publicado
     * Retorna null si no hay contenidos.
     */
    java.util.Map<String, Object> obtenerContenidos(String nroRestaurante, String nroSucursal);

    /**
     * Marca como publicados (publicado=1) los contenidos legacy indicados.
     * @return número de filas actualizadas en el sistema legacy.
     */
    int marcarPublicado(String nroRestaurante, java.util.List<String> nroContenidos);
}

