package ar.edu.ubp.das.backend.dto.soap;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "registrarContenidoResponse", namespace = "http://das.ubp.edu.ar/restaurante")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistrarContenidoSoapDto {

    @XmlElement(name = "nroContenido", namespace = "http://das.ubp.edu.ar/restaurante", required = true)
    private String nroContenido;

    @XmlElement(name = "exitoso", namespace = "http://das.ubp.edu.ar/restaurante", required = true)
    private boolean exitoso;

    @XmlElement(name = "mensaje", namespace = "http://das.ubp.edu.ar/restaurante", required = true)
    private String mensaje;

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

