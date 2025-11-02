package ar.edu.ubp.das.backend.dto.restaurante;

/**
 * Response genérico para registro de contenido en restaurantes.
 * Compatible con SOAP y REST.
 */
public class RegistrarContenidoResponse {

    private String nroContenido;
    private boolean exitoso;
    private String mensaje;

    public RegistrarContenidoResponse() {}

    public RegistrarContenidoResponse(String nroContenido, boolean exitoso, String mensaje) {
        this.nroContenido = nroContenido;
        this.exitoso = exitoso;
        this.mensaje = mensaje;
    }

    public String getNroContenido() {
        return nroContenido;
    }

    public void setNroContenido(String nroContenido) {
        this.nroContenido = nroContenido;
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

