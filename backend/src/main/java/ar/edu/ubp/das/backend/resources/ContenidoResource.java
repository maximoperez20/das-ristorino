package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.ContenidoGeneradoDto;
import ar.edu.ubp.das.backend.dto.GenerarContenidoRequestDto;
import ar.edu.ubp.das.backend.service.ContenidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para generación de contenido publicitario con IA.
 * Este endpoint genera contenido usando OpenAI y lo sincroniza con el servicio SOAP del restaurante.
 * Documentación detallada en: openapi-docs.yaml
 */
@RestController
@RequestMapping("/api/contenidos")
@CrossOrigin(origins = "*")
public class ContenidoResource {

    @Autowired
    private ContenidoService contenidoService;

    @PostMapping("/generar")
    public ResponseEntity<?> generarContenido(@Valid @RequestBody GenerarContenidoRequestDto request) {
        try {
            ContenidoGeneradoDto contenido = contenidoService.generarContenido(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(contenido);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al generar contenido: " + e.getMessage()));
        }
    }
}

