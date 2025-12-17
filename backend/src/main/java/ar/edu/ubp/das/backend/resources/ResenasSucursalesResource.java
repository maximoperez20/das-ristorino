package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.ConfirmarResenaDto;

import ar.edu.ubp.das.backend.dto.ResenasSucursalesDto;
import ar.edu.ubp.das.backend.resources.util.ResponseHelper;

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

    
    private final ResenaSucursalService resenaSucursalService;

    public ResenasSucursalesResource(ResenaSucursalService resenaSucursalService) {
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
    
    @PostMapping("/insertar-resena-sucursal")
    public ResponseEntity<?> insertarResenaSucursal (
         @Valid @RequestBody ConfirmarResenaDto request,
            Authentication authentication) {
        try {

            if(authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
                return ResponseHelper.unauthorized("No autenticado");
            }

            Jwt jwt = (Jwt) authentication.getPrincipal();
            String clienteIdFromToken = jwt.getClaimAsString("nroCliente");

            if(clienteIdFromToken == null ||clienteIdFromToken.isEmpty()) {
                return ResponseHelper.unauthorized("NroCliente no presente en el token"); 
            }
        

            resenaSucursalService.insertarResenaSucursal(clienteIdFromToken, request);

            return ResponseEntity.ok().build();
        } catch (Exception e) {  
            logger.error("Error al insertar reseña de sucursal", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
    

}
