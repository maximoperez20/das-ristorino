package ar.edu.ubp.das.backend.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.ubp.das.backend.dto.NuevaResenaRequest;
import ar.edu.ubp.das.backend.dto.ResenaDto;
import ar.edu.ubp.das.backend.service.ResenaService;

@RestController
@RequestMapping("/api/resenas")
@CrossOrigin(origins = "*")
public class ResenaResource {

    @Autowired
    private ResenaService resenaService;

    @GetMapping("/{nroRestaurante}/{nroSucursal}")
    public ResponseEntity<List<ResenaDto>> obtenerResenas(
            @PathVariable String nroRestaurante,
            @PathVariable String nroSucursal) {
        List<ResenaDto> resenas = resenaService.obtenerResenas(nroRestaurante, nroSucursal);
        return ResponseEntity.ok(resenas);
    }

    @PostMapping
    public ResponseEntity<Void> insertarResena(@RequestBody NuevaResenaRequest request){
        resenaService.insertarResena(
            request.getNroRestaurante(),
            request.getNroSucursal(),
            request.getNroCliente(),
            request.getCalificacion(),
            request.getComentario());
        return ResponseEntity.ok().build();
    }
}

    

