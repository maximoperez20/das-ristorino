package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.ResenasSucursalesDto;
import ar.edu.ubp.das.backend.dto.ReservaResponseDto;

import java.sql.Types;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.ubp.das.backend.components.SimpleJdbcCallFactory;


@Repository
public class ResenasSucursalesRepository {
    
    @Autowired
    private SimpleJdbcCallFactory jdbcCallFactory;
    
    @Autowired
    private ObjectMapper objectMapper;

    // Obtener reseña por restaurante y sucursal
    public List<ResenasSucursalesDto> getBySucursalRestaurante(String id_sucursal, String id_restaurante) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_sucursal", id_sucursal)
                .addValue("nro_restaurante", id_restaurante);
        return jdbcCallFactory.executeQuery("get_resenas_x_sucursales", "dbo", params, "resenas", ResenasSucursalesDto.class);
    }

     /**
     * Insertar reseña en una sucursal de restaurante
     * @param nroRestaurante Número de restaurante
     * @param nroSucursal Número de sucursal     
     * @param nroCliente Número de cliente
     * @param comentario Texto de comentario
     * @param valoracion Valoracion numerica
     */
    public void insertarResenaSucursal(
            String nroRestaurante,
            String nroSucursal,
            String nroCliente,
            String comentario,
            int valoracion) {
        try {                    
            SqlParameterSource params = new MapSqlParameterSource()                                
                    .addValue("nro_restaurante", nroRestaurante)
                    .addValue("nro_sucursal", nroSucursal)
                    .addValue("nro_cliente", nroCliente)
                    .addValue("comentario", comentario)
                    .addValue("valoracion", valoracion);
            
            jdbcCallFactory.execute("sp_insertar_resena_sucursal", "dbo", params);
        } catch (Exception e) {
            throw new RuntimeException("Error al insertar resena de sucursal: " + e.getMessage(), e);
        }
    }

}
