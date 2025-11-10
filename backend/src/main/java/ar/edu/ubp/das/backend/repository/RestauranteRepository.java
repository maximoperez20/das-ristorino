package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.PromocionDto;
import ar.edu.ubp.das.backend.dto.RestauranteDto;
import ar.edu.ubp.das.backend.dto.RestauranteDetalleDto;
import ar.edu.ubp.das.backend.dto.SucursalDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    
    // RowMapper para SucursalDto
    private final RowMapper<SucursalDto> sucursalRowMapper = new RowMapper<SucursalDto>() {
        @Override
        public SucursalDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            SucursalDto sucursal = new SucursalDto();
            sucursal.setNroRestaurante(rs.getString("nro_restaurante"));
            sucursal.setNroSucursal(rs.getString("nro_sucursal"));
            sucursal.setNombre(rs.getString("nombre"));
            sucursal.setDireccion(rs.getString("direccion"));
            sucursal.setLocalidad(rs.getString("localidad"));
            sucursal.setProvincia(rs.getString("provincia"));
            sucursal.setCodigoPostal(rs.getString("codigo_postal"));
            sucursal.setTelefonos(rs.getString("telefonos"));
            sucursal.setCapacidad(rs.getInt("capacidad"));
            sucursal.setMinToleranciaReserva(rs.getInt("min_tolerancia_reserva"));
            return sucursal;
        }
    };
    
    // RowMapper para PromocionDto (usado en detalle)
    private final RowMapper<PromocionDto> promocionRowMapper = new RowMapper<PromocionDto>() {
        @Override
        public PromocionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            PromocionDto promocion = new PromocionDto();
            promocion.setNroRestaurante(rs.getString("nro_restaurante"));
            promocion.setNroIdioma(rs.getInt("nro_idioma"));
            promocion.setNroContenido(rs.getString("nro_contenido"));
            promocion.setTitulo(rs.getString("titulo"));
            promocion.setDescripcion(rs.getString("descripcion"));
            promocion.setDescuentoPorcentaje(rs.getBigDecimal("descuento_porcentaje"));
            promocion.setDescuentoFijo(rs.getBigDecimal("descuento_fijo"));
            
            java.sql.Timestamp fechaInicio = rs.getTimestamp("fecha_inicio");
            if (fechaInicio != null) {
                promocion.setFechaInicio(fechaInicio.toLocalDateTime());
            }
            
            java.sql.Timestamp fechaFin = rs.getTimestamp("fecha_fin");
            if (fechaFin != null) {
                promocion.setFechaFin(fechaFin.toLocalDateTime());
            }
            
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
     * Obtener detalle completo de un restaurante (para Requerimiento 11)
     */
    public Optional<RestauranteDetalleDto> findDetalleById(String nroRestaurante) {
        // Obtener datos básicos
        RestauranteDto restaurante = findById(nroRestaurante)
                .orElse(null);
        
        if (restaurante == null) {
            return Optional.empty();
        }
        RestauranteDetalleDto detalle = new RestauranteDetalleDto();
        
        // Mapear datos básicos
        detalle.setId(restaurante.getId());
        detalle.setNombre(restaurante.getNombre());
        detalle.setDireccion(restaurante.getDireccion());
        detalle.setTelefono(restaurante.getTelefono());
        detalle.setEmail(restaurante.getEmail());
        detalle.setCapacidad(restaurante.getCapacidad());
        detalle.setHorarioApertura(restaurante.getHorarioApertura());
        detalle.setHorarioCierre(restaurante.getHorarioCierre());
        detalle.setDescripcion(restaurante.getDescripcion());
        detalle.setCalificacion(restaurante.getCalificacion());
        detalle.setActivo(restaurante.getActivo());
        detalle.setDiasAtencion(restaurante.getDiasAtencion());
        
        // Obtener tipos de cocina
        List<String> tiposCocina = obtenerTiposCocina(nroRestaurante);
        detalle.setTipoCocina(tiposCocina);
        
        // Obtener sucursales
        List<SucursalDto> sucursales = obtenerSucursales(nroRestaurante);
        detalle.setSucursales(sucursales);
        
        // Obtener promociones vigentes
        List<PromocionDto> promociones = obtenerPromociones(nroRestaurante);
        detalle.setPromociones(promociones);
        
        // Imágenes (por ahora vacío, se puede implementar después)
        detalle.setImagenes(new ArrayList<>());
        
        return Optional.of(detalle);
    }
    
    /**
     * Obtener tipos de cocina de un restaurante
     */
    private List<String> obtenerTiposCocina(String nroRestaurante) {
        String sql = "EXEC sp_ObtenerTiposCocinaPorRestaurante ?";
        return jdbcTemplate.queryForList(sql, String.class, nroRestaurante);
    }
    
    /**
     * Obtener sucursales de un restaurante
     */
    public List<SucursalDto> obtenerSucursales(String nroRestaurante) {
        String sql = "EXEC sp_ObtenerSucursalesPorRestaurante ?";
        return jdbcTemplate.query(sql, sucursalRowMapper, nroRestaurante);
    }
    
    /**
     * Obtener promociones vigentes de un restaurante
     */
    private List<PromocionDto> obtenerPromociones(String nroRestaurante) {
        String sql = "EXEC sp_ObtenerPromocionesPorRestaurante ?";
        return jdbcTemplate.query(sql, promocionRowMapper, nroRestaurante);
    }
    
    /**
     * Buscar restaurantes por nombre (búsqueda parcial)
     */
    public List<RestauranteDto> findByNombreContaining(String nombre) {
        String sql = "EXEC sp_BuscarRestaurantesPorNombre ?";
        return jdbcTemplate.query(sql, restauranteRowMapper, "%" + nombre + "%");
    }

    /**
     * Buscar restaurantes usando análisis NLP
     * 
     * @param tiposComida Lista de tipos de comida (puede ser null)
     * @param barrio Barrio (puede ser null)
     * @param localidad Localidad (puede ser null)
     * @param ambiente Ambiente (puede ser null)
     * @param rangoPrecio Rango de precio (puede ser null)
     * @param palabrasClave Lista de palabras clave para búsqueda en nombre/descripción (puede ser null)
     * @return Lista de restaurantes que coinciden con los criterios
     */
    public List<RestauranteDto> buscarPorNLP(List<String> tiposComida, String barrio, 
                                             String localidad, String ambiente, 
                                             String rangoPrecio, List<String> palabrasClave) {
        String sql = "EXEC sp_BuscarRestaurantesPorNLP ?, ?, ?, ?, ?, ?";
        
        // Convertir listas a strings separados por comas
        String tiposComidaStr = tiposComida != null && !tiposComida.isEmpty() 
            ? String.join(",", tiposComida) : null;
        String barrioStr = barrio != null && !barrio.isEmpty() ? barrio : null;
        String localidadStr = localidad != null && !localidad.isEmpty() ? localidad : null;
        String ambienteStr = ambiente != null && !ambiente.isEmpty() ? ambiente : null;
        String rangoPrecioStr = rangoPrecio != null && !rangoPrecio.isEmpty() ? rangoPrecio : null;
        String palabrasClaveStr = palabrasClave != null && !palabrasClave.isEmpty() 
            ? String.join(",", palabrasClave) : null;
        
        return jdbcTemplate.query(sql, restauranteRowMapper, 
            tiposComidaStr, barrioStr, localidadStr, ambienteStr, rangoPrecioStr, palabrasClaveStr);
    }
}
