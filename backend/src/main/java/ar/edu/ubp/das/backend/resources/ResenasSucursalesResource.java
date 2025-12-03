package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.ActualizarReservaDto;
import ar.edu.ubp.das.backend.dto.CambiarEstadoDto;
import ar.edu.ubp.das.backend.dto.ConfirmarReservaDto;
import ar.edu.ubp.das.backend.dto.ConfirmarReservaResponseDto;
import ar.edu.ubp.das.backend.dto.CrearReservaDto;
import ar.edu.ubp.das.backend.dto.HorarioDisponibleDto;
import ar.edu.ubp.das.backend.dto.ResenasSucursalesDto;
import ar.edu.ubp.das.backend.dto.ReservaResponseDto;
import ar.edu.ubp.das.backend.exception.HorarioNoDisponibleException;
import ar.edu.ubp.das.backend.resources.util.ResponseHelper;
import ar.edu.ubp.das.backend.service.ReservaService;
import ar.edu.ubp.das.backend.service.RestauranteService;
import ar.edu.ubp.das.backend.service.LanguageService;
import ar.edu.ubp.das.backend.service.ResenaSucursalService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
@CrossOrigin(origins = "*")
public class ResenasSucursalesResource {
    private static final Logger logger = LoggerFactory.getLogger(ResenasSucursalesResource.class);

    private final LanguageService languageService;
    private final ResenaSucursalService resenaSucursalService;

    public ResenasSucursalesResource(LanguageService languageService, ResenaSucursalService resenaSucursalService) {
        this.languageService = languageService;
        this.resenaSucursalService = resenaSucursalService;
    }

    @GetMapping("/{nroRestaurante}/resenas-sucursales/{nroSucursal}")
    public ResponseEntity<List<ResenasSucursalesDto>> obtenerResenasSucursales (
        @PathVariable String nroRestaurante,
        @PathVariable String nroSucursal,
        @RequestHeader (value = "X-Nro-Idioma", required = false) Integer nroIdiomaHeader) 
    {
        try {
            List<ResenasSucursalesDto> resultados = resenaSucursalService.getResenasBySucursalRestaurante(nroSucursal, nroRestaurante);   
            return ResponseEntity.ok(resultados);
        } catch (Exception e) {  
            logger.error("Error al obtener reseñas de sucursales", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } 
    }    
}
