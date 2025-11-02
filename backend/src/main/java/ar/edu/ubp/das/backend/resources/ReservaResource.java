package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.*;
import ar.edu.ubp.das.backend.service.ReservaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaResource {

    private static final Logger logger = LoggerFactory.getLogger(ReservaResource.class);
    
    private final ReservaService reservaService;
    
    public ReservaResource(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // GET /api/reservas - Obtener todas las reservas
    @GetMapping
    public ResponseEntity<List<ReservaResponseDto>> getAllReservas() {
        List<ReservaResponseDto> reservas = reservaService.obtenerTodasLasReservas();
        return ResponseEntity.ok(reservas);
    }

    // GET /api/reservas/{id} - Obtener una reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDto> getReservaById(@PathVariable String id) {
        return reservaService.obtenerReservaPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/reservas - Crear una nueva reserva
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

    // PUT /api/reservas/{id} - Actualizar una reserva existente
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

    // DELETE /api/reservas/{id} - Eliminar una reserva
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

    // PUT /api/reservas/{id}/estado - Cambiar estado de una reserva
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
}
