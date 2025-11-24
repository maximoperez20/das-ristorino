package ar.edu.ubp.das.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (accesibles sin autenticación)
                       .requestMatchers(
                           "/actuator/health",
                           "/api/restaurantes/**",     // Consulta de restaurantes y búsqueda NLP - PÚBLICO
                           "/api/promociones/**",      // Consulta de promociones - PÚBLICO
                           "/api/localidades/**",      // Consulta de localidades - PÚBLICO
                           "/api/preferencias/categorias",  // Consulta de categorías de preferencias - PÚBLICO
                           "/api/clientes/register",   // Registro de cliente - PÚBLICO
                           "/api/clientes/login",      // Login de cliente - PÚBLICO
                           // Swagger UI (documentación API)
                           "/swagger-ui/**",
                           "/swagger-ui.html",
                           "/v3/api-docs/**",
                           "/webjars/**"
                       ).permitAll()
                // Endpoints protegidos (requieren token JWT)
                .requestMatchers(
                    "/api/reservas/**",
                    "/api/preferencias/guardar",       // Guardar preferencias - PRIVADO
                    "/api/preferencias/mis-preferencias",  // Obtener preferencias del cliente - PRIVADO
                    "/api/usuarios/**",
                    "/api/contenidos/**"        // Generación de contenido con IA - PRIVADO
                ).authenticated()
                // Por defecto, cualquier otro endpoint requiere autenticación
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:4201", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Crear la clave de la misma forma que JwtService
        // Usar SHA-256 hash para asegurar tamaño consistente de 32 bytes
        // Esto es compatible con Keys.hmacShaKeyFor() de JJWT
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedKey = digest.digest(jwtSecret.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec key = new SecretKeySpec(hashedKey, "HmacSHA256");
            return NimbusJwtDecoder.withSecretKey(key)
                    .build();
        } catch (NoSuchAlgorithmException e) {
            // Fallback: usar la clave directamente si SHA-256 no está disponible
            byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
            // Asegurar mínimo 32 bytes para HS256
            if (keyBytes.length < 32) {
                byte[] expandedKey = new byte[32];
                System.arraycopy(keyBytes, 0, expandedKey, 0, keyBytes.length);
                for (int i = keyBytes.length; i < 32; i++) {
                    expandedKey[i] = keyBytes[i % keyBytes.length];
                }
                keyBytes = expandedKey;
            }
            SecretKeySpec key = new SecretKeySpec(keyBytes, "HmacSHA256");
            return NimbusJwtDecoder.withSecretKey(key)
                    .build();
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
