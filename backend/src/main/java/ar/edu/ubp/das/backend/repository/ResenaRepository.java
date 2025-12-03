package ar.edu.ubp.das.backend.repository;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ar.edu.ubp.das.backend.dto.ResenaDto;

import java.util.List;

@Repository
public class ResenaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<ResenaDto> resenaRowMapper = (rs, rowNum) -> {
        ResenaDto resena = new ResenaDto();
        
        resena.setNombreCompleto(rs.getString("nombreCompleto"));
        resena.setCalificacion(rs.getInt("calificacion"));
        resena.setComentario(rs.getString("comentario"));
        
        java.sql.Timestamp timestamp = rs.getTimestamp("fecha_hora_registro");
        if (timestamp != null) {
            resena.setFechaHora(timestamp.toLocalDateTime().toString());
        }
        
        return resena;
    };

    public List<ResenaDto> obtenerResenas(String nroRestaurante, String nroSucursal){
        String sql = "EXEC sp_ObtenerResenas_sucursales ?, ?";
        return jdbcTemplate.query(sql, resenaRowMapper, nroRestaurante, nroSucursal);
    }

    public void insertarResena(String nroRestaurante, String nroSucursal, String nroCliente, Integer calificacion, String comentario) {
    String sql = "EXEC sp_InsertarResena_sucursal ?, ?, ?, ?, ?";
    
    jdbcTemplate.update(
        sql,
        nroRestaurante,
        nroSucursal,
        nroCliente,
        calificacion,
        comentario
    );
}



    
}
