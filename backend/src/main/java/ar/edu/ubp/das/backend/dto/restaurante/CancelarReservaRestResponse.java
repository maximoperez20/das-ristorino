package ar.edu.ubp.das.backend.dto.restaurante;

public class CancelarReservaRestResponse {
    private Boolean exitosa;
    private String mensaje;

    public Boolean getExitosa() {
        return exitosa;
    }

    public void setExitosa(Boolean exitosa) {
        this.exitosa = exitosa;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
