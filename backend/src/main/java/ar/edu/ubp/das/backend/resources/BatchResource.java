package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.response.BatchResponse;
import ar.edu.ubp.das.backend.resources.util.ResponseHelper;
import ar.edu.ubp.das.backend.service.BatchClickService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para operaciones batch.
 * 
 * Principios aplicados:
 * - Encapsulación: Usa DTOs tipados en lugar de Maps genéricos
 * - DRY: Usa ResponseHelper para construcción de respuestas
 */
@RestController
@RequestMapping("/api/batch")
public class BatchResource {

    private static final Logger logger = LoggerFactory.getLogger(BatchResource.class);

    private final BatchClickService batchClickService;
    
    public BatchResource(BatchClickService batchClickService) {
        this.batchClickService = batchClickService;
    }

    @PostMapping("/procesar-clicks")
    public ResponseEntity<BatchResponse> ejecutarBatchClicks() {
        logger.info("Ejecutando batch de clicks manualmente...");
        
        try {
            batchClickService.procesarClicksNoNotificados();
            return ResponseHelper.batchResponse(
                "Batch de clicks ejecutado exitosamente",
                "completado",
                HttpStatus.OK
            );
        } catch (RuntimeException e) {
            logger.warn("Error al ejecutar batch de clicks: {}", e.getMessage());
            return ResponseHelper.batchResponse(
                e.getMessage(),
                "error",
                HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            logger.error("Error inesperado al ejecutar batch de clicks", e);
            return ResponseHelper.batchResponse(
                "Error al ejecutar batch de clicks: " + e.getMessage(),
                "error",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
