package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.TokenRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
     * Usa la misma forma de crear la clave que SecurityConfig para compatibilidad
     */
    public String generateToken(TokenRequest tokenRequest) {
        return generateToken(tokenRequest.getNroCliente(), tokenRequest.getCorreo(), 
                           tokenRequest.getNombre(), tokenRequest.getApellido());
    }
    
    public String generateToken(String nroCliente, String correo, String nombre, String apellido) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        // Crear la clave usando SHA-256 hash para asegurar tamaño consistente de 32 bytes
        // Esto es compatible con Nimbus JWT decoder de Spring Security
        SecretKey key = getSecretKey();
        
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
    
    /**
     * Genera una SecretKey a partir del jwtSecret usando SHA-256 hash
     * Esto asegura que la clave tenga exactamente 32 bytes (tamaño requerido para HS256)
     * y sea compatible con Nimbus JWT decoder
     */
    private SecretKey getSecretKey() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedKey = digest.digest(jwtSecret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(hashedKey);
        } catch (NoSuchAlgorithmException e) {
            // Fallback: usar la clave directamente si SHA-256 no está disponible
            return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        }
    }
}

