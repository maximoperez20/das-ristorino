package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.PromocionDto;
import ar.edu.ubp.das.backend.dto.RestauranteDto;
import ar.edu.ubp.das.backend.dto.RestauranteDetalleDto;
import ar.edu.ubp.das.backend.dto.SucursalDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
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
            // El id ya no se devuelve desde los stored procedures, solo nro_restaurante
            // Verificamos si existe la columna id antes de intentar leerla
            try {
                rs.findColumn("id");
                restaurante.setId(rs.getLong("id"));
            } catch (SQLException e) {
                // Campo id no existe en el ResultSet, dejarlo null
                restaurante.setId(null);
            }
            restaurante.setNroRestaurante(rs.getString("nro_restaurante"));
            restaurante.setNombre(rs.getString("nombre"));
            restaurante.setDireccion(rs.getString("direccion"));
            restaurante.setTelefono(rs.getString("telefono"));
            restaurante.setEmail(rs.getString("email"));
            restaurante.setCapacidad(rs.getInt("capacidad"));
            restaurante.setHorarioApertura(rs.getTime("horario_apertura").toLocalTime());
            restaurante.setHorarioCierre(rs.getTime("horario_cierre").toLocalTime());
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
     * @param nroRestaurante UUID del restaurante
     * @param nroIdioma Número de idioma (0=es-AR, 1=en-US). Si es null, usa 0 por defecto.
     */
    public Optional<RestauranteDto> findById(String nroRestaurante, Integer nroIdioma) {
        String sql = "EXEC sp_ObtenerRestaurantePorId ?, ?";
        int idioma = nroIdioma != null ? nroIdioma : 0;
        List<RestauranteDto> restaurantes = jdbcTemplate.query(sql, restauranteRowMapper, nroRestaurante, idioma);
        return restaurantes.isEmpty() ? Optional.empty() : Optional.of(restaurantes.get(0));
    }
    
    /**
     * Obtener restaurante por UUID (nroRestaurante) - versión sin idioma (usa default 0)
     */
    public Optional<RestauranteDto> findById(String nroRestaurante) {
        return findById(nroRestaurante, 0);
    }
    
    /**
     * Obtener detalle completo de un restaurante (para Requerimiento 11)
     * @param nroRestaurante UUID del restaurante
     * @param nroIdioma Número de idioma (0=es-AR, 1=en-US)
     */
    public Optional<RestauranteDetalleDto> findDetalleById(String nroRestaurante, Integer nroIdioma) {
        String sql = "{call sp_ObtenerRestaurantePorId(?, ?)}";
        
        return jdbcTemplate.execute(sql, (CallableStatementCallback<Optional<RestauranteDetalleDto>>) cs -> {
            cs.setString(1, nroRestaurante);
            cs.setInt(2, nroIdioma != null ? nroIdioma : 0);
            
            boolean hasResults = cs.execute();
            
            RestauranteDetalleDto detalle = new RestauranteDetalleDto();
            List<PromocionDto> promociones = new ArrayList<>();
            
            // Leer primer result set: datos del restaurante
            if (hasResults) {
                try (ResultSet rs = cs.getResultSet()) {
                    if (rs.next()) {
                        RestauranteDto restaurante = restauranteRowMapper.mapRow(rs, 1);
                        
                        // Mapear datos básicos
                        detalle.setId(restaurante.getId());
                        detalle.setNombre(restaurante.getNombre());
                        detalle.setDireccion(restaurante.getDireccion());
                        detalle.setTelefono(restaurante.getTelefono());
                        detalle.setEmail(restaurante.getEmail());
                        detalle.setCapacidad(restaurante.getCapacidad());
                        detalle.setHorarioApertura(restaurante.getHorarioApertura());
                        detalle.setHorarioCierre(restaurante.getHorarioCierre());
                        detalle.setCalificacion(restaurante.getCalificacion());
                        detalle.setActivo(restaurante.getActivo());
                        detalle.setDiasAtencion(restaurante.getDiasAtencion());
                    } else {
                        // No se encontró el restaurante
                        return Optional.empty();
                    }
                }
            }
            
            // Leer segundo result set: promociones
            if (cs.getMoreResults()) {
                try (ResultSet rs = cs.getResultSet()) {
                    int rowNum = 0;
                    while (rs.next()) {
                        promociones.add(promocionRowMapper.mapRow(rs, rowNum++));
                    }
                }
            }
            
            // Si no se encontró el restaurante, retornar empty
            if (detalle.getNombre() == null) {
                return Optional.empty();
            }
            
            // Obtener tipos de cocina
            List<String> tiposCocina = obtenerTiposCocina(nroRestaurante, nroIdioma);
            detalle.setTipoCocina(tiposCocina);
            
            // Obtener sucursales
            List<SucursalDto> sucursales = obtenerSucursales(nroRestaurante);
            detalle.setSucursales(sucursales);
            
            // Asignar promociones obtenidas del stored procedure
            detalle.setPromociones(promociones);
            
            // Imágenes (por ahora vacío, se puede implementar después)
            detalle.setImagenes(new ArrayList<>());
            
            return Optional.of(detalle);
        });
    }
    
    /**
     * Obtener tipos de cocina de un restaurante
     * @param nroRestaurante UUID del restaurante
     * @param nroIdioma Número de idioma (0=es-AR, 1=en-US)
     */
    private List<String> obtenerTiposCocina(String nroRestaurante, Integer nroIdioma) {
        String sql = "EXEC sp_ObtenerTiposCocinaPorRestaurante ?, ?";
        return jdbcTemplate.queryForList(sql, String.class, nroRestaurante, nroIdioma);
    }
    
    /**
     * Obtener sucursales de un restaurante
     */
    public List<SucursalDto> obtenerSucursales(String nroRestaurante) {
        String sql = "EXEC sp_ObtenerSucursalesPorRestaurante ?";
        return jdbcTemplate.query(sql, sucursalRowMapper, nroRestaurante);
    }
    
    /**
     * Buscar restaurantes por nombre (búsqueda parcial)
     */
    public List<RestauranteDto> findByNombreContaining(String nombre) {
        String sql = "EXEC sp_BuscarRestaurantesPorNombre ?";
        return jdbcTemplate.query(sql, restauranteRowMapper, "%" + nombre + "%");
    }

    /**
     * Buscar restaurantes usando análisis NLP.
     * 
     * @param parametros DTO con todos los parámetros de búsqueda
     * @return Lista de restaurantes que coinciden con los criterios
     */
    public List<RestauranteDto> buscarPorNLP(ar.edu.ubp.das.backend.dto.BusquedaNLPParametrosDto parametros) {
        String sql = "EXEC sp_BuscarRestaurantesPorNLP ?, ?, ?, ?, ?, ?, ?";
        
        // Usar el método helper del DTO para convertir a formato del SP
        Object[] params = parametros.toStoredProcedureParameters();
        
        // Log de parámetros que se envían al SP
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RestauranteRepository.class);
        logger.info("🔎 Ejecutando sp_BuscarRestaurantesPorNLP con parámetros:");
        logger.info("   - @tiposComida: '{}'", params[0]);
        logger.info("   - @barrios: '{}'", params[1]);
        logger.info("   - @localidades: '{}'", params[2]);
        logger.info("   - @ambientes: '{}'", params[3]);
        logger.info("   - @rangosPrecio: '{}'", params[4]);
        logger.info("   - @palabrasClave: '{}'", params[5]);
        logger.info("   - @nroCliente: '{}'", params[6]);
        
        List<RestauranteDto> resultados = jdbcTemplate.query(sql, restauranteRowMapper, params);
        
        logger.info("📊 SP devolvió {} restaurantes", resultados.size());
        
        return resultados;
    }
    
    /**
     * Método sobrecargado para mantener compatibilidad con código existente.
     * @deprecated Usar buscarPorNLP(BusquedaNLPParametrosDto) en su lugar
     */
    @Deprecated
    public List<RestauranteDto> buscarPorNLP(List<String> tiposComida, String barrio, 
                                            String localidad, String ambiente, 
                                            String rangoPrecio, List<String> palabrasClave,
                                            String nroCliente) {
        ar.edu.ubp.das.backend.dto.BusquedaNLPParametrosDto parametros = 
            new ar.edu.ubp.das.backend.dto.BusquedaNLPParametrosDto(
                tiposComida, barrio, localidad, ambiente, rangoPrecio, palabrasClave, nroCliente);
        return buscarPorNLP(parametros);
    }
    
    /**
     * Obtiene sugerencias de restaurantes basadas en preferencias del usuario o restaurantes populares.
     * 
     * @param excluirRestaurantes Lista de restaurantes a excluir (los que ya están en resultados exactos)
     * @param nroCliente UUID del cliente autenticado (opcional, si está presente usa sus preferencias)
     * @param limite Cantidad máxima de sugerencias a devolver
     * @return Lista de restaurantes sugeridos
     */
    public List<RestauranteDto> obtenerSugerencias(List<RestauranteDto> excluirRestaurantes, String nroCliente, int limite) {
        // Construir lista de UUIDs a excluir
        String excluirIds = null;
        if (excluirRestaurantes != null && !excluirRestaurantes.isEmpty()) {
            excluirIds = excluirRestaurantes.stream()
                .map(RestauranteDto::getNroRestaurante)
                .filter(id -> id != null)
                .collect(java.util.stream.Collectors.joining(","));
        }
        
        String sql = "EXEC sp_ObtenerSugerenciasRestaurantes ?, ?, ?";
        return jdbcTemplate.query(sql, restauranteRowMapper, excluirIds, nroCliente, limite);
    }
    
    /**
     * Verificar si un restaurante existe por su nroRestaurante
     * @param nroRestaurante UUID del restaurante
     * @return true si existe, false en caso contrario
     */
    public boolean existeRestaurante(String nroRestaurante) {
        String sql = "SELECT COUNT(*) FROM restaurantes WHERE nro_restaurante = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, nroRestaurante);
        return count != null && count > 0;
    }
    
    /**
     * Verificar si una sucursal existe y pertenece al restaurante especificado
     * @param nroRestaurante UUID del restaurante
     * @param nroSucursal UUID de la sucursal (ID interno de das-ristorino)
     * @return true si existe y pertenece al restaurante, false en caso contrario
     */
    public boolean existeSucursal(String nroRestaurante, String nroSucursal) {
        String sql = "SELECT COUNT(*) FROM sucursales_restaurantes " +
                     "WHERE nro_restaurante = ? AND nro_sucursal = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, nroRestaurante, nroSucursal);
        return count != null && count > 0;
    }
    
    /**
     * Obtener el cod_sucursal_restaurante (ID de la sucursal en das-restaurante-soap)
     * a partir del nro_sucursal (ID interno de das-ristorino)
     * @param nroRestaurante UUID del restaurante
     * @param nroSucursal UUID de la sucursal (ID interno de das-ristorino)
     * @return cod_sucursal_restaurante (ID en das-restaurante-soap) o null si no existe
     */
    public String obtenerCodSucursalRestaurante(String nroRestaurante, String nroSucursal) {
        String sql = "SELECT cod_sucursal_restaurante FROM sucursales_restaurantes " +
                     "WHERE nro_restaurante = ? AND nro_sucursal = ?";
        try {
            List<String> result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("cod_sucursal_restaurante"),
                nroRestaurante,
                nroSucursal
            );
            return result.isEmpty() ? null : result.get(0);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Obtener el cod_zona_restaurante (código externo del SOAP) basado en el cod_zona interno de Ristorino
     * Útil cuando necesitamos comunicarnos con el SOAP y requerimos el código externo
     */
    public String obtenerCodZonaRestaurante(String nroRestaurante, String nroSucursal, String codZona) {
        String sql = "SELECT cod_zona_restaurante FROM zonas_sucursales_restaurantes " +
                     "WHERE nro_restaurante = ? AND nro_sucursal = ? AND cod_zona = ? AND habilitada = 1";
        try {
            List<String> result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("cod_zona_restaurante"),
                nroRestaurante,
                nroSucursal,
                codZona
            );
            if (result.isEmpty()) {
                throw new RuntimeException("Zona no encontrada para cod_zona: " + codZona);
            }
            String codZonaRestaurante = result.get(0);
            if (codZonaRestaurante == null || codZonaRestaurante.trim().isEmpty()) {
                throw new RuntimeException("La zona no tiene cod_zona_restaurante configurado para cod_zona: " + codZona);
            }
            return codZonaRestaurante;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener cod_zona_restaurante: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener el cod_zona interno de Ristorino basado en el cod_zona_restaurante (código externo del SOAP)
     * Útil cuando recibimos el código externo del SOAP y necesitamos el código interno de Ristorino
     * @param nroRestaurante UUID del restaurante
     * @param nroSucursal UUID de la sucursal (ID interno de das-ristorino)
     * @param codZonaRestaurante Código de zona en el sistema del restaurante (SOAP)
     * @return cod_zona interno de Ristorino o null si no se encuentra
     */
    public String obtenerCodZonaInterno(String nroRestaurante, String nroSucursal, String codZonaRestaurante) {
        String sql = "SELECT cod_zona FROM zonas_sucursales_restaurantes " +
                     "WHERE nro_restaurante = ? AND nro_sucursal = ? AND cod_zona_restaurante = ? AND habilitada = 1";
        try {
            List<String> result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("cod_zona"),
                nroRestaurante,
                nroSucursal,
                codZonaRestaurante
            );
            return result.isEmpty() ? null : result.get(0);
        } catch (Exception e) {
            return null;
        }
    }
}
