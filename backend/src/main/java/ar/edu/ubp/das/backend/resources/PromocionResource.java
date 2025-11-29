package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.ClickResponseDto;
import ar.edu.ubp.das.backend.dto.PromocionDto;
import ar.edu.ubp.das.backend.dto.RegistrarClickDto;
import ar.edu.ubp.das.backend.resources.util.ResponseHelper;
import ar.edu.ubp.das.backend.service.ClickService;
import ar.edu.ubp.das.backend.service.PromocionService;
import ar.edu.ubp.das.backend.service.LanguageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private final LanguageService languageService;
    
    public PromocionResource(PromocionService promocionService, ClickService clickService, LanguageService languageService) {
        this.promocionService = promocionService;
        this.clickService = clickService;
        this.languageService = languageService;
    }

    @GetMapping
    public ResponseEntity<List<PromocionDto>> getAllPromociones(
            @RequestHeader(value = "X-Nro-Idioma", required = false) Integer nroIdiomaHeader) {
        Integer nroIdioma = languageService.getNroIdiomaFromRequest(nroIdiomaHeader);
        List<PromocionDto> promociones = promocionService.obtenerTodasLasPromociones(nroIdioma);
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
                return ResponseHelper.internalServerError("No se pudo registrar el click");
            }
        } catch (RuntimeException e) {
            logger.warn("Error al registrar click: {}", e.getMessage());
            return ResponseHelper.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al registrar click", e);
            return ResponseHelper.internalServerError("Error al registrar click: " + e.getMessage());
        }
    }
}
