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
    
    public List<ReservaResponseDto> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }
    
    public Optional<ReservaResponseDto> obtenerReservaPorId(String id) {
        return reservaRepository.findById(id);
    }
    
    public ReservaResponseDto crearReserva(CrearReservaDto crearReservaDto) {
        return reservaRepository.save(crearReservaDto);
    }
    
    public boolean actualizarReserva(String id, ActualizarReservaDto actualizarReservaDto) {
        return reservaRepository.update(actualizarReservaDto, id);
    }
    
    public boolean eliminarReserva(String id) {
        return reservaRepository.deleteById(id);
    }
    
    public boolean cambiarEstadoReserva(String id, String nuevoEstado) {
        return reservaRepository.updateEstado(id, nuevoEstado);
    }
    
    public boolean existeReserva(String id) {
        return reservaRepository.existsById(id);
    }
    
    public List<ReservaResponseDto> obtenerReservasPorNroCliente(String nroCliente) {
        return reservaRepository.findByNroCliente(nroCliente);
    }
}
