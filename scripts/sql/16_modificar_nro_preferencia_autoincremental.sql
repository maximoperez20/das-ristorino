USE das_ristorino;
GO

-- Script para modificar nro_preferencia a autoincremental en preferencias_reservas_restaurantes
-- IMPORTANTE: Este script elimina la foreign key que referencia a preferencias_restaurantes

PRINT 'Iniciando modificación de nro_preferencia a autoincremental...';
GO

BEGIN TRANSACTION;
BEGIN TRY
    -- 1. Eliminar la Foreign Key que incluye nro_preferencia
    IF EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_preferencias_reservas_restaurantes_preferencia')
    BEGIN
        PRINT 'Eliminando Foreign Key FK_preferencias_reservas_restaurantes_preferencia...';
        ALTER TABLE dbo.preferencias_reservas_restaurantes
        DROP CONSTRAINT FK_preferencias_reservas_restaurantes_preferencia;
        PRINT 'Foreign Key eliminada exitosamente.';
    END
    ELSE
    BEGIN
        PRINT 'La Foreign Key FK_preferencias_reservas_restaurantes_preferencia no existe.';
    END

    -- 2. Eliminar la Primary Key actual
    IF EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = 'PK_preferencias_reservas_restaurantes')
    BEGIN
        PRINT 'Eliminando Primary Key PK_preferencias_reservas_restaurantes...';
        ALTER TABLE dbo.preferencias_reservas_restaurantes
        DROP CONSTRAINT PK_preferencias_reservas_restaurantes;
        PRINT 'Primary Key eliminada exitosamente.';
    END
    ELSE
    BEGIN
        PRINT 'La Primary Key PK_preferencias_reservas_restaurantes no existe.';
    END

    -- 3. Crear tabla temporal para migrar datos si existen
    IF EXISTS (SELECT 1 FROM sys.tables WHERE name = 'preferencias_reservas_restaurantes' AND schema_id = SCHEMA_ID('dbo'))
    BEGIN
        PRINT 'Creando tabla temporal para migración de datos...';
        
        -- Crear tabla temporal con la nueva estructura
        SELECT 
            nro_reserva,
            nro_cliente,
            nro_restaurante,
            cod_categoria,
            nro_valor_dominio,
            ROW_NUMBER() OVER (ORDER BY nro_reserva, nro_cliente, nro_restaurante, cod_categoria, nro_valor_dominio) AS nro_preferencia,
            observaciones
        INTO #temp_preferencias_reservas_restaurantes
        FROM dbo.preferencias_reservas_restaurantes;

        PRINT 'Datos migrados a tabla temporal.';
        
        -- Eliminar la tabla original
        DROP TABLE dbo.preferencias_reservas_restaurantes;
        PRINT 'Tabla original eliminada.';
    END

    -- 4. Crear la tabla con nro_preferencia como IDENTITY
    PRINT 'Creando tabla con nro_preferencia autoincremental...';
    CREATE TABLE dbo.preferencias_reservas_restaurantes (
        nro_reserva VARCHAR(36) NOT NULL,
        nro_cliente VARCHAR(36) NOT NULL,
        nro_restaurante VARCHAR(36) NOT NULL,
        cod_categoria VARCHAR(36) NOT NULL,
        nro_valor_dominio INT NOT NULL,
        nro_preferencia INT IDENTITY(1,1) NOT NULL,  -- ✅ AUTOINCREMENTAL
        observaciones NVARCHAR(400) NULL,
        CONSTRAINT PK_preferencias_reservas_restaurantes PRIMARY KEY (nro_reserva, nro_cliente, nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia),
        CONSTRAINT FK_preferencias_reservas_restaurantes_reserva FOREIGN KEY (nro_reserva) REFERENCES dbo.reservas_restaurantes(nro_reserva),
        CONSTRAINT FK_preferencias_reservas_restaurantes_cliente FOREIGN KEY (nro_cliente) REFERENCES dbo.clientes(nro_cliente),
        CONSTRAINT FK_preferencias_reservas_restaurantes_restaurante FOREIGN KEY (nro_restaurante) REFERENCES dbo.restaurantes(nro_restaurante),
        CONSTRAINT FK_preferencias_reservas_restaurantes_categoria FOREIGN KEY (cod_categoria) REFERENCES dbo.categorias_preferencias(cod_categoria),
        CONSTRAINT FK_preferencias_reservas_restaurantes_valor_dominio FOREIGN KEY (cod_categoria, nro_valor_dominio) REFERENCES dbo.dominio_categorias_preferencias(cod_categoria, nro_valor_dominio)
        -- ✅ NOTA: Se eliminó la FK a preferencias_restaurantes porque nro_preferencia ahora es autoincremental
    );

    PRINT 'Tabla creada exitosamente.';

    -- 5. Migrar datos de vuelta si existían
    IF EXISTS (SELECT 1 FROM sys.tables WHERE name = '#temp_preferencias_reservas_restaurantes')
    BEGIN
        PRINT 'Migrando datos de vuelta a la tabla...';
        
        SET IDENTITY_INSERT dbo.preferencias_reservas_restaurantes ON;
        
        INSERT INTO dbo.preferencias_reservas_restaurantes (
            nro_reserva,
            nro_cliente,
            nro_restaurante,
            cod_categoria,
            nro_valor_dominio,
            nro_preferencia,
            observaciones
        )
        SELECT 
            nro_reserva,
            nro_cliente,
            nro_restaurante,
            cod_categoria,
            nro_valor_dominio,
            nro_preferencia,
            observaciones
        FROM #temp_preferencias_reservas_restaurantes;
        
        SET IDENTITY_INSERT dbo.preferencias_reservas_restaurantes OFF;
        
        DROP TABLE #temp_preferencias_reservas_restaurantes;
        
        PRINT 'Datos migrados exitosamente.';
    END

    COMMIT TRANSACTION;
    PRINT 'Modificación completada exitosamente!';
    PRINT 'nro_preferencia ahora es autoincremental (IDENTITY).';
END TRY
BEGIN CATCH
    ROLLBACK TRANSACTION;
    PRINT 'Error durante la modificación:';
    PRINT ERROR_MESSAGE();
    THROW;
END CATCH
GO
