package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.client.RestauranteClient;
import ar.edu.ubp.das.backend.client.RestauranteClientFactory;
import ar.edu.ubp.das.backend.dto.ClickNoNotificadoDto;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClickRequest;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClicksBatchRequest;
import ar.edu.ubp.das.backend.dto.restaurante.NotificarClicksBatchResponse;
import ar.edu.ubp.das.backend.repository.ClickRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BatchClickService {

    private static final Logger logger = LoggerFactory.getLogger(BatchClickService.class);

    @Autowired
    private ClickRepository clickRepository;

    @Autowired
    private RestauranteClientFactory restauranteClientFactory;

    public void procesarClicksNoNotificados() {
        try {
            List<ClickNoNotificadoDto> clicksNoNotificados = clickRepository.obtenerClicksNoNotificados();
            
            if (clicksNoNotificados.isEmpty()) {
                logger.info("No hay clicks pendientes de notificación");
                return;
            }

            logger.info("Iniciando procesamiento en bloque de {} clicks", clicksNoNotificados.size());

            // Agrupar clicks por restaurante
            Map<String, List<ClickNoNotificadoDto>> clicksPorRestaurante = new HashMap<>();
            int clicksSinCodRestaurante = 0;

            for (ClickNoNotificadoDto click : clicksNoNotificados) {
                if (click.getCodContenidoRestaurante() == null || click.getCodContenidoRestaurante().trim().isEmpty()) {
                    logger.warn("Click {} no tiene cod_contenido_restaurante, saltando...", click.getNroClick());
                    clicksSinCodRestaurante++;
                    continue;
                }

                String nroRestaurante = click.getNroRestaurante();
                clicksPorRestaurante.computeIfAbsent(nroRestaurante, k -> new ArrayList<>()).add(click);
            }

            if (clicksPorRestaurante.isEmpty()) {
                logger.warn("No hay clicks válidos para procesar (todos sin cod_contenido_restaurante)");
                return;
            }

            int totalExitosos = 0;
            int totalFallidos = clicksSinCodRestaurante;
            int totalRestaurantes = clicksPorRestaurante.size();

            // Procesar cada grupo de restaurante en bloque
            for (Map.Entry<String, List<ClickNoNotificadoDto>> entry : clicksPorRestaurante.entrySet()) {
                String nroRestaurante = entry.getKey();
                List<ClickNoNotificadoDto> clicksRestaurante = entry.getValue();

                try {
                    logger.info("Procesando {} clicks para restaurante {}", clicksRestaurante.size(), nroRestaurante);

                    // Convertir a NotificarClickRequest
                    List<NotificarClickRequest> clicksRequest = new ArrayList<>();
                    for (ClickNoNotificadoDto click : clicksRestaurante) {
                        clicksRequest.add(new NotificarClickRequest(
                                click.getNroRestaurante(),
                                click.getCodContenidoRestaurante(),
                                click.getNroClick(),
                                click.getFechaHoraRegistro(),
                                click.getNroCliente(),
                                click.getCostoClick()
                        ));
                    }

                    // Enviar en bloque
                    RestauranteClient client = restauranteClientFactory.getClient(nroRestaurante);
                    NotificarClicksBatchRequest batchRequest = new NotificarClicksBatchRequest(
                            nroRestaurante,
                            clicksRequest
                    );

                    NotificarClicksBatchResponse batchResponse = client.notificarClicksBatch(batchRequest);

                    logger.info("Respuesta batch recibida - Exitoso: {}, Total: {}, Exitosos: {}, Fallidos: {}", 
                            batchResponse != null ? batchResponse.isExitoso() : false,
                            batchResponse != null ? batchResponse.getTotalClicks() : 0,
                            batchResponse != null ? batchResponse.getClicksExitosos() : 0,
                            batchResponse != null ? batchResponse.getClicksFallidos() : 0);

                    // Procesar resultados
                    if (batchResponse != null && batchResponse.getResultados() != null) {
                        logger.info("Procesando {} resultados individuales", batchResponse.getResultados().size());
                        for (NotificarClicksBatchResponse.ClickProcesadoDto resultado : batchResponse.getResultados()) {
                            if (resultado.isExitoso()) {
                                // Buscar el click original para marcarlo como notificado
                                ClickNoNotificadoDto clickOriginal = clicksRestaurante.stream()
                                        .filter(c -> c.getNroClick().equals(resultado.getNroClick()))
                                        .findFirst()
                                        .orElse(null);

                                if (clickOriginal != null) {
                                    clickRepository.marcarClickComoNotificado(
                                            clickOriginal.getNroRestaurante(),
                                            clickOriginal.getNroIdioma(),
                                            clickOriginal.getNroContenido(),
                                            clickOriginal.getNroClick()
                                    );
                                    logger.debug("Click {} marcado como notificado", resultado.getNroClick());
                                    totalExitosos++;
                                } else {
                                    logger.warn("No se encontró el click original para nroClick: {}", resultado.getNroClick());
                                }
                            } else {
                                totalFallidos++;
                                logger.warn("Click {} no pudo ser notificado: {}", 
                                        resultado.getNroClick(), resultado.getMensaje());
                            }
                        }
                    } else {
                        // Si la respuesta no tiene resultados detallados, marcar todos como fallidos
                        logger.error("Respuesta de batch inválida para restaurante {}", nroRestaurante);
                        totalFallidos += clicksRestaurante.size();
                    }

                } catch (Exception e) {
                    logger.error("Error al procesar batch de clicks para restaurante {}: {}", 
                            nroRestaurante, e.getMessage(), e);
                    totalFallidos += clicksRestaurante.size();
                }
            }

            logger.info("Batch de notificación completado - Restaurantes: {}, Total procesados: {}, Exitosos: {}, Fallidos: {}", 
                    totalRestaurantes, clicksNoNotificados.size(), totalExitosos, totalFallidos);

        } catch (Exception e) {
            logger.error("Error crítico en batch de notificación de clicks: {}", e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void ejecutarBatchProgramado() {
        procesarClicksNoNotificados();
    }
}

