package ar.edu.ubp.das.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

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
                           "/api/resenas/**",          // Reseñas públicas - PÚBLICO
                           "/api/preferencias/categorias",  // Consulta de categorías de preferencias - PÚBLICO
                           "/api/preferencias/*/especialidades-alimentarias",  // Especialidades por restaurante - PÚBLICO
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
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
                .bearerTokenResolver(request -> {
                    // Para endpoints públicos, no intentar resolver el token
                    String path = request.getRequestURI();
                    if (path.contains("/especialidades-alimentarias") || 
                        path.contains("/categorias") ||
                        path.contains("/api/restaurantes") ||
                        path.contains("/api/promociones") ||
                        path.contains("/api/localidades") ||
                        path.contains("/api/resenas") ||
                        path.contains("/api/clientes/register") ||
                        path.contains("/api/clientes/login")) {
                        logger.debug("Endpoint público detectado: {}, no se validará JWT", path);
                        return null; // No validar token para endpoints públicos
                    }
                    // Para otros endpoints, resolver el token normalmente
                    String authHeader = request.getHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        return authHeader.substring(7);
                    }
                    return null;
                })
                .authenticationEntryPoint((request, response, authException) -> {
                    String path = request.getRequestURI();
                    logger.error("=== JWT AUTHENTICATION ERROR ===");
                    logger.error("Path: {}", path);
                    logger.error("Exception: {}", authException.getClass().getSimpleName());
                    logger.error("Message: {}", authException.getMessage());
                    logger.error("Cause: {}", authException.getCause() != null ? authException.getCause().getMessage() : "N/A");
                    
                    // Si es un endpoint público, no debería llegar aquí, pero loggeamos de todas formas
                    if (path.contains("/especialidades-alimentarias") || path.contains("/categorias")) {
                        logger.error("ERROR: Endpoint público rechazado por autenticación!");
                        logger.error("Esto no debería pasar - el endpoint debería estar en permitAll()");
                        // Permitir acceso sin autenticación para endpoints públicos
                        response.setStatus(HttpServletResponse.SC_OK);
                        return;
                    }
                    
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"" + authException.getMessage() + "\"}");
                })
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    String path = request.getRequestURI();
                    logger.error("=== ACCESS DENIED ===");
                    logger.error("Path: {}", path);
                    logger.error("Exception: {}", accessDeniedException.getMessage());
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                })
            );
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
            logger.error("Error al crear JwtDecoder con SHA-256, usando fallback", e);
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
