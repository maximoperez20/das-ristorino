package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.RegistrarReservaRistorinoDto;
import ar.edu.ubp.das.backend.dto.ResenasSucursalesDto;
import ar.edu.ubp.das.backend.dto.ActualizarReservaDto;
import ar.edu.ubp.das.backend.dto.ConfirmarResenaDto;
import ar.edu.ubp.das.backend.dto.ConfirmarReservaDto;
import ar.edu.ubp.das.backend.dto.ConfirmarReservaResponseDto;
import ar.edu.ubp.das.backend.dto.CrearReservaDto;
import ar.edu.ubp.das.backend.dto.HorarioDisponibleDto;
import ar.edu.ubp.das.backend.dto.ReservaResponseDto;
import ar.edu.ubp.das.backend.dto.SucursalDto;
import ar.edu.ubp.das.backend.dto.UsuarioDto;
import ar.edu.ubp.das.backend.repository.ReservaRepository;
import ar.edu.ubp.das.backend.repository.RestauranteRepository;
import ar.edu.ubp.das.backend.repository.ClienteRepository;
import ar.edu.ubp.das.backend.repository.ResenasSucursalesRepository;
import ar.edu.ubp.das.backend.client.RestauranteClient;
import ar.edu.ubp.das.backend.client.RestauranteClientFactory;
import ar.edu.ubp.das.backend.exception.HorarioNoDisponibleException;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class ResenaSucursalService {
    private final ResenasSucursalesRepository resenasSucursalesRepository;
    private static final Logger logger = LoggerFactory.getLogger(ResenaSucursalService.class);
    
    public ResenaSucursalService(ResenasSucursalesRepository resenasSucursalesRepository) {
        this.resenasSucursalesRepository = resenasSucursalesRepository;
    }

    public List<ResenasSucursalesDto> getResenasBySucursalRestaurante(String id_sucursal, String id_restaurante) {
        return resenasSucursalesRepository.getBySucursalRestaurante(id_sucursal, id_restaurante);
    }

    public void insertarResenaSucursal(String nroCliente, ConfirmarResenaDto request) {
        logger.info("Guardando reseña {}", nroCliente);
        resenasSucursalesRepository.insertarResenaSucursal(nroCliente, request);
    }
}
