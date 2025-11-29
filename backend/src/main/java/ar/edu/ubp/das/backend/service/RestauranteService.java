package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.client.RestauranteClient;
import ar.edu.ubp.das.backend.client.RestauranteClientFactory;
import ar.edu.ubp.das.backend.dto.HorarioDisponibleDto;
import ar.edu.ubp.das.backend.dto.RestauranteDto;
import ar.edu.ubp.das.backend.dto.RestauranteDetalleDto;
import ar.edu.ubp.das.backend.dto.SucursalDto;
import ar.edu.ubp.das.backend.repository.RestauranteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de restaurantes
 * Solo operaciones de consulta (lectura)
 */
@Service
public class RestauranteService {
    
    private final RestauranteRepository restauranteRepository;
    private final RestauranteClientFactory restauranteClientFactory;
    
    public RestauranteService(RestauranteRepository restauranteRepository, RestauranteClientFactory restauranteClientFactory) {
        this.restauranteRepository = restauranteRepository;
        this.restauranteClientFactory = restauranteClientFactory;
    }
    
    public List<RestauranteDto> obtenerTodosLosRestaurantes() {
        return restauranteRepository.findAll();
    }
    
    public Optional<RestauranteDto> obtenerRestaurantePorId(String nroRestaurante) {
        return restauranteRepository.findById(nroRestaurante);
    }
    
    public Optional<RestauranteDetalleDto> obtenerDetalleRestaurantePorId(String nroRestaurante, Integer nroIdioma) {
        return restauranteRepository.findDetalleById(nroRestaurante, nroIdioma);
    }
    
    public List<SucursalDto> obtenerSucursales(String nroRestaurante) {
        return restauranteRepository.obtenerSucursales(nroRestaurante);
    }
    
    /**
     * Obtener horarios disponibles para una sucursal, zona y fecha específica
     * 
     * Nota: nroSucursal es el ID interno de das-ristorino. Para comunicarse con
     * das-restaurante-soap, se usa cod_sucursal_restaurante.
     * 
     * @param nroRestaurante UUID del restaurante (mismo en ambas bases de datos)
     * @param nroSucursal UUID de la sucursal (ID interno de das-ristorino)
     * @param codZona UUID de la zona (opcional)
     * @param fecha Fecha para consultar disponibilidad
     * @param cantidad Cantidad de personas (opcional)
     * @return Lista de horarios disponibles
     * @throws RuntimeException si el restaurante o la sucursal no existen, o si la sucursal no está sincronizada
     */
    public List<HorarioDisponibleDto> obtenerHorariosDisponibles(
            String nroRestaurante,
            String nroSucursal,
            String codZona,
            LocalDate fecha,
            Integer cantidad) {
        if (!restauranteRepository.existeRestaurante(nroRestaurante)) {
            throw new RuntimeException("Restaurante no encontrado: " + nroRestaurante);
        }
        
        if (!restauranteRepository.existeSucursal(nroRestaurante, nroSucursal)) {
            throw new RuntimeException("Sucursal no encontrada: " + nroSucursal + 
                                     " para el restaurante: " + nroRestaurante);
        }
        
        String codSucursalRestaurante = restauranteRepository.obtenerCodSucursalRestaurante(nroRestaurante, nroSucursal);
        
        if (codSucursalRestaurante == null || codSucursalRestaurante.trim().isEmpty()) {
            throw new RuntimeException("La sucursal " + nroSucursal + 
                                     " no está sincronizada con el sistema del restaurante. " +
                                     "cod_sucursal_restaurante no está configurado.");
        }
        RestauranteClient client = restauranteClientFactory.getClient(nroRestaurante);
        List<HorarioDisponibleDto> horarios = client.getHorariosDisponibles(nroRestaurante, codSucursalRestaurante, codZona, fecha, cantidad);
        
        // Mapear cod_zona_restaurante (externo del SOAP) a cod_zona (interno de Ristorino)
        // El frontend necesita el cod_zona interno para poder confirmar la reserva
        for (HorarioDisponibleDto horario : horarios) {
            if (horario.getCodZona() != null && !horario.getCodZona().trim().isEmpty()) {
                String codZonaInterno = restauranteRepository.obtenerCodZonaInterno(
                    nroRestaurante, 
                    nroSucursal, 
                    horario.getCodZona() // Este es el cod_zona_restaurante que viene del SOAP
                );
                if (codZonaInterno != null && !codZonaInterno.trim().isEmpty()) {
                    horario.setCodZona(codZonaInterno); // Reemplazar con el código interno
                }
            }
        }
        
        return horarios;
    }
    
}
