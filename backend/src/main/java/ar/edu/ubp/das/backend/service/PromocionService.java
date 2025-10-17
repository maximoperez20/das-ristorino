package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.PromocionDto;
import ar.edu.ubp.das.backend.repository.PromocionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PromocionService {
    
    @Autowired
    private PromocionRepository promocionRepository;
    
    // Obtener todas las promociones
    public List<PromocionDto> obtenerTodasLasPromociones() {
        return promocionRepository.findAll();
    }
    
    // Obtener promoción por ID
    public Optional<PromocionDto> obtenerPromocionPorId(Long id) {
        return promocionRepository.findById(id);
    }
    
    // Crear nueva promoción
    public PromocionDto crearPromocion(PromocionDto promocionDto) {
        return promocionRepository.save(promocionDto);
    }
    
    // Actualizar promoción existente
    public boolean actualizarPromocion(Long id, PromocionDto promocionDto) {
        promocionDto.setId(id);
        return promocionRepository.update(promocionDto);
    }
    
    // Eliminar promoción
    public boolean eliminarPromocion(Long id) {
        return promocionRepository.deleteById(id);
    }
    
    // Obtener promociones por restaurante
    public List<PromocionDto> obtenerPromocionesPorRestaurante(Long restauranteId) {
        return promocionRepository.findByRestauranteId(restauranteId);
    }
    
    // Obtener promociones activas
    public List<PromocionDto> obtenerPromocionesActivas() {
        return promocionRepository.findByEstado("ACTIVA");
    }
    
    // Obtener promociones por estado
    public List<PromocionDto> obtenerPromocionesPorEstado(String estado) {
        return promocionRepository.findByEstado(estado);
    }
    
    // Obtener promociones vigentes (fecha actual entre inicio y fin)
    public List<PromocionDto> obtenerPromocionesVigentes() {
        return promocionRepository.findVigentes();
    }
    
    // Cambiar estado de promoción
    public boolean cambiarEstadoPromocion(Long id, String nuevoEstado) {
        return promocionRepository.updateEstado(id, nuevoEstado);
    }
    
    // Validar código de promoción
    public Optional<PromocionDto> validarCodigoPromocion(String codigo) {
        return promocionRepository.findByCodigoPromocion(codigo);
    }
    
    // Obtener promociones por rango de descuento
    public List<PromocionDto> obtenerPromocionesPorRangoDescuento(BigDecimal descuentoMin, BigDecimal descuentoMax) {
        return promocionRepository.findByDescuentoBetween(descuentoMin, descuentoMax);
    }
    
    // Verificar si existe una promoción
    public boolean existePromocion(Long id) {
        return promocionRepository.existsById(id);
    }
    
    // Obtener estadísticas de promociones
    public Map<String, Object> obtenerEstadisticasPromociones() {
        return promocionRepository.getEstadisticas();
    }
}
