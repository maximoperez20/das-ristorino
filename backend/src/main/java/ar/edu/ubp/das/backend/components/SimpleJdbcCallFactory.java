package ar.edu.ubp.das.backend.components;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SimpleJdbcCallFactory {
    
    private final JdbcTemplate jdbcTemplate;
    
    public SimpleJdbcCallFactory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public <T> Map<String, Object> executeQueryWithOutputs(String procedureName, String schemaName, SqlParameterSource params, String resultSetName, Class<T> mappedClass) {
        SimpleJdbcCall jdbcCall = createCall(procedureName, schemaName)
                .returningResultSet(resultSetName, BeanPropertyRowMapper.newInstance(mappedClass));
        return jdbcCall.execute(params);
    }
    
    @SuppressWarnings("unchecked")
    public <T> List<T> executeQuery(String procedureName, String schemaName, SqlParameterSource params, String resultSetName, Class<T> mappedClass) {
        Map<String, Object> out = executeQueryWithOutputs(procedureName, schemaName, params, resultSetName, mappedClass);
        return (List<T>) out.get(resultSetName);
    }
    
    public <T> List<T> executeQuery(String procedureName, String schemaName, String resultSetName, Class<T> mappedClass) {
        return executeQuery(procedureName, schemaName, new MapSqlParameterSource(), resultSetName, mappedClass);
    }
    
    public <T> Map<String, Object> executeWithOutputs(String procedureName, String schemaName, SqlParameterSource params) {
        SimpleJdbcCall jdbcCall = createCall(procedureName, schemaName);
        return jdbcCall.execute(params);
    }
    
    public void execute(String procedureName, String schemaName, SqlParameterSource params) {
        executeWithOutputs(procedureName, schemaName, params);
    }
    
    public void execute(String procedureName, String schemaName) {
        execute(procedureName, schemaName, new MapSqlParameterSource());
    }
    
    private SimpleJdbcCall createCall(String procedureName, String schemaName) {
        return new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(procedureName)
                .withSchemaName(schemaName);
    }
}
