package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.PromocionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository para consulta de promociones
 * Solo operaciones de lectura
 */
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
    
    /**
     * Obtener todas las promociones
     */
    public List<PromocionDto> findAll() {
        String sql = "EXEC sp_ObtenerTodasLasPromociones";
        return jdbcTemplate.query(sql, promocionRowMapper);
    }
    
    /**
     * Obtener promoción por ID
     */
    public Optional<PromocionDto> findById(Long id) {
        String sql = "EXEC sp_ObtenerPromocionPorId ?";
        List<PromocionDto> promociones = jdbcTemplate.query(sql, promocionRowMapper, id);
        return promociones.isEmpty() ? Optional.empty() : Optional.of(promociones.get(0));
    }
}
