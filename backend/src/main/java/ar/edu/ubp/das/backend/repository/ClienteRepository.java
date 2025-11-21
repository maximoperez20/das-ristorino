package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.components.SimpleJdbcCallFactory;
import ar.edu.ubp.das.backend.dto.LocalidadClienteDto;
import ar.edu.ubp.das.backend.dto.UsuarioDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository para gestión de clientes
 */
@Repository
public class ClienteRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private SimpleJdbcCallFactory jdbcCallFactory;
    
    // RowMapper para convertir ResultSet a UsuarioDto
    private final RowMapper<UsuarioDto> clienteRowMapper = new RowMapper<UsuarioDto>() {
        @Override
        public UsuarioDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            UsuarioDto usuario = new UsuarioDto();
            usuario.setNroCliente(rs.getString("nro_cliente"));
            usuario.setApellido(rs.getString("apellido"));
            usuario.setNombre(rs.getString("nombre"));
            usuario.setClave(rs.getString("clave"));
            usuario.setCorreo(rs.getString("correo"));
            usuario.setTelefonos(rs.getString("telefonos"));
            usuario.setNroLocalidad(rs.getString("nro_localidad"));
            usuario.setHabilitado(rs.getBoolean("habilitado"));
            return usuario;
        }
    };
    
    /**
     * Guardar un nuevo cliente
     */
    public UsuarioDto save(UsuarioDto usuario) {
        String sql = "EXEC sp_CrearCliente ?, ?, ?, ?, ?, ?";
        
        // El stored procedure retorna el cliente creado
        List<UsuarioDto> clientes = jdbcTemplate.query(sql, clienteRowMapper,
                usuario.getApellido(),
                usuario.getNombre(),
                usuario.getClave(),
                usuario.getCorreo(),
                usuario.getTelefonos(),
                usuario.getNroLocalidad()
        );
        
        return clientes.isEmpty() ? null : clientes.get(0);
    }
    
    /**
     * Buscar cliente por correo
     */
    public UsuarioDto findByCorreo(String correo) {
        String sql = "SELECT nro_cliente, apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado " +
                     "FROM clientes WHERE correo = ?";
        List<UsuarioDto> clientes = jdbcTemplate.query(sql, clienteRowMapper, correo);
        return clientes.isEmpty() ? null : clientes.get(0);
    }
    
    /**
     * Verificar si existe un cliente con el correo dado
     */
    public boolean existsByCorreo(String correo) {
        String sql = "SELECT COUNT(*) FROM clientes WHERE correo = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, correo);
        return count != null && count > 0;
    }
    
    /**
     * Obtener el nombre de la localidad del cliente por su nroCliente
     * Usa stored procedure sp_ObtenerLocalidadPorNroCliente
     * @param nroCliente UUID del cliente
     * @return Nombre de la localidad o null si no se encuentra
     */
    public String obtenerLocalidadPorNroCliente(String nroCliente) {
        try {
            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("nroCliente", nroCliente);
            
            List<LocalidadClienteDto> resultados = jdbcCallFactory.executeQuery(
                    "sp_ObtenerLocalidadPorNroCliente", "dbo", params, "localidad", LocalidadClienteDto.class);
            
            return resultados.isEmpty() || resultados.get(0).getLocalidad() == null 
                    ? null 
                    : resultados.get(0).getLocalidad();
        } catch (Exception e) {
            return null;
        }
    }
    
    public UsuarioDto findByNroCliente(String nroCliente) {
        String sql = "SELECT nro_cliente, apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado " +
                     "FROM clientes WHERE nro_cliente = ?";
        List<UsuarioDto> clientes = jdbcTemplate.query(sql, clienteRowMapper, nroCliente);
        return clientes.isEmpty() ? null : clientes.get(0);
    }
}
