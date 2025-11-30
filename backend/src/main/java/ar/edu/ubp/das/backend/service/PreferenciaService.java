package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.CategoriaConDominiosDto;
import ar.edu.ubp.das.backend.dto.DominioPreferenciaDto;
import ar.edu.ubp.das.backend.dto.GuardarPreferenciasDto;
import ar.edu.ubp.das.backend.dto.PreferenciaClienteDto;
import ar.edu.ubp.das.backend.repository.PreferenciaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para gestión de preferencias gastronómicas
 */
@Service
public class PreferenciaService {
    
    private static final Logger logger = LoggerFactory.getLogger(PreferenciaService.class);
    
    private final PreferenciaRepository preferenciaRepository;
    
    public PreferenciaService(PreferenciaRepository preferenciaRepository) {
        this.preferenciaRepository = preferenciaRepository;
    }
    
    /**
     * Obtener todas las categorías con sus dominios
     * @param nroIdioma Número de idioma (0=es-AR, 1=en-US)
     */
    public List<CategoriaConDominiosDto> obtenerTodasLasCategoriasConDominios(Integer nroIdioma) {
        logger.info("Obteniendo todas las categorías con sus dominios para nro_idioma: {}", nroIdioma);
        return preferenciaRepository.obtenerTodasLasCategoriasConDominios(nroIdioma);
    }

    public List<DominioPreferenciaDto> obtenerEspecialidadesAlimentariasPorRestaurante(String nroRestaurante, Integer nroIdioma) {
        return preferenciaRepository.obtenerEspecialidadesAlimentariasPorRestaurante(nroRestaurante, nroIdioma);
    }
    
    /**
     * Guardar preferencias de un cliente
     */
    public int guardarPreferenciasCliente(String nroCliente, List<GuardarPreferenciasDto.PreferenciaItemDto> preferencias) {
        logger.info("Guardando preferencias para cliente: {}", nroCliente);
        return preferenciaRepository.guardarPreferenciasCliente(nroCliente, preferencias);
    }
    
    /**
     * Obtener preferencias de un cliente
     * @param nroCliente UUID del cliente
     * @param nroIdioma Número de idioma (0=es-AR, 1=en-US)
     */
    public List<PreferenciaClienteDto> obtenerPreferenciasCliente(String nroCliente, Integer nroIdioma) {
        logger.info("Obteniendo preferencias para cliente: {} con nro_idioma: {}", nroCliente, nroIdioma);
        return preferenciaRepository.obtenerPreferenciasCliente(nroCliente, nroIdioma);
    }
}

