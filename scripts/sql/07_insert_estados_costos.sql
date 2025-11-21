-- Script para insertar estados de reservas, tipos de costo y costos
-- Ejecutar después de crear las tablas y stored procedures

USE das_ristorino;
GO

-- =====================================================
-- ESTADOS DE RESERVAS
-- =====================================================

IF NOT EXISTS (SELECT 1 FROM estados_reservas WHERE nom_estado = N'Pendiente')
BEGIN
    INSERT INTO estados_reservas (nom_estado) VALUES (N'Pendiente');
    PRINT 'Estado "Pendiente" creado';
END
ELSE
    PRINT 'Estado "Pendiente" ya existe';

IF NOT EXISTS (SELECT 1 FROM estados_reservas WHERE nom_estado = N'Confirmada')
BEGIN
    INSERT INTO estados_reservas (nom_estado) VALUES (N'Confirmada');
    PRINT 'Estado "Confirmada" creado';
END
ELSE
    PRINT 'Estado "Confirmada" ya existe';

IF NOT EXISTS (SELECT 1 FROM estados_reservas WHERE nom_estado = N'Cancelada')
BEGIN
    INSERT INTO estados_reservas (nom_estado) VALUES (N'Cancelada');
    PRINT 'Estado "Cancelada" creado';
END
ELSE
    PRINT 'Estado "Cancelada" ya existe';

GO

-- =====================================================
-- COSTOS - Tipo RESERVA
-- =====================================================

-- Insertar costo de reserva con vigencia desde hoy
-- El monto puede ajustarse según necesidades del negocio
IF NOT EXISTS (
    SELECT 1 FROM costos 
    WHERE tipo_costo = 'RESERVA' 
    AND fecha_ini_vigencia = CAST(GETDATE() AS DATE)
)
BEGIN
    INSERT INTO costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto)
    VALUES ('RESERVA', CAST(GETDATE() AS DATE), NULL, 50.00);
    PRINT 'Costo de reserva creado: $50.00 (vigente desde hoy)';
END
ELSE
    PRINT 'Costo de reserva ya existe para la fecha de hoy';

GO

PRINT '';
PRINT '========================================';
PRINT 'Resumen de datos insertados:';
PRINT '========================================';
PRINT 'Estados de reservas:';
SELECT cod_estado, nom_estado FROM estados_reservas ORDER BY nom_estado;
PRINT '';
PRINT 'Costos vigentes:';
SELECT tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto 
FROM costos 
WHERE fecha_fin_vigencia IS NULL OR fecha_fin_vigencia >= CAST(GETDATE() AS DATE)
ORDER BY tipo_costo, fecha_ini_vigencia;
PRINT '========================================';
GO

