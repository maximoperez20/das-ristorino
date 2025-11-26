package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.*;
import ar.edu.ubp.das.backend.service.ReservaService;
import ar.edu.ubp.das.backend.service.RestauranteService;
import ar.edu.ubp.das.backend.service.LanguageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaResource {

    private static final Logger logger = LoggerFactory.getLogger(ReservaResource.class);
    
    private final ReservaService reservaService;
    private final LanguageService languageService;
    
    @Autowired
    private RestauranteService restauranteService;
    
    public ReservaResource(ReservaService reservaService, LanguageService languageService) {
        this.reservaService = reservaService;
        this.languageService = languageService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponseDto>> getAllReservas() {
        List<ReservaResponseDto> reservas = reservaService.obtenerTodasLasReservas();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDto> getReservaById(@PathVariable String id) {
        return reservaService.obtenerReservaPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createReserva(@Valid @RequestBody CrearReservaDto crearReservaDto) {
        try {
            ReservaResponseDto reservaGuardada = reservaService.crearReserva(crearReservaDto);
            if (reservaGuardada != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(reservaGuardada);
            } else {
                logger.error("Error al crear reserva: El servicio retornó null");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "No se pudo crear la reserva"));
            }
        } catch (RuntimeException e) {
            logger.warn("Error al crear reserva: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado al crear reserva", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear la reserva: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReserva(@PathVariable String id, @Valid @RequestBody ActualizarReservaDto actualizarReservaDto) {
        if (!reservaService.existeReserva(id)) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            boolean actualizado = reservaService.actualizarReserva(id, actualizarReservaDto);
            if (actualizado) {
                return reservaService.obtenerReservaPorId(id)
                        .<ResponseEntity<?>>map(ResponseEntity::ok)
                        .orElseGet(() -> {
                            logger.warn("Reserva actualizada pero no se pudo obtener: {}", id);
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(Map.of("error", "No se pudo recuperar la reserva actualizada"));
                        });
            } else {
                logger.warn("No se pudo actualizar la reserva: {}", id);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "No se pudo actualizar la reserva"));
            }
        } catch (RuntimeException e) {
            logger.warn("Error al actualizar reserva {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar reserva: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar la reserva: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable String id) {
        if (!reservaService.existeReserva(id)) {
            return ResponseEntity.notFound().build();
        }
        
        boolean eliminado = reservaService.eliminarReserva(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> updateEstadoReserva(@PathVariable String id, @Valid @RequestBody CambiarEstadoDto cambiarEstadoDto) {
        if (!reservaService.existeReserva(id)) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            boolean actualizado = reservaService.cambiarEstadoReserva(id, cambiarEstadoDto.getEstado());
            if (actualizado) {
                return reservaService.obtenerReservaPorId(id)
                        .<ResponseEntity<?>>map(ResponseEntity::ok)
                        .orElseGet(() -> {
                            logger.warn("Estado actualizado pero no se pudo obtener reserva: {}", id);
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(Map.of("error", "No se pudo recuperar la reserva actualizada"));
                        });
            } else {
                logger.warn("No se pudo cambiar el estado de la reserva: {}", id);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "No se pudo cambiar el estado de la reserva"));
            }
        } catch (RuntimeException e) {
            logger.warn("Error al cambiar estado de reserva {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado al cambiar estado de reserva: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cambiar el estado: " + e.getMessage()));
        }
    }
    
    @GetMapping("/mis-reservas")
    public ResponseEntity<?> getMisReservas(
            Authentication authentication,
            @RequestHeader(value = "X-Nro-Idioma", required = false) Integer nroIdiomaHeader) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
                logger.warn("Intento de acceso sin autenticación válida");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "No autenticado"));
            }
            
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String nroCliente = jwt.getClaimAsString("nroCliente");
            
            if (nroCliente == null || nroCliente.isEmpty()) {
                logger.warn("Token JWT no contiene nroCliente");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Token inválido: falta nroCliente"));
            }
            
            Integer nroIdioma = languageService.getNroIdiomaFromRequest(nroIdiomaHeader);
            logger.info("Obteniendo reservas para cliente: {} con nro_idioma: {}", nroCliente, nroIdioma);
            List<ReservaResponseDto> reservas = reservaService.obtenerReservasPorNroCliente(nroCliente, nroIdioma);
            return ResponseEntity.ok(reservas);
            
        } catch (Exception e) {
            logger.error("Error inesperado al obtener reservas del usuario", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener reservas: " + e.getMessage()));
        }
    }
    
    @PostMapping("/confirmar")
    public ResponseEntity<?> confirmarReserva(
            @Valid @RequestBody ConfirmarReservaDto request,
            Authentication authentication) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
                logger.warn("Intento de confirmar reserva sin autenticación válida");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Debe estar autenticado para confirmar una reserva"));
            }
            
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String nroCliente = jwt.getClaimAsString("nroCliente");
            
            if (nroCliente == null || nroCliente.isEmpty()) {
                logger.warn("Token JWT no contiene nroCliente");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Token inválido: falta nroCliente"));
            }
            
            ConfirmarReservaResponseDto response = reservaService.confirmarReserva(request, nroCliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            logger.warn("Error al confirmar reserva: {}", e.getMessage());
            
            if (e.getMessage().contains("disponibilidad") || e.getMessage().contains("No hay")) {
                try {
                    List<HorarioDisponibleDto> horarios = restauranteService.obtenerHorariosDisponibles(
                            request.getNroRestaurante(),
                            request.getNroSucursal(),
                            request.getCodZona(),
                            request.getFechaReserva(),
                            null
                    );
                    
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", e.getMessage());
                    errorResponse.put("horarios", horarios);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                } catch (Exception ex) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", e.getMessage()));
                }
            }
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado al confirmar reserva", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al confirmar reserva: " + e.getMessage()));
        }
    }
}
