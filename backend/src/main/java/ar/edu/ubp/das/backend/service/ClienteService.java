package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.ClienteResponseDto;
import ar.edu.ubp.das.backend.dto.CrearClienteDto;
import ar.edu.ubp.das.backend.dto.UsuarioDto;
import ar.edu.ubp.das.backend.repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestión de clientes
 */
@Service
public class ClienteService {
    
    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Crear un nuevo cliente
     */
    public ClienteResponseDto crearCliente(CrearClienteDto crearClienteDto) {
        logger.info("Creando cliente con correo: {}", crearClienteDto.getCorreo());
        
        // Verificar si el correo ya existe
        if (clienteRepository.existsByCorreo(crearClienteDto.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        
        // Crear el DTO del usuario
        UsuarioDto usuario = new UsuarioDto();
        usuario.setApellido(crearClienteDto.getApellido());
        usuario.setNombre(crearClienteDto.getNombre());
        usuario.setCorreo(crearClienteDto.getCorreo());
        usuario.setTelefonos(crearClienteDto.getTelefonos());
        usuario.setNroLocalidad(crearClienteDto.getNroLocalidad());
        usuario.setHabilitado(true);
        
        // Hashear la contraseña
        String hashedPassword = passwordEncoder.encode(crearClienteDto.getPassword());
        usuario.setClave(hashedPassword);
        
        // Guardar el cliente
        UsuarioDto clienteCreado = clienteRepository.save(usuario);
        
        logger.info("Cliente creado exitosamente con nro_cliente: {}", clienteCreado.getNroCliente());
        
        // Convertir a DTO de respuesta (sin exponer la contraseña)
        ClienteResponseDto response = new ClienteResponseDto();
        response.setNroCliente(clienteCreado.getNroCliente());
        response.setApellido(clienteCreado.getApellido());
        response.setNombre(clienteCreado.getNombre());
        response.setCorreo(clienteCreado.getCorreo());
        response.setTelefonos(clienteCreado.getTelefonos());
        response.setNroLocalidad(clienteCreado.getNroLocalidad());
        response.setHabilitado(clienteCreado.getHabilitado());
        
        return response;
    }
}

