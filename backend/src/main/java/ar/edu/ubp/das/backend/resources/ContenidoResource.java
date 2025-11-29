package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.ContenidoGeneradoDto;
import ar.edu.ubp.das.backend.dto.GenerarContenidoRequestDto;
import ar.edu.ubp.das.backend.resources.util.ResponseHelper;
import ar.edu.ubp.das.backend.service.ContenidoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para generación de contenido publicitario con IA.
 * Genera contenido usando OpenAI y lo guarda en das-ristorino.
 */
@RestController
@RequestMapping("/api/contenidos")
@CrossOrigin(origins = "*")
public class ContenidoResource {

    private static final Logger logger = LoggerFactory.getLogger(ContenidoResource.class);
    
    private final ContenidoService contenidoService;
    
    public ContenidoResource(ContenidoService contenidoService) {
        this.contenidoService = contenidoService;
    }

    @PostMapping("/generar")
    public ResponseEntity<?> generarContenido(@Valid @RequestBody GenerarContenidoRequestDto request) {
        try {
            ContenidoGeneradoDto contenido = contenidoService.generarContenido(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(contenido);
        } catch (RuntimeException e) {
            logger.warn("Error al generar contenido: {}", e.getMessage());
            return ResponseHelper.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al generar contenido", e);
            return ResponseHelper.internalServerError("Error al generar contenido: " + e.getMessage());
        }
    }
}

