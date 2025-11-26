package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.components.SimpleJdbcCallFactory;
import ar.edu.ubp.das.backend.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository para gestión de preferencias gastronómicas
 */
@Repository
public class PreferenciaRepository {
    
    @Autowired
    private SimpleJdbcCallFactory jdbcCallFactory;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Obtener todas las categorías con sus dominios
     * @param nroIdioma Número de idioma (0=es-AR, 1=en-US)
     */
    public List<CategoriaConDominiosDto> obtenerTodasLasCategoriasConDominios(Integer nroIdioma) {
        // Ejecutar el stored procedure que retorna dos result sets
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_ObtenerTodasLasCategoriasConDominios")
                .withSchemaName("dbo")
                .returningResultSet("categorias", BeanPropertyRowMapper.newInstance(CategoriaPreferenciaDto.class))
                .returningResultSet("dominios", BeanPropertyRowMapper.newInstance(DominioPreferenciaDto.class));
        
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_idioma", nroIdioma);
        
        Map<String, Object> result = jdbcCall.execute(params);
        
        // Obtener los result sets
        @SuppressWarnings("unchecked")
        List<CategoriaPreferenciaDto> categorias = (List<CategoriaPreferenciaDto>) result.get("categorias");
        @SuppressWarnings("unchecked")
        List<DominioPreferenciaDto> dominios = (List<DominioPreferenciaDto>) result.get("dominios");
        
        // Agrupar dominios por categoría
        Map<String, List<DominioPreferenciaDto>> dominiosPorCategoria = dominios.stream()
                .collect(Collectors.groupingBy(DominioPreferenciaDto::getCodCategoria));
        
        // Crear la lista de categorías con sus dominios
        List<CategoriaConDominiosDto> resultado = new ArrayList<>();
        for (CategoriaPreferenciaDto categoria : categorias) {
            CategoriaConDominiosDto categoriaConDominios = new CategoriaConDominiosDto(
                    categoria.getCodCategoria(),
                    categoria.getNombre()
            );
            categoriaConDominios.setDominios(
                    dominiosPorCategoria.getOrDefault(categoria.getCodCategoria(), new ArrayList<>())
            );
            resultado.add(categoriaConDominios);
        }
        
        return resultado;
    }
    
    /**
     * Guardar preferencias de un cliente
     */
    public int guardarPreferenciasCliente(String nroCliente, List<GuardarPreferenciasDto.PreferenciaItemDto> preferencias) {
        try {
            // Convertir la lista a JSON
            String preferenciasJson = objectMapper.writeValueAsString(preferencias);
            
            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("nro_cliente", nroCliente)
                    .addValue("preferencias", preferenciasJson, Types.NVARCHAR);
            
            Map<String, Object> result = jdbcCallFactory.executeWithOutputs(
                    "sp_GuardarPreferenciasCliente", "dbo", params);
            
            if (result != null && result.containsKey("preferencias_guardadas")) {
                return ((Number) result.get("preferencias_guardadas")).intValue();
            }
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar preferencias: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtener preferencias de un cliente
     */
    public List<PreferenciaClienteDto> obtenerPreferenciasCliente(String nroCliente) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_cliente", nroCliente);
        
        return jdbcCallFactory.executeQuery(
                "sp_ObtenerPreferenciasCliente", "dbo", params, "preferencias", PreferenciaClienteDto.class);
    }
}

