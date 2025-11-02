package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.client.RestauranteClient;
import ar.edu.ubp.das.backend.client.RestauranteClientFactory;
import ar.edu.ubp.das.backend.dto.ContenidoGeneradoDto;
import ar.edu.ubp.das.backend.dto.GenerarContenidoRequestDto;
import ar.edu.ubp.das.backend.dto.RestauranteContextoDto;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoRequest;
import ar.edu.ubp.das.backend.dto.restaurante.RegistrarContenidoResponse;
import ar.edu.ubp.das.backend.repository.ContenidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestión de contenidos generados con IA.
 * Orquesta la recopilación de datos, generación con OpenAI y almacenamiento.
 */
@Service
public class ContenidoService {

    private static final Logger logger = LoggerFactory.getLogger(ContenidoService.class);

    @Autowired
    private ContenidoRepository contenidoRepository;

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private RestauranteClientFactory restauranteClientFactory;

    @Value("${openai.prompt.id}")
    private String defaultPromptId;

    /**
     * Genera contenido publicitario con IA para un restaurante/sucursal.
     *
     * @param request Datos de la solicitud (restaurante, sucursal, idioma)
     * @return DTO con el contenido generado y guardado
     * @throws RuntimeException si no se encuentra el restaurante o hay error en la generación
     */
    public ContenidoGeneradoDto generarContenido(GenerarContenidoRequestDto request) {
        logger.info("Iniciando generación de contenido para restaurante: {}, sucursal: {}, idioma: {}", 
                    request.getNroRestaurante(), 
                    request.getNroSucursal(), 
                    request.getNroIdioma());

        // Obtener contexto del restaurante desde la BD
        RestauranteContextoDto contexto = contenidoRepository.obtenerContextoRestaurante(
            request.getNroRestaurante(), 
            request.getNroSucursal()
        ).orElseThrow(() -> new RuntimeException("Restaurante no encontrado con ID: " + request.getNroRestaurante()));
        logger.info("Contexto del restaurante obtenido: {}", contexto);

        // Determinar qué prompt ID usar
        String promptId = (request.getPromptId() != null && !request.getPromptId().isEmpty()) 
                          ? request.getPromptId() 
                          : defaultPromptId;
        
        logger.info("🎯 Usando Prompt ID: {}", promptId);

        // Obtener información del idioma
        String codIdioma = contenidoRepository.obtenerCodIdioma(request.getNroIdioma());
        String nomIdioma = contenidoRepository.obtenerNomIdioma(request.getNroIdioma());
        logger.info("🌐 Idioma seleccionado: {} ({})", nomIdioma, codIdioma);

        // Construir prompt con el contexto e idioma
        String prompt = openAIService.construirPrompt(
            contexto.getRazonSocial(),
            contexto.getNombreSucursal(),
            contexto.getDireccion(),
            contexto.getLocalidad(),
            contexto.getTiposComida(),
            contexto.getAmbientes(),
            contexto.getRangosPrecios(),
            contexto.getObservacionesAdicionales(),
            request.getContextoAdicional(),
            promptId,
            codIdioma,
            nomIdioma
        );

        logger.info("Prompt construido. Longitud: {} caracteres", prompt.length());

        // Generar contenido con OpenAI
        String contenidoGenerado;
        try {
            contenidoGenerado = openAIService.generarContenidoPublicitario(prompt, promptId);
            logger.info("Contenido generado exitosamente. Longitud: {} caracteres", contenidoGenerado.length());
        } catch (Exception e) {
            logger.error("Error al generar contenido con OpenAI: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar contenido con IA: " + e.getMessage(), e);
        }

        // Guardar en la base de datos
        ContenidoGeneradoDto resultado = contenidoRepository.guardarContenidoGenerado(
            request.getNroRestaurante(),
            request.getNroSucursal(),
            request.getNroIdioma(),
            contenidoGenerado
        ).orElseThrow(() -> new RuntimeException("Error al guardar el contenido generado en la base de datos"));
        resultado.setNombreRestaurante(contexto.getRazonSocial());
        resultado.setNombreSucursal(contexto.getNombreSucursal());

        logger.info("Contenido guardado exitosamente con nro_contenido: {}", resultado.getNroContenido());

        try {
            logger.info("Registrando contenido en el sistema del restaurante...");

            String codSucursalRestaurante = null;
            if (request.getNroSucursal() != null && !request.getNroSucursal().trim().isEmpty()) {
                codSucursalRestaurante = contenidoRepository.obtenerCodSucursalRestaurante(
                    request.getNroRestaurante(),
                    request.getNroSucursal()
                );
                if (codSucursalRestaurante == null) {
                    logger.warn("Sucursal encontrada pero cod_sucursal_restaurante es NULL. La sucursal puede no estar sincronizada con el sistema del restaurante.");
                } else {
                    logger.info("Cod sucursal restaurante obtenido: {}", codSucursalRestaurante);
                }
            }

            RestauranteClient client = restauranteClientFactory.getClient(request.getNroRestaurante());
            
            RegistrarContenidoRequest registroRequest = new RegistrarContenidoRequest(
                request.getNroRestaurante(),
                codSucursalRestaurante,
                contenidoGenerado,
                null,
                null
            );

            RegistrarContenidoResponse response = client.registrarContenido(registroRequest);

            if (response.isExitoso()) {
                logger.info("Contenido registrado exitosamente. ID del restaurante: {}", 
                    response.getNroContenido());
                
                contenidoRepository.actualizarCodContenidoRestaurante(
                    request.getNroRestaurante(),
                    request.getNroIdioma(),
                    resultado.getNroContenido(),
                    response.getNroContenido()
                );
                
                logger.info("cod_contenido_restaurante actualizado exitosamente");
            } else {
                logger.warn("No se pudo registrar el contenido: {}", response.getMensaje());
            }
            
        } catch (Exception e) {
            logger.error("Error al registrar contenido en el sistema del restaurante (continuando de todas formas): {}", 
                e.getMessage(), e);
        }

        return resultado;
    }
}

