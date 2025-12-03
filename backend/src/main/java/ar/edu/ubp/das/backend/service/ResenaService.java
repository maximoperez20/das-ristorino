package ar.edu.ubp.das.backend.service;

import org.springframework.stereotype.Service;
import ar.edu.ubp.das.backend.repository.ResenaRepository;
import ar.edu.ubp.das.backend.dto.ResenaDto;
import ar.edu.ubp.das.backend.dto.ResenaRequestDto;

import java.util.List;

@Service
public class ResenaService {

  private final ResenaRepository resenaRepository;

  public ResenaService(ResenaRepository resenaRepository) {
    this.resenaRepository = resenaRepository;
  }

  public List<ResenaDto> obtenerResenasPorRestaurante(String nroRestaurante, String nroSucursal) {
    return resenaRepository.obtenerResenasPorRestaurante(nroRestaurante, nroSucursal);
  }

  public void crearResena(ResenaRequestDto resenaRequestDto) {
    resenaRepository.crearResena(resenaRequestDto); 
  }
}
