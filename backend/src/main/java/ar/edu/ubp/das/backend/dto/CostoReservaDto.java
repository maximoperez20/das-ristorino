package ar.edu.ubp.das.backend.dto;

import java.math.BigDecimal;

public class CostoReservaDto {
    
    private BigDecimal monto;

    public CostoReservaDto() {}

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}

