package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.CategoriaConDominiosDto;
import ar.edu.ubp.das.backend.dto.DominioPreferenciaDto;
import ar.edu.ubp.das.backend.dto.GuardarPreferenciasDto;
import ar.edu.ubp.das.backend.dto.PreferenciaClienteDto;
import ar.edu.ubp.das.backend.dto.response.PreferenciasGuardadasResponse;
import ar.edu.ubp.das.backend.resources.util.ResponseHelper;
import ar.edu.ubp.das.backend.service.PreferenciaService;
import ar.edu.ubp.das.backend.service.LanguageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de preferencias gastronómicas
 */
@RestController
@RequestMapping("/api/preferencias")
@CrossOrigin(origins = "*")
public class PreferenciaResource {
    
    private static final Logger logger = LoggerFactory.getLogger(PreferenciaResource.class);
    
    private final PreferenciaService preferenciaService;
    private final LanguageService languageService;
    
    public PreferenciaResource(PreferenciaService preferenciaService, LanguageService languageService) {
        this.preferenciaService = preferenciaService;
        this.languageService = languageService;
    }
    
    /**
     * GET /api/preferencias/categorias - Obtener todas las categorías con sus dominios
     * Endpoint público (no requiere autenticación)
     */
    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaConDominiosDto>> obtenerCategorias(
            @RequestHeader(value = "X-Nro-Idioma", required = false) Integer nroIdiomaHeader) {
        try {
            Integer nroIdioma = languageService.getNroIdiomaFromRequest(nroIdiomaHeader);
            List<CategoriaConDominiosDto> categorias = preferenciaService.obtenerTodasLasCategoriasConDominios(nroIdioma);
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
                return ResponseHelper.unauthorized("No autenticado");
            }
            
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String nroCliente = jwt.getClaimAsString("nroCliente");
            
            if (nroCliente == null || nroCliente.isEmpty()) {
                return ResponseHelper.unauthorized("Token inválido: falta nroCliente");
            }
            
            int guardadas = preferenciaService.guardarPreferenciasCliente(
                    nroCliente, 
                    guardarPreferenciasDto.getPreferencias()
            );
            
            PreferenciasGuardadasResponse response = new PreferenciasGuardadasResponse(
                    "Preferencias guardadas exitosamente",
                    guardadas
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al guardar preferencias", e);
            return ResponseHelper.internalServerError("Error al guardar preferencias: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/preferencias/mis-preferencias - Obtener preferencias del cliente autenticado
     * Endpoint protegido (requiere autenticación)
     */
    @GetMapping("/mis-preferencias")
    public ResponseEntity<List<PreferenciaClienteDto>> obtenerMisPreferencias(
            Authentication authentication,
            @RequestHeader(value = "X-Nro-Idioma", required = false) Integer nroIdiomaHeader) {
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
            
            Integer nroIdioma = languageService.getNroIdiomaFromRequest(nroIdiomaHeader);
            List<PreferenciaClienteDto> preferencias = preferenciaService.obtenerPreferenciasCliente(nroCliente, nroIdioma);
            return ResponseEntity.ok(preferencias);
        } catch (Exception e) {
            logger.error("Error al obtener preferencias del cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{nroRestaurante}/especialidades-alimentarias")
    public ResponseEntity<List<DominioPreferenciaDto>> obtenerEspecialidadesAlimentariasPorRestaurante(
        @PathVariable String nroRestaurante,
        @RequestHeader(value = "X-Nro-Idioma", required = false) Integer nroIdiomaHeader)
    {
        try {
            List<DominioPreferenciaDto> resultados = preferenciaService.obtenerEspecialidadesAlimentariasPorRestaurante(nroRestaurante, nroIdiomaHeader);   
            return ResponseEntity.ok(resultados);
        } catch (Exception e) {
            // TODO: handle exception
            logger.error("Error al obtener preferencias del cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

