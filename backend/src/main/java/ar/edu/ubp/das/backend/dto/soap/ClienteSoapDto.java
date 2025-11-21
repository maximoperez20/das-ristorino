package ar.edu.ubp.das.backend.dto.soap;

import jakarta.xml.bind.annotation.*;

@XmlType(name = "clienteType", namespace = "http://das.ubp.edu.ar/restaurante")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClienteSoapDto {
    
    @XmlElement(name = "apellido", namespace = "http://das.ubp.edu.ar/restaurante")
    private String apellido;
    
    @XmlElement(name = "nombre", namespace = "http://das.ubp.edu.ar/restaurante")
    private String nombre;
    
    @XmlElement(name = "correo", namespace = "http://das.ubp.edu.ar/restaurante")
    private String correo;
    
    @XmlElement(name = "telefonos", namespace = "http://das.ubp.edu.ar/restaurante", nillable = true)
    private String telefonos;

    public ClienteSoapDto() {}

    public ClienteSoapDto(String apellido, String nombre, String correo, String telefonos) {
        this.apellido = apellido;
        this.nombre = nombre;
        this.correo = correo;
        this.telefonos = telefonos;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }
}

