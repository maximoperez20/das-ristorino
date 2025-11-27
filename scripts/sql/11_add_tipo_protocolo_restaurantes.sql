-- ==========================================================
-- Script para agregar columnas tipo_protocolo y url_servicio a restaurantes
-- Permite mapear qué tipo de sistema usa cada restaurante (SOAP o REST)
-- y en qué URL/puerto está configurado su servicio
-- ==========================================================

USE das_ristorino;
GO

-- Agregar columna tipo_protocolo a la tabla restaurantes
IF NOT EXISTS (
    SELECT 1 
    FROM sys.columns 
    WHERE object_id = OBJECT_ID('dbo.restaurantes') 
    AND name = 'tipo_protocolo'
)
BEGIN
    ALTER TABLE dbo.restaurantes
    ADD tipo_protocolo VARCHAR(10) NOT NULL DEFAULT 'SOAP'
        CONSTRAINT CK_restaurantes_protocolo 
        CHECK (tipo_protocolo IN ('SOAP', 'REST'));
    
    PRINT 'Columna tipo_protocolo agregada a restaurantes';
END
ELSE
BEGIN
    PRINT 'Columna tipo_protocolo ya existe en restaurantes';
END
GO

-- Agregar columna url_servicio a la tabla restaurantes
IF NOT EXISTS (
    SELECT 1 
    FROM sys.columns 
    WHERE object_id = OBJECT_ID('dbo.restaurantes') 
    AND name = 'url_servicio'
)
BEGIN
    ALTER TABLE dbo.restaurantes
    ADD url_servicio NVARCHAR(500) NULL;
    
    PRINT 'Columna url_servicio agregada a restaurantes';
END
ELSE
BEGIN
    PRINT 'Columna url_servicio ya existe en restaurantes';
END
GO

-- Crear índice para mejorar consultas por protocolo
IF NOT EXISTS (
    SELECT 1 
    FROM sys.indexes 
    WHERE object_id = OBJECT_ID('dbo.restaurantes') 
    AND name = 'IX_restaurantes_tipo_protocolo'
)
BEGIN
    CREATE INDEX IX_restaurantes_tipo_protocolo 
    ON dbo.restaurantes(tipo_protocolo);
    
    PRINT 'Índice IX_restaurantes_tipo_protocolo creado';
END
ELSE
BEGIN
    PRINT 'Índice IX_restaurantes_tipo_protocolo ya existe';
END
GO

-- Actualizar restaurantes existentes (por defecto todos SOAP con URL por defecto)
UPDATE dbo.restaurantes
SET tipo_protocolo = 'SOAP',
    url_servicio = 'http://localhost:8081/ws/restaurantes.wsdl'
WHERE tipo_protocolo IS NULL OR tipo_protocolo = '' OR url_servicio IS NULL;
GO

PRINT 'Script completado: tipo_protocolo y url_servicio agregados a restaurantes';
GO

