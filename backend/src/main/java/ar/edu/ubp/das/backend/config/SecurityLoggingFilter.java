package ar.edu.ubp.das.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

/**
 * Filtro para loggear información de seguridad y autenticación
 */
@Component
@Order(1)
public class SecurityLoggingFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityLoggingFilter.class);
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Loggear información de la petición
        logger.info("=== SECURITY REQUEST ===");
        logger.info("Method: {}", method);
        logger.info("Path: {}", path);
        logger.info("Query String: {}", request.getQueryString());
        
        // Loggear headers relevantes
        String authHeader = request.getHeader("Authorization");
        logger.info("Authorization Header: {}", authHeader != null ? (authHeader.length() > 20 ? authHeader.substring(0, 20) + "..." : authHeader) : "NO PRESENT");
        
        // Loggear todos los headers para debugging
        Enumeration<String> headerNames = request.getHeaderNames();
        logger.info("All Headers:");
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if (headerName.equalsIgnoreCase("Authorization") && headerValue != null && headerValue.length() > 50) {
                logger.info("  {}: {}...", headerName, headerValue.substring(0, 50));
            } else {
                logger.info("  {}: {}", headerName, headerValue);
            }
        }
        
        // Continuar con el filtro
        filterChain.doFilter(request, response);
        
        // Loggear respuesta
        logger.info("Response Status: {}", response.getStatus());
        logger.info("=== END SECURITY REQUEST ===");
    }
}

