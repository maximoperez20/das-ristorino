package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.RestauranteDto;
import ar.edu.ubp.das.backend.service.RestauranteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
public class RestauranteResource {

    @Autowired
    private RestauranteService restauranteService;

    // GET /api/restaurantes - Obtener todos los restaurantes
    @GetMapping
    public ResponseEntity<List<RestauranteDto>> getAllRestaurantes() {
        List<RestauranteDto> restaurantes = restauranteService.obtenerTodosLosRestaurantes();
        return ResponseEntity.ok(restaurantes);
    }

    // GET /api/restaurantes/{id} - Obtener un restaurante por ID
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteDto> getRestauranteById(@PathVariable Long id) {
        Optional<RestauranteDto> restaurante = restauranteService.obtenerRestaurantePorId(id);
        if (restaurante.isPresent()) {
            return ResponseEntity.ok(restaurante.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/restaurantes - Crear un nuevo restaurante
    @PostMapping
    public ResponseEntity<RestauranteDto> createRestaurante(@Valid @RequestBody RestauranteDto restauranteDto) {
        try {
            RestauranteDto restauranteGuardado = restauranteService.crearRestaurante(restauranteDto);
            if (restauranteGuardado != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(restauranteGuardado);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT /api/restaurantes/{id} - Actualizar un restaurante existente
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteDto> updateRestaurante(@PathVariable Long id, @Valid @RequestBody RestauranteDto restauranteDto) {
        if (!restauranteService.existeRestaurante(id)) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            boolean actualizado = restauranteService.actualizarRestaurante(id, restauranteDto);
            if (actualizado) {
                Optional<RestauranteDto> restauranteActualizado = restauranteService.obtenerRestaurantePorId(id);
                return ResponseEntity.ok(restauranteActualizado.get());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/restaurantes/{id} - Eliminar un restaurante
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurante(@PathVariable Long id) {
        if (!restauranteService.existeRestaurante(id)) {
            return ResponseEntity.notFound().build();
        }
        
        boolean eliminado = restauranteService.eliminarRestaurante(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/restaurantes/categoria/{categoria} - Obtener restaurantes por categoría
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<RestauranteDto>> getRestaurantesByCategoria(@PathVariable String categoria) {
        List<RestauranteDto> restaurantes = restauranteService.obtenerRestaurantesPorCategoria(categoria);
        return ResponseEntity.ok(restaurantes);
    }

    // GET /api/restaurantes/activos - Obtener restaurantes activos
    @GetMapping("/activos")
    public ResponseEntity<List<RestauranteDto>> getRestaurantesActivos() {
        List<RestauranteDto> restaurantes = restauranteService.obtenerRestaurantesActivos();
        return ResponseEntity.ok(restaurantes);
    }

    // GET /api/restaurantes/buscar - Buscar restaurantes por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<RestauranteDto>> buscarRestaurantes(@RequestParam String nombre) {
        List<RestauranteDto> restaurantes = restauranteService.buscarRestaurantesPorNombre(nombre);
        return ResponseEntity.ok(restaurantes);
    }

    // GET /api/restaurantes/calificacion/{calificacion} - Obtener restaurantes por calificación mínima
    @GetMapping("/calificacion/{calificacion}")
    public ResponseEntity<List<RestauranteDto>> getRestaurantesByCalificacion(@PathVariable Double calificacion) {
        List<RestauranteDto> restaurantes = restauranteService.obtenerRestaurantesPorCalificacion(calificacion);
        return ResponseEntity.ok(restaurantes);
    }

    // PUT /api/restaurantes/{id}/calificacion - Actualizar calificación de restaurante
    @PutMapping("/{id}/calificacion")
    public ResponseEntity<RestauranteDto> updateCalificacionRestaurante(@PathVariable Long id, @RequestBody Map<String, Double> calificacionRequest) {
        if (!restauranteService.existeRestaurante(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Double nuevaCalificacion = calificacionRequest.get("calificacion");
        if (nuevaCalificacion == null || nuevaCalificacion < 0.0 || nuevaCalificacion > 5.0) {
            return ResponseEntity.badRequest().build();
        }
        
        boolean actualizado = restauranteService.actualizarCalificacionRestaurante(id, nuevaCalificacion);
        if (actualizado) {
            Optional<RestauranteDto> restaurante = restauranteService.obtenerRestaurantePorId(id);
            return ResponseEntity.ok(restaurante.get());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/restaurantes/estadisticas - Obtener estadísticas de restaurantes
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> getEstadisticasRestaurantes() {
        Map<String, Object> estadisticas = restauranteService.obtenerEstadisticasRestaurantes();
        return ResponseEntity.ok(estadisticas);
    }
}
