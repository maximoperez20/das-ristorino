package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.ResenaDto;
import ar.edu.ubp.das.backend.service.ResenaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de reseñas
 */
@RestController
@RequestMapping("/api/resenas")
@CrossOrigin(origins = "*")
public class ResenaResource {

    private final ResenaService resenaService;

    public ResenaResource(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @GetMapping("/{nroRestaurante}/{nroSucursal}")
    public ResponseEntity<List<ResenaDto>> getResenasPorRestaurante(
            @PathVariable String nroRestaurante, 
            @PathVariable String nroSucursal) {
        List<ResenaDto> resenas = resenaService.obtenerResenasPorRestaurante(nroRestaurante, nroSucursal);
        return ResponseEntity.ok(resenas);
    }

    // @PostMapping
    // public ResponseEntity<ResenaDto> createResena(@Valid @RequestBody ResenaRequestDto resenaRequestDto) {
    //   resenaService.crearResena(resenaRequestDto);
    //   return ResponseEntity.status(HttpStatus.CREATED).build();
    // }
}