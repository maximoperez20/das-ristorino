package ar.edu.ubp.das.backend.dto.soap;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "registrarReservaResponse", namespace = "http://das.ubp.edu.ar/restaurante")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistrarReservaSoapDto {
    
    @XmlElement(name = "codReserva", namespace = "http://das.ubp.edu.ar/restaurante")
    private String codReserva;
    
    @XmlElement(name = "confirmada", namespace = "http://das.ubp.edu.ar/restaurante")
    private boolean confirmada;
    
    @XmlElement(name = "mensaje", namespace = "http://das.ubp.edu.ar/restaurante")
    private String mensaje;

    public RegistrarReservaSoapDto() {}

    public String getCodReserva() {
        return codReserva;
    }

    public void setCodReserva(String codReserva) {
        this.codReserva = codReserva;
    }

    public boolean isConfirmada() {
        return confirmada;
    }

    public void setConfirmada(boolean confirmada) {
        this.confirmada = confirmada;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}

