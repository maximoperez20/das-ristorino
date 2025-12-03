package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.ResenaDto;
import ar.edu.ubp.das.backend.dto.ResenaRequestDto;
import ar.edu.ubp.das.backend.components.SimpleJdbcCallFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ArrayList;

@Repository
public class ResenaRepository {
    
  @Autowired
  private SimpleJdbcCallFactory jdbcCallFactory;

  public List<ResenaDto> obtenerResenasPorRestaurante(String nroRestaurante, String nroSucursal) {
    SqlParameterSource params = new MapSqlParameterSource()
            .addValue("nro_restaurante", nroRestaurante)
            .addValue("nro_sucursal", nroSucursal);

    List<ResenaDto> resenas = jdbcCallFactory.executeQuery("sp_ObtenerResenasPorRestaurante", "dbo", params, "resenas", ResenaDto.class);
    return resenas != null && !resenas.isEmpty() ? resenas : new ArrayList<>();
  }

  public void crearResena(ResenaRequestDto resenaRequestDto) {
    SqlParameterSource params = new MapSqlParameterSource()
            .addValue("nro_cliente", resenaRequestDto.getNroCliente())
            .addValue("nro_restaurante", resenaRequestDto.getNroRestaurante())
            .addValue("nro_sucursal", resenaRequestDto.getNroSucursal())
            .addValue("comentario", resenaRequestDto.getComentario())
            .addValue("calificacion", resenaRequestDto.getCalificacion());

    jdbcCallFactory.execute("sp_InsertarResena", "dbo", params);
  }
}