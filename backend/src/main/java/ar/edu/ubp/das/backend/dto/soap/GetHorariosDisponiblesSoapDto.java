package ar.edu.ubp.das.backend.dto.soap;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "getHorariosDisponiblesResponse", namespace = "http://das.ubp.edu.ar/restaurante")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetHorariosDisponiblesSoapDto {

    @XmlElement(name = "jsonResponse", namespace = "http://das.ubp.edu.ar/restaurante", required = true)
    private String jsonResponse;

    public GetHorariosDisponiblesSoapDto() {}

    public String getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(String jsonResponse) {
        this.jsonResponse = jsonResponse;
    }
}
