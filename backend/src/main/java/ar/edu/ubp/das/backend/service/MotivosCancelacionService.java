package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.MotivosCancelacionDto;
import ar.edu.ubp.das.backend.repository.MotivosCancelacionRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@Service
public class MotivosCancelacionService {
    private final MotivosCancelacionRepository motivosCancelacionRepository;
    private static final Logger logger = LoggerFactory.getLogger(MotivosCancelacionService.class);

    
    public MotivosCancelacionService(MotivosCancelacionRepository motivosCancelacionRepository) {
        this.motivosCancelacionRepository = motivosCancelacionRepository;
    }

    public List<MotivosCancelacionDto> getAllMotivosCancelacion() {
        return motivosCancelacionRepository.getAllMotivosCancelacion();
    }
}
