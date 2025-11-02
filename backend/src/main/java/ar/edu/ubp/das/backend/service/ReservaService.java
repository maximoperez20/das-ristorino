package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.*;
import ar.edu.ubp.das.backend.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de reservas
 */
@Service
public class ReservaService {
    
    private final ReservaRepository reservaRepository;
    
    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }
    
    /**
     * Obtener todas las reservas
     */
    public List<ReservaResponseDto> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }
    
    /**
     * Obtener reserva por ID
     */
    public Optional<ReservaResponseDto> obtenerReservaPorId(String id) {
        return reservaRepository.findById(id);
    }
    
    /**
     * Crear nueva reserva
     */
    public ReservaResponseDto crearReserva(CrearReservaDto crearReservaDto) {
        return reservaRepository.save(crearReservaDto);
    }
    
    /**
     * Actualizar reserva existente
     */
    public boolean actualizarReserva(String id, ActualizarReservaDto actualizarReservaDto) {
        return reservaRepository.update(actualizarReservaDto, id);
    }
    
    /**
     * Eliminar (cancelar) reserva
     */
    public boolean eliminarReserva(String id) {
        return reservaRepository.deleteById(id);
    }
    
    /**
     * Cambiar estado de una reserva
     */
    public boolean cambiarEstadoReserva(String id, String nuevoEstado) {
        return reservaRepository.updateEstado(id, nuevoEstado);
    }
    
    /**
     * Verificar si existe una reserva con el ID dado
     */
    public boolean existeReserva(String id) {
        return reservaRepository.existsById(id);
    }
}
