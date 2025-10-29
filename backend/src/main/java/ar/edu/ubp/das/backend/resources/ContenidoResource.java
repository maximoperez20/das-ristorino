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
 * Endpoint protegido (requiere autenticación JWT).
 */
@RestController
@RequestMapping("/api/contenidos")
@CrossOrigin(origins = "*")
public class ContenidoResource {

    @Autowired
    private ContenidoService contenidoService;

    /**
     * Genera contenido publicitario con IA para un restaurante/sucursal.
     * 
     * POST /api/contenidos/generar
     * 
     * Body:
     * {
     *   "nroRestaurante": "uuid-del-restaurante",
     *   "nroSucursal": "uuid-de-sucursal-opcional",
     *   "nroIdioma": "uuid-del-idioma",
     *   "contextoAdicional": "info extra opcional"
     * }
     * 
     * Respuesta:
     * {
     *   "nroRestaurante": "uuid",
     *   "nroSucursal": "uuid",
     *   "nroIdioma": "uuid",
     *   "nroContenido": "uuid-del-contenido-generado",
     *   "nombreRestaurante": "Los Aroza SRL",
     *   "nombreSucursal": "Los Aroza - Centro",
     *   "contenidoGenerado": "Texto publicitario generado...",
     *   "fechaIniVigencia": "2025-10-29",
     *   "fechaFinVigencia": "2025-11-29"
     * }
     */
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

