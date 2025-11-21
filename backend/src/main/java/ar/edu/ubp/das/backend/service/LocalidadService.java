package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.LocalidadDto;
import ar.edu.ubp.das.backend.repository.LocalidadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para gestión de localidades
 */
@Service
public class LocalidadService {
    
    private final LocalidadRepository localidadRepository;
    
    public LocalidadService(LocalidadRepository localidadRepository) {
        this.localidadRepository = localidadRepository;
    }
    
    public List<LocalidadDto> obtenerTodasLasLocalidades() {
        return localidadRepository.findAll();
    }
}
