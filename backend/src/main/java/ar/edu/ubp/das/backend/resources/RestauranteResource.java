package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.BusquedaNLPRequestDto;
import ar.edu.ubp.das.backend.dto.RestauranteDto;
import ar.edu.ubp.das.backend.dto.RestauranteDetalleDto;
import ar.edu.ubp.das.backend.dto.SucursalDto;
import ar.edu.ubp.das.backend.service.BusquedaNLPService;
import ar.edu.ubp.das.backend.service.RestauranteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para consulta de restaurantes
 * Endpoints públicos (no requieren autenticación)
 * Cumple Requerimiento 11: Visualizar información de restaurantes
 */
@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
public class RestauranteResource {

    @Autowired
    private RestauranteService restauranteService;

    @Autowired
    private BusquedaNLPService busquedaNLPService;

    /**
     * GET /api/restaurantes - Obtener todos los restaurantes
     */
    @GetMapping
    public ResponseEntity<List<RestauranteDto>> getAllRestaurantes() {
        List<RestauranteDto> restaurantes = restauranteService.obtenerTodosLosRestaurantes();
        return ResponseEntity.ok(restaurantes);
    }

    /**
     * GET /api/restaurantes/{nroRestaurante} - Obtener ficha completa de un restaurante (Requerimiento 11)
     * Incluye: nombre, tipo de cocina, descripción, imágenes, promociones vigentes y sucursales
     */
    @GetMapping("/{nroRestaurante}")
    public ResponseEntity<RestauranteDetalleDto> getRestauranteById(@PathVariable String nroRestaurante) {
        Optional<RestauranteDetalleDto> restaurante = restauranteService.obtenerDetalleRestaurantePorId(nroRestaurante);
        return restaurante.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * GET /api/restaurantes/{nroRestaurante}/sucursales - Obtener sucursales de un restaurante
     */
    @GetMapping("/{nroRestaurante}/sucursales")
    public ResponseEntity<List<SucursalDto>> getSucursales(@PathVariable String nroRestaurante) {
        List<SucursalDto> sucursales = restauranteService.obtenerSucursales(nroRestaurante);
        return ResponseEntity.ok(sucursales);
    }

    /**
     * GET /api/restaurantes/buscar - Buscar restaurantes por nombre
     * @param nombre Nombre o parte del nombre del restaurante
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<RestauranteDto>> buscarRestaurantes(@RequestParam String nombre) {
        List<RestauranteDto> restaurantes = restauranteService.buscarRestaurantesPorNombre(nombre);
        return ResponseEntity.ok(restaurantes);
    }

    /**
     * POST /api/restaurantes/buscar-nlp - Buscar restaurantes con lenguaje natural (NLP)
     * Cumple Requerimientos 10 y 35: Búsqueda con lenguaje natural
     * 
     * Permite al usuario expresarse libremente en lenguaje natural para recibir 
     * resultados personalizados y contextuales.
     * 
     * Ejemplos de consultas:
     * - "quiero comer algo picante en el centro"
     * - "cena romántica con sushi"
     * - "almuerzo vegano económico en Nueva Córdoba"
     * - "dónde puedo comer algo picante esta noche"
     * 
     * @param request Solicitud con la consulta en lenguaje natural
     * @return Lista de restaurantes que coinciden con la intención del usuario
     */
    @PostMapping("/buscar-nlp")
    public ResponseEntity<?> buscarRestaurantesPorNLP(@Valid @RequestBody BusquedaNLPRequestDto request) {
        try {
            List<RestauranteDto> restaurantes = busquedaNLPService.buscarRestaurantesPorNLP(request);
            return ResponseEntity.ok(restaurantes);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al procesar la búsqueda: " + e.getMessage()));
        }
    }
}
