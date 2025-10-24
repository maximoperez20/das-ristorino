package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.PromocionDto;
import ar.edu.ubp.das.backend.repository.PromocionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de promociones
 * Solo operaciones de consulta (lectura)
 */
@Service
public class PromocionService {
    
    @Autowired
    private PromocionRepository promocionRepository;
    
    /**
     * Obtener todas las promociones
     */
    public List<PromocionDto> obtenerTodasLasPromociones() {
        return promocionRepository.findAll();
    }
    
    /**
     * Obtener promoción por ID
     */
    public Optional<PromocionDto> obtenerPromocionPorId(Long id) {
        return promocionRepository.findById(id);
    }
}
