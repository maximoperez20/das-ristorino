package ar.edu.ubp.das.backend.dto.restaurante;

public class RegistrarReservaResponse {
    
    private String codReserva;
    private boolean confirmada;
    private String mensaje;

    public RegistrarReservaResponse() {}

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

