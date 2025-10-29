package ar.edu.ubp.das.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI para documentación automática de la API.
 * 
 * Documentación detallada: src/main/resources/openapi-docs.yaml
 * Swagger UI: http://localhost:8080/swagger-ui.html
 * API Docs JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ristorinoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Ristorino")
                        .description("Portal gastronómico para descubrir y reservar en restaurantes de Córdoba")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo DAS - UBP")
                                .email("soporte@ristorino.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("BearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("BearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT - Obtener desde: POST /api/clientes/register o POST /api/clientes/login")));
    }
}

