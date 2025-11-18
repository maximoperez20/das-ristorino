package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.ClickNoNotificadoDto;
import ar.edu.ubp.das.backend.dto.ClickResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ClickRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final RowMapper<ClickResponseDto> clickRowMapper = new RowMapper<ClickResponseDto>() {
        @Override
        public ClickResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            ClickResponseDto click = new ClickResponseDto();
            click.setNroClick(rs.getString("nro_click"));
            click.setNroRestaurante(rs.getString("nro_restaurante"));
            click.setNroIdioma(rs.getInt("nro_idioma"));
            click.setNroContenido(rs.getString("nro_contenido"));
            click.setFechaHoraRegistro(rs.getTimestamp("fecha_hora_registro").toLocalDateTime());
            click.setNroCliente(rs.getString("nro_cliente"));
            
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
    
    public ClickResponseDto registrarClick(String nroRestaurante, Integer nroIdioma, String nroContenido, String nroCliente) {
        String sql = "EXEC sp_RegistrarClickPromocion ?, ?, ?, ?";
        
        List<ClickResponseDto> clicks = jdbcTemplate.query(sql, clickRowMapper, 
            nroRestaurante, nroIdioma, nroContenido, nroCliente);
        
        return clicks.isEmpty() ? null : clicks.get(0);
    }
    
    public List<ClickNoNotificadoDto> obtenerClicksNoNotificados() {
        String sql = "EXEC sp_ObtenerClicksNoNotificados";
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ClickNoNotificadoDto dto = new ClickNoNotificadoDto();
            dto.setNroRestaurante(rs.getString("nro_restaurante"));
            dto.setNroIdioma(rs.getInt("nro_idioma"));
            dto.setNroContenido(rs.getString("nro_contenido"));
            dto.setNroClick(rs.getString("nro_click"));
            
            java.sql.Timestamp timestamp = rs.getTimestamp("fecha_hora_registro");
            if (timestamp != null) {
                dto.setFechaHoraRegistro(timestamp.toLocalDateTime());
            }
            
            dto.setNroCliente(rs.getString("nro_cliente"));
            
            BigDecimal costoClick = rs.getBigDecimal("costo_click");
            if (!rs.wasNull()) {
                dto.setCostoClick(costoClick);
            }
            
            dto.setCodContenidoRestaurante(rs.getString("cod_contenido_restaurante"));
            
            return dto;
        });
    }
    
    public void marcarClickComoNotificado(String nroRestaurante, Integer nroIdioma, String nroContenido, String nroClick) {
        String sql = "EXEC sp_MarcarClickComoNotificado ?, ?, ?, ?";
        
        int filasActualizadas = jdbcTemplate.update(sql, nroRestaurante, nroIdioma, nroContenido, nroClick);
        
        if (filasActualizadas == 0) {
            throw new RuntimeException(String.format(
                "No se actualizó ningún registro. Parámetros: nroRestaurante=%s, nroIdioma=%d, nroContenido=%s, nroClick=%s",
                nroRestaurante, nroIdioma, nroContenido, nroClick));
        }
    }
}

