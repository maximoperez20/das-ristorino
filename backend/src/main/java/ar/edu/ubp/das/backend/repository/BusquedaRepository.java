package ar.edu.ubp.das.backend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para obtener catálogos necesarios para búsqueda NLP
 */
@Repository
public class BusquedaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Obtiene todos los tipos de comida disponibles
     */
    public List<String> obtenerTiposComida() {
        String sql = "SELECT DISTINCT dcp.nom_valor_dominio " +
                     "FROM dominio_categorias_preferencias dcp " +
                     "JOIN categorias_preferencias cp ON dcp.cod_categoria = cp.cod_categoria " +
                     "WHERE cp.nom_categoria = 'Tipo de comida' " +
                     "ORDER BY dcp.nom_valor_dominio";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    /**
     * Obtiene todos los barrios disponibles de las sucursales
     */
    public List<String> obtenerBarrios() {
        String sql = "SELECT DISTINCT barrio " +
                     "FROM sucursales_restaurantes " +
                     "WHERE barrio IS NOT NULL AND barrio <> '' " +
                     "ORDER BY barrio";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    /**
     * Obtiene todas las localidades disponibles
     */
    public List<String> obtenerLocalidades() {
        String sql = "SELECT DISTINCT nom_localidad " +
                     "FROM localidades " +
                     "ORDER BY nom_localidad";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    /**
     * Obtiene todos los ambientes disponibles
     */
    public List<String> obtenerAmbientes() {
        String sql = "SELECT DISTINCT dcp.nom_valor_dominio " +
                     "FROM dominio_categorias_preferencias dcp " +
                     "JOIN categorias_preferencias cp ON dcp.cod_categoria = cp.cod_categoria " +
                     "WHERE cp.nom_categoria = 'Ambiente' " +
                     "ORDER BY dcp.nom_valor_dominio";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    /**
     * Obtiene todos los rangos de precio disponibles
     */
    public List<String> obtenerRangosPrecio() {
        String sql = "SELECT DISTINCT dcp.nom_valor_dominio " +
                     "FROM dominio_categorias_preferencias dcp " +
                     "JOIN categorias_preferencias cp ON dcp.cod_categoria = cp.cod_categoria " +
                     "WHERE cp.nom_categoria = 'Rango de precio' " +
                     "ORDER BY dcp.nom_valor_dominio";
        return jdbcTemplate.queryForList(sql, String.class);
    }
}

