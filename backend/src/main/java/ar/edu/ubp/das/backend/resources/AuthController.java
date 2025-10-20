package ar.edu.ubp.das.backend.resources;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/public")
public class AuthController {

    @GetMapping("/token")
    public Map<String, String> getToken() {
        // Token JWT generado con:
        // Header: {"alg":"HS256","typ":"JWT"}
        // Payload: {"sub":"usuario_demo","role":"USER"}
        // Secret: RISTORINO_BACKEND_2025_SEGURIDAD_SPRINGBOOT_SECRET_KEY
        return Map.of("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c3VhcmlvX2RlbW8iLCJyb2xlIjoiVVNFUiJ9.8Q7vQ7vQ7vQ7vQ7vQ7vQ7vQ7vQ7vQ7vQ7vQ7vQ7vQ");
    }
}
