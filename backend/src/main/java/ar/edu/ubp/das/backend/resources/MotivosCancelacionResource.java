package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.MotivosCancelacionDto;
import ar.edu.ubp.das.backend.service.MotivosCancelacionService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/motivos-cancelacion")
@CrossOrigin(origins = "*")
public class MotivosCancelacionResource {
    private static final Logger logger = LoggerFactory.getLogger(MotivosCancelacionResource.class);
    private final MotivosCancelacionService motivosCancelacionService;

    public MotivosCancelacionResource(MotivosCancelacionService motivosCancelacionService) {
        this.motivosCancelacionService = motivosCancelacionService;
    }

    @GetMapping
    public ResponseEntity<List<MotivosCancelacionDto>> getAllMotivosCancelacion() {
        List<MotivosCancelacionDto> motivos = motivosCancelacionService.getAllMotivosCancelacion();
        return ResponseEntity.ok(motivos);
    }

    
}
