package ar.edu.ubp.das.backend.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Servicio para generación y validación de tokens JWT
 */
@Service
public class JwtService {
    
    @Value("${security.jwt.secret}")
    private String jwtSecret;
    
    @Value("${security.jwt.expiration:86400000}") // 24 horas por defecto
    private long jwtExpiration;
    
    /**
     * Genera un token JWT para un cliente
     */
    public String generateToken(String nroCliente, String correo, String nombre, String apellido) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .claim("nroCliente", nroCliente)
                .claim("correo", correo)
                .claim("nombre", nombre)
                .claim("apellido", apellido)
                .subject(correo)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }
}

