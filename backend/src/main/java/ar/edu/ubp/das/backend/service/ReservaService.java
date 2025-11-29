package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.ActualizarReservaDto;
import ar.edu.ubp.das.backend.dto.ConfirmarReservaDto;
import ar.edu.ubp.das.backend.dto.ConfirmarReservaResponseDto;
import ar.edu.ubp.das.backend.dto.CrearReservaDto;
import ar.edu.ubp.das.backend.dto.HorarioDisponibleDto;
import ar.edu.ubp.das.backend.dto.ReservaResponseDto;
import ar.edu.ubp.das.backend.dto.SucursalDto;
import ar.edu.ubp.das.backend.dto.UsuarioDto;
import ar.edu.ubp.das.backend.repository.ReservaRepository;
import ar.edu.ubp.das.backend.repository.RestauranteRepository;
import ar.edu.ubp.das.backend.repository.ClienteRepository;
import ar.edu.ubp.das.backend.client.RestauranteClient;
import ar.edu.ubp.das.backend.client.RestauranteClientFactory;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {
    
    private final ReservaRepository reservaRepository;
    private final RestauranteRepository restauranteRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteClientFactory restauranteClientFactory;
    
    public ReservaService(ReservaRepository reservaRepository,
                         RestauranteRepository restauranteRepository,
                         ClienteRepository clienteRepository,
                         RestauranteClientFactory restauranteClientFactory) {
        this.reservaRepository = reservaRepository;
        this.restauranteRepository = restauranteRepository;
        this.clienteRepository = clienteRepository;
        this.restauranteClientFactory = restauranteClientFactory;
    }
    
    public List<ReservaResponseDto> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }
    
    public Optional<ReservaResponseDto> obtenerReservaPorId(String id) {
        return reservaRepository.findById(id);
    }
    
    public ReservaResponseDto crearReserva(CrearReservaDto crearReservaDto) {
        return reservaRepository.save(crearReservaDto);
    }
    
    public boolean actualizarReserva(String id, ActualizarReservaDto actualizarReservaDto) {
        return reservaRepository.update(actualizarReservaDto, id);
    }
    
    public boolean eliminarReserva(String id) {
        return reservaRepository.deleteById(id);
    }
    
    public boolean cambiarEstadoReserva(String id, String nuevoEstado) {
        return reservaRepository.updateEstado(id, nuevoEstado);
    }
    
    public boolean existeReserva(String id) {
        return reservaRepository.existsById(id);
    }
    
    public List<ReservaResponseDto> obtenerReservasPorNroCliente(String nroCliente, Integer nroIdioma) {
        return reservaRepository.findByNroCliente(nroCliente, nroIdioma);
    }
    
    public ConfirmarReservaResponseDto confirmarReserva(ConfirmarReservaDto request, String nroCliente) {
        int cantTotal = request.getCantAdultos() + request.getCantMenores();
        
        String codSucursalRestaurante = restauranteRepository.obtenerCodSucursalRestaurante(
                request.getNroRestaurante(),
                request.getNroSucursal()
        );
        
        if (codSucursalRestaurante == null || codSucursalRestaurante.trim().isEmpty()) {
            throw new RuntimeException("Sucursal no encontrada en el sistema del restaurante");
        }
        
        // El codZona que viene del request es el cod_zona interno de Ristorino
        // Necesitamos obtener el cod_zona_restaurante (externo) para comunicarnos con el SOAP
        String codZonaRestaurante = restauranteRepository.obtenerCodZonaRestaurante(
                request.getNroRestaurante(),
                request.getNroSucursal(),
                request.getCodZona()  // Este es el cod_zona interno
        );
        
        RestauranteClient client = restauranteClientFactory.getClient(request.getNroRestaurante());


        // MOVER ESTA LOGICA AL RESTAURANTE
        List<HorarioDisponibleDto> horarios = client.getHorariosDisponibles(
                request.getNroRestaurante(),
                codSucursalRestaurante,
                codZonaRestaurante,  // Usamos el cod_zona_restaurante (externo) para la consulta SOAP
                request.getFechaReserva(),
                cantTotal
        );
        
        HorarioDisponibleDto horarioSeleccionado = horarios.stream()
                .filter(h -> h.getCodZona().equals(codZonaRestaurante) 
                        && h.getHoraDesde() != null 
                        && h.getHoraDesde().equals(request.getHoraDesde()))
                .findFirst()
                .orElse(null);
        
        if (horarioSeleccionado == null) {
            throw new RuntimeException("El horario seleccionado no está disponible");
        }
        
        if (horarioSeleccionado.getDisponibilidad() < cantTotal) {
            throw new RuntimeException("No hay suficiente capacidad disponible. Disponibilidad: " + 
                    horarioSeleccionado.getDisponibilidad() + ", Solicitado: " + cantTotal);
        }
        
        if (request.getCantMenores() > 0 && (horarioSeleccionado.getPermiteMenores() == null || !horarioSeleccionado.getPermiteMenores())) {
            throw new RuntimeException("La zona seleccionada no permite menores");
        }
        
        BigDecimal costoReserva = reservaRepository.obtenerCostoReserva(request.getFechaReserva());
        
        UsuarioDto cliente = clienteRepository.findByNroCliente(nroCliente);
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }
        
        String codEstadoConfirmada = obtenerCodigoEstado("Confirmada");
        String codigoReserva = reservaRepository.registrarReservaRistorino(
                request.getNroRestaurante(),
                request.getNroSucursal(),
                request.getCodZona(),  // Usamos el cod_zona interno directamente
                request.getFechaReserva(),
                request.getHoraDesde(),
                nroCliente,
                request.getCantAdultos(),
                request.getCantMenores(),
                codEstadoConfirmada,
                costoReserva,
                null,
                null
        );
        
        String codReservaRestaurante = client.registrarReserva(
                nroCliente,
                cliente.getApellido(),
                cliente.getNombre(),
                cliente.getCorreo(),
                cliente.getTelefonos(),
                request.getNroRestaurante(),
                codSucursalRestaurante,
                codZonaRestaurante,  // Usamos el cod_zona_restaurante (externo) para el SOAP
                request.getFechaReserva(),
                request.getHoraDesde(),
                request.getCantAdultos(),
                request.getCantMenores()
        );
        
        reservaRepository.actualizarCodReservaSucursal(codigoReserva, codReservaRestaurante);
        
        SucursalDto sucursal = restauranteRepository.obtenerSucursales(request.getNroRestaurante())
                .stream()
                .filter(s -> s.getNroSucursal().equals(request.getNroSucursal()))
                .findFirst()
                .orElse(null);
        
        String urlMapa = null;
        if (sucursal != null && sucursal.getDireccion() != null) {
            String direccionCompleta = sucursal.getDireccion();
            if (sucursal.getLocalidad() != null) {
                direccionCompleta += ", " + sucursal.getLocalidad();
            }
            if (sucursal.getProvincia() != null) {
                direccionCompleta += ", " + sucursal.getProvincia();
            }
            urlMapa = "https://www.google.com/maps/search/?api=1&query=" + 
                     URLEncoder.encode(direccionCompleta, StandardCharsets.UTF_8);
        }
        
        ConfirmarReservaResponseDto response = new ConfirmarReservaResponseDto();
        response.setCodigoReserva(codigoReserva);
        response.setNroRestaurante(request.getNroRestaurante());
        response.setNroSucursal(request.getNroSucursal());
        response.setCodZona(request.getCodZona());  // Mantenemos el cod_zona del restaurante en la respuesta
        response.setFechaReserva(request.getFechaReserva());
        response.setHoraDesde(request.getHoraDesde());
        response.setCantAdultos(request.getCantAdultos());
        response.setCantMenores(request.getCantMenores());
        response.setCostoReserva(costoReserva);
        response.setMensaje("Reserva confirmada exitosamente");
        response.setUrlMapa(urlMapa);
        
        return response;
    }
    
    private String obtenerCodigoEstado(String nomEstado) {
        return reservaRepository.obtenerCodigoEstado(nomEstado);
    }
}
