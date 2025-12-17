package ar.edu.ubp.das.backend.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Configuración de Gson con TypeAdapters para tipos de Java 8+ (LocalDate, LocalTime, BigDecimal).
 * 
 * Gson no puede serializar/deserializar LocalDate y LocalTime por defecto porque
 * estos tipos tienen campos privados que Gson no puede acceder mediante reflexión.
 * 
 * BigDecimal se serializa como Double por defecto, lo que causa pérdida de precisión
 * y errores de casting. Esta configuración lo serializa c  omo String.
 * 
 * Esta configuración proporciona adapters personalizados que convierten estos tipos
 * a/desde strings en formato ISO (yyyy-MM-dd para LocalDate, HH:mm:ss para LocalTime,
 * string de número para BigDecimal).
 */
@Configuration
public class GsonConfig {

    /**
     * Formato ISO para fechas (yyyy-MM-dd)
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    /**
     * Formato ISO para horas (HH:mm:ss)
     */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

    /**
     * Crea un Gson configurado con TypeAdapters para LocalDate, LocalTime y BigDecimal.
     * 
     * @return Gson configurado
     */
    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .registerTypeAdapter(BigDecimal.class, new BigDecimalAdapter())
                .create();
    }

    /**
     * TypeAdapter para LocalDate: serializa/deserializa como string ISO (yyyy-MM-dd)
     */
    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(DATE_FORMATTER));
            }
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String dateStr = in.nextString();
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        }
    }

    /**
     * TypeAdapter para LocalTime: serializa/deserializa como string ISO (HH:mm:ss)
     */
    private static class LocalTimeAdapter extends TypeAdapter<LocalTime> {
        @Override
        public void write(JsonWriter out, LocalTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(TIME_FORMATTER));
            }
        }

        @Override
        public LocalTime read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String timeStr = in.nextString();
            return LocalTime.parse(timeStr, TIME_FORMATTER);
        }
    }

    /**
     * TypeAdapter para BigDecimal: serializa/deserializa como string numérico.
     * Evita pérdida de precisión (Double no preserva valores de BigDecimal).
     */
    private static class BigDecimalAdapter extends TypeAdapter<BigDecimal> {
        @Override
        public void write(JsonWriter out, BigDecimal value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString());
            }
        }

        @Override
        public BigDecimal read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String decimalStr = in.nextString();
            try {
                return new BigDecimal(decimalStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("No se puede parsear como BigDecimal: " + decimalStr, e);
            }
        }
    }
}

