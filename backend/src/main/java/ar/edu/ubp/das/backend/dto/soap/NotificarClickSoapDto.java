package ar.edu.ubp.das.backend.dto.soap;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "notificarClickResponse", namespace = "http://das.ubp.edu.ar/restaurante")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificarClickSoapDto {

    @XmlElement(name = "jsonResponse", namespace = "http://das.ubp.edu.ar/restaurante", required = true)
    private String jsonResponse;

    public NotificarClickSoapDto() {}

    public String getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(String jsonResponse) {
        this.jsonResponse = jsonResponse;
    }
}
