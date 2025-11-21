-- Script para agregar la columna cod_reserva_sucursal a la tabla reservas_restaurantes
-- Este campo almacena el código de reserva generado por el sistema del restaurante

USE das_ristorino;
GO

-- Verificar si la columna ya existe
IF NOT EXISTS (
    SELECT 1 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'dbo' 
    AND TABLE_NAME = 'reservas_restaurantes' 
    AND COLUMN_NAME = 'cod_reserva_sucursal'
)
BEGIN
    ALTER TABLE dbo.reservas_restaurantes
    ADD cod_reserva_sucursal VARCHAR(36) NULL;
    
    PRINT 'Columna cod_reserva_sucursal agregada exitosamente a reservas_restaurantes';
END
ELSE
BEGIN
    PRINT 'La columna cod_reserva_sucursal ya existe en reservas_restaurantes';
END
GO

