package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.PromocionDto;
import ar.edu.ubp.das.backend.service.PromocionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/promociones")
@CrossOrigin(origins = "*")
public class PromocionResource {

    @Autowired
    private PromocionService promocionService;

    // GET /api/promociones - Obtener todas las promociones
    @GetMapping
    public ResponseEntity<List<PromocionDto>> getAllPromociones() {
        List<PromocionDto> promociones = promocionService.obtenerTodasLasPromociones();
        return ResponseEntity.ok(promociones);
    }

    // GET /api/promociones/{id} - Obtener una promoción por ID
    @GetMapping("/{id}")
    public ResponseEntity<PromocionDto> getPromocionById(@PathVariable Long id) {
        Optional<PromocionDto> promocion = promocionService.obtenerPromocionPorId(id);
        if (promocion.isPresent()) {
            return ResponseEntity.ok(promocion.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/promociones - Crear una nueva promoción
    @PostMapping
    public ResponseEntity<PromocionDto> createPromocion(@Valid @RequestBody PromocionDto promocionDto) {
        try {
            PromocionDto promocionGuardada = promocionService.crearPromocion(promocionDto);
            if (promocionGuardada != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(promocionGuardada);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT /api/promociones/{id} - Actualizar una promoción existente
    @PutMapping("/{id}")
    public ResponseEntity<PromocionDto> updatePromocion(@PathVariable Long id, @Valid @RequestBody PromocionDto promocionDto) {
        if (!promocionService.existePromocion(id)) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            boolean actualizado = promocionService.actualizarPromocion(id, promocionDto);
            if (actualizado) {
                Optional<PromocionDto> promocionActualizada = promocionService.obtenerPromocionPorId(id);
                return ResponseEntity.ok(promocionActualizada.get());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/promociones/{id} - Eliminar una promoción
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromocion(@PathVariable Long id) {
        if (!promocionService.existePromocion(id)) {
            return ResponseEntity.notFound().build();
        }
        
        boolean eliminado = promocionService.eliminarPromocion(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/promociones/restaurante/{restauranteId} - Obtener promociones por restaurante
    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<List<PromocionDto>> getPromocionesByRestaurante(@PathVariable Long restauranteId) {
        List<PromocionDto> promociones = promocionService.obtenerPromocionesPorRestaurante(restauranteId);
        return ResponseEntity.ok(promociones);
    }

    // GET /api/promociones/activas - Obtener promociones activas
    @GetMapping("/activas")
    public ResponseEntity<List<PromocionDto>> getPromocionesActivas() {
        List<PromocionDto> promociones = promocionService.obtenerPromocionesActivas();
        return ResponseEntity.ok(promociones);
    }

    // GET /api/promociones/estado/{estado} - Obtener promociones por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PromocionDto>> getPromocionesByEstado(@PathVariable String estado) {
        List<PromocionDto> promociones = promocionService.obtenerPromocionesPorEstado(estado);
        return ResponseEntity.ok(promociones);
    }

    // GET /api/promociones/vigentes - Obtener promociones vigentes (fecha actual entre inicio y fin)
    @GetMapping("/vigentes")
    public ResponseEntity<List<PromocionDto>> getPromocionesVigentes() {
        List<PromocionDto> promociones = promocionService.obtenerPromocionesVigentes();
        return ResponseEntity.ok(promociones);
    }

    // PUT /api/promociones/{id}/estado - Cambiar estado de una promoción
    @PutMapping("/{id}/estado")
    public ResponseEntity<PromocionDto> updateEstadoPromocion(@PathVariable Long id, @RequestBody Map<String, String> estadoRequest) {
        if (!promocionService.existePromocion(id)) {
            return ResponseEntity.notFound().build();
        }
        
        String nuevoEstado = estadoRequest.get("estado");
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        boolean actualizado = promocionService.cambiarEstadoPromocion(id, nuevoEstado);
        if (actualizado) {
            Optional<PromocionDto> promocion = promocionService.obtenerPromocionPorId(id);
            return ResponseEntity.ok(promocion.get());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/promociones/validar-codigo/{codigo} - Validar código de promoción
    @GetMapping("/validar-codigo/{codigo}")
    public ResponseEntity<PromocionDto> validarCodigoPromocion(@PathVariable String codigo) {
        Optional<PromocionDto> promocion = promocionService.validarCodigoPromocion(codigo);
        if (promocion.isPresent()) {
            return ResponseEntity.ok(promocion.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/promociones/descuento - Obtener promociones por rango de descuento
    @GetMapping("/descuento")
    public ResponseEntity<List<PromocionDto>> getPromocionesByDescuento(
            @RequestParam BigDecimal descuentoMin, 
            @RequestParam BigDecimal descuentoMax) {
        List<PromocionDto> promociones = promocionService.obtenerPromocionesPorRangoDescuento(descuentoMin, descuentoMax);
        return ResponseEntity.ok(promociones);
    }

    // GET /api/promociones/estadisticas - Obtener estadísticas de promociones
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> getEstadisticasPromociones() {
        Map<String, Object> estadisticas = promocionService.obtenerEstadisticasPromociones();
        return ResponseEntity.ok(estadisticas);
    }
}
