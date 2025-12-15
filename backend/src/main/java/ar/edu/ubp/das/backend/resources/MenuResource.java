package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.restaurante.ObtenerMenuResponse;
import ar.edu.ubp.das.backend.service.MenuService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menus")
@CrossOrigin(origins = "*")
public class MenuResource {
    private static final Logger logger = LoggerFactory.getLogger(MenuResource.class);
    
    private final MenuService menuService;

    public MenuResource(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/{nroRestaurante}/sucursales/{nroSucursal}")
    public ResponseEntity<ObtenerMenuResponse> obtenerMenuSucursal(
            @PathVariable String nroRestaurante,
            @PathVariable String nroSucursal) {
        try {
            ObtenerMenuResponse menuResponse = menuService.obtenerMenuSucursal(nroRestaurante, nroSucursal);
            return ResponseEntity.ok(menuResponse);
        } catch (IllegalArgumentException e) {
            logger.error("Error al obtener el menú: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("Error inesperado al obtener el menú: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
