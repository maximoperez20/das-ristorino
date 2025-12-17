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
            
            // UUIDs (claves reales de la BD)
            promocion.setNroRestaurante(rs.getString("nro_restaurante"));
            promocion.setNroIdioma(rs.getInt("nro_idioma"));
            promocion.setNroContenido(rs.getString("nro_contenido"));
            
            promocion.setTitulo(rs.getString("titulo"));
            promocion.setDescripcion(rs.getString("descripcion"));
            promocion.setDescuentoPorcentaje(rs.getBigDecimal("descuento_porcentaje"));
            promocion.setDescuentoFijo(rs.getBigDecimal("descuento_fijo"));
            
            // Manejar fechas que pueden ser NULL
            java.sql.Timestamp fechaInicio = rs.getTimestamp("fecha_inicio");
            if (fechaInicio != null) {
                promocion.setFechaInicio(fechaInicio.toLocalDateTime());
            }
            
            java.sql.Timestamp fechaFin = rs.getTimestamp("fecha_fin");
            if (fechaFin != null) {
                promocion.setFechaFin(fechaFin.toLocalDateTime());
            }
            
            promocion.setEstado(rs.getString("estado"));
            // imagen_url puede ser NULL
            String imagenUrl = rs.getString("imagen_url");
            if (!rs.wasNull()) {
                promocion.setImagenUrl(imagenUrl);
            }
            // min_personas y max_personas pueden ser NULL
            int minPersonas = rs.getInt("min_personas");
            promocion.setMinPersonas(rs.wasNull() ? null : minPersonas);
            int maxPersonas = rs.getInt("max_personas");
            promocion.setMaxPersonas(rs.wasNull() ? null : maxPersonas);
            promocion.setCodigoPromocion(rs.getString("codigo_promocion"));
            // requiere_codigo puede ser NULL
            boolean requiereCodigo = rs.getBoolean("requiere_codigo");
            promocion.setRequiereCodigo(rs.wasNull() ? false : requiereCodigo);
            // proposito_corto puede ser NULL
            String propositoCorto = rs.getString("proposito_corto");
            if (!rs.wasNull()) {
                promocion.setPropositoCorto(propositoCorto);
            }
            return promocion;
        }
    };
    
    /**
     * Obtener todas las promociones
     * @param nroIdioma Número de idioma (0=es-AR, 1=en-US)
     */
    public List<PromocionDto> findAll(Integer nroIdioma) {
        String sql = "EXEC sp_ObtenerTodasLasPromociones ?";
        return jdbcTemplate.query(sql, promocionRowMapper, nroIdioma);
    }
    
    /**
     * Obtener promoción por contenido UUID
     */
    public Optional<PromocionDto> findByContenidoId(String nroContenido) {
        String sql = "SELECT " +
                    "cr.nro_restaurante, cr.nro_idioma, cr.nro_contenido, " +
                    "LEFT(ISNULL(cr.contenido_promocional, cr.contenido_a_publicar), 100) AS titulo, " +
                    "ISNULL(cr.contenido_promocional, cr.contenido_a_publicar) AS descripcion, " +
                    "CAST(NULL AS DECIMAL(10,2)) AS descuento_porcentaje, " +
                    "CAST(NULL AS DECIMAL(10,2)) AS descuento_fijo, " +
                    "CAST(cr.fecha_ini_vigencia AS DATETIME2) AS fecha_inicio, " +
                    "CAST(cr.fecha_fin_vigencia AS DATETIME2) AS fecha_fin, " +
                    "CASE WHEN cr.fecha_ini_vigencia IS NOT NULL AND cr.fecha_fin_vigencia IS NOT NULL " +
                    "     AND CAST(GETDATE() AS DATE) BETWEEN cr.fecha_ini_vigencia AND cr.fecha_fin_vigencia " +
                    "     THEN 'ACTIVA' ELSE 'INACTIVA' END AS estado, " +
                    "cr.imagen_promocional AS imagen_url, " +
                    "CAST(NULL AS INT) AS min_personas, " +
                    "CAST(NULL AS INT) AS max_personas, " +
                    "cr.cod_contenido_restaurante AS codigo_promocion, " +
                    "CAST(0 AS BIT) AS requiere_codigo, " +
                    "cr.proposito_corto " +
                    "FROM contenidos_restaurantes cr WHERE cr.nro_contenido = ?";
        List<PromocionDto> promociones = jdbcTemplate.query(sql, promocionRowMapper, nroContenido);
        return promociones.isEmpty() ? Optional.empty() : Optional.of(promociones.get(0));
    }
}
