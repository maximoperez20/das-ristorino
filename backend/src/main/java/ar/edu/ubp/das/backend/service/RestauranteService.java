package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.RestauranteDto;
import ar.edu.ubp.das.backend.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RestauranteService {
    
    @Autowired
    private RestauranteRepository restauranteRepository;
    
    // Obtener todos los restaurantes
    public List<RestauranteDto> obtenerTodosLosRestaurantes() {
        return restauranteRepository.findAll();
    }
    
    // Obtener restaurante por ID
    public Optional<RestauranteDto> obtenerRestaurantePorId(Long id) {
        return restauranteRepository.findById(id);
    }
    
    // Crear nuevo restaurante
    public RestauranteDto crearRestaurante(RestauranteDto restauranteDto) {
        return restauranteRepository.save(restauranteDto);
    }
    
    // Actualizar restaurante existente
    public boolean actualizarRestaurante(Long id, RestauranteDto restauranteDto) {
        restauranteDto.setId(id);
        return restauranteRepository.update(restauranteDto);
    }
    
    // Eliminar restaurante
    public boolean eliminarRestaurante(Long id) {
        return restauranteRepository.deleteById(id);
    }
    
    // Obtener restaurantes por categoría
    public List<RestauranteDto> obtenerRestaurantesPorCategoria(String categoria) {
        return restauranteRepository.findByCategoria(categoria);
    }
    
    // Obtener restaurantes activos
    public List<RestauranteDto> obtenerRestaurantesActivos() {
        return restauranteRepository.findByActivoTrue();
    }
    
    // Buscar restaurantes por nombre
    public List<RestauranteDto> buscarRestaurantesPorNombre(String nombre) {
        return restauranteRepository.findByNombreContaining(nombre);
    }
    
    // Obtener restaurantes por calificación mínima
    public List<RestauranteDto> obtenerRestaurantesPorCalificacion(Double calificacionMinima) {
        return restauranteRepository.findByCalificacionGreaterThanEqual(calificacionMinima);
    }
    
    // Actualizar calificación de restaurante
    public boolean actualizarCalificacionRestaurante(Long id, Double nuevaCalificacion) {
        return restauranteRepository.updateCalificacion(id, nuevaCalificacion);
    }
    
    // Verificar si existe un restaurante
    public boolean existeRestaurante(Long id) {
        return restauranteRepository.existsById(id);
    }
    
    // Obtener estadísticas de restaurantes
    public Map<String, Object> obtenerEstadisticasRestaurantes() {
        return restauranteRepository.getEstadisticas();
    }
}
