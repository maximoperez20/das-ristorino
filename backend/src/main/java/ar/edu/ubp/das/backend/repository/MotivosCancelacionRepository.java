package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.MotivosCancelacionDto;


import ar.edu.ubp.das.backend.dto.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;



import ar.edu.ubp.das.backend.components.SimpleJdbcCallFactory;


@Repository
public class MotivosCancelacionRepository {

    @Autowired
    private SimpleJdbcCallFactory jdbcCallFactory;
    
    public List<MotivosCancelacionDto> getAllMotivosCancelacion() {
        SqlParameterSource params = new MapSqlParameterSource();
        return jdbcCallFactory.executeQuery("sp_get_motivos_cancelacion", "dbo", params, "motivos_cancelacion", MotivosCancelacionDto.class);
    }
}
