package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.PromocionDto;
import ar.edu.ubp.das.backend.repository.PromocionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de promociones
 * Solo operaciones de consulta (lectura)
 */
@Service
public class PromocionService {
    
    private final PromocionRepository promocionRepository;
    
    public PromocionService(PromocionRepository promocionRepository) {
        this.promocionRepository = promocionRepository;
    }
    
    /**
     * Obtener todas las promociones
     * @param nroIdioma Número de idioma (0=es-AR, 1=en-US)
     */
    public List<PromocionDto> obtenerTodasLasPromociones(Integer nroIdioma) {
        return promocionRepository.findAll(nroIdioma);
    }
    
    /**
     * Obtener promoción por UUID de contenido
     */
    public Optional<PromocionDto> obtenerPromocionPorId(String nroContenido) {
        return promocionRepository.findByContenidoId(nroContenido);
    }
}
