package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.RegistrarReservaRistorinoDto;

import ar.edu.ubp.das.backend.dto.ActualizarReservaDto;
import ar.edu.ubp.das.backend.dto.ConfirmarReservaDto;
import ar.edu.ubp.das.backend.dto.ConfirmarReservaResponseDto;
import ar.edu.ubp.das.backend.dto.CrearReservaDto;
import ar.edu.ubp.das.backend.dto.DatosCancelarReservaDto;
import ar.edu.ubp.das.backend.dto.HorarioDisponibleDto;
import ar.edu.ubp.das.backend.dto.ReservaResponseDto;
import ar.edu.ubp.das.backend.dto.SucursalDto;
import ar.edu.ubp.das.backend.dto.UsuarioDto;
import ar.edu.ubp.das.backend.repository.ReservaRepository;
import ar.edu.ubp.das.backend.repository.RestauranteRepository;
import ar.edu.ubp.das.backend.repository.ClienteRepository;
import ar.edu.ubp.das.backend.client.RestauranteClient;
import ar.edu.ubp.das.backend.client.RestauranteClientFactory;
import ar.edu.ubp.das.backend.exception.HorarioNoDisponibleException;
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
        
        BigDecimal costoReserva = reservaRepository.obtenerCostoReserva(request.getFechaReserva());
        
        UsuarioDto cliente = clienteRepository.findByNroCliente(nroCliente);
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }
        
        // Crear la reserva inicialmente con estado "Pendiente"
        String codEstadoPendiente = obtenerCodigoEstado("Pendiente");
        if (codEstadoPendiente == null) {
            throw new RuntimeException("El estado 'Pendiente' no existe en la base de datos. Verifique la configuración de estados.");
        }
        
        // Crear DTO tipado en lugar de pasar 13 parámetros sueltos
        RegistrarReservaRistorinoDto reservaDto = new RegistrarReservaRistorinoDto(
                request.getNroRestaurante(),
                request.getNroSucursal(),
                request.getCodZona(),  // Usamos el cod_zona interno directamente
                request.getFechaReserva(),
                request.getHoraDesde(),
                nroCliente,
                request.getCantAdultos(),
                request.getCantMenores(),
                codEstadoPendiente,  // Estado inicial: Pendiente
                costoReserva,
                request.getPreferenciasReserva(),
                request.getObservaciones(),  // observaciones --> notas
                null   // codReservaSucursal
        );
        
        // Registrar primero en Ristorino con estado Pendiente
        String codigoReserva = reservaRepository.registrarReservaRistorino(reservaDto);
        
        try {
            // Intentar registrar en el restaurante (ahora con validación en el SP)
            // El SP valida disponibilidad, capacidad, zona habilitada y turno válido
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
                    request.getCantMenores(),
                    request.getObservaciones()
            );
            
            // Si se registró exitosamente en el restaurante, actualizar el código de reserva del restaurante
            reservaRepository.actualizarCodReservaSucursal(codigoReserva, codReservaRestaurante);
            
            // Actualizar el estado a "Confirmada" ya que se confirmó exitosamente en el restaurante
            reservaRepository.updateEstado(codigoReserva, "Confirmada");
            
        } catch (RuntimeException e) {
            // Si falla por disponibilidad o validación, obtener nuevos horarios y hacer rollback
            String mensajeError = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            
            if (mensajeError.contains("disponibilidad") || 
                mensajeError.contains("capacidad") ||
                mensajeError.contains("no permite menores") ||
                mensajeError.contains("no está disponible") ||
                mensajeError.contains("no existe") ||
                mensajeError.contains("habilitada")) {
                
                // Obtener horarios actualizados para mostrar al usuario
                // Obtener TODOS los horarios (sin filtrar por codZona) para mostrar todas las opciones disponibles
                List<HorarioDisponibleDto> horariosActualizados = client.getHorariosDisponibles(
                    request.getNroRestaurante(),
                    codSucursalRestaurante,
                    null, // null para obtener todas las zonas
                    request.getFechaReserva(),
                    cantTotal
                );
                
                // Mapear cod_zona_restaurante a cod_zona interno
                for (HorarioDisponibleDto horario : horariosActualizados) {
                    if (horario.getCodZona() != null && !horario.getCodZona().trim().isEmpty()) {
                        String codZonaInterno = restauranteRepository.obtenerCodZonaInterno(
                            request.getNroRestaurante(), 
                            request.getNroSucursal(), 
                            horario.getCodZona()
                        );
                        if (codZonaInterno != null && !codZonaInterno.trim().isEmpty()) {
                            horario.setCodZona(codZonaInterno);
                        }
                    }
                }
                
                // Cancelar la reserva en Ristorino (rollback)
                reservaRepository.updateEstado(codigoReserva, "Cancelada");
                
                // Lanzar excepción con información de horarios actualizados
                throw new HorarioNoDisponibleException(
                    "El horario seleccionado ya no está disponible. Por favor, seleccione otro horario.",
                    horariosActualizados
                );
            }
            
            // Para otros errores, también hacer rollback pero sin horarios
            reservaRepository.updateEstado(codigoReserva, "Cancelada");
            throw e; // Re-lanzar el error original
        }
        
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

    public boolean cancelarReserva(String nroReserva, String motivoCancelacion) {
        Optional<DatosCancelarReservaDto> datosReserva = reservaRepository.obtenerDatosCancelarReservaDto(nroReserva);
        if (datosReserva.isEmpty()) {
            throw new RuntimeException("Reserva no encontrada");
        }
        DatosCancelarReservaDto reserva = datosReserva.get();

        RestauranteClient client = restauranteClientFactory.getClient(reserva.getNroRestaurante());
        client.cancelarReserva(reserva.getNroRestaurante(), reserva.getCodReservaSucursal(), motivoCancelacion);
        
        reservaRepository.cancelarReserva(nroReserva);
        
        return true;
    }
}
