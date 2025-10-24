package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.RestauranteDto;
import ar.edu.ubp.das.backend.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de restaurantes
 * Solo operaciones de consulta (lectura)
 */
@Service
public class RestauranteService {
    
    @Autowired
    private RestauranteRepository restauranteRepository;
    
    /**
     * Obtener todos los restaurantes
     */
    public List<RestauranteDto> obtenerTodosLosRestaurantes() {
        return restauranteRepository.findAll();
    }
    
    /**
     * Obtener restaurante por ID
     */
    public Optional<RestauranteDto> obtenerRestaurantePorId(Long id) {
        return restauranteRepository.findById(id);
    }
    
    /**
     * Buscar restaurantes por nombre (búsqueda parcial)
     */
    public List<RestauranteDto> buscarRestaurantesPorNombre(String nombre) {
        return restauranteRepository.findByNombreContaining(nombre);
    }
}
