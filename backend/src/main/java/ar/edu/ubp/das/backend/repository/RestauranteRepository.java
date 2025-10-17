package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.RestauranteDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    
    // Obtener todos los restaurantes
    public List<RestauranteDto> findAll() {
        String sql = "EXEC sp_ObtenerTodosLosRestaurantes";
        return jdbcTemplate.query(sql, restauranteRowMapper);
    }
    
    // Obtener restaurante por ID
    public Optional<RestauranteDto> findById(Long id) {
        String sql = "EXEC sp_ObtenerRestaurantePorId ?";
        List<RestauranteDto> restaurantes = jdbcTemplate.query(sql, restauranteRowMapper, id);
        return restaurantes.isEmpty() ? Optional.empty() : Optional.of(restaurantes.get(0));
    }
    
    // Crear nuevo restaurante
    public RestauranteDto save(RestauranteDto restauranteDto) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_CrearRestaurante")
                .declareParameters(
                    new SqlParameter("nombre", Types.NVARCHAR),
                    new SqlParameter("direccion", Types.NVARCHAR),
                    new SqlParameter("telefono", Types.NVARCHAR),
                    new SqlParameter("email", Types.NVARCHAR),
                    new SqlParameter("capacidad", Types.INTEGER),
                    new SqlParameter("horario_apertura", Types.TIME),
                    new SqlParameter("horario_cierre", Types.TIME),
                    new SqlParameter("descripcion", Types.NVARCHAR),
                    new SqlParameter("categoria", Types.NVARCHAR),
                    new SqlParameter("imagen_url", Types.NVARCHAR),
                    new SqlOutParameter("nuevo_id", Types.BIGINT)
                );
        
        Map<String, Object> result = jdbcCall.execute(
            restauranteDto.getNombre(),
            restauranteDto.getDireccion(),
            restauranteDto.getTelefono(),
            restauranteDto.getEmail(),
            restauranteDto.getCapacidad(),
            java.sql.Time.valueOf(restauranteDto.getHorarioApertura()),
            java.sql.Time.valueOf(restauranteDto.getHorarioCierre()),
            restauranteDto.getDescripcion(),
            restauranteDto.getCategoria(),
            restauranteDto.getImagenUrl()
        );
        
        Long nuevoId = (Long) result.get("nuevo_id");
        restauranteDto.setId(nuevoId);
        return restauranteDto;
    }
    
    // Actualizar restaurante existente
    public boolean update(RestauranteDto restauranteDto) {
        String sql = """
            EXEC sp_ActualizarRestaurante ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
            """;
        
        int rowsAffected = jdbcTemplate.update(sql,
            restauranteDto.getId(),
            restauranteDto.getNombre(),
            restauranteDto.getDireccion(),
            restauranteDto.getTelefono(),
            restauranteDto.getEmail(),
            restauranteDto.getCapacidad(),
            java.sql.Time.valueOf(restauranteDto.getHorarioApertura()),
            java.sql.Time.valueOf(restauranteDto.getHorarioCierre()),
            restauranteDto.getDescripcion(),
            restauranteDto.getCategoria(),
            restauranteDto.getImagenUrl()
        );
        
        return rowsAffected > 0;
    }
    
    // Eliminar restaurante
    public boolean deleteById(Long id) {
        String sql = "EXEC sp_EliminarRestaurante ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }
    
    // Obtener restaurantes por categoría
    public List<RestauranteDto> findByCategoria(String categoria) {
        String sql = "EXEC sp_ObtenerRestaurantesPorCategoria ?";
        return jdbcTemplate.query(sql, restauranteRowMapper, categoria);
    }
    
    // Obtener restaurantes activos
    public List<RestauranteDto> findByActivoTrue() {
        String sql = "EXEC sp_ObtenerRestaurantesActivos";
        return jdbcTemplate.query(sql, restauranteRowMapper);
    }
    
    // Buscar restaurantes por nombre
    public List<RestauranteDto> findByNombreContaining(String nombre) {
        String sql = "EXEC sp_BuscarRestaurantesPorNombre ?";
        return jdbcTemplate.query(sql, restauranteRowMapper, "%" + nombre + "%");
    }
    
    // Obtener restaurantes por calificación mínima
    public List<RestauranteDto> findByCalificacionGreaterThanEqual(Double calificacionMinima) {
        String sql = "EXEC sp_ObtenerRestaurantesPorCalificacion ?";
        return jdbcTemplate.query(sql, restauranteRowMapper, calificacionMinima);
    }
    
    // Actualizar calificación de restaurante
    public boolean updateCalificacion(Long id, Double nuevaCalificacion) {
        String sql = "EXEC sp_ActualizarCalificacionRestaurante ?, ?";
        int rowsAffected = jdbcTemplate.update(sql, id, nuevaCalificacion);
        return rowsAffected > 0;
    }
    
    // Verificar si existe un restaurante
    public boolean existsById(Long id) {
        String sql = "EXEC sp_ExisteRestaurante ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
    
    // Contar total de restaurantes
    public long count() {
        String sql = "EXEC sp_ContarRestaurantes";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
    
    // Obtener estadísticas de restaurantes
    public Map<String, Object> getEstadisticas() {
        String sql = "EXEC sp_ObtenerEstadisticasRestaurantes";
        return jdbcTemplate.queryForMap(sql);
    }
}
