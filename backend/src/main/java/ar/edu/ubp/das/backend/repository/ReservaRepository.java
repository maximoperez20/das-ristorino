package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.ReservaResponseDto;
import ar.edu.ubp.das.backend.components.SimpleJdbcCallFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ReservaRepository {
    
    @Autowired
    private SimpleJdbcCallFactory jdbcCallFactory;
    
    // Obtener todas las reservas
    public List<ReservaResponseDto> findAll() {
        return jdbcCallFactory.executeQuery("sp_ObtenerTodasLasReservas", "dbo", "reservas", ReservaResponseDto.class);
    }
    
    // Obtener reserva por ID
    public Optional<ReservaResponseDto> findById(String id) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);
        List<ReservaResponseDto> reservas = jdbcCallFactory.executeQuery("sp_ObtenerReservaPorId", "dbo", params, "reservas", ReservaResponseDto.class);
        return reservas.isEmpty() ? Optional.empty() : Optional.of(reservas.get(0));
    }
    
    // Crear nueva reserva
    public ReservaResponseDto save(ar.edu.ubp.das.backend.dto.CrearReservaDto crearReservaDto) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nombre_cliente", crearReservaDto.getNombreCliente())
                .addValue("email", crearReservaDto.getEmail())
                .addValue("telefono", crearReservaDto.getTelefono())
                .addValue("fecha_hora", java.sql.Timestamp.valueOf(crearReservaDto.getFechaHora()))
                .addValue("cantidad_personas", crearReservaDto.getCantidadPersonas())
                .addValue("observaciones", crearReservaDto.getObservaciones())
                .addValue("nuevo_id", null, Types.VARCHAR); // OUTPUT
        
        Map<String, Object> result = jdbcCallFactory.executeWithOutputs("sp_CrearReserva", "dbo", params);
        String nuevoId = result.get("nuevo_id").toString();
        return findById(nuevoId).orElse(null);
    }
    
    // Actualizar reserva existente
    public boolean update(ar.edu.ubp.das.backend.dto.ActualizarReservaDto actualizarReservaDto, String id) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("nombre_cliente", actualizarReservaDto.getNombreCliente())
                .addValue("email", actualizarReservaDto.getEmail())
                .addValue("telefono", actualizarReservaDto.getTelefono())
                .addValue("fecha_hora", java.sql.Timestamp.valueOf(actualizarReservaDto.getFechaHora()))
                .addValue("cantidad_personas", actualizarReservaDto.getCantidadPersonas())
                .addValue("estado", actualizarReservaDto.getEstado())
                .addValue("observaciones", actualizarReservaDto.getObservaciones());
        
        Map<String, Object> result = jdbcCallFactory.executeWithOutputs("sp_ActualizarReserva", "dbo", params);
        return result != null && result.size() > 0;
    }
    
    // Eliminar reserva
    public boolean deleteById(String id) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);
        
        Map<String, Object> result = jdbcCallFactory.executeWithOutputs("sp_EliminarReserva", "dbo", params);
        return result != null && result.size() > 0;
    }
    
    /**
     * Cambiar estado de una reserva
     */
    public boolean updateEstado(String id, String nuevoEstado) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("nuevo_estado", nuevoEstado);
        
        Map<String, Object> result = jdbcCallFactory.executeWithOutputs("sp_CambiarEstadoReserva", "dbo", params);
        return result != null && result.size() > 0;
    }
    
    /**
     * Verificar si existe una reserva
     */
    public boolean existsById(String id) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);
        
        Map<String, Object> result = jdbcCallFactory.executeWithOutputs("sp_ExisteReserva", "dbo", params);
        if (result != null && result.containsKey("existe")) {
            Integer count = (Integer) result.get("existe");
            return count != null && count > 0;
        }
        return false;
    }
    
    /**
     * Obtener reservas por nro_cliente
     */
    public List<ReservaResponseDto> findByNroCliente(String nroCliente) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_cliente", nroCliente);
        return jdbcCallFactory.executeQuery("sp_ObtenerReservasPorNroCliente", "dbo", params, "reservas", ReservaResponseDto.class);
    }
}
