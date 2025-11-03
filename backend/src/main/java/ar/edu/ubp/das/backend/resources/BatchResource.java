package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.service.BatchClickService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/batch")
public class BatchResource {

    private static final Logger logger = LoggerFactory.getLogger(BatchResource.class);

    private final BatchClickService batchClickService;
    
    public BatchResource(BatchClickService batchClickService) {
        this.batchClickService = batchClickService;
    }

    @PostMapping("/procesar-clicks")
    public ResponseEntity<Map<String, String>> ejecutarBatchClicks() {
        logger.info("Ejecutando batch de clicks manualmente...");
        
        try {
            batchClickService.procesarClicksNoNotificados();
            return ResponseEntity.ok(Map.of(
                "mensaje", "Batch de clicks ejecutado exitosamente",
                "estado", "completado"
            ));
        } catch (RuntimeException e) {
            logger.warn("Error al ejecutar batch de clicks: {}", e.getMessage());
            return ResponseEntity.status(400).body(Map.of(
                "mensaje", e.getMessage(),
                "estado", "error"
            ));
        } catch (Exception e) {
            logger.error("Error inesperado al ejecutar batch de clicks", e);
            return ResponseEntity.status(500).body(Map.of(
                "mensaje", "Error al ejecutar batch de clicks: " + e.getMessage(),
                "estado", "error"
            ));
        }
    }
}
