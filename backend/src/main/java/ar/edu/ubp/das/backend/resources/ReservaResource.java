package ar.edu.ubp.das.backend.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaResource {

    // Simulamos una base de datos en memoria
    private final Map<Long, Reserva> reservas = new HashMap<>();
    private Long nextId = 1L;

    // Clase interna para representar una reserva
    public static class Reserva {
        private Long id;
        private String nombreCliente;
        private String email;
        private String telefono;
        private LocalDateTime fechaHora;
        private Integer cantidadPersonas;
        private String estado;
        private String observaciones;

        // Constructores
        public Reserva() {}

        public Reserva(Long id, String nombreCliente, String email, String telefono, 
                      LocalDateTime fechaHora, Integer cantidadPersonas, String estado, String observaciones) {
            this.id = id;
            this.nombreCliente = nombreCliente;
            this.email = email;
            this.telefono = telefono;
            this.fechaHora = fechaHora;
            this.cantidadPersonas = cantidadPersonas;
            this.estado = estado;
            this.observaciones = observaciones;
        }

        // Getters y Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getNombreCliente() { return nombreCliente; }
        public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
        
        public LocalDateTime getFechaHora() { return fechaHora; }
        public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
        
        public Integer getCantidadPersonas() { return cantidadPersonas; }
        public void setCantidadPersonas(Integer cantidadPersonas) { this.cantidadPersonas = cantidadPersonas; }
        
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        
        public String getObservaciones() { return observaciones; }
        public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    }

    // Inicializar con datos dummy
    {
        // Agregar algunas reservas de ejemplo
        reservas.put(1L, new Reserva(1L, "Juan Pérez", "juan@email.com", "123456789", 
            LocalDateTime.now().plusDays(1), 4, "CONFIRMADA", "Mesa cerca de la ventana"));
        reservas.put(2L, new Reserva(2L, "María García", "maria@email.com", "987654321", 
            LocalDateTime.now().plusDays(2), 2, "PENDIENTE", "Cumpleaños"));
        reservas.put(3L, new Reserva(3L, "Carlos López", "carlos@email.com", "555666777", 
            LocalDateTime.now().plusDays(3), 6, "CONFIRMADA", "Cena de negocios"));
        nextId = 4L;
    }

    // GET /api/reservas - Obtener todas las reservas
    @GetMapping
    public ResponseEntity<List<Reserva>> getAllReservas() {
        return ResponseEntity.ok(new ArrayList<>(reservas.values()));
    }

    // GET /api/reservas/{id} - Obtener una reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> getReservaById(@PathVariable Long id) {
        Reserva reserva = reservas.get(id);
        if (reserva != null) {
            return ResponseEntity.ok(reserva);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/reservas - Crear una nueva reserva
    @PostMapping
    public ResponseEntity<Reserva> createReserva(@RequestBody Reserva reserva) {
        reserva.setId(nextId++);
        reserva.setEstado("PENDIENTE");
        reservas.put(reserva.getId(), reserva);
        return ResponseEntity.ok(reserva);
    }

    // PUT /api/reservas/{id} - Actualizar una reserva existente
    @PutMapping("/{id}")
    public ResponseEntity<Reserva> updateReserva(@PathVariable Long id, @RequestBody Reserva reservaActualizada) {
        if (reservas.containsKey(id)) {
            reservaActualizada.setId(id);
            reservas.put(id, reservaActualizada);
            return ResponseEntity.ok(reservaActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/reservas/{id} - Eliminar una reserva
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable Long id) {
        if (reservas.containsKey(id)) {
            reservas.remove(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/reservas/estado/{estado} - Obtener reservas por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Reserva>> getReservasByEstado(@PathVariable String estado) {
        List<Reserva> reservasFiltradas = reservas.values().stream()
            .filter(reserva -> reserva.getEstado().equalsIgnoreCase(estado))
            .toList();
        return ResponseEntity.ok(reservasFiltradas);
    }

    // PUT /api/reservas/{id}/estado - Cambiar estado de una reserva
    @PutMapping("/{id}/estado")
    public ResponseEntity<Reserva> updateEstadoReserva(@PathVariable Long id, @RequestBody Map<String, String> estadoRequest) {
        Reserva reserva = reservas.get(id);
        if (reserva != null) {
            String nuevoEstado = estadoRequest.get("estado");
            reserva.setEstado(nuevoEstado);
            return ResponseEntity.ok(reserva);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/reservas/cliente/{email} - Obtener reservas por email del cliente
    @GetMapping("/cliente/{email}")
    public ResponseEntity<List<Reserva>> getReservasByCliente(@PathVariable String email) {
        List<Reserva> reservasCliente = reservas.values().stream()
            .filter(reserva -> reserva.getEmail().equalsIgnoreCase(email))
            .toList();
        return ResponseEntity.ok(reservasCliente);
    }
}
