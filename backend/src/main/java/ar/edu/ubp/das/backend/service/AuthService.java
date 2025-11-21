package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.AuthResponseDto;
import ar.edu.ubp.das.backend.dto.ClienteResponseDto;
import ar.edu.ubp.das.backend.dto.CrearClienteDto;
import ar.edu.ubp.das.backend.dto.LoginRequestDto;
import ar.edu.ubp.das.backend.dto.UsuarioDto;
import ar.edu.ubp.das.backend.repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación para clientes
 */
@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    private final ClienteRepository clienteRepository;
    private final ClienteService clienteService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    public AuthService(ClienteRepository clienteRepository, ClienteService clienteService,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.clienteRepository = clienteRepository;
        this.clienteService = clienteService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    
    /**
     * Login de cliente
     */
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        logger.info("Intentando login para correo: {}", loginRequest.getCorreo());
        
        UsuarioDto cliente = clienteRepository.findByCorreo(loginRequest.getCorreo());
        
        if (cliente == null) {
            logger.warn("Cliente no encontrado: {}", loginRequest.getCorreo());
            throw new RuntimeException("Credenciales inválidas");
        }
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), cliente.getClave())) {
            logger.warn("Contraseña incorrecta para: {}", loginRequest.getCorreo());
            throw new RuntimeException("Credenciales inválidas");
        }
        
        if (!cliente.getHabilitado()) {
            logger.warn("Cliente deshabilitado: {}", loginRequest.getCorreo());
            throw new RuntimeException("Usuario deshabilitado");
        }
        
        String token = jwtService.generateToken(
            cliente.getNroCliente(),
            cliente.getCorreo(),
            cliente.getNombre(),
            cliente.getApellido()
        );
        
        logger.info("Login exitoso para: {}", loginRequest.getCorreo());
        
        return new AuthResponseDto(
            token,
            cliente.getNroCliente(),
            cliente.getNombre(),
            cliente.getApellido(),
            cliente.getCorreo()
        );
    }
    
    /**
     * Registro de nuevo cliente con login automático.
     * Genera token JWT después de crear el cliente.
     */
    public AuthResponseDto register(CrearClienteDto crearClienteDto) {
        logger.info("Registrando nuevo cliente: {}", crearClienteDto.getCorreo());
        
        ClienteResponseDto clienteCreado = clienteService.crearCliente(crearClienteDto);
        
        String token = jwtService.generateToken(
            clienteCreado.getNroCliente(),
            clienteCreado.getCorreo(),
            clienteCreado.getNombre(),
            clienteCreado.getApellido()
        );
        
        logger.info("Registro y login automático exitoso para: {}", crearClienteDto.getCorreo());
        
        return new AuthResponseDto(
            token,
            clienteCreado.getNroCliente(),
            clienteCreado.getNombre(),
            clienteCreado.getApellido(),
            clienteCreado.getCorreo()
        );
    }
}

