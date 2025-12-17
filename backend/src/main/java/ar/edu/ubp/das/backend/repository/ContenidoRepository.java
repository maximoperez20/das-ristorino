package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.AtributosRestauranteDto;

import ar.edu.ubp.das.backend.dto.ContenidoGeneradoDto;
import ar.edu.ubp.das.backend.dto.RestauranteContextoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository para gestión de contenidos de restaurantes.
 * Obtiene información contextual y guarda contenido generado por IA.
 */
@Repository
public class ContenidoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Obtiene el contexto completo de un restaurante/sucursal para generar contenido.
     * Incluye datos básicos, ubicación, preferencias y horarios.
     *
     * @param nroRestaurante UUID del restaurante
     * @param nroSucursal UUID de la sucursal (puede ser null)
     * @return Contexto del restaurante
     */
    public Optional<RestauranteContextoDto> obtenerContextoRestaurante(String nroRestaurante, String nroSucursal) {
        String sql = 
            "SELECT " +
            "    r.razon_social, " +
            "    s.nom_sucursal, " +
            "    CONCAT(s.calle, ' ', CAST(s.nro_calle AS VARCHAR), ', ', s.barrio) AS direccion, " +
            "    l.nom_localidad, " +
            "    p.nom_provincia, " +
            "    s.total_comensales " +
            "FROM restaurantes r " +
            "LEFT JOIN sucursales_restaurantes s ON r.nro_restaurante = s.nro_restaurante " +
            "    AND (? IS NULL OR s.nro_sucursal = ?) " +
            "LEFT JOIN localidades l ON s.nro_localidad = l.nro_localidad " +
            "LEFT JOIN provincias p ON l.cod_provincia = p.cod_provincia " +
            "WHERE r.nro_restaurante = ?";

        List<RestauranteContextoDto> resultados = jdbcTemplate.query(
            sql, 
            (rs, rowNum) -> mapearContextoBasico(rs),
            nroSucursal, nroSucursal, nroRestaurante
        );

        if (resultados.isEmpty()) {
            return Optional.empty();
        }

        RestauranteContextoDto contexto = resultados.get(0);

        // Obtener preferencias (tipos de comida, ambiente, precios)
        obtenerPreferencias(nroRestaurante, nroSucursal, contexto);

        // Obtener horarios
        obtenerHorarios(nroRestaurante, nroSucursal, contexto);

        // Obtener identidad gastronómica y comunicacional usando el método unificado
        java.util.Map<String, String> atributosYConfig = obtenerTodosLosAtributosYConfiguracion(nroRestaurante);
        
        // Mapear los atributos específicos al contexto
        if (atributosYConfig.containsKey("Tipo de cocina")) {
            contexto.setTipoCocina(atributosYConfig.get("Tipo de cocina"));
        }
        if (atributosYConfig.containsKey("Estilo de atención")) {
            contexto.setEstiloAtencion(atributosYConfig.get("Estilo de atención"));
        }
        if (atributosYConfig.containsKey("Platos emblemáticos")) {
            contexto.setPlatosEmblematicos(atributosYConfig.get("Platos emblemáticos"));
        }

        return Optional.of(contexto);
    }

    /**
     * Mapea los datos básicos del restaurante desde el ResultSet.
     */
    private RestauranteContextoDto mapearContextoBasico(ResultSet rs) throws SQLException {
        RestauranteContextoDto contexto = new RestauranteContextoDto();
        contexto.setRazonSocial(rs.getString("razon_social"));
        contexto.setNombreSucursal(rs.getString("nom_sucursal"));
        contexto.setDireccion(rs.getString("direccion"));
        contexto.setLocalidad(rs.getString("nom_localidad"));
        contexto.setProvincia(rs.getString("nom_provincia"));
        
        Integer totalComensales = rs.getInt("total_comensales");
        if (!rs.wasNull()) {
            contexto.setTotalComensales(totalComensales);
        }
        
        return contexto;
    }

    /**
     * Obtiene las preferencias del restaurante (tipo de comida, ambiente, precio).
     */
    private void obtenerPreferencias(String nroRestaurante, String nroSucursal, RestauranteContextoDto contexto) {
        String sql = 
            "SELECT " +
            "    cp.nom_categoria, " +
            "    dcp.nom_valor_dominio, " +
            "    pr.observaciones " +
            "FROM preferencias_restaurantes pr " +
            "JOIN categorias_preferencias cp ON pr.cod_categoria = cp.cod_categoria " +
            "JOIN dominio_categorias_preferencias dcp " +
            "    ON pr.cod_categoria = dcp.cod_categoria " +
            "    AND pr.nro_valor_dominio = dcp.nro_valor_dominio " +
            "WHERE pr.nro_restaurante = ? " +
            "    AND (pr.nro_sucursal IS NULL OR pr.nro_sucursal = ? OR ? IS NULL) " +
            "ORDER BY cp.nom_categoria, pr.nro_preferencia";

        jdbcTemplate.query(sql, rs -> {
            String categoria = rs.getString("nom_categoria");
            String valor = rs.getString("nom_valor_dominio");
            String observaciones = rs.getString("observaciones");

            if (categoria.equalsIgnoreCase("Tipo de comida")) {
                contexto.getTiposComida().add(valor);
                if (observaciones != null && !observaciones.isEmpty()) {
                    if (contexto.getObservacionesAdicionales() == null) {
                        contexto.setObservacionesAdicionales(observaciones);
                    } else {
                        contexto.setObservacionesAdicionales(
                            contexto.getObservacionesAdicionales() + " " + observaciones
                        );
                    }
                }
            } else if (categoria.equalsIgnoreCase("Ambiente")) {
                contexto.getAmbientes().add(valor);
                if (observaciones != null && !observaciones.isEmpty()) {
                    if (contexto.getObservacionesAdicionales() == null) {
                        contexto.setObservacionesAdicionales(observaciones);
                    } else {
                        contexto.setObservacionesAdicionales(
                            contexto.getObservacionesAdicionales() + " " + observaciones
                        );
                    }
                }
            } else if (categoria.equalsIgnoreCase("Rango de precio")) {
                contexto.getRangosPrecios().add(valor);
            }
        }, nroRestaurante, nroSucursal, nroSucursal);
    }

    /**
     * Obtiene los horarios del restaurante/sucursal.
     */
    private void obtenerHorarios(String nroRestaurante, String nroSucursal, RestauranteContextoDto contexto) {
        String sql = 
            "SELECT " +
            "    CONVERT(VARCHAR(5), hora_desde, 108) AS hora_desde, " +
            "    CONVERT(VARCHAR(5), hora_hasta, 108) AS hora_hasta " +
            "FROM turnos_sucursales_restaurantes " +
            "WHERE nro_restaurante = ? " +
            "    AND (? IS NULL OR nro_sucursal = ?) " +
            "    AND habilitado = 1 " +
            "ORDER BY hora_desde";

        jdbcTemplate.query(sql, rs -> {
            String horario = rs.getString("hora_desde") + " - " + rs.getString("hora_hasta");
            contexto.getHorarios().add(horario);
        }, nroRestaurante, nroSucursal, nroSucursal);
    }

    /**
     * Obtiene TODOS los atributos y configuracion_restaurantes para un restaurante.
     * Retorna un mapa donde la clave es el nombre del atributo y el valor es su valor.
     * 
     * @param nroRestaurante UUID del restaurante
     * @return Mapa con todos los atributos y sus valores
     */
    public java.util.Map<String, String> obtenerTodosLosAtributosYConfiguracion(String nroRestaurante) {
        String sql = 
            "SELECT " +
            "    a.nom_atributo, " +
            "    cr.valor " +
            "FROM configuracion_restaurantes cr " +
            "JOIN atributos a ON cr.cod_atributo = a.cod_atributo " +
            "WHERE cr.nro_restaurante = ? " +
            "    AND cr.valor IS NOT NULL " +
            "    AND cr.valor != '' " +
            "ORDER BY a.nom_atributo";

        AtributosRestauranteDto atributosDto = new AtributosRestauranteDto();
        
        jdbcTemplate.query(sql, rs -> {
            String nomAtributo = rs.getString("nom_atributo");
            String valor = rs.getString("valor");
            
            if (nomAtributo != null && valor != null && !valor.trim().isEmpty()) {
                atributosDto.agregarAtributo(nomAtributo, valor);
            }
        }, nroRestaurante);
        
        return atributosDto.getAtributos();
    }

    /**
     * Guarda el contenido generado por IA en la tabla contenidos_restaurantes.
     * El costo de click se obtiene automáticamente en el stored procedure desde la tabla costos (tipo_costo = 'CLICK').
     *
     * @param nroRestaurante UUID del restaurante
     * @param nroSucursal UUID de la sucursal (puede ser null)
     * @param nroIdioma ID del idioma (INT)
     * @param contenidoGenerado Texto generado por IA
     * @param codContenidoRestaurante ID del contenido en el sistema SOAP (nro_contenido del SOAP)
     * @return DTO con los datos del contenido guardado
     */
    public Optional<ContenidoGeneradoDto> guardarContenidoGenerado(
            String nroRestaurante,
            String nroSucursal,
            Integer nroIdioma,
            String contenidoGenerado,
            String codContenidoRestaurante,
            String propositoCorto) {

        String sql = "EXEC sp_GuardarContenidoGenerado ?, ?, ?, ?, ?, ?";
        
        try {
            List<ContenidoGeneradoDto> result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    ContenidoGeneradoDto dto = new ContenidoGeneradoDto();
                    dto.setNroRestaurante(rs.getString("nro_restaurante"));
                    dto.setNroSucursal(rs.getString("nro_sucursal"));
                    dto.setNroIdioma(rs.getInt("nro_idioma"));
                    dto.setNroContenido(rs.getString("nro_contenido"));
                    dto.setContenidoGenerado(rs.getString("contenido_a_publicar"));

                    java.sql.Date fechaIni = rs.getDate("fecha_ini_vigencia");
                    if (fechaIni != null) {
                        dto.setFechaIniVigencia(fechaIni.toLocalDate());
                    }

                    java.sql.Date fechaFin = rs.getDate("fecha_fin_vigencia");
                    if (fechaFin != null) {
                        dto.setFechaFinVigencia(fechaFin.toLocalDate());
                    }

                    java.math.BigDecimal costo = rs.getBigDecimal("costo_click");
                    if (!rs.wasNull()) {
                        dto.setCostoClick(costo);
                    }

                    return dto;
                },
                nroRestaurante,
                nroSucursal,
                nroIdioma,
                contenidoGenerado,
                codContenidoRestaurante,
                propositoCorto
            );
            
            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
            
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar contenido generado: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza el cod_contenido_restaurante con el nroContenido devuelto por el sistema SOAP.
     * Este valor es crítico para identificar clicks que deben ser notificados.
     * 
     * @param nroRestaurante UUID del restaurante
     * @param nroIdioma ID del idioma (INT)
     * @param nroContenido UUID del contenido en das_ristorino
     * @param codContenidoRestaurante UUID del contenido en das_restaurante (nroContenido devuelto por SOAP)
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean actualizarCodContenidoRestaurante(
            String nroRestaurante,
            Integer nroIdioma,
            String nroContenido,
            String codContenidoRestaurante) {
        
        // Validar que codContenidoRestaurante no sea null o vacío
        if (codContenidoRestaurante == null || codContenidoRestaurante.trim().isEmpty()) {
            throw new RuntimeException("codContenidoRestaurante no puede ser null o vacío. El sistema SOAP debe devolver un nroContenido válido.");
        }
        
        String sql = "EXEC sp_ActualizarCodContenidoRestaurante ?, ?, ?, ?";
        
        try {
            String resultado = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    return rs.getString("cod_contenido_restaurante");
                },
                nroRestaurante,
                nroIdioma,
                nroContenido,
                codContenidoRestaurante
            );
            
            // Verificar que la actualización fue exitosa
            return resultado != null && resultado.equals(codContenidoRestaurante);
        } catch (org.springframework.dao.DataAccessException e) {
            // Si el stored procedure lanza un RAISERROR, se captura aquí
            throw new RuntimeException("Error al actualizar cod_contenido_restaurante. " +
                    "Verificar que el contenido existe (nroRestaurante=" + nroRestaurante + 
                    ", nroIdioma=" + nroIdioma + ", nroContenido=" + nroContenido + "). " +
                    "Error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al actualizar cod_contenido_restaurante: " + e.getMessage(), e);
        }
    }

    public String obtenerCodSucursalRestaurante(String nroRestaurante, String nroSucursal) {
        if (nroSucursal == null) {
            return null;
        }

        String sql = "SELECT cod_sucursal_restaurante FROM sucursales_restaurantes WHERE nro_restaurante = ? AND nro_sucursal = ?";

        try {
            List<String> result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("cod_sucursal_restaurante"),
                nroRestaurante,
                nroSucursal
            );

            if (result.isEmpty()) {
                throw new RuntimeException("Sucursal no encontrada en ristorino: nro_restaurante=" + nroRestaurante + ", nro_sucursal=" + nroSucursal);
            }

            return result.get(0);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener cod_sucursal_restaurante: " + e.getMessage(), e);
        }
    }

    public String obtenerCodIdioma(Integer nroIdioma) {
        String sql = "SELECT cod_idioma FROM idiomas WHERE nro_idioma = ?";

        try {
            List<String> result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("cod_idioma"),
                nroIdioma
            );

            if (result.isEmpty()) {
                throw new RuntimeException("Idioma no encontrado: nro_idioma=" + nroIdioma);
            }

            return result.get(0);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener cod_idioma: " + e.getMessage(), e);
        }
    }

    public String obtenerNomIdioma(Integer nroIdioma) {
        String sql = "SELECT nom_idioma FROM idiomas WHERE nro_idioma = ?";

        try {
            List<String> result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("nom_idioma"),
                nroIdioma
            );

            if (result.isEmpty()) {
                throw new RuntimeException("Idioma no encontrado: nro_idioma=" + nroIdioma);
            }

            return result.get(0);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener nom_idioma: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene el nro_sucursal interno de Ristorino a partir del cod_sucursal_restaurante (código externo del SOAP).
     * 
     * @param nroRestaurante UUID del restaurante
     * @param codSucursalRestaurante Código de sucursal del sistema SOAP (cod_sucursal_restaurante)
     * @return nro_sucursal interno de Ristorino, o null si no se encuentra
     */
    public String obtenerNroSucursalPorCodSucursalRestaurante(String nroRestaurante, String codSucursalRestaurante) {
        if (codSucursalRestaurante == null || codSucursalRestaurante.trim().isEmpty()) {
            return null;
        }

        String sql = 
            "SELECT nro_sucursal " +
            "FROM sucursales_restaurantes " +
            "WHERE nro_restaurante = ? AND cod_sucursal_restaurante = ?";

        try {
            List<String> result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("nro_sucursal"),
                nroRestaurante,
                codSucursalRestaurante
            );

            return result.isEmpty() ? null : result.get(0);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener nro_sucursal por cod_sucursal_restaurante: " + e.getMessage(), e);
        }
    }
}

