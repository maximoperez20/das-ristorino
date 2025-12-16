package ar.edu.ubp.das.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO para encapsular los parámetros del stored procedure sp_RegistrarReservaRistorino.
 * Reemplaza 13 parámetros sueltos por un objeto tipado, mejorando la legibilidad y mantenibilidad.
 */
public class RegistrarReservaRistorinoDto {
    
    private String nroRestaurante;
    private String nroSucursal;
    private String codZona;
    private LocalDate fechaReserva;
    private LocalTime horaDesde;
    private String nroCliente;
    private Integer cantAdultos;
    private Integer cantMenores;
    private String codEstado;
    private BigDecimal costoReserva;
    private String observaciones;
    private List<Integer> preferenciasReserva;
    private String notas;
    private String codReservaSucursal;
    
    public RegistrarReservaRistorinoDto() {}
    
    public RegistrarReservaRistorinoDto(
            String nroRestaurante,
            String nroSucursal,
            String codZona,
            LocalDate fechaReserva,
            LocalTime horaDesde,
            String nroCliente,
            Integer cantAdultos,
            Integer cantMenores,
            String codEstado,
            BigDecimal costoReserva,
            List<Integer> preferenciasReserva,
            String notas,
            String codReservaSucursal) {
        this.nroRestaurante = nroRestaurante;
        this.nroSucursal = nroSucursal;
        this.codZona = codZona;
        this.fechaReserva = fechaReserva;
        this.horaDesde = horaDesde;
        this.nroCliente = nroCliente;
        this.cantAdultos = cantAdultos;
        this.cantMenores = cantMenores;
        this.codEstado = codEstado;
        this.costoReserva = costoReserva;
        this.preferenciasReserva = preferenciasReserva;
        this.notas = notas;
        this.codReservaSucursal = codReservaSucursal;
    }
    
    // Getters y setters
    public String getNroRestaurante() {
        return nroRestaurante;
    }
    
    public void setNroRestaurante(String nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }
    
    public String getNroSucursal() {
        return nroSucursal;
    }
    
    public void setNroSucursal(String nroSucursal) {
        this.nroSucursal = nroSucursal;
    }
    
    public String getCodZona() {
        return codZona;
    }
    
    public void setCodZona(String codZona) {
        this.codZona = codZona;
    }
    
    public LocalDate getFechaReserva() {
        return fechaReserva;
    }
    
    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }
    
    public LocalTime getHoraDesde() {
        return horaDesde;
    }
    
    public void setHoraDesde(LocalTime horaDesde) {
        this.horaDesde = horaDesde;
    }
    
    public String getNroCliente() {
        return nroCliente;
    }
    
    public void setNroCliente(String nroCliente) {
        this.nroCliente = nroCliente;
    }
    
    public Integer getCantAdultos() {
        return cantAdultos;
    }
    
    public void setCantAdultos(Integer cantAdultos) {
        this.cantAdultos = cantAdultos;
    }
    
    public Integer getCantMenores() {
        return cantMenores;
    }
    
    public void setCantMenores(Integer cantMenores) {
        this.cantMenores = cantMenores;
    }
    
    public String getCodEstado() {
        return codEstado;
    }
    
    public void setCodEstado(String codEstado) {
        this.codEstado = codEstado;
    }
    
    public BigDecimal getCostoReserva() {
        return costoReserva;
    }
    
    public void setCostoReserva(BigDecimal costoReserva) {
        this.costoReserva = costoReserva;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public List<Integer> getPreferenciasReserva() {
        return preferenciasReserva;
    }
    
    public void setPreferenciasReserva(List<Integer> preferenciasReserva) {
        this.preferenciasReserva = preferenciasReserva;
    }
    
    public String getNotas() {
        return notas;
    }
    
    public void setNotas(String notas) {
        this.notas = notas;
    }
    
    public String getCodReservaSucursal() {
        return codReservaSucursal;
    }
    
    public void setCodReservaSucursal(String codReservaSucursal) {
        this.codReservaSucursal = codReservaSucursal;
    }
    
    /**
     * Convierte este DTO a SqlParameterSource para uso con Spring JDBC.
     * Encapsula la lógica de conversión de tipos (LocalDate a Date, LocalTime a Time, etc.)
     */
    public org.springframework.jdbc.core.namedparam.SqlParameterSource toSqlParameterSource() {
        return new org.springframework.jdbc.core.namedparam.MapSqlParameterSource()
                .addValue("nro_reserva", null, java.sql.Types.VARCHAR)
                .addValue("nro_restaurante", nroRestaurante)
                .addValue("nro_sucursal", nroSucursal)
                .addValue("cod_zona", codZona)
                .addValue("fecha_reserva", fechaReserva != null ? java.sql.Date.valueOf(fechaReserva) : null)
                .addValue("hora_desde", horaDesde != null ? java.sql.Time.valueOf(horaDesde) : null)
                .addValue("nro_cliente", nroCliente)
                .addValue("cant_adultos", cantAdultos)
                .addValue("cant_menores", cantMenores)
                .addValue("cod_estado", codEstado)
                .addValue("costo_reserva", costoReserva)
                .addValue("notas", notas)
                .addValue("cod_reserva_sucursal", codReservaSucursal);
    }
}

