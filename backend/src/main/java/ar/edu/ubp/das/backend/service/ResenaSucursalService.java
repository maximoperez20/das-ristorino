package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.ResenasSucursalesDto;

import ar.edu.ubp.das.backend.dto.ConfirmarResenaDto;

import ar.edu.ubp.das.backend.repository.ResenasSucursalesRepository;

import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


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
