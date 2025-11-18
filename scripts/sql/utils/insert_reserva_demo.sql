-- Script para insertar una reserva de prueba para el cliente 10A683AF-FB0D-4407-A4F7-188430A50270
-- Este script obtiene valores válidos de restaurantes, sucursales, zonas y turnos existentes

USE das_ristorino;
GO

DECLARE @nro_cliente VARCHAR(36) = '10A683AF-FB0D-4407-A4F7-188430A50270';
DECLARE @nro_reserva VARCHAR(36) = NEWID();
DECLARE @nro_restaurante VARCHAR(36);
DECLARE @nro_sucursal VARCHAR(36);
DECLARE @cod_zona VARCHAR(36);
DECLARE @hora_desde TIME(0);
DECLARE @fecha_reserva DATE = DATEADD(DAY, 7, CAST(GETDATE() AS DATE)); -- Reserva para dentro de 7 días
DECLARE @cant_adultos SMALLINT = 2;
DECLARE @cant_menores SMALLINT = 0;
DECLARE @notas NVARCHAR(400) = N'Reserva de prueba para testing';

-- Verificar que el cliente existe
IF NOT EXISTS (SELECT 1 FROM clientes WHERE nro_cliente = @nro_cliente)
BEGIN
    PRINT 'ERROR: El cliente ' + @nro_cliente + ' no existe en la base de datos.';
    PRINT 'Por favor, verifica que el cliente esté registrado antes de crear la reserva.';
    RETURN;
END

-- Obtener un restaurante, sucursal, zona y turno válidos
SELECT TOP 1 
    @nro_restaurante = t.nro_restaurante,
    @nro_sucursal = t.nro_sucursal,
    @hora_desde = t.hora_desde
FROM turnos_sucursales_restaurantes t
WHERE t.habilitado = 1
ORDER BY NEWID(); -- Seleccionar aleatoriamente

-- Obtener una zona válida para la sucursal seleccionada
SELECT TOP 1 
    @cod_zona = z.cod_zona
FROM zonas_sucursales_restaurantes z
WHERE z.nro_restaurante = @nro_restaurante
  AND z.nro_sucursal = @nro_sucursal
  AND z.habilitada = 1
ORDER BY NEWID(); -- Seleccionar aleatoriamente

-- Verificar que se encontraron valores válidos
IF @nro_restaurante IS NULL OR @nro_sucursal IS NULL OR @cod_zona IS NULL OR @hora_desde IS NULL
BEGIN
    PRINT 'ERROR: No se encontraron restaurantes, sucursales, zonas o turnos disponibles.';
    PRINT 'Por favor, asegúrate de que existan datos en las tablas:';
    PRINT '  - restaurantes';
    PRINT '  - sucursales_restaurantes';
    PRINT '  - zonas_sucursales_restaurantes';
    PRINT '  - turnos_sucursales_restaurantes';
    RETURN;
END

-- Insertar la reserva
BEGIN TRY
    INSERT INTO reservas_restaurantes (
        nro_reserva,
        nro_restaurante,
        nro_sucursal,
        cod_zona,
        fecha_reserva,
        hora_desde,
        nro_cliente,
        cant_adultos,
        cant_menores,
        cancelada,
        fecha_hora_registro,
        notas
    )
    VALUES (
        @nro_reserva,
        @nro_restaurante,
        @nro_sucursal,
        @cod_zona,
        @fecha_reserva,
        @hora_desde,
        @nro_cliente,
        @cant_adultos,
        @cant_menores,
        0, -- No cancelada
        SYSDATETIME(),
        @notas
    );
    
    PRINT 'Reserva insertada exitosamente!';
    PRINT 'Nro Reserva: ' + @nro_reserva;
    PRINT 'Cliente: ' + @nro_cliente;
    PRINT 'Fecha: ' + CAST(@fecha_reserva AS VARCHAR(10));
    PRINT 'Hora: ' + CAST(@hora_desde AS VARCHAR(8));
    PRINT 'Personas: ' + CAST(@cant_adultos AS VARCHAR(2)) + ' adultos, ' + CAST(@cant_menores AS VARCHAR(2)) + ' menores';
    PRINT 'Restaurante: ' + @nro_restaurante;
    PRINT 'Sucursal: ' + @nro_sucursal;
    PRINT 'Zona: ' + @cod_zona;
    
END TRY
BEGIN CATCH
    PRINT 'ERROR al insertar la reserva:';
    PRINT ERROR_MESSAGE();
    PRINT 'Código de error: ' + CAST(ERROR_NUMBER() AS VARCHAR(10));
END CATCH
GO

