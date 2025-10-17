package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.PromocionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PromocionRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // RowMapper para convertir ResultSet a PromocionDto
    private final RowMapper<PromocionDto> promocionRowMapper = new RowMapper<PromocionDto>() {
        @Override
        public PromocionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            PromocionDto promocion = new PromocionDto();
            promocion.setId(rs.getLong("id"));
            promocion.setRestauranteId(rs.getLong("restaurante_id"));
            promocion.setTitulo(rs.getString("titulo"));
            promocion.setDescripcion(rs.getString("descripcion"));
            promocion.setDescuentoPorcentaje(rs.getBigDecimal("descuento_porcentaje"));
            promocion.setDescuentoFijo(rs.getBigDecimal("descuento_fijo"));
            promocion.setFechaInicio(rs.getTimestamp("fecha_inicio").toLocalDateTime());
            promocion.setFechaFin(rs.getTimestamp("fecha_fin").toLocalDateTime());
            promocion.setEstado(rs.getString("estado"));
            promocion.setImagenUrl(rs.getString("imagen_url"));
            promocion.setMinPersonas(rs.getInt("min_personas"));
            promocion.setMaxPersonas(rs.getInt("max_personas"));
            promocion.setCodigoPromocion(rs.getString("codigo_promocion"));
            promocion.setRequiereCodigo(rs.getBoolean("requiere_codigo"));
            return promocion;
        }
    };
    
    // Obtener todas las promociones
    public List<PromocionDto> findAll() {
        String sql = "EXEC sp_ObtenerTodasLasPromociones";
        return jdbcTemplate.query(sql, promocionRowMapper);
    }
    
    // Obtener promoción por ID
    public Optional<PromocionDto> findById(Long id) {
        String sql = "EXEC sp_ObtenerPromocionPorId ?";
        List<PromocionDto> promociones = jdbcTemplate.query(sql, promocionRowMapper, id);
        return promociones.isEmpty() ? Optional.empty() : Optional.of(promociones.get(0));
    }
    
    // Crear nueva promoción
    public PromocionDto save(PromocionDto promocionDto) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_CrearPromocion")
                .declareParameters(
                    new SqlParameter("restaurante_id", Types.BIGINT),
                    new SqlParameter("titulo", Types.NVARCHAR),
                    new SqlParameter("descripcion", Types.NVARCHAR),
                    new SqlParameter("descuento_porcentaje", Types.DECIMAL),
                    new SqlParameter("descuento_fijo", Types.DECIMAL),
                    new SqlParameter("fecha_inicio", Types.TIMESTAMP),
                    new SqlParameter("fecha_fin", Types.TIMESTAMP),
                    new SqlParameter("imagen_url", Types.NVARCHAR),
                    new SqlParameter("min_personas", Types.INTEGER),
                    new SqlParameter("max_personas", Types.INTEGER),
                    new SqlParameter("codigo_promocion", Types.NVARCHAR),
                    new SqlParameter("requiere_codigo", Types.BIT),
                    new SqlOutParameter("nuevo_id", Types.BIGINT)
                );
        
        Map<String, Object> result = jdbcCall.execute(
            promocionDto.getRestauranteId(),
            promocionDto.getTitulo(),
            promocionDto.getDescripcion(),
            promocionDto.getDescuentoPorcentaje(),
            promocionDto.getDescuentoFijo(),
            java.sql.Timestamp.valueOf(promocionDto.getFechaInicio()),
            java.sql.Timestamp.valueOf(promocionDto.getFechaFin()),
            promocionDto.getImagenUrl(),
            promocionDto.getMinPersonas(),
            promocionDto.getMaxPersonas(),
            promocionDto.getCodigoPromocion(),
            promocionDto.getRequiereCodigo()
        );
        
        Long nuevoId = (Long) result.get("nuevo_id");
        promocionDto.setId(nuevoId);
        return promocionDto;
    }
    
    // Actualizar promoción existente
    public boolean update(PromocionDto promocionDto) {
        String sql = """
            EXEC sp_ActualizarPromocion ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
            """;
        
        int rowsAffected = jdbcTemplate.update(sql,
            promocionDto.getId(),
            promocionDto.getRestauranteId(),
            promocionDto.getTitulo(),
            promocionDto.getDescripcion(),
            promocionDto.getDescuentoPorcentaje(),
            promocionDto.getDescuentoFijo(),
            java.sql.Timestamp.valueOf(promocionDto.getFechaInicio()),
            java.sql.Timestamp.valueOf(promocionDto.getFechaFin()),
            promocionDto.getImagenUrl(),
            promocionDto.getMinPersonas(),
            promocionDto.getMaxPersonas(),
            promocionDto.getCodigoPromocion(),
            promocionDto.getRequiereCodigo()
        );
        
        return rowsAffected > 0;
    }
    
    // Eliminar promoción
    public boolean deleteById(Long id) {
        String sql = "EXEC sp_EliminarPromocion ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }
    
    // Obtener promociones por restaurante
    public List<PromocionDto> findByRestauranteId(Long restauranteId) {
        String sql = "EXEC sp_ObtenerPromocionesPorRestaurante ?";
        return jdbcTemplate.query(sql, promocionRowMapper, restauranteId);
    }
    
    // Obtener promociones activas
    public List<PromocionDto> findByEstado(String estado) {
        String sql = "EXEC sp_ObtenerPromocionesPorEstado ?";
        return jdbcTemplate.query(sql, promocionRowMapper, estado);
    }
    
    // Obtener promociones vigentes (fecha actual entre inicio y fin)
    public List<PromocionDto> findVigentes() {
        String sql = "EXEC sp_ObtenerPromocionesVigentes";
        return jdbcTemplate.query(sql, promocionRowMapper);
    }
    
    // Cambiar estado de promoción
    public boolean updateEstado(Long id, String nuevoEstado) {
        String sql = "EXEC sp_CambiarEstadoPromocion ?, ?";
        int rowsAffected = jdbcTemplate.update(sql, id, nuevoEstado);
        return rowsAffected > 0;
    }
    
    // Validar código de promoción
    public Optional<PromocionDto> findByCodigoPromocion(String codigo) {
        String sql = "EXEC sp_ValidarCodigoPromocion ?";
        List<PromocionDto> promociones = jdbcTemplate.query(sql, promocionRowMapper, codigo);
        return promociones.isEmpty() ? Optional.empty() : Optional.of(promociones.get(0));
    }
    
    // Obtener promociones por rango de descuento
    public List<PromocionDto> findByDescuentoBetween(BigDecimal descuentoMin, BigDecimal descuentoMax) {
        String sql = "EXEC sp_ObtenerPromocionesPorRangoDescuento ?, ?";
        return jdbcTemplate.query(sql, promocionRowMapper, descuentoMin, descuentoMax);
    }
    
    // Verificar si existe una promoción
    public boolean existsById(Long id) {
        String sql = "EXEC sp_ExistePromocion ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
    
    // Contar total de promociones
    public long count() {
        String sql = "EXEC sp_ContarPromociones";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
    
    // Obtener estadísticas de promociones
    public Map<String, Object> getEstadisticas() {
        String sql = "EXEC sp_ObtenerEstadisticasPromociones";
        return jdbcTemplate.queryForMap(sql);
    }
}
