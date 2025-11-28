package ar.edu.ubp.das.backend.components;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import java.sql.Types;
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
        // Declarar explícitamente los parámetros OUTPUT si están presentes en params
        if (params instanceof MapSqlParameterSource) {
            MapSqlParameterSource mapParams = (MapSqlParameterSource) params;
            for (String paramName : mapParams.getParameterNames()) {
                Object value = mapParams.getValue(paramName);
                // Si el valor es null y el tipo es VARCHAR, asumimos que es OUTPUT
                if (value == null && mapParams.hasValue(paramName)) {
                    Integer sqlType = mapParams.getSqlType(paramName);
                    if (sqlType != null && sqlType == Types.VARCHAR) {
                        jdbcCall.declareParameters(new SqlOutParameter(paramName, Types.VARCHAR));
                    }
                }
            }
        }
        return jdbcCall.execute(params);
    }
    
    public <T> Map<String, Object> executeWithOutputs(String procedureName, String schemaName, SqlParameterSource params, SqlOutParameter... outParams) {
        SimpleJdbcCall jdbcCall = createCall(procedureName, schemaName);
        if (outParams != null && outParams.length > 0) {
            jdbcCall.declareParameters(outParams);
        }
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
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
