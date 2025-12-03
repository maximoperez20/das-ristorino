package ar.edu.ubp.das.backend.dto;

import java.util.List;

/**
 * DTO para construir el contexto de catálogos para enviar a OpenAI
 */
public class BusquedaContextoDto {
    
    private String consultaUsuario;
    private ContextoDto contexto;

    // Constructores
    public BusquedaContextoDto() {}

    public BusquedaContextoDto(String consultaUsuario, ContextoDto contexto) {
        this.consultaUsuario = consultaUsuario;
        this.contexto = contexto;
    }

    // Getters y Setters
    public String getConsultaUsuario() {
        return consultaUsuario;
    }

    public void setConsultaUsuario(String consultaUsuario) {
        this.consultaUsuario = consultaUsuario;
    }

    public ContextoDto getContexto() {
        return contexto;
    }

    public void setContexto(ContextoDto contexto) {
        this.contexto = contexto;
    }

    /**
     * Clase interna para el contexto de catálogos
     */
    public static class ContextoDto {
        private List<String> tiposComida;
        private List<String> barrios;
        private List<String> localidades;
        private List<String> ambientes;
        private List<String> rangosPrecio;
        private PreferenciasUsuarioDto preferenciasUsuario;

        // Constructores
        public ContextoDto() {}

        // Getters y Setters
        public List<String> getTiposComida() {
            return tiposComida;
        }

        public void setTiposComida(List<String> tiposComida) {
            this.tiposComida = tiposComida;
        }

        public List<String> getBarrios() {
            return barrios;
        }

        public void setBarrios(List<String> barrios) {
            this.barrios = barrios;
        }

        public List<String> getLocalidades() {
            return localidades;
        }

        public void setLocalidades(List<String> localidades) {
            this.localidades = localidades;
        }

        public List<String> getAmbientes() {
            return ambientes;
        }

        public void setAmbientes(List<String> ambientes) {
            this.ambientes = ambientes;
        }

        public List<String> getRangosPrecio() {
            return rangosPrecio;
        }

        public void setRangosPrecio(List<String> rangosPrecio) {
            this.rangosPrecio = rangosPrecio;
        }

        public PreferenciasUsuarioDto getPreferenciasUsuario() {
            return preferenciasUsuario;
        }

        public void setPreferenciasUsuario(PreferenciasUsuarioDto preferenciasUsuario) {
            this.preferenciasUsuario = preferenciasUsuario;
        }
    }

    /**
     * DTO para representar las preferencias del usuario en el contexto
     */
    public static class PreferenciasUsuarioDto {
        private List<String> tiposComida;
        private List<String> ambientes;
        private List<String> rangosPrecio;

        public PreferenciasUsuarioDto() {
            this.tiposComida = new java.util.ArrayList<>();
            this.ambientes = new java.util.ArrayList<>();
            this.rangosPrecio = new java.util.ArrayList<>();
        }

        public List<String> getTiposComida() {
            return tiposComida;
        }

        public void setTiposComida(List<String> tiposComida) {
            this.tiposComida = tiposComida != null ? tiposComida : new java.util.ArrayList<>();
        }

        public List<String> getAmbientes() {
            return ambientes;
        }

        public void setAmbientes(List<String> ambientes) {
            this.ambientes = ambientes != null ? ambientes : new java.util.ArrayList<>();
        }

        public List<String> getRangosPrecio() {
            return rangosPrecio;
        }

        public void setRangosPrecio(List<String> rangosPrecio) {
            this.rangosPrecio = rangosPrecio != null ? rangosPrecio : new java.util.ArrayList<>();
        }

        public boolean tienePreferencias() {
            return (tiposComida != null && !tiposComida.isEmpty()) ||
                   (ambientes != null && !ambientes.isEmpty()) ||
                   (rangosPrecio != null && !rangosPrecio.isEmpty());
        }
    }
}

