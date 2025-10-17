-- Script para crear stored procedures para la tabla reservas_restaurantes
-- Basado en la nueva estructura de base de datos

USE das_ristorino;
GO

-- 1. Obtener todas las reservas
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_ObtenerTodasLasReservas') 
    DROP PROCEDURE sp_ObtenerTodasLasReservas;
GO

CREATE PROCEDURE sp_ObtenerTodasLasReservas
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        rr.nro_reserva as id,
        c.nombre + ' ' + c.apellido as nombre_cliente,
        c.correo as email,
        c.telefonos as telefono,
        CAST(CAST(rr.fecha_reserva AS VARCHAR(10)) + ' ' + CAST(rr.hora_desde AS VARCHAR(8)) AS DATETIME2) as fecha_hora,
        (rr.cant_adultos + rr.cant_menores) as cantidad_personas,
        CASE 
            WHEN rr.cancelada = 1 THEN 'CANCELADA'
            WHEN er.nom_estado IS NOT NULL THEN er.nom_estado
            ELSE 'PENDIENTE'
        END as estado,
        rr.notas as observaciones,
        rr.fecha_hora_registro as fecha_creacion,
        rr.fecha_hora_cancelacion as fecha_actualizacion
    FROM reservas_restaurantes rr
    LEFT JOIN clientes c ON c.nro_cliente = rr.nro_cliente
    LEFT JOIN estados_reservas er ON er.cod_estado = rr.cod_estado
    ORDER BY rr.fecha_reserva, rr.hora_desde;
END;
GO

-- 2. Obtener reserva por ID
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_ObtenerReservaPorId') 
    DROP PROCEDURE sp_ObtenerReservaPorId;
GO

CREATE PROCEDURE sp_ObtenerReservaPorId
    @id VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        rr.nro_reserva as id,
        c.nombre + ' ' + c.apellido as nombre_cliente,
        c.correo as email,
        c.telefonos as telefono,
        CAST(CAST(rr.fecha_reserva AS VARCHAR(10)) + ' ' + CAST(rr.hora_desde AS VARCHAR(8)) AS DATETIME2) as fecha_hora,
        (rr.cant_adultos + rr.cant_menores) as cantidad_personas,
        CASE 
            WHEN rr.cancelada = 1 THEN 'CANCELADA'
            WHEN er.nom_estado IS NOT NULL THEN er.nom_estado
            ELSE 'PENDIENTE'
        END as estado,
        rr.notas as observaciones,
        rr.fecha_hora_registro as fecha_creacion,
        rr.fecha_hora_cancelacion as fecha_actualizacion
    FROM reservas_restaurantes rr
    LEFT JOIN clientes c ON c.nro_cliente = rr.nro_cliente
    LEFT JOIN estados_reservas er ON er.cod_estado = rr.cod_estado
    WHERE rr.nro_reserva = @id;
END;
GO

-- 3. Crear nueva reserva (simplificado para demo)
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_CrearReserva') 
    DROP PROCEDURE sp_CrearReserva;
GO

CREATE PROCEDURE sp_CrearReserva
    @nombre_cliente NVARCHAR(100),
    @email NVARCHAR(100),
    @telefono NVARCHAR(20),
    @fecha_hora DATETIME2,
    @cantidad_personas INT,
    @observaciones NVARCHAR(500),
    @nuevo_id VARCHAR(36) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Crear cliente temporal si no existe
    DECLARE @nro_cliente VARCHAR(36);
    SELECT @nro_cliente = nro_cliente FROM clientes WHERE correo = @email;
    
    IF @nro_cliente IS NULL
    BEGIN
        SET @nro_cliente = NEWID();
        INSERT INTO clientes (nro_cliente, apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado)
        VALUES (@nro_cliente, 'Demo', @nombre_cliente, 'temp', @email, @telefono, 
                (SELECT TOP 1 nro_localidad FROM localidades), 1);
    END;
    
    -- Crear reserva
    SET @nuevo_id = NEWID();
    INSERT INTO reservas_restaurantes (
        nro_reserva, nro_restaurante, nro_sucursal, cod_zona, 
        fecha_reserva, hora_desde, nro_cliente, 
        cant_adultos, cant_menores, cancelada, 
        fecha_hora_registro, notas
    )
    VALUES (
        @nuevo_id,
        (SELECT TOP 1 nro_restaurante FROM restaurantes),
        (SELECT TOP 1 nro_sucursal FROM sucursales_restaurantes),
        (SELECT TOP 1 cod_zona FROM zonas_sucursales_restaurantes),
        CAST(@fecha_hora AS DATE),
        CAST(@fecha_hora AS TIME),
        @nro_cliente,
        @cantidad_personas,
        0,
        0,
        GETDATE(),
        @observaciones
    );
END;
GO

-- 4. Actualizar reserva existente
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_ActualizarReserva') 
    DROP PROCEDURE sp_ActualizarReserva;
GO

CREATE PROCEDURE sp_ActualizarReserva
    @id VARCHAR(36),
    @nombre_cliente NVARCHAR(100),
    @email NVARCHAR(100),
    @telefono NVARCHAR(20),
    @fecha_hora DATETIME2,
    @cantidad_personas INT,
    @estado NVARCHAR(20),
    @observaciones NVARCHAR(500)
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Actualizar cliente
    UPDATE clientes 
    SET nombre = @nombre_cliente, 
        telefonos = @telefono
    WHERE nro_cliente = (SELECT nro_cliente FROM reservas_restaurantes WHERE nro_reserva = @id);
    
    -- Actualizar reserva
    UPDATE reservas_restaurantes
    SET
        fecha_reserva = CAST(@fecha_hora AS DATE),
        hora_desde = CAST(@fecha_hora AS TIME),
        cant_adultos = @cantidad_personas,
        cant_menores = 0,
        notas = @observaciones,
        cancelada = CASE WHEN @estado = 'CANCELADA' THEN 1 ELSE 0 END
    WHERE nro_reserva = @id;
    
    SELECT @@ROWCOUNT;
END;
GO

-- 5. Eliminar reserva
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_EliminarReserva') 
    DROP PROCEDURE sp_EliminarReserva;
GO

CREATE PROCEDURE sp_EliminarReserva
    @id VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;
    DELETE FROM reservas_restaurantes WHERE nro_reserva = @id;
    SELECT @@ROWCOUNT;
END;
GO

-- 6. Obtener reservas por estado
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_ObtenerReservasPorEstado') 
    DROP PROCEDURE sp_ObtenerReservasPorEstado;
GO

CREATE PROCEDURE sp_ObtenerReservasPorEstado
    @estado NVARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        rr.nro_reserva as id,
        c.nombre + ' ' + c.apellido as nombre_cliente,
        c.correo as email,
        c.telefonos as telefono,
        CAST(CAST(rr.fecha_reserva AS VARCHAR(10)) + ' ' + CAST(rr.hora_desde AS VARCHAR(8)) AS DATETIME2) as fecha_hora,
        (rr.cant_adultos + rr.cant_menores) as cantidad_personas,
        CASE 
            WHEN rr.cancelada = 1 THEN 'CANCELADA'
            WHEN er.nom_estado IS NOT NULL THEN er.nom_estado
            ELSE 'PENDIENTE'
        END as estado,
        rr.notas as observaciones,
        rr.fecha_hora_registro as fecha_creacion,
        rr.fecha_hora_cancelacion as fecha_actualizacion
    FROM reservas_restaurantes rr
    LEFT JOIN clientes c ON c.nro_cliente = rr.nro_cliente
    LEFT JOIN estados_reservas er ON er.cod_estado = rr.cod_estado
    WHERE 
        (@estado = 'CANCELADA' AND rr.cancelada = 1) OR
        (@estado = 'PENDIENTE' AND rr.cancelada = 0 AND er.nom_estado IS NULL) OR
        (er.nom_estado = @estado)
    ORDER BY rr.fecha_reserva, rr.hora_desde;
END;
GO

-- 7. Cambiar estado de una reserva
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_CambiarEstadoReserva') 
    DROP PROCEDURE sp_CambiarEstadoReserva;
GO

CREATE PROCEDURE sp_CambiarEstadoReserva
    @id VARCHAR(36),
    @nuevo_estado NVARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    
    IF @nuevo_estado = 'CANCELADA'
    BEGIN
        UPDATE reservas_restaurantes
        SET cancelada = 1, fecha_hora_cancelacion = GETDATE()
        WHERE nro_reserva = @id;
    END
    ELSE
    BEGIN
        UPDATE reservas_restaurantes
        SET cancelada = 0, fecha_hora_cancelacion = NULL
        WHERE nro_reserva = @id;
    END;
    
    SELECT @@ROWCOUNT;
END;
GO

-- 8. Obtener reservas por email del cliente
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_ObtenerReservasPorCliente') 
    DROP PROCEDURE sp_ObtenerReservasPorCliente;
GO

CREATE PROCEDURE sp_ObtenerReservasPorCliente
    @email NVARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        rr.nro_reserva as id,
        c.nombre + ' ' + c.apellido as nombre_cliente,
        c.correo as email,
        c.telefonos as telefono,
        CAST(CAST(rr.fecha_reserva AS VARCHAR(10)) + ' ' + CAST(rr.hora_desde AS VARCHAR(8)) AS DATETIME2) as fecha_hora,
        (rr.cant_adultos + rr.cant_menores) as cantidad_personas,
        CASE 
            WHEN rr.cancelada = 1 THEN 'CANCELADA'
            WHEN er.nom_estado IS NOT NULL THEN er.nom_estado
            ELSE 'PENDIENTE'
        END as estado,
        rr.notas as observaciones,
        rr.fecha_hora_registro as fecha_creacion,
        rr.fecha_hora_cancelacion as fecha_actualizacion
    FROM reservas_restaurantes rr
    LEFT JOIN clientes c ON c.nro_cliente = rr.nro_cliente
    LEFT JOIN estados_reservas er ON er.cod_estado = rr.cod_estado
    WHERE c.correo = @email
    ORDER BY rr.fecha_reserva, rr.hora_desde;
END;
GO

-- 9. Contar total de reservas
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_ContarReservas') 
    DROP PROCEDURE sp_ContarReservas;
GO

CREATE PROCEDURE sp_ContarReservas
AS
BEGIN
    SET NOCOUNT ON;
    SELECT COUNT(*) AS total_reservas FROM reservas_restaurantes;
END;
GO

-- 10. Verificar si existe una reserva
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_ExisteReserva') 
    DROP PROCEDURE sp_ExisteReserva;
GO

CREATE PROCEDURE sp_ExisteReserva
    @id VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT COUNT(*) AS existe FROM reservas_restaurantes WHERE nro_reserva = @id;
END;
GO

-- 11. Obtener reservas por rango de fechas
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_ObtenerReservasPorRangoFechas') 
    DROP PROCEDURE sp_ObtenerReservasPorRangoFechas;
GO

CREATE PROCEDURE sp_ObtenerReservasPorRangoFechas
    @fecha_inicio DATETIME2,
    @fecha_fin DATETIME2
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        rr.nro_reserva as id,
        c.nombre + ' ' + c.apellido as nombre_cliente,
        c.correo as email,
        c.telefonos as telefono,
        CAST(CAST(rr.fecha_reserva AS VARCHAR(10)) + ' ' + CAST(rr.hora_desde AS VARCHAR(8)) AS DATETIME2) as fecha_hora,
        (rr.cant_adultos + rr.cant_menores) as cantidad_personas,
        CASE 
            WHEN rr.cancelada = 1 THEN 'CANCELADA'
            WHEN er.nom_estado IS NOT NULL THEN er.nom_estado
            ELSE 'PENDIENTE'
        END as estado,
        rr.notas as observaciones,
        rr.fecha_hora_registro as fecha_creacion,
        rr.fecha_hora_cancelacion as fecha_actualizacion
    FROM reservas_restaurantes rr
    LEFT JOIN clientes c ON c.nro_cliente = rr.nro_cliente
    LEFT JOIN estados_reservas er ON er.cod_estado = rr.cod_estado
    WHERE CAST(CAST(rr.fecha_reserva AS VARCHAR(10)) + ' ' + CAST(rr.hora_desde AS VARCHAR(8)) AS DATETIME2) BETWEEN @fecha_inicio AND @fecha_fin
    ORDER BY rr.fecha_reserva, rr.hora_desde;
END;
GO

-- 12. Obtener estadísticas de reservas
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_ObtenerEstadisticasReservas') 
    DROP PROCEDURE sp_ObtenerEstadisticasReservas;
GO

CREATE PROCEDURE sp_ObtenerEstadisticasReservas
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        COUNT(*) as total_reservas,
        COUNT(CASE WHEN cancelada = 0 THEN 1 END) as reservas_confirmadas,
        COUNT(CASE WHEN cancelada = 0 AND cod_estado IS NULL THEN 1 END) as reservas_pendientes,
        COUNT(CASE WHEN cancelada = 1 THEN 1 END) as reservas_canceladas,
        AVG(CAST((cant_adultos + cant_menores) AS FLOAT)) as promedio_personas,
        SUM(cant_adultos + cant_menores) as total_personas
    FROM reservas_restaurantes;
END;
GO

PRINT 'Stored procedures creados exitosamente!';
