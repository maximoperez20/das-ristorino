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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ar.edu.ubp.das.backend.dto.ClicksPorRestauranteDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BatchClickService {

    private static final Logger logger = LoggerFactory.getLogger(BatchClickService.class);

    private final ClickRepository clickRepository;
    private final RestauranteClientFactory restauranteClientFactory;
    
    public BatchClickService(ClickRepository clickRepository, RestauranteClientFactory restauranteClientFactory) {
        this.clickRepository = clickRepository;
        this.restauranteClientFactory = restauranteClientFactory;
    }

    public void procesarClicksNoNotificados() {
        try {
            List<ClickNoNotificadoDto> clicksNoNotificados = clickRepository.obtenerClicksNoNotificados();
            
            if (clicksNoNotificados.isEmpty()) {
                return;
            }

            logger.info("Iniciando procesamiento en bloque de {} clicks", clicksNoNotificados.size());

            Map<String, ClicksPorRestauranteDto> clicksPorRestaurante = new HashMap<>();
            int clicksSinCodRestaurante = 0;

            for (ClickNoNotificadoDto click : clicksNoNotificados) {
                if (click.getCodContenidoRestaurante() == null || click.getCodContenidoRestaurante().trim().isEmpty()) {
                    clicksSinCodRestaurante++;
                    continue;
                }
                clicksPorRestaurante.computeIfAbsent(
                    click.getNroRestaurante(), 
                    k -> new ClicksPorRestauranteDto(click.getNroRestaurante())
                ).agregarClick(click);
            }

            if (clicksPorRestaurante.isEmpty()) {
                logger.warn("No hay clicks válidos para procesar");
                return;
            }

            int totalExitosos = 0;
            int totalFallidos = clicksSinCodRestaurante;

            for (ClicksPorRestauranteDto clicksDto : clicksPorRestaurante.values()) {
                String nroRestaurante = clicksDto.getNroRestaurante();
                List<ClickNoNotificadoDto> clicksRestaurante = clicksDto.getClicks();

                try {
                    Map<String, ClickNoNotificadoDto> clicksMap = clicksRestaurante.stream()
                            .collect(Collectors.toMap(ClickNoNotificadoDto::getNroClick, c -> c));

                    List<NotificarClickRequest> clicksRequest = clicksRestaurante.stream()
                            .map(click -> new NotificarClickRequest(
                                    click.getNroRestaurante(),
                                    click.getCodContenidoRestaurante(),
                                    click.getNroClick(),
                                    click.getFechaHoraRegistro(),
                                    click.getNroCliente(),
                                    click.getCostoClick()
                            ))
                            .collect(Collectors.toList());

                    RestauranteClient client = restauranteClientFactory.getClient(nroRestaurante);
                    NotificarClicksBatchRequest batchRequest = new NotificarClicksBatchRequest(nroRestaurante, clicksRequest);
                    NotificarClicksBatchResponse batchResponse = client.notificarClicksBatch(batchRequest);

                    if (batchResponse != null && batchResponse.getResultados() != null) {
                        for (NotificarClicksBatchResponse.ClickProcesadoDto resultado : batchResponse.getResultados()) {
                            if (resultado.isExitoso()) {
                                ClickNoNotificadoDto clickOriginal = clicksMap.get(resultado.getNroClick());
                                if (clickOriginal != null) {
                                    try {
                                        clickRepository.marcarClickComoNotificado(
                                                clickOriginal.getNroRestaurante(),
                                                clickOriginal.getNroIdioma(),
                                                clickOriginal.getNroContenido(),
                                                clickOriginal.getNroClick()
                                        );
                                        totalExitosos++;
                                    } catch (Exception e) {
                                        logger.error("Error al marcar click {} como notificado: {}", 
                                                resultado.getNroClick(), e.getMessage());
                                        totalFallidos++;
                                    }
                                } else {
                                    totalFallidos++;
                                }
                            } else {
                                totalFallidos++;
                            }
                        }
                    } else {
                        logger.error("Respuesta de batch inválida para restaurante {}", nroRestaurante);
                        totalFallidos += clicksRestaurante.size();
                    }

                } catch (Exception e) {
                    logger.error("Error al procesar batch de clicks para restaurante {}: {}", 
                            nroRestaurante, e.getMessage(), e);
                    totalFallidos += clicksRestaurante.size();
                }
            }

            logger.info("Batch completado - Restaurantes: {}, Total: {}, Exitosos: {}, Fallidos: {}", 
                    clicksPorRestaurante.size(), clicksNoNotificados.size(), totalExitosos, totalFallidos);

        } catch (Exception e) {
            logger.error("Error crítico en batch de notificación de clicks: {}", e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void ejecutarBatchProgramado() {
        procesarClicksNoNotificados();
    }
}

