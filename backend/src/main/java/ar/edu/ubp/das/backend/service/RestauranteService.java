package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.RestauranteDto;
import ar.edu.ubp.das.backend.dto.RestauranteDetalleDto;
import ar.edu.ubp.das.backend.dto.SucursalDto;
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
     * Obtener restaurante por UUID (nroRestaurante)
     */
    public Optional<RestauranteDto> obtenerRestaurantePorId(String nroRestaurante) {
        return restauranteRepository.findById(nroRestaurante);
    }
    
    /**
     * Obtener detalle completo de un restaurante (Requerimiento 11)
     * Incluye: datos básicos, tipo de cocina, descripción, sucursales y promociones vigentes
     */
    public Optional<RestauranteDetalleDto> obtenerDetalleRestaurantePorId(String nroRestaurante) {
        return restauranteRepository.findDetalleById(nroRestaurante);
    }
    
    /**
     * Obtener sucursales de un restaurante
     */
    public List<SucursalDto> obtenerSucursales(String nroRestaurante) {
        return restauranteRepository.obtenerSucursales(nroRestaurante);
    }
    
    /**
     * Buscar restaurantes por nombre (búsqueda parcial)
     */
    public List<RestauranteDto> buscarRestaurantesPorNombre(String nombre) {
        return restauranteRepository.findByNombreContaining(nombre);
    }
    
}
