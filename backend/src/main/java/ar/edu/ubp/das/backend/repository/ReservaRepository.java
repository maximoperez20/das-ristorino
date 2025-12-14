package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.ReservaResponseDto;
import ar.edu.ubp.das.backend.dto.CostoReservaDto;
import ar.edu.ubp.das.backend.dto.DatosCancelarReservaDto;
import ar.edu.ubp.das.backend.dto.EstadoReservaDto;
import ar.edu.ubp.das.backend.dto.RegistrarReservaRistorinoDto;
import ar.edu.ubp.das.backend.components.SimpleJdbcCallFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlOutParameter;
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
    
    @Autowired
    private ObjectMapper objectMapper;
    
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
     * @param nroCliente UUID del cliente
     * @param nroIdioma Número de idioma (0=es-AR, 1=en-US)
     */
    public List<ReservaResponseDto> findByNroCliente(String nroCliente, Integer nroIdioma) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_cliente", nroCliente)
                .addValue("nro_idioma", nroIdioma);
        return jdbcCallFactory.executeQuery("sp_ObtenerReservasPorNroCliente", "dbo", params, "reservas", ReservaResponseDto.class);
    }
    
    public java.math.BigDecimal obtenerCostoReserva(java.time.LocalDate fechaReserva) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("fecha_reserva", java.sql.Date.valueOf(fechaReserva));
        List<CostoReservaDto> results = jdbcCallFactory.executeQuery("sp_ObtenerCostoReserva", "dbo", params, "resultado", CostoReservaDto.class);
        if (results != null && !results.isEmpty() && results.get(0).getMonto() != null) {
            return results.get(0).getMonto();
        }
        return java.math.BigDecimal.ZERO;
    }
    
    /**
     * Registra una reserva en Ristorino usando un DTO tipado.
     * 
     * @param request DTO con todos los parámetros de la reserva
     * @return Código de la reserva generada
     */
    public String registrarReservaRistorino(RegistrarReservaRistorinoDto request) {
        // Usar el método helper del DTO para convertir a SqlParameterSource
        SqlParameterSource params = request.toSqlParameterSource();
        Map<String, Object> result = jdbcCallFactory.executeWithOutputs(
                "sp_RegistrarReservaRistorino", 
                "dbo", 
                params,
                new SqlOutParameter("nro_reserva", Types.VARCHAR)
        );
        if (result != null && result.containsKey("nro_reserva") && result.get("nro_reserva") != null) {
            String nroReserva = result.get("nro_reserva").toString();
            
            // Insertar preferencias si existen
            if (request.getPreferenciasReserva() != null && !request.getPreferenciasReserva().isEmpty()) {
                insertarPreferenciasReserva(nroReserva, request.getNroCliente(), request.getNroRestaurante(), request.getPreferenciasReserva());
            }
            
            return nroReserva;
        }
        return null;
    }
    
    /**
     * Insertar preferencias de una reserva
     * @param nroReserva Número de reserva
     * @param nroCliente Número de cliente
     * @param nroRestaurante Número de restaurante
     * @param preferencias Lista de nro_valor_dominio seleccionados
     */
    public void insertarPreferenciasReserva(
            String nroReserva,
            String nroCliente,
            String nroRestaurante,
            List<Integer> preferencias) {
        try {
            // Convertir la lista a JSON array: [1, 2, 3]
            String preferenciasJson = objectMapper.writeValueAsString(preferencias);
            
            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("nro_reserva", nroReserva)
                    .addValue("nro_cliente", nroCliente)
                    .addValue("nro_restaurante", nroRestaurante)
                    .addValue("preferencias", preferenciasJson, Types.NVARCHAR);
            
            jdbcCallFactory.execute("sp_InsertarPreferenciasReserva", "dbo", params);
        } catch (Exception e) {
            throw new RuntimeException("Error al insertar preferencias de reserva: " + e.getMessage(), e);
        }
    }
    
    public void actualizarCodReservaSucursal(String nroReserva, String codReservaSucursal) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_reserva", nroReserva)
                .addValue("cod_reserva_sucursal", codReservaSucursal);
        jdbcCallFactory.execute("sp_ActualizarCodReservaSucursal", "dbo", params);
    }
    
    /**
     * Obtener código de estado por nombre de estado
     */
    public String obtenerCodigoEstado(String nomEstado) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nom_estado", nomEstado);
        List<EstadoReservaDto> results = jdbcCallFactory.executeQuery(
                "sp_ObtenerCodigoEstado", "dbo", params, "estado", EstadoReservaDto.class);
        if (results != null && !results.isEmpty()) {
            return results.get(0).getCodEstado();
        }
        return null;
    }

    public Optional<DatosCancelarReservaDto> obtenerDatosCancelarReservaDto(String nroReserva){
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_reserva", nroReserva);
        List<DatosCancelarReservaDto> results = jdbcCallFactory.executeQuery(
                "sp_ObtenerCancelacionReserva", "dbo", params, "datos_cancelar", DatosCancelarReservaDto.class);
        if (results != null && !results.isEmpty()) {
            return Optional.of(results.get(0));
        }
        return null;
    }

    public void cancelarReserva(String nroReserva) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_reserva", nroReserva);
        jdbcCallFactory.execute("sp_CancelarReservaRistorino", "dbo", params);
    }


}
