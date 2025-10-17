package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.*;
import ar.edu.ubp.das.backend.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReservaService {
    
    @Autowired
    private ReservaRepository reservaRepository;
    
    // Obtener todas las reservas
    public List<ReservaResponseDto> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }
    
    // Obtener reserva por ID
    public Optional<ReservaResponseDto> obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id);
    }
    
    // Crear nueva reserva
    public ReservaResponseDto crearReserva(CrearReservaDto crearReservaDto) {
        return reservaRepository.save(crearReservaDto);
    }
    
    // Actualizar reserva existente
    public boolean actualizarReserva(Long id, ActualizarReservaDto actualizarReservaDto) {
        return reservaRepository.update(actualizarReservaDto, id);
    }
    
    // Eliminar reserva
    public boolean eliminarReserva(Long id) {
        return reservaRepository.deleteById(id);
    }
    
    // Obtener reservas por estado
    public List<ReservaResponseDto> obtenerReservasPorEstado(String estado) {
        return reservaRepository.findByEstado(estado);
    }
    
    // Cambiar estado de una reserva
    public boolean cambiarEstadoReserva(Long id, String nuevoEstado) {
        return reservaRepository.updateEstado(id, nuevoEstado);
    }
    
    // Obtener reservas por email del cliente
    public List<ReservaResponseDto> obtenerReservasPorCliente(String email) {
        return reservaRepository.findByEmail(email);
    }
    
    // Obtener reservas por restaurante
    public List<ReservaResponseDto> obtenerReservasPorRestaurante(Long restauranteId) {
        return reservaRepository.findByRestauranteId(restauranteId);
    }
    
    // Contar total de reservas
    public long contarReservas() {
        return reservaRepository.count();
    }
    
    // Verificar si existe una reserva con el ID dado
    public boolean existeReserva(Long id) {
        return reservaRepository.existsById(id);
    }
    
    // Obtener reservas por rango de fechas
    public List<ReservaResponseDto> obtenerReservasPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return reservaRepository.findByFechaHoraBetween(fechaInicio, fechaFin);
    }
    
    // Obtener estadísticas de reservas
    public Map<String, Object> obtenerEstadisticasReservas() {
        return reservaRepository.getEstadisticas();
    }
}
