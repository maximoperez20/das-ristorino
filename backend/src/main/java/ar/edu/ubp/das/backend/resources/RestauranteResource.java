package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.BusquedaNLPRequestDto;
import ar.edu.ubp.das.backend.dto.BusquedaNLPResultadoDto;
import ar.edu.ubp.das.backend.dto.HorarioDisponibleDto;
import ar.edu.ubp.das.backend.dto.RestauranteDto;
import ar.edu.ubp.das.backend.dto.RestauranteDetalleDto;
import ar.edu.ubp.das.backend.dto.SucursalDto;
import ar.edu.ubp.das.backend.dto.response.HorariosDisponiblesResponse;
import ar.edu.ubp.das.backend.resources.util.ResponseHelper;
import ar.edu.ubp.das.backend.service.BusquedaNLPService;
import ar.edu.ubp.das.backend.service.RestauranteService;
import ar.edu.ubp.das.backend.service.LanguageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para consulta de restaurantes
 * Endpoints públicos (no requieren autenticación)
 * Cumple Requerimiento 11: Visualizar información de restaurantes
 */
@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
public class RestauranteResource {

    private static final Logger logger = LoggerFactory.getLogger(RestauranteResource.class);
    
    private final RestauranteService restauranteService;
    private final BusquedaNLPService busquedaNLPService;
    private final LanguageService languageService;
    
    public RestauranteResource(RestauranteService restauranteService, BusquedaNLPService busquedaNLPService, LanguageService languageService) {
        this.restauranteService = restauranteService;
        this.busquedaNLPService = busquedaNLPService;
        this.languageService = languageService;
    }

    @GetMapping
    public ResponseEntity<List<RestauranteDto>> getAllRestaurantes() {
        List<RestauranteDto> restaurantes = restauranteService.obtenerTodosLosRestaurantes();
        return ResponseEntity.ok(restaurantes);
    }

    /**
     * GET /api/restaurantes/{nroRestaurante} - Obtener ficha completa de un restaurante (Requerimiento 11)
     * Incluye: nombre, tipo de cocina, descripción, imágenes, promociones vigentes y sucursales
     */
    @GetMapping("/{nroRestaurante}")
    public ResponseEntity<RestauranteDetalleDto> getRestauranteById(
            @PathVariable String nroRestaurante,
            @RequestHeader(value = "X-Nro-Idioma", required = false) Integer nroIdiomaHeader) {
        Integer nroIdioma = languageService.getNroIdiomaFromRequest(nroIdiomaHeader);
        Optional<RestauranteDetalleDto> restaurante = restauranteService.obtenerDetalleRestaurantePorId(nroRestaurante, nroIdioma);
        return restaurante.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{nroRestaurante}/sucursales")
    public ResponseEntity<List<SucursalDto>> getSucursales(@PathVariable String nroRestaurante) {
        List<SucursalDto> sucursales = restauranteService.obtenerSucursales(nroRestaurante);
        return ResponseEntity.ok(sucursales);
    }
    
    /**
     * POST /api/restaurantes/buscar-nlp - Buscar restaurantes con lenguaje natural (NLP)
     * Cumple Requerimientos 10 y 35: Búsqueda con lenguaje natural
     * 
     * Permite al usuario expresarse libremente en lenguaje natural para recibir 
     * resultados personalizados y contextuales.
     * 
     * Si el usuario está autenticado, se utilizarán sus preferencias gastronómicas
     * para mejorar los resultados de búsqueda.
     * 
     * Ejemplos de consultas:
     * - "quiero comer algo picante en el centro"
     * - "cena romántica con sushi"
     * - "almuerzo vegano económico en Nueva Córdoba"
     * - "dónde puedo comer algo picante esta noche"
     * 
     * @param request Solicitud con la consulta en lenguaje natural
     * @param authentication Objeto de autenticación de Spring Security (opcional)
     * @return Lista de restaurantes que coinciden con la intención del usuario
     */
    @PostMapping("/buscar-nlp")
    public ResponseEntity<?> buscarRestaurantesPorNLP(
            @Valid @RequestBody BusquedaNLPRequestDto request,
            Authentication authentication) {
        try {
            // Obtener nroCliente del JWT si está autenticado
            String nroCliente = null;
            if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                nroCliente = jwt.getClaimAsString("nroCliente");
            }
            
            BusquedaNLPResultadoDto resultado = busquedaNLPService.buscarRestaurantesPorNLP(request, nroCliente);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            logger.warn("Error al procesar búsqueda NLP: {}", e.getMessage());
            return ResponseHelper.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al procesar búsqueda NLP", e);
            return ResponseHelper.internalServerError("Error al procesar la búsqueda: " + e.getMessage());
        }
    }

    /**
     * GET /api/restaurantes/{nroRestaurante}/sucursales/{nroSucursal}/horarios-disponibles
     * Obtiene los horarios disponibles para una sucursal, agrupados por zona.
     * Si codZona no se especifica, devuelve todas las zonas con sus horarios.
     * 
     * @param nroRestaurante UUID del restaurante
     * @param nroSucursal UUID de la sucursal
     * @param codZona UUID de la zona (query parameter, opcional - si es null devuelve todas las zonas)
     * @param fecha Fecha para consultar disponibilidad (query parameter, formato: yyyy-MM-dd)
     * @param cantidad Cantidad de personas (query parameter, opcional)
     * @return Lista de horarios disponibles. Si codZona es null, los horarios vienen agrupados por zona.
     */
    @GetMapping("/{nroRestaurante}/sucursales/{nroSucursal}/horarios-disponibles")
    public ResponseEntity<?> getHorariosDisponibles(
            @PathVariable String nroRestaurante,
            @PathVariable String nroSucursal,
            @RequestParam(required = false) String codZona,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Integer cantidad) {
        try {
            List<HorarioDisponibleDto> horarios = restauranteService.obtenerHorariosDisponibles(
                    nroRestaurante, nroSucursal, codZona, fecha, cantidad);
            
            // Si codZona es null, agrupar por zona para una mejor respuesta
            if (codZona == null) {
                HorariosDisponiblesResponse response = ResponseHelper.agruparHorariosPorZona(horarios, fecha);
                return ResponseEntity.ok(response);
            }
            
            // Si codZona está especificado, devolver lista plana
            return ResponseEntity.ok(horarios);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            logger.warn("Error al consultar horarios disponibles: {}", errorMessage);
            
            HttpStatus status = ResponseHelper.determinarStatusDesdeError(errorMessage);
            return ResponseHelper.error(errorMessage, status);
        } catch (Exception e) {
            logger.error("Error inesperado al consultar horarios disponibles", e);
            return ResponseHelper.internalServerError("Error al consultar horarios disponibles: " + e.getMessage());
        }
    }
}
