package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.client.RestauranteSoapClient;
import ar.edu.ubp.das.backend.dto.ClickNoNotificadoDto;
import ar.edu.ubp.das.backend.dto.soap.NotificarClickSoapDto;
import ar.edu.ubp.das.backend.repository.ClickRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BatchClickService {

    private static final Logger logger = LoggerFactory.getLogger(BatchClickService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ClickRepository clickRepository;

    @Autowired
    private RestauranteSoapClient restauranteSoapClient;

    public void procesarClicksNoNotificados() {
        logger.info("========================================");
        logger.info("Iniciando batch de notificación de clicks - {}", LocalDateTime.now().format(DATE_FORMATTER));
        logger.info("========================================");

        try {
            List<ClickNoNotificadoDto> clicksNoNotificados = clickRepository.obtenerClicksNoNotificados();
            
            if (clicksNoNotificados.isEmpty()) {
                logger.info("No hay clicks pendientes de notificar");
                return;
            }

            logger.info("Clicks pendientes de notificar: {}", clicksNoNotificados.size());

            int exitosos = 0;
            int fallidos = 0;
            int procesados = 0;

            for (ClickNoNotificadoDto click : clicksNoNotificados) {
                try {
                    procesados++;
                    logger.debug("Procesando click {}/{} - nroClick: {}, restaurante: {}, contenido: {}", 
                            procesados, clicksNoNotificados.size(), 
                            click.getNroClick(), click.getNroRestaurante(), click.getCodContenidoRestaurante());

                    if (click.getCodContenidoRestaurante() == null || click.getCodContenidoRestaurante().trim().isEmpty()) {
                        logger.warn("Click {} no tiene cod_contenido_restaurante, saltando...", click.getNroClick());
                        fallidos++;
                        continue;
                    }

                    NotificarClickSoapDto response = restauranteSoapClient.notificarClick(
                            click.getNroRestaurante(),
                            click.getCodContenidoRestaurante(),
                            click.getNroClick(),
                            click.getFechaHoraRegistro(),
                            click.getNroCliente(),
                            click.getCostoClick()
                    );

                    if (response.isExitoso()) {
                        clickRepository.marcarClickComoNotificado(
                                click.getNroRestaurante(),
                                click.getNroIdioma(),
                                click.getNroContenido(),
                                click.getNroClick()
                        );
                        exitosos++;
                        logger.info("Click {} notificado exitosamente", click.getNroClick());
                    } else {
                        fallidos++;
                        logger.warn("Click {} no pudo ser notificado: {}", click.getNroClick(), response.getMensaje());
                    }

                } catch (Exception e) {
                    fallidos++;
                    logger.error("Error al procesar click {}: {}", click.getNroClick(), e.getMessage(), e);
                }
            }

            logger.info("========================================");
            logger.info("Batch de notificación completado");
            logger.info("Total procesados: {}", procesados);
            logger.info("Exitosos: {}", exitosos);
            logger.info("Fallidos: {}", fallidos);
            logger.info("========================================");

        } catch (Exception e) {
            logger.error("Error crítico en batch de notificación de clicks: {}", e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void ejecutarBatchProgramado() {
        procesarClicksNoNotificados();
    }
}

