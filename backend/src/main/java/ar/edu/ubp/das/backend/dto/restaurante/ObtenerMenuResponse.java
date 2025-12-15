package ar.edu.ubp.das.backend.dto.restaurante;


public class ObtenerMenuResponse {
    
    private Long nroMenu;    
    private String nombreArchivo;
    private String tipoMime;    
    private Long tamanoBytes;
    private byte[] datosArchivoBase64;    
    private boolean exitoso;
    private String mensaje;

    public ObtenerMenuResponse() {}

    public ObtenerMenuResponse(Long nroMenu, String nombreArchivo, String tipoMime, Long tamanoBytes, byte[] datosArchivoBase64, boolean exitoso, String mensaje) {
        this.nroMenu = nroMenu;
        this.nombreArchivo = nombreArchivo;
        this.tipoMime = tipoMime;
        this.tamanoBytes = tamanoBytes;
        this.datosArchivoBase64 = datosArchivoBase64;
        this.exitoso = exitoso;
        this.mensaje = mensaje;
    }

    public Long getNroMenu() {
        return nroMenu;
    }
 
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public Long getTamanoBytes() {
        return tamanoBytes;
    }

    public byte[] getDatosArchivoBase64() {
        return datosArchivoBase64;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setNroMenu(Long nroMenu) {
        this.nroMenu = nroMenu;
    }
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }
    public void setTamanoBytes(Long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }
    public void setDatosArchivoBase64(byte[] datosArchivoBase64) {
        this.datosArchivoBase64 = datosArchivoBase64;
    }
    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}