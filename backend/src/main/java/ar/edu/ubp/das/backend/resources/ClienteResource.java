package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.AuthResponseDto;
import ar.edu.ubp.das.backend.dto.CrearClienteDto;
import ar.edu.ubp.das.backend.dto.LoginRequestDto;
import ar.edu.ubp.das.backend.service.AuthService;
import ar.edu.ubp.das.backend.service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para autenticación y gestión de clientes.
 * Endpoints públicos para registro y login.
 */
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteResource {

    private static final Logger logger = LoggerFactory.getLogger(ClienteResource.class);
    
    private final AuthService authService;
    private final JwtService jwtService;
    
    public ClienteResource(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody CrearClienteDto crearClienteDto) {
        try {
            AuthResponseDto response = authService.register(crearClienteDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            logger.warn("Error al registrar cliente: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado al registrar cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar cliente: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            AuthResponseDto response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.warn("Error al iniciar sesión: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado al iniciar sesión", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al iniciar sesión: " + e.getMessage()));
        }
    }

    /**
     * Endpoint de testing para generar tokens JWT válidos sin autenticación.
     * Útil para pruebas con Postman o herramientas similares.
     * 
     * NOTA: Este endpoint solo debe estar disponible en desarrollo/testing.
     */
    @PostMapping("/test-token")
    public ResponseEntity<?> generateTestToken(
            @RequestParam(required = false, defaultValue = "test-user") String nroCliente,
            @RequestParam(required = false, defaultValue = "test@example.com") String correo,
            @RequestParam(required = false, defaultValue = "Test") String nombre,
            @RequestParam(required = false, defaultValue = "User") String apellido) {
        try {
            String token = jwtService.generateToken(nroCliente, correo, nombre, apellido);
            return ResponseEntity.ok(Map.of(
                "token", token,
                "nroCliente", nroCliente,
                "correo", correo,
                "nombre", nombre,
                "apellido", apellido,
                "note", "Este es un token de testing. Usa el endpoint /api/clientes/login para tokens de producción."
            ));
        } catch (Exception e) {
            logger.error("Error al generar token de testing", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al generar token: " + e.getMessage()));
        }
    }
}
