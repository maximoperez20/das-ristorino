package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.LocalidadDto;
import ar.edu.ubp.das.backend.components.SimpleJdbcCallFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para obtener localidades
 */
@Repository
public class LocalidadRepository {

    @Autowired
    private SimpleJdbcCallFactory jdbcCallFactory;

    /**
     * Obtiene todas las localidades con su provincia
     */
    public List<LocalidadDto> findAll() {
        return jdbcCallFactory.executeQuery("sp_ObtenerTodasLasLocalidades", "dbo", "localidades", LocalidadDto.class);
    }
}
