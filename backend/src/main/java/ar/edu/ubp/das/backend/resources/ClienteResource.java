package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.AuthResponseDto;
import ar.edu.ubp.das.backend.dto.CrearClienteDto;
import ar.edu.ubp.das.backend.dto.LoginRequestDto;
import ar.edu.ubp.das.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para autenticación y gestión de clientes
 * Endpoints públicos para registro y login
 */
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
@Tag(name = "Clientes", description = "API para autenticación y gestión de clientes")
public class ClienteResource {

    @Autowired
    private AuthService authService;

    /**
     * POST /api/clientes/register - Registro de nuevo cliente
     * Endpoint público (no requiere autenticación)
     * Retorna un token JWT automáticamente después del registro
     */
    @Operation(
        summary = "Registrar nuevo cliente",
        description = "Crea un nuevo cliente en el sistema y retorna un token JWT automáticamente para iniciar sesión"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Cliente registrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos o correo ya registrado",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody CrearClienteDto crearClienteDto) {
        try {
            AuthResponseDto response = authService.register(crearClienteDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar cliente: " + e.getMessage()));
        }
    }

    /**
     * POST /api/clientes/login - Login de cliente
     * Endpoint público (no requiere autenticación)
     * Retorna un token JWT para acceder a endpoints protegidos
     */
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un cliente y retorna un token JWT para acceder a recursos protegidos"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login exitoso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales inválidas o usuario deshabilitado",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            AuthResponseDto response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al iniciar sesión: " + e.getMessage()));
        }
    }
}
