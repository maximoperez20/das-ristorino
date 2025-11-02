package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.ClickResponseDto;
import ar.edu.ubp.das.backend.dto.PromocionDto;
import ar.edu.ubp.das.backend.dto.RegistrarClickDto;
import ar.edu.ubp.das.backend.service.ClickService;
import ar.edu.ubp.das.backend.service.PromocionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para consulta de promociones y registro de clicks.
 * Endpoints públicos (no requieren autenticación).
 */
@RestController
@RequestMapping("/api/promociones")
@CrossOrigin(origins = "*")
public class PromocionResource {

    private static final Logger logger = LoggerFactory.getLogger(PromocionResource.class);
    
    private final PromocionService promocionService;
    private final ClickService clickService;
    
    public PromocionResource(PromocionService promocionService, ClickService clickService) {
        this.promocionService = promocionService;
        this.clickService = clickService;
    }

    @GetMapping
    public ResponseEntity<List<PromocionDto>> getAllPromociones() {
        List<PromocionDto> promociones = promocionService.obtenerTodasLasPromociones();
        return ResponseEntity.ok(promociones);
    }

    @GetMapping("/{nroContenido}")
    public ResponseEntity<PromocionDto> getPromocionById(@PathVariable String nroContenido) {
        Optional<PromocionDto> promocion = promocionService.obtenerPromocionPorId(nroContenido);
        return promocion.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping("/click")
    public ResponseEntity<?> registrarClick(@Valid @RequestBody RegistrarClickDto registrarClickDto) {
        try {
            ClickResponseDto click = clickService.registrarClick(registrarClickDto);
            if (click != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(click);
            } else {
                logger.warn("No se pudo registrar el click para promoción: {}", registrarClickDto.getNroContenido());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "No se pudo registrar el click"));
            }
        } catch (RuntimeException e) {
            logger.warn("Error al registrar click: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado al registrar click", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar click: " + e.getMessage()));
        }
    }
}
