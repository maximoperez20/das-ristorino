package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.ClickResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository para gestión de clicks en promociones
 */
@Repository
public class ClickRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // RowMapper para convertir ResultSet a ClickResponseDto
    private final RowMapper<ClickResponseDto> clickRowMapper = new RowMapper<ClickResponseDto>() {
        @Override
        public ClickResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            ClickResponseDto click = new ClickResponseDto();
            click.setNroClick(rs.getString("nro_click"));
            click.setNroRestaurante(rs.getString("nro_restaurante"));
            click.setNroIdioma(rs.getString("nro_idioma"));
            click.setNroContenido(rs.getString("nro_contenido"));
            click.setFechaHoraRegistro(rs.getTimestamp("fecha_hora_registro").toLocalDateTime());
            click.setNroCliente(rs.getString("nro_cliente"));
            
            // costo_click puede ser NULL
            Double costoClick = rs.getDouble("costo_click");
            if (rs.wasNull()) {
                click.setCostoClick(null);
            } else {
                click.setCostoClick(costoClick);
            }
            
            click.setNotificado(rs.getBoolean("notificado"));
            return click;
        }
    };
    
    /**
     * Registrar un click en una promoción/contenido
     */
    public ClickResponseDto registrarClick(String nroRestaurante, String nroIdioma, String nroContenido, String nroCliente) {
        String sql = "EXEC sp_RegistrarClickPromocion ?, ?, ?, ?";
        
        List<ClickResponseDto> clicks = jdbcTemplate.query(sql, clickRowMapper, 
            nroRestaurante, nroIdioma, nroContenido, nroCliente);
        
        return clicks.isEmpty() ? null : clicks.get(0);
    }
}

