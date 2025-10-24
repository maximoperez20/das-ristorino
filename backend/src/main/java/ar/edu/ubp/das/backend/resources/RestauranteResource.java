package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.RestauranteDto;
import ar.edu.ubp.das.backend.service.RestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para consulta de restaurantes
 * Endpoints públicos (no requieren autenticación)
 */
@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
public class RestauranteResource {

    @Autowired
    private RestauranteService restauranteService;

    /**
     * GET /api/restaurantes - Obtener todos los restaurantes
     */
    @GetMapping
    public ResponseEntity<List<RestauranteDto>> getAllRestaurantes() {
        List<RestauranteDto> restaurantes = restauranteService.obtenerTodosLosRestaurantes();
        return ResponseEntity.ok(restaurantes);
    }

    /**
     * GET /api/restaurantes/{id} - Obtener un restaurante por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteDto> getRestauranteById(@PathVariable Long id) {
        Optional<RestauranteDto> restaurante = restauranteService.obtenerRestaurantePorId(id);
        return restaurante.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
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
}
