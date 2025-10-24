package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.PromocionDto;
import ar.edu.ubp.das.backend.service.PromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para consulta de promociones
 * Endpoints públicos (no requieren autenticación)
 */
@RestController
@RequestMapping("/api/promociones")
@CrossOrigin(origins = "*")
public class PromocionResource {

    @Autowired
    private PromocionService promocionService;

    /**
     * GET /api/promociones - Obtener todas las promociones
     */
    @GetMapping
    public ResponseEntity<List<PromocionDto>> getAllPromociones() {
        List<PromocionDto> promociones = promocionService.obtenerTodasLasPromociones();
        return ResponseEntity.ok(promociones);
    }

    /**
     * GET /api/promociones/{id} - Obtener una promoción por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PromocionDto> getPromocionById(@PathVariable Long id) {
        Optional<PromocionDto> promocion = promocionService.obtenerPromocionPorId(id);
        return promocion.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
