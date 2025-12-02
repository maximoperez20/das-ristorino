package ar.edu.ubp.das.backend.resources.util;

import ar.edu.ubp.das.backend.dto.HorarioDisponibleDto;
import ar.edu.ubp.das.backend.dto.response.BatchResponse;
import ar.edu.ubp.das.backend.dto.response.ErrorResponse;
import ar.edu.ubp.das.backend.dto.response.ErrorWithHorariosResponse;
import ar.edu.ubp.das.backend.dto.response.HorarioTurnoDto;
import ar.edu.ubp.das.backend.dto.response.HorariosDisponiblesResponse;
import ar.edu.ubp.das.backend.dto.response.ZonaConHorariosDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilidad para construir respuestas HTTP de manera consistente.
 * Encapsula la lógica de construcción de respuestas y manejo de errores.
 * 
 * Principio DRY: Evita duplicación de código en los recursos.
 */
public class ResponseHelper {
    
    /**
     * Construye una respuesta de error.
     * 
     * @param errorMessage Mensaje de error
     * @param status Código de estado HTTP
     * @return ResponseEntity con el error
     */
    public static ResponseEntity<ErrorResponse> error(String errorMessage, HttpStatus status) {
        return ResponseEntity.status(status).body(new ErrorResponse(errorMessage));
    }
    
    /**
     * Construye una respuesta de error con código 400 (Bad Request).
     */
    public static ResponseEntity<ErrorResponse> badRequest(String errorMessage) {
        return error(errorMessage, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Construye una respuesta de error con código 404 (Not Found).
     */
    public static ResponseEntity<ErrorResponse> notFound(String errorMessage) {
        return error(errorMessage, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Construye una respuesta de error con código 401 (Unauthorized).
     */
    public static ResponseEntity<ErrorResponse> unauthorized(String errorMessage) {
        return error(errorMessage, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Construye una respuesta de error con código 500 (Internal Server Error).
     */
    public static ResponseEntity<ErrorResponse> internalServerError(String errorMessage) {
        return error(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Construye una respuesta de error con código 502 (Bad Gateway).
     */
    public static ResponseEntity<ErrorResponse> badGateway(String errorMessage) {
        return error(errorMessage, HttpStatus.BAD_GATEWAY);
    }
    
    /**
     * Construye una respuesta de error que incluye horarios alternativos agrupados por zona.
     * Usado cuando una reserva falla por falta de disponibilidad.
     * Los horarios se agrupan por zona para mantener consistencia con la respuesta normal.
     */
    public static ResponseEntity<ErrorWithHorariosResponse> errorWithHorarios(
            String errorMessage, List<HorarioDisponibleDto> horarios) {
        // Agrupar horarios por zona usando la misma lógica que la respuesta normal
        HorariosDisponiblesResponse horariosAgrupados = agruparHorariosPorZona(horarios, null);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorWithHorariosResponse(errorMessage, horariosAgrupados));
    }
    
    /**
     * Construye una respuesta de batch.
     */
    public static ResponseEntity<BatchResponse> batchResponse(String mensaje, String estado, HttpStatus status) {
        return ResponseEntity.status(status).body(new BatchResponse(mensaje, estado));
    }
    
    /**
     * Agrupa horarios disponibles por zona.
     * 
     * @param horarios Lista de horarios disponibles
     * @param fecha Fecha de la consulta (puede ser null, se usará la fecha actual)
     * @return DTO con horarios agrupados por zona
     */
    public static HorariosDisponiblesResponse agruparHorariosPorZona(
            List<HorarioDisponibleDto> horarios, LocalDate fecha) {
        
        // Si fecha es null, usar fecha actual
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        
        if (horarios == null || horarios.isEmpty()) {
            return new HorariosDisponiblesResponse(new ArrayList<>(), fecha);
        }
        
        // Agrupar horarios por zona
        Map<String, List<HorarioTurnoDto>> horariosPorZona = new HashMap<>();
        Map<String, ZonaConHorariosDto> zonasInfo = new HashMap<>();
        
        for (HorarioDisponibleDto horario : horarios) {
            String zonaKey = horario.getCodZona();
            
            // Guardar información de la zona (solo la primera vez)
            if (!zonasInfo.containsKey(zonaKey)) {
                ZonaConHorariosDto zona = new ZonaConHorariosDto();
                zona.setCodZona(horario.getCodZona());
                zona.setNomZona(horario.getNomZona());
                zona.setCapacidadZona(horario.getCapacidadZona());
                zona.setPermiteMenores(horario.getPermiteMenores());
                zonasInfo.put(zonaKey, zona);
                horariosPorZona.put(zonaKey, new ArrayList<>());
            }
            
            // Agregar el turno a la lista de horarios de la zona
            HorarioTurnoDto turno = new HorarioTurnoDto(
                    horario.getHoraDesde(),
                    horario.getHoraHasta(),
                    horario.getYaReservados(),
                    horario.getDisponibilidad()
            );
            horariosPorZona.get(zonaKey).add(turno);
        }
        
        // Asignar las listas de horarios a cada zona
        List<ZonaConHorariosDto> zonasFinales = new ArrayList<>();
        for (Map.Entry<String, ZonaConHorariosDto> entry : zonasInfo.entrySet()) {
            String zonaKey = entry.getKey();
            ZonaConHorariosDto zona = entry.getValue();
            zona.setHorarios(horariosPorZona.get(zonaKey));
            zonasFinales.add(zona);
        }
        
        return new HorariosDisponiblesResponse(zonasFinales, fecha);
    }
    
    /**
     * Determina el código de estado HTTP según el mensaje de error.
     */
    public static HttpStatus determinarStatusDesdeError(String errorMessage) {
        if (errorMessage == null) {
            return HttpStatus.BAD_REQUEST;
        }
        
        String lowerMessage = errorMessage.toLowerCase();
        if (lowerMessage.contains("no encontrado") || lowerMessage.contains("no encontrada")) {
            return HttpStatus.NOT_FOUND;
        } else if (lowerMessage.contains("error en comunicación")) {
            return HttpStatus.BAD_GATEWAY;
        }
        
        return HttpStatus.BAD_REQUEST;
    }
}

