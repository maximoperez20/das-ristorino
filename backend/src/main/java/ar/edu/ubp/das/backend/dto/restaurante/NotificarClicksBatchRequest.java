package ar.edu.ubp.das.backend.dto.restaurante;

import java.util.List;

/**
 * Request para notificar múltiples clicks en bloque a un restaurante.
 * Compatible con SOAP y REST.
 */
public class NotificarClicksBatchRequest {

    private String nroRestaurante;
    private List<NotificarClickRequest> clicks;

    public NotificarClicksBatchRequest() {}

    public NotificarClicksBatchRequest(String nroRestaurante, List<NotificarClickRequest> clicks) {
        this.nroRestaurante = nroRestaurante;
        this.clicks = clicks;
    }

    public String getNroRestaurante() {
        return nroRestaurante;
    }

    public void setNroRestaurante(String nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }

    public List<NotificarClickRequest> getClicks() {
        return clicks;
    }

    public void setClicks(List<NotificarClickRequest> clicks) {
        this.clicks = clicks;
    }
}

