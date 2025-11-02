package ar.edu.ubp.das.backend.dto.restaurante;

/**
 * Response genérico para notificación de clicks en restaurantes.
 * Compatible con SOAP y REST.
 */
public class NotificarClickResponse {

    private boolean exitoso;
    private String mensaje;

    public NotificarClickResponse() {}

    public NotificarClickResponse(boolean exitoso, String mensaje) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}

