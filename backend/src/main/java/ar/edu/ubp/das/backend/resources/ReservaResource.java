package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.ActualizarReservaDto;
import ar.edu.ubp.das.backend.dto.CambiarEstadoDto;
import ar.edu.ubp.das.backend.dto.CancelarReservaConMotivo;
import ar.edu.ubp.das.backend.dto.ConfirmarReservaDto;
import ar.edu.ubp.das.backend.dto.ConfirmarReservaResponseDto;
import ar.edu.ubp.das.backend.dto.CrearReservaDto;
import ar.edu.ubp.das.backend.dto.HorarioDisponibleDto;
import ar.edu.ubp.das.backend.dto.ReservaResponseDto;
import ar.edu.ubp.das.backend.exception.HorarioNoDisponibleException;
import ar.edu.ubp.das.backend.resources.util.ResponseHelper;
import ar.edu.ubp.das.backend.service.ReservaService;
import ar.edu.ubp.das.backend.service.RestauranteService;
import ar.edu.ubp.das.backend.service.LanguageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaResource {

    private static final Logger logger = LoggerFactory.getLogger(ReservaResource.class);
    
    private final ReservaService reservaService;
    private final LanguageService languageService;
    private final RestauranteService restauranteService;
    
    public ReservaResource(ReservaService reservaService, LanguageService languageService, RestauranteService restauranteService) {
        this.reservaService = reservaService;
        this.languageService = languageService;
        this.restauranteService = restauranteService;
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
                return ResponseHelper.internalServerError("No se pudo crear la reserva");
            }
        } catch (RuntimeException e) {
            logger.warn("Error al crear reserva: {}", e.getMessage());
            return ResponseHelper.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al crear reserva", e);
            return ResponseHelper.internalServerError("Error al crear la reserva: " + e.getMessage());
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
                            return ResponseHelper.internalServerError("No se pudo recuperar la reserva actualizada");
                        });
            } else {
                logger.warn("No se pudo actualizar la reserva: {}", id);
                return ResponseHelper.internalServerError("No se pudo actualizar la reserva");
            }
        } catch (RuntimeException e) {
            logger.warn("Error al actualizar reserva {}: {}", id, e.getMessage());
            return ResponseHelper.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar reserva: {}", id, e);
            return ResponseHelper.internalServerError("Error al actualizar la reserva: " + e.getMessage());
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
                            return ResponseHelper.internalServerError("No se pudo recuperar la reserva actualizada");
                        });
            } else {
                logger.warn("No se pudo cambiar el estado de la reserva: {}", id);
                return ResponseHelper.internalServerError("No se pudo cambiar el estado de la reserva");
            }
        } catch (RuntimeException e) {
            logger.warn("Error al cambiar estado de reserva {}: {}", id, e.getMessage());
            return ResponseHelper.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al cambiar estado de reserva: {}", id, e);
            return ResponseHelper.internalServerError("Error al cambiar el estado: " + e.getMessage());
        }
    }

    @PutMapping(value = "/cancelar/{nroReserva}", consumes = "application/json")
    public ResponseEntity<?> cancelarReserva(@PathVariable String nroReserva, @RequestBody(required = false) CancelarReservaConMotivo motivoCancelacion) {
        try {
            boolean cancelado = reservaService.cancelarReserva(nroReserva, motivoCancelacion != null ? motivoCancelacion.getMotivoCancelacion() : null);
            if (cancelado) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseHelper.internalServerError("No se pudo cancelar la reserva");
            }
        } catch (RuntimeException e) {
            logger.warn("Error al cancelar reserva {}: {}", nroReserva, e.getMessage());
            return ResponseHelper.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al cancelar reserva: {}", nroReserva, e);
            return ResponseHelper.internalServerError("Error al cancelar la reserva: " + e.getMessage());
        }
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<?> getMisReservas(
            Authentication authentication,
            @RequestHeader(value = "X-Nro-Idioma", required = false) Integer nroIdiomaHeader) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
                logger.warn("Intento de acceso sin autenticación válida");
                return ResponseHelper.unauthorized("No autenticado");
            }
            
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String nroCliente = jwt.getClaimAsString("nroCliente");
            
            if (nroCliente == null || nroCliente.isEmpty()) {
                logger.warn("Token JWT no contiene nroCliente");
                return ResponseHelper.badRequest("Token inválido: falta nroCliente");
            }
            
            Integer nroIdioma = languageService.getNroIdiomaFromRequest(nroIdiomaHeader);
            logger.info("Obteniendo reservas para cliente: {} con nro_idioma: {}", nroCliente, nroIdioma);
            List<ReservaResponseDto> reservas = reservaService.obtenerReservasPorNroCliente(nroCliente, nroIdioma);
            return ResponseEntity.ok(reservas);
            
        } catch (Exception e) {
            logger.error("Error inesperado al obtener reservas del usuario", e);
            return ResponseHelper.internalServerError("Error al obtener reservas: " + e.getMessage());
        }
    }
    
    @PostMapping("/confirmar")
    public ResponseEntity<?> confirmarReserva(
            @Valid @RequestBody ConfirmarReservaDto request,
            Authentication authentication) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
                logger.warn("Intento de confirmar reserva sin autenticación válida");
                return ResponseHelper.unauthorized("Debe estar autenticado para confirmar una reserva");
            }
            
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String nroCliente = jwt.getClaimAsString("nroCliente");
            
            if (nroCliente == null || nroCliente.isEmpty()) {
                logger.warn("Token JWT no contiene nroCliente");
                return ResponseHelper.badRequest("Token inválido: falta nroCliente");
            }
            
            ConfirmarReservaResponseDto response = reservaService.confirmarReserva(request, nroCliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (HorarioNoDisponibleException e) {
            // Excepción específica que incluye horarios disponibles actualizados
            logger.warn("Horario no disponible al confirmar reserva: {}", e.getMessage());
            List<HorarioDisponibleDto> horarios = e.getHorariosDisponibles();
            
            // Mapear cod_zona_restaurante a cod_zona interno si es necesario
            // (esto ya debería estar hecho en el servicio, pero por si acaso)
            return ResponseHelper.errorWithHorarios(e.getMessage(), horarios);
            
        } catch (RuntimeException e) {
            logger.warn("Error al confirmar reserva: {}", e.getMessage());
            
            // Para otros errores de disponibilidad, intentar obtener horarios
            String mensajeError = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (mensajeError.contains("disponibilidad") || 
                mensajeError.contains("capacidad") ||
                mensajeError.contains("no está disponible") ||
                mensajeError.contains("no hay")) {
                try {
                    // Obtener TODOS los horarios (sin filtrar por codZona) para mostrar todas las opciones
                    List<HorarioDisponibleDto> horarios = restauranteService.obtenerHorariosDisponibles(
                            request.getNroRestaurante(),
                            request.getNroSucursal(),
                            null, // null para obtener todas las zonas
                            request.getFechaReserva(),
                            null
                    );
                    
                    return ResponseHelper.errorWithHorarios(e.getMessage(), horarios);
                } catch (Exception ex) {
                    logger.error("Error al obtener horarios después de error de disponibilidad", ex);
                    return ResponseHelper.badRequest(e.getMessage());
                }
            }
            
            return ResponseHelper.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al confirmar reserva", e);
            return ResponseHelper.internalServerError("Error al confirmar reserva: " + e.getMessage());
        }
    }
}
