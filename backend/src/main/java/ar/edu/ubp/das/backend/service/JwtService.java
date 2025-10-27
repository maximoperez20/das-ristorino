package ar.edu.ubp.das.backend.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        Map<String, Object> claims = new HashMap<>();
        claims.put("nroCliente", nroCliente);
        claims.put("correo", correo);
        claims.put("nombre", nombre);
        claims.put("apellido", apellido);
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(correo)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}

