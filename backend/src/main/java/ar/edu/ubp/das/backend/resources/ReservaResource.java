package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.*;
import ar.edu.ubp.das.backend.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaResource {

    @Autowired
    private ReservaService reservaService;

    // GET /api/reservas - Obtener todas las reservas
    @GetMapping
    public ResponseEntity<List<ReservaResponseDto>> getAllReservas() {
        List<ReservaResponseDto> reservas = reservaService.obtenerTodasLasReservas();
        return ResponseEntity.ok(reservas);
    }

    // GET /api/reservas/{id} - Obtener una reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDto> getReservaById(@PathVariable String id) {
        Optional<ReservaResponseDto> reserva = reservaService.obtenerReservaPorId(id);
        if (reserva.isPresent()) {
            return ResponseEntity.ok(reserva.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/reservas - Crear una nueva reserva
    @PostMapping
    public ResponseEntity<ReservaResponseDto> createReserva(@Valid @RequestBody CrearReservaDto crearReservaDto) {
        try {
            ReservaResponseDto reservaGuardada = reservaService.crearReserva(crearReservaDto);
            if (reservaGuardada != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(reservaGuardada);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT /api/reservas/{id} - Actualizar una reserva existente
    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDto> updateReserva(@PathVariable String id, @Valid @RequestBody ActualizarReservaDto actualizarReservaDto) {
        if (!reservaService.existeReserva(id)) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            boolean actualizado = reservaService.actualizarReserva(id, actualizarReservaDto);
            if (actualizado) {
                Optional<ReservaResponseDto> reservaActualizada = reservaService.obtenerReservaPorId(id);
                return ResponseEntity.ok(reservaActualizada.get());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

    // GET /api/reservas/estado/{estado} - Obtener reservas por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReservaResponseDto>> getReservasByEstado(@PathVariable String estado) {
        List<ReservaResponseDto> reservas = reservaService.obtenerReservasPorEstado(estado);
        return ResponseEntity.ok(reservas);
    }

    // PUT /api/reservas/{id}/estado - Cambiar estado de una reserva
    @PutMapping("/{id}/estado")
    public ResponseEntity<ReservaResponseDto> updateEstadoReserva(@PathVariable String id, @Valid @RequestBody CambiarEstadoDto cambiarEstadoDto) {
        if (!reservaService.existeReserva(id)) {
            return ResponseEntity.notFound().build();
        }
        
        boolean actualizado = reservaService.cambiarEstadoReserva(id, cambiarEstadoDto.getEstado());
        if (actualizado) {
            Optional<ReservaResponseDto> reserva = reservaService.obtenerReservaPorId(id);
            return ResponseEntity.ok(reserva.get());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/reservas/cliente/{email} - Obtener reservas por email del cliente
    @GetMapping("/cliente/{email}")
    public ResponseEntity<List<ReservaResponseDto>> getReservasByCliente(@PathVariable String email) {
        List<ReservaResponseDto> reservas = reservaService.obtenerReservasPorCliente(email);
        return ResponseEntity.ok(reservas);
    }
    
    // GET /api/reservas/count - Obtener el total de reservas
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getReservasCount() {
        long count = reservaService.contarReservas();
        return ResponseEntity.ok(Map.of("total", count));
    }
    
    // GET /api/reservas/rango-fechas - Obtener reservas por rango de fechas
    @GetMapping("/rango-fechas")
    public ResponseEntity<List<ReservaResponseDto>> getReservasPorRangoFechas(
            @RequestParam LocalDateTime fechaInicio, 
            @RequestParam LocalDateTime fechaFin) {
        List<ReservaResponseDto> reservas = reservaService.obtenerReservasPorRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(reservas);
    }
    
    // GET /api/reservas/estadisticas - Obtener estadísticas de reservas
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> getEstadisticasReservas() {
        Map<String, Object> estadisticas = reservaService.obtenerEstadisticasReservas();
        return ResponseEntity.ok(estadisticas);
    }
}
