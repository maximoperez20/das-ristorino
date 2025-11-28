package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.ClickResponseDto;
import ar.edu.ubp.das.backend.dto.RegistrarClickDto;
import ar.edu.ubp.das.backend.repository.ClickRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestión de clicks en promociones
 */
@Service
public class ClickService {
    
    private static final Logger logger = LoggerFactory.getLogger(ClickService.class);
    
    private final ClickRepository clickRepository;
    
    public ClickService(ClickRepository clickRepository) {
        this.clickRepository = clickRepository;
    }
    
    /**
     * Registra un click en una promoción
     * 
     * @param registrarClickDto DTO con los datos del click
     * @return ClickResponseDto con los datos del click registrado
     */
    public ClickResponseDto registrarClick(RegistrarClickDto registrarClickDto) {
        logger.info("Registrando click en promoción - Restaurante: {}, Idioma: {}, Contenido: {}, Cliente: {}", 
            registrarClickDto.getNroRestaurante(),
            registrarClickDto.getNroIdioma(),
            registrarClickDto.getNroContenido(),
            registrarClickDto.getNroCliente() != null ? registrarClickDto.getNroCliente() : "Anónimo");
        
        try {
            ClickResponseDto click = clickRepository.registrarClick(
                registrarClickDto.getNroRestaurante(),
                registrarClickDto.getNroIdioma(),
                registrarClickDto.getNroContenido(),
                registrarClickDto.getNroCliente()
            );
            
            if (click != null) {
                logger.info("Click registrado exitosamente - ID: {}", click.getNroClick());
            } else {
                logger.warn("No se pudo registrar el click");
            }
            
            return click;
        } catch (Exception e) {
            logger.error("Error al registrar click: {}", e.getMessage(), e);
            throw new RuntimeException("Error al registrar el click en la promoción", e);
        }
    }
}

