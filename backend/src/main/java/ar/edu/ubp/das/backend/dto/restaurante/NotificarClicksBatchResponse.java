package ar.edu.ubp.das.backend.dto.restaurante;

import java.util.List;

public class NotificarClicksBatchResponse {

    private boolean exitoso;
    private String mensaje;
    private int totalClicks;
    private int clicksExitosos;
    private int clicksFallidos;
    private List<ClickProcesadoDto> resultados;

    public NotificarClicksBatchResponse() {}

    public NotificarClicksBatchResponse(boolean exitoso, String mensaje, int totalClicks, 
                                       int clicksExitosos, int clicksFallidos, 
                                       List<ClickProcesadoDto> resultados) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.totalClicks = totalClicks;
        this.clicksExitosos = clicksExitosos;
        this.clicksFallidos = clicksFallidos;
        this.resultados = resultados;
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

    public int getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(int totalClicks) {
        this.totalClicks = totalClicks;
    }

    public int getClicksExitosos() {
        return clicksExitosos;
    }

    public void setClicksExitosos(int clicksExitosos) {
        this.clicksExitosos = clicksExitosos;
    }

    public int getClicksFallidos() {
        return clicksFallidos;
    }

    public void setClicksFallidos(int clicksFallidos) {
        this.clicksFallidos = clicksFallidos;
    }

    public List<ClickProcesadoDto> getResultados() {
        return resultados;
    }

    public void setResultados(List<ClickProcesadoDto> resultados) {
        this.resultados = resultados;
    }

    public static class ClickProcesadoDto {
        private String nroClick;
        private boolean exitoso;
        private String mensaje;

        public ClickProcesadoDto() {}

        public ClickProcesadoDto(String nroClick, boolean exitoso, String mensaje) {
            this.nroClick = nroClick;
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }

        public String getNroClick() {
            return nroClick;
        }

        public void setNroClick(String nroClick) {
            this.nroClick = nroClick;
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
}

