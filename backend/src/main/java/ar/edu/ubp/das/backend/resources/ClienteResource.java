package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.ClienteResponseDto;
import ar.edu.ubp.das.backend.dto.CrearClienteDto;
import ar.edu.ubp.das.backend.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para gestión de clientes
 */
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteResource {

    @Autowired
    private ClienteService clienteService;

    /**
     * POST /api/clientes - Crear un nuevo cliente (Registro público)
     * Endpoint público (no requiere autenticación)
     */
    @PostMapping
    public ResponseEntity<?> crearCliente(@Valid @RequestBody CrearClienteDto crearClienteDto) {
        try {
            ClienteResponseDto cliente = clienteService.crearCliente(crearClienteDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear cliente: " + e.getMessage()));
        }
    }
}

