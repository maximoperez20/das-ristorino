package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.LocalidadDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository para obtener localidades
 */
@Repository
public class LocalidadRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<LocalidadDto> localidadRowMapper = new RowMapper<LocalidadDto>() {
        @Override
        public LocalidadDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            LocalidadDto localidad = new LocalidadDto();
            localidad.setNroLocalidad(rs.getString("nro_localidad"));
            localidad.setNombre(rs.getString("nom_localidad"));
            localidad.setProvincia(rs.getString("nom_provincia"));
            return localidad;
        }
    };

    /**
     * Obtiene todas las localidades con su provincia
     */
    public List<LocalidadDto> findAll() {
        String sql = "SELECT l.nro_localidad, l.nom_localidad, p.nom_provincia " +
                     "FROM localidades l " +
                     "INNER JOIN provincias p ON l.cod_provincia = p.cod_provincia " +
                     "ORDER BY p.nom_provincia, l.nom_localidad";
        return jdbcTemplate.query(sql, localidadRowMapper);
    }
}
