package ar.edu.ubp.das.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.ubp.das.backend.dto.ResenaDto;
import ar.edu.ubp.das.backend.repository.ResenaRepository;
import java.util.List;

@Service
public class ResenaService {

    private static final Logger logger = LoggerFactory.getLogger(ResenaService.class);


    @Autowired
    private ResenaRepository resenaRepository;

    // Método para obtener reseñas (sin validación necesaria)
    public List<ResenaDto> obtenerResenas(String nroRestaurante, String nroSucursal) {
        logger.info("Obteniendo reseñas para restaurante: {} sucursal: {}", nroRestaurante, nroSucursal);
        return resenaRepository.obtenerResenas(nroRestaurante, nroSucursal);
    }

    // Método para insertar con validaciones
    public void insertarResena(String nroRestaurante, String nroSucursal, 
                               String nroCliente, Integer calificacion, 
                               String comentario) {
        logger.info("Insertando reseña para restaurante: {} sucursal: {} cliente: {}", nroRestaurante, nroSucursal, nroCliente);
        // Aquí van tus validaciones
        // Validar que los IDs no sean null ni vacíos
            if (nroRestaurante == null || nroRestaurante.trim().isEmpty()) {
                throw new IllegalArgumentException("El número de restaurante es obligatorio");
            }
            if (nroSucursal == null || nroSucursal.trim().isEmpty()) {
                throw new IllegalArgumentException("El número de sucursal es obligatorio");
            }
            if (nroCliente == null || nroCliente.trim().isEmpty()) {
                throw new IllegalArgumentException("El número de cliente es obligatorio");
            }

            // Validar calificación (1-5)
            if (calificacion == null || calificacion < 1 || calificacion > 5) {
                throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
            }

            // Validar comentario
            if (comentario == null || comentario.trim().isEmpty()) {
                throw new IllegalArgumentException("El comentario es obligatorio");
            }
            if (comentario.length() > 1000) {
                throw new IllegalArgumentException("El comentario no puede superar los 1000 caracteres");
            }
        // Si algo falla, lanzá una excepción (ej: IllegalArgumentException)
        
        resenaRepository.insertarResena(nroRestaurante, nroSucursal, 
                                       nroCliente, calificacion, comentario);
    }

    
}
