package ar.edu.ubp.das.backend.dto.soap;

import jakarta.xml.bind.annotation.*;
import java.time.LocalDateTime;

@XmlRootElement(name = "getMenuActivoResponse", namespace = "http://das.ubp.edu.ar/restaurante")
@XmlAccessorType(XmlAccessType.FIELD)
public class ObtenerMenuSoapDto {
    
    @XmlElement(namespace = "http://das.ubp.edu.ar/restaurante")
    private Long nroMenu;
    
    @XmlElement(namespace = "http://das.ubp.edu.ar/restaurante")
    private String nombreArchivo;
    
    @XmlElement(namespace = "http://das.ubp.edu.ar/restaurante")
    private String tipoMime;
    
    @XmlElement(namespace = "http://das.ubp.edu.ar/restaurante")
    private Long tamanoBytes;
    
    @XmlElement(namespace = "http://das.ubp.edu.ar/restaurante")
    private String hashSha256;
    
    @XmlElement(namespace = "http://das.ubp.edu.ar/restaurante")
    private String datosArchivoBase64;
    
    @XmlElement(namespace = "http://das.ubp.edu.ar/restaurante")
    private LocalDateTime fechaCreacion;
    
    @XmlElement(namespace = "http://das.ubp.edu.ar/restaurante")
    private boolean exitoso;
    
    @XmlElement(namespace = "http://das.ubp.edu.ar/restaurante")
    private String mensaje;

    public ObtenerMenuSoapDto() {}

    // Getters
    public Long getNroMenu() { return nroMenu; }
    public String getNombreArchivo() { return nombreArchivo; }
    public String getTipoMime() { return tipoMime; }
    public Long getTamanoBytes() { return tamanoBytes; }
    public String getHashSha256() { return hashSha256; }
    public String getDatosArchivoBase64() { return datosArchivoBase64; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isExitoso() { return exitoso; }
    public String getMensaje() { return mensaje; }

    // Setters
    public void setNroMenu(Long nroMenu) { this.nroMenu = nroMenu; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
    public void setTipoMime(String tipoMime) { this.tipoMime = tipoMime; }
    public void setTamanoBytes(Long tamanoBytes) { this.tamanoBytes = tamanoBytes; }
    public void setHashSha256(String hashSha256) { this.hashSha256 = hashSha256; }
    public void setDatosArchivoBase64(String datosArchivoBase64) { this.datosArchivoBase64 = datosArchivoBase64; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public void setExitoso(boolean exitoso) { this.exitoso = exitoso; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
