package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.RestauranteDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository para consulta de restaurantes
 * Solo operaciones de lectura
 */
@Repository
public class RestauranteRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // RowMapper para convertir ResultSet a RestauranteDto
    private final RowMapper<RestauranteDto> restauranteRowMapper = new RowMapper<RestauranteDto>() {
        @Override
        public RestauranteDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            RestauranteDto restaurante = new RestauranteDto();
            restaurante.setId(rs.getLong("id"));
            restaurante.setNombre(rs.getString("nombre"));
            restaurante.setDireccion(rs.getString("direccion"));
            restaurante.setTelefono(rs.getString("telefono"));
            restaurante.setEmail(rs.getString("email"));
            restaurante.setCapacidad(rs.getInt("capacidad"));
            restaurante.setHorarioApertura(rs.getTime("horario_apertura").toLocalTime());
            restaurante.setHorarioCierre(rs.getTime("horario_cierre").toLocalTime());
            restaurante.setDescripcion(rs.getString("descripcion"));
            restaurante.setCategoria(rs.getString("categoria"));
            restaurante.setCalificacion(rs.getDouble("calificacion"));
            restaurante.setActivo(rs.getBoolean("activo"));
            restaurante.setImagenUrl(rs.getString("imagen_url"));
            return restaurante;
        }
    };
    
    /**
     * Obtener todos los restaurantes
     */
    public List<RestauranteDto> findAll() {
        String sql = "EXEC sp_ObtenerTodosLosRestaurantes";
        return jdbcTemplate.query(sql, restauranteRowMapper);
    }
    
    /**
     * Obtener restaurante por UUID (nroRestaurante)
     */
    public Optional<RestauranteDto> findById(String nroRestaurante) {
        String sql = "EXEC sp_ObtenerRestaurantePorId ?";
        List<RestauranteDto> restaurantes = jdbcTemplate.query(sql, restauranteRowMapper, nroRestaurante);
        return restaurantes.isEmpty() ? Optional.empty() : Optional.of(restaurantes.get(0));
    }
    
    /**
     * Buscar restaurantes por nombre (búsqueda parcial)
     */
    public List<RestauranteDto> findByNombreContaining(String nombre) {
        String sql = "EXEC sp_BuscarRestaurantesPorNombre ?";
        return jdbcTemplate.query(sql, restauranteRowMapper, "%" + nombre + "%");
    }
}
