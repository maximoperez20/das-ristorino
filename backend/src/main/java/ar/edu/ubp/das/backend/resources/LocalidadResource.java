package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.LocalidadDto;
import ar.edu.ubp.das.backend.service.LocalidadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para consulta de localidades
 * Endpoint público (no requiere autenticación)
 */
@RestController
@RequestMapping("/api/localidades")
@CrossOrigin(origins = "*")
public class LocalidadResource {
    
    private final LocalidadService localidadService;
    
    public LocalidadResource(LocalidadService localidadService) {
        this.localidadService = localidadService;
    }

    /**
     * GET /api/localidades - Obtener todas las localidades
     */
    @GetMapping
    public ResponseEntity<List<LocalidadDto>> getAllLocalidades() {
        List<LocalidadDto> localidades = localidadService.obtenerTodasLasLocalidades();
        return ResponseEntity.ok(localidades);
    }
}
