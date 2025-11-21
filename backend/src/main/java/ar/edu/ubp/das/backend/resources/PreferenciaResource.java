package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.*;
import ar.edu.ubp.das.backend.service.PreferenciaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de preferencias gastronómicas
 */
@RestController
@RequestMapping("/api/preferencias")
@CrossOrigin(origins = "*")
public class PreferenciaResource {
    
    private static final Logger logger = LoggerFactory.getLogger(PreferenciaResource.class);
    
    private final PreferenciaService preferenciaService;
    
    public PreferenciaResource(PreferenciaService preferenciaService) {
        this.preferenciaService = preferenciaService;
    }
    
    /**
     * GET /api/preferencias/categorias - Obtener todas las categorías con sus dominios
     * Endpoint público (no requiere autenticación)
     */
    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaConDominiosDto>> obtenerCategorias() {
        try {
            List<CategoriaConDominiosDto> categorias = preferenciaService.obtenerTodasLasCategoriasConDominios();
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            logger.error("Error al obtener categorías de preferencias", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
    
    /**
     * POST /api/preferencias/guardar - Guardar preferencias del cliente autenticado
     * Endpoint protegido (requiere autenticación)
     */
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarPreferencias(
            @Valid @RequestBody GuardarPreferenciasDto guardarPreferenciasDto,
            Authentication authentication) {
        try {
            // Obtener nroCliente del JWT
            if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "No autenticado"));
            }
            
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String nroCliente = jwt.getClaimAsString("nroCliente");
            
            if (nroCliente == null || nroCliente.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token inválido: falta nroCliente"));
            }
            
            int guardadas = preferenciaService.guardarPreferenciasCliente(
                    nroCliente, 
                    guardarPreferenciasDto.getPreferencias()
            );
            
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Preferencias guardadas exitosamente",
                    "preferenciasGuardadas", guardadas
            ));
        } catch (Exception e) {
            logger.error("Error al guardar preferencias", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al guardar preferencias: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/preferencias/mis-preferencias - Obtener preferencias del cliente autenticado
     * Endpoint protegido (requiere autenticación)
     */
    @GetMapping("/mis-preferencias")
    public ResponseEntity<List<PreferenciaClienteDto>> obtenerMisPreferencias(Authentication authentication) {
        try {
            // Obtener nroCliente del JWT
            if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String nroCliente = jwt.getClaimAsString("nroCliente");
            
            if (nroCliente == null || nroCliente.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            List<PreferenciaClienteDto> preferencias = preferenciaService.obtenerPreferenciasCliente(nroCliente);
            return ResponseEntity.ok(preferencias);
        } catch (Exception e) {
            logger.error("Error al obtener preferencias del cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

