package ar.edu.ubp.das.backend.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilidades para manejo de fechas.
 * Centraliza el formato de fechas y el timezone (UTC-3, Buenos Aires).
 */
public class DateUtils {

    /**
     * Timezone de Buenos Aires (UTC-3)
     */
    public static final ZoneId TIMEZONE_BUENOS_AIRES = ZoneId.of("America/Argentina/Buenos_Aires");

    /**
     * Formato de fecha unificado: dd/MM/yyyy
     */
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Formato de fecha y hora: dd/MM/yyyy HH:mm
     */
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Formato de hora: HH:mm
     */
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Formato ISO para APIs (yyyy-MM-dd)
     */
    public static final DateTimeFormatter ISO_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Convierte una fecha a String con formato dd/MM/yyyy
     * 
     * @param fecha Fecha a formatear
     * @return String con formato dd/MM/yyyy o null si la fecha es null
     */
    public static String formatearFecha(LocalDate fecha) {
        if (fecha == null) {
            return null;
        }
        return fecha.format(DATE_FORMAT);
    }

    /**
     * Convierte una fecha y hora a String con formato dd/MM/yyyy HH:mm
     * 
     * @param fechaHora Fecha y hora a formatear
     * @return String con formato dd/MM/yyyy HH:mm o null si la fecha es null
     */
    public static String formatearFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            return null;
        }
        return fechaHora.format(DATETIME_FORMAT);
    }

    /**
     * Obtiene la fecha actual en timezone de Buenos Aires
     * 
     * @return LocalDate en timezone de Buenos Aires
     */
    public static LocalDate fechaActual() {
        return ZonedDateTime.now(TIMEZONE_BUENOS_AIRES).toLocalDate();
    }

    /**
     * Obtiene la fecha y hora actual en timezone de Buenos Aires
     * 
     * @return LocalDateTime en timezone de Buenos Aires
     */
    public static LocalDateTime fechaHoraActual() {
        return ZonedDateTime.now(TIMEZONE_BUENOS_AIRES).toLocalDateTime();
    }

    /**
     * Convierte una fecha ISO (yyyy-MM-dd) a LocalDate
     * 
     * @param fechaISO Fecha en formato ISO (yyyy-MM-dd)
     * @return LocalDate o null si la fecha es null o inválida
     */
    public static LocalDate parsearFechaISO(String fechaISO) {
        if (fechaISO == null || fechaISO.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(fechaISO, ISO_DATE_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convierte una fecha en formato dd/MM/yyyy a LocalDate
     * 
     * @param fecha Fecha en formato dd/MM/yyyy
     * @return LocalDate o null si la fecha es null o inválida
     */
    public static LocalDate parsearFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(fecha, DATE_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }
}

