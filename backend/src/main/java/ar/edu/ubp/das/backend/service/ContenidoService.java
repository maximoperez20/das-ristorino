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
        // Obtener contexto del restaurante desde la BD
        RestauranteContextoDto contexto = contenidoRepository.obtenerContextoRestaurante(
            request.getNroRestaurante(), 
            request.getNroSucursal()
        ).orElseThrow(() -> new RuntimeException("Restaurante no encontrado con ID: " + request.getNroRestaurante()));

        // Determinar qué prompt ID usar
        String promptId = (request.getPromptId() != null && !request.getPromptId().isEmpty()) 
                          ? request.getPromptId() 
                          : defaultPromptId;

        // Obtener información del idioma
        String codIdioma = contenidoRepository.obtenerCodIdioma(request.getNroIdioma());
        String nomIdioma = contenidoRepository.obtenerNomIdioma(request.getNroIdioma());

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

        // Generar contenido con OpenAI
        String contenidoGenerado;
        try {
            contenidoGenerado = openAIService.generarContenidoPublicitario(prompt, promptId);
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

        try {
            String codSucursalRestaurante = null;
            if (request.getNroSucursal() != null && !request.getNroSucursal().trim().isEmpty()) {
                codSucursalRestaurante = contenidoRepository.obtenerCodSucursalRestaurante(
                    request.getNroRestaurante(),
                    request.getNroSucursal()
                );
                if (codSucursalRestaurante == null) {
                    logger.warn("Sucursal encontrada pero cod_sucursal_restaurante es NULL. La sucursal puede no estar sincronizada con el sistema del restaurante.");
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

            if (response.isExitoso() && response.getNroContenido() != null && !response.getNroContenido().trim().isEmpty()) {
                try {
                    boolean actualizado = contenidoRepository.actualizarCodContenidoRestaurante(
                        request.getNroRestaurante(),
                        request.getNroIdioma(),
                        resultado.getNroContenido(),
                        response.getNroContenido()
                    );
                    
                    if (!actualizado) {
                        logger.error("ERROR CRÍTICO: No se pudo actualizar cod_contenido_restaurante. " +
                                "Los clicks no podrán ser notificados. " +
                                "nroRestaurante: {}, nroIdioma: {}, nroContenido: {}, codContenidoRestaurante: {}",
                                request.getNroRestaurante(), request.getNroIdioma(), 
                                resultado.getNroContenido(), response.getNroContenido());
                    }
                } catch (Exception e) {
                    logger.error("ERROR CRÍTICO al actualizar cod_contenido_restaurante. " +
                            "Los clicks de este contenido NO podrán ser notificados. " +
                            "nroRestaurante: {}, nroIdioma: {}, nroContenido: {}, codContenidoRestaurante: {}. " +
                            "Error: {}", 
                            request.getNroRestaurante(), request.getNroIdioma(), 
                            resultado.getNroContenido(), response.getNroContenido(), e.getMessage(), e);
                }
            } else {
                logger.warn("No se pudo registrar el contenido en SOAP o no se devolvió nroContenido. " +
                        "El cod_contenido_restaurante NO se actualizará. " +
                        "Mensaje: {}", response.getMensaje() != null ? response.getMensaje() : "Sin mensaje");
            }
            
        } catch (Exception e) {
            logger.error("Error al registrar contenido en el sistema del restaurante (continuando de todas formas): {}", 
                e.getMessage(), e);
            logger.error("IMPORTANTE: El cod_contenido_restaurante NO se actualizará debido al error. " +
                    "Los clicks de este contenido NO podrán ser notificados hasta que se registre correctamente en SOAP.");
        }

        return resultado;
    }
}

