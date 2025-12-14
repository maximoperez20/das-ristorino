-- Script para crear stored procedures para el sistema Ristorino
-- Incluye procedimientos para clientes, reservas, restaurantes y promociones

USE das_ristorino;
GO

-- =====================================================
-- STORED PROCEDURES PARA CLIENTES
-- =====================================================

-- 1. Crear cliente
CREATE OR ALTER PROCEDURE sp_CrearCliente
    @apellido VARCHAR(120),
    @nombre VARCHAR(120),
    @clave VARCHAR(200),
    @correo VARCHAR(150),
    @telefonos VARCHAR(120),
    @nro_localidad VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Insertar el nuevo cliente (nro_cliente se genera automáticamente con NEWID())
    INSERT INTO clientes (apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado)
    VALUES (@apellido, @nombre, @clave, @correo, @telefonos, @nro_localidad, 1);
    
    -- Retornar el cliente recién creado
    SELECT 
        nro_cliente,
        apellido,
        nombre,
        clave,
        correo,
        telefonos,
        nro_localidad,
        habilitado
    FROM clientes
    WHERE correo = @correo;
END;
GO

-- 2. Obtener localidad del cliente por nroCliente
CREATE OR ALTER PROCEDURE sp_ObtenerLocalidadPorNroCliente
    @nroCliente VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        l.nom_localidad AS localidad
    FROM clientes c
    INNER JOIN localidades l ON c.nro_localidad = l.nro_localidad
    WHERE c.nro_cliente = @nroCliente;
END;
GO

-- =====================================================
-- STORED PROCEDURES PARA RESERVAS
-- =====================================================

-- 1. Obtener todas las reservas
CREATE OR ALTER PROCEDURE sp_ObtenerTodasLasReservas
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
CREATE OR ALTER PROCEDURE sp_ObtenerReservaPorId
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
CREATE OR ALTER PROCEDURE sp_CrearReserva
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
CREATE OR ALTER PROCEDURE sp_ActualizarReserva
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
CREATE OR ALTER PROCEDURE sp_EliminarReserva
    @id VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;
    DELETE FROM reservas_restaurantes WHERE nro_reserva = @id;
    SELECT @@ROWCOUNT;
END;
GO

-- 6. Obtener reservas por estado
CREATE OR ALTER PROCEDURE sp_ObtenerReservasPorEstado
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
CREATE OR ALTER PROCEDURE sp_CambiarEstadoReserva
    @id VARCHAR(36),
    @nuevo_estado NVARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @cod_estado_nuevo VARCHAR(36);
    DECLARE @estado_normalizado NVARCHAR(20) = UPPER(LTRIM(RTRIM(@nuevo_estado)));
    
    -- Obtener el código del estado según el nombre
    SELECT @cod_estado_nuevo = cod_estado 
    FROM estados_reservas 
    WHERE UPPER(LTRIM(RTRIM(nom_estado))) = @estado_normalizado;
    
    -- Si el estado es "CANCELADA"
    IF @estado_normalizado = 'CANCELADA'
    BEGIN
        -- Actualizar cancelada, fecha_hora_cancelacion y cod_estado
        UPDATE reservas_restaurantes
        SET cancelada = 1, 
            fecha_hora_cancelacion = GETDATE(),
            cod_estado = @cod_estado_nuevo
        WHERE nro_reserva = @id;
    END
    -- Si el estado es "CONFIRMADA"
    ELSE IF @estado_normalizado = 'CONFIRMADA'
    BEGIN
        -- Actualizar cod_estado a Confirmada y limpiar cancelada
        UPDATE reservas_restaurantes
        SET cancelada = 0, 
            fecha_hora_cancelacion = NULL,
            cod_estado = @cod_estado_nuevo
        WHERE nro_reserva = @id;
    END
    -- Si el estado es "PENDIENTE"
    ELSE IF @estado_normalizado = 'PENDIENTE'
    BEGIN
        -- Actualizar cod_estado a Pendiente y limpiar cancelada
        UPDATE reservas_restaurantes
        SET cancelada = 0, 
            fecha_hora_cancelacion = NULL,
            cod_estado = @cod_estado_nuevo
        WHERE nro_reserva = @id;
    END
    ELSE
    BEGIN
        -- Para otros estados, solo actualizar cod_estado si se encontró
        IF @cod_estado_nuevo IS NOT NULL
        BEGIN
            UPDATE reservas_restaurantes
            SET cod_estado = @cod_estado_nuevo
            WHERE nro_reserva = @id;
        END
        ELSE
        BEGIN
            -- Si no se encontró el estado, solo limpiar cancelada si se reactiva
            UPDATE reservas_restaurantes
            SET cancelada = 0, 
                fecha_hora_cancelacion = NULL
            WHERE nro_reserva = @id;
        END
    END;
    
    SELECT @@ROWCOUNT;
END;
GO

-- 8. Obtener reservas por email del cliente
CREATE OR ALTER PROCEDURE sp_ObtenerReservasPorCliente
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

-- 8.1. Obtener reservas por nro_cliente
CREATE OR ALTER PROCEDURE sp_ObtenerReservasPorNroCliente
    @nro_cliente VARCHAR(36),
    @nro_idioma INT = 0  -- Default: es-AR
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
        rr.cant_adultos as cant_adultos,
        rr.cant_menores as cant_menores,
        CASE 
            WHEN rr.cancelada = 1 THEN ISNULL(ier.estado, 'CANCELADA')
            WHEN er.nom_estado IS NOT NULL THEN ISNULL(ier.estado, er.nom_estado)
            ELSE ISNULL(ier.estado, 'PENDIENTE')
        END as estado,
        rr.notas as observaciones,
        rr.fecha_hora_registro as fecha_creacion,
        rr.fecha_hora_cancelacion as fecha_actualizacion,
        r.razon_social as nombre_restaurante,
        s.nom_sucursal as nombre_sucursal,
        ISNULL(iz.zona, 'Zona') as nombre_zona,
        ISNULL(
            (SELECT STRING_AGG(ISNULL(idcp.valor_dominio, dcp.nom_valor_dominio), ', ')
             FROM preferencias_reservas_restaurantes prr
             INNER JOIN dominio_categorias_preferencias dcp 
                 ON prr.cod_categoria = dcp.cod_categoria 
                 AND prr.nro_valor_dominio = dcp.nro_valor_dominio
             LEFT JOIN idiomas_dominio_cat_preferencias idcp
                 ON dcp.cod_categoria = idcp.cod_categoria
                 AND dcp.nro_valor_dominio = idcp.nro_valor_dominio
                 AND idcp.nro_idioma = @nro_idioma
             WHERE prr.nro_reserva = rr.nro_reserva),
            ''
        ) as preferencias
    FROM reservas_restaurantes rr
    LEFT JOIN clientes c ON c.nro_cliente = rr.nro_cliente
    LEFT JOIN estados_reservas er ON er.cod_estado = rr.cod_estado
    LEFT JOIN idiomas_estados_reservas ier 
        ON er.cod_estado = ier.cod_estado 
        AND ier.nro_idioma = @nro_idioma
    LEFT JOIN restaurantes r ON r.nro_restaurante = rr.nro_restaurante
    LEFT JOIN sucursales_restaurantes s ON s.nro_restaurante = rr.nro_restaurante AND s.nro_sucursal = rr.nro_sucursal
    LEFT JOIN idiomas_zonas_suc_restaurantes iz
        ON iz.nro_restaurante = rr.nro_restaurante 
        AND iz.nro_sucursal = rr.nro_sucursal 
        AND iz.cod_zona = rr.cod_zona
        AND iz.nro_idioma = @nro_idioma
    WHERE rr.nro_cliente = @nro_cliente
    ORDER BY rr.fecha_reserva DESC, rr.hora_desde DESC;
END;
GO

-- 9. Contar total de reservas
CREATE OR ALTER PROCEDURE sp_ContarReservas
AS
BEGIN
    SET NOCOUNT ON;
    SELECT COUNT(*) AS total_reservas FROM reservas_restaurantes;
END;
GO

-- 10. Verificar si existe una reserva
CREATE OR ALTER PROCEDURE sp_ExisteReserva
    @id VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT COUNT(*) AS existe FROM reservas_restaurantes WHERE nro_reserva = @id;
END;
GO

-- 11. Obtener reservas por rango de fechas
CREATE OR ALTER PROCEDURE sp_ObtenerReservasPorRangoFechas
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
CREATE OR ALTER PROCEDURE sp_ObtenerEstadisticasReservas
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

-- =============================
-- Restaurantes (SPs mínimos)
-- =============================
CREATE OR ALTER PROCEDURE sp_ObtenerTodosLosRestaurantes
AS
BEGIN
    SET NOCOUNT ON;

    ;WITH datos AS (
        SELECT 
            r.nro_restaurante,
            r.razon_social,
            MIN(s.calle) AS calle,
            MIN(s.nro_calle) AS nro_calle,
            MIN(s.barrio) AS barrio,
            MIN(s.telefonos) AS telefono,
            MAX(s.total_comensales) AS capacidad,
            MIN(t.hora_desde) AS horario_apertura,
            MAX(t.hora_hasta) AS horario_cierre
        FROM restaurantes r
        LEFT JOIN sucursales_restaurantes s ON s.nro_restaurante = r.nro_restaurante
        LEFT JOIN turnos_sucursales_restaurantes t ON t.nro_restaurante = r.nro_restaurante
        GROUP BY r.nro_restaurante, r.razon_social
    )
    SELECT 
        CAST(ROW_NUMBER() OVER (ORDER BY razon_social) AS BIGINT) AS id,
        nro_restaurante,
        razon_social AS nombre,
        LTRIM(RTRIM(
            ISNULL(calle,'') +
            CASE WHEN nro_calle IS NOT NULL THEN ' ' + CAST(nro_calle AS VARCHAR(10)) ELSE '' END +
            CASE WHEN barrio IS NOT NULL THEN ', ' + barrio ELSE '' END
        )) AS direccion,
        telefono AS telefono,
        CAST(NULL AS VARCHAR(100)) AS email,
        ISNULL(capacidad, 0) AS capacidad,
        ISNULL(horario_apertura, CAST('08:00:00' AS TIME(0))) AS horario_apertura,
        ISNULL(horario_cierre,  CAST('23:00:00' AS TIME(0))) AS horario_cierre,
        CAST(NULL AS VARCHAR(100)) AS categoria,
        CAST(4.0 AS FLOAT) AS calificacion,
        CAST(1 AS BIT) AS activo,
        CAST(NULL AS VARCHAR(255)) AS imagen_url
    FROM datos
    ORDER BY nombre;
END;
GO

CREATE OR ALTER PROCEDURE sp_ObtenerRestaurantePorId
    @nroRestaurante VARCHAR(36),
    @nro_idioma INT = 0  -- Default: es-AR
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @idioma INT = ISNULL(@nro_idioma, 0);

    -- Primer result set: Datos del restaurante
    ;WITH datos AS (
        SELECT 
            r.nro_restaurante,
            r.razon_social,
            MIN(s.calle) AS calle,
            MIN(s.nro_calle) AS nro_calle,
            MIN(s.barrio) AS barrio,
            MIN(s.telefonos) AS telefono,
            MAX(s.total_comensales) AS capacidad,
            MIN(t.hora_desde) AS horario_apertura,
            MAX(t.hora_hasta) AS horario_cierre
        FROM restaurantes r
        LEFT JOIN sucursales_restaurantes s ON s.nro_restaurante = r.nro_restaurante
        LEFT JOIN turnos_sucursales_restaurantes t ON t.nro_restaurante = r.nro_restaurante
        WHERE r.nro_restaurante = @nroRestaurante
        GROUP BY r.nro_restaurante, r.razon_social
    ), enumerado AS (
        SELECT 
            CAST(ROW_NUMBER() OVER (ORDER BY razon_social) AS BIGINT) AS id,
            *
        FROM datos
    )
    SELECT 
        e.id,
        e.nro_restaurante,
        e.razon_social AS nombre,
        LTRIM(RTRIM(
            ISNULL(e.calle,'') +
            CASE WHEN e.nro_calle IS NOT NULL THEN ' ' + CAST(e.nro_calle AS VARCHAR(10)) ELSE '' END +
            CASE WHEN e.barrio IS NOT NULL THEN ', ' + e.barrio ELSE '' END
        )) AS direccion,
        e.telefono AS telefono,
        -- Email desde configuracion_restaurantes (si existe el atributo)
        (SELECT TOP 1 valor FROM configuracion_restaurantes cr 
         JOIN atributos a ON cr.cod_atributo = a.cod_atributo 
         WHERE cr.nro_restaurante = @nroRestaurante 
           AND a.nom_atributo = 'email') AS email,
        ISNULL(e.capacidad, 0) AS capacidad,
        ISNULL(e.horario_apertura, CAST('08:00:00' AS TIME(0))) AS horario_apertura,
        ISNULL(e.horario_cierre,  CAST('23:00:00' AS TIME(0))) AS horario_cierre,
        -- Categoría/Tipo de cocina (primera preferencia de tipo de comida)
        (SELECT TOP 1 dcp.nom_valor_dominio 
         FROM preferencias_restaurantes pr
         JOIN categorias_preferencias cp ON pr.cod_categoria = cp.cod_categoria
         JOIN dominio_categorias_preferencias dcp ON pr.cod_categoria = dcp.cod_categoria 
           AND pr.nro_valor_dominio = dcp.nro_valor_dominio
         WHERE pr.nro_restaurante = @nroRestaurante 
           AND cp.nom_categoria = 'Tipo de comida'
           AND pr.nro_sucursal IS NULL
         ORDER BY pr.nro_preferencia) AS categoria,
        CAST(4.0 AS FLOAT) AS calificacion,
        CAST(1 AS BIT) AS activo,
        CAST(NULL AS VARCHAR(255)) AS imagen_url  -- Imágenes se obtienen en otro stored procedure
    FROM enumerado e;

    -- Segundo result set: Promociones vigentes del restaurante
    SELECT 
        cr.nro_restaurante,
        cr.nro_idioma,
        cr.nro_contenido,
        LEFT(ISNULL(cr.contenido_promocional, cr.contenido_a_publicar), 100) AS titulo,
        ISNULL(cr.contenido_promocional, cr.contenido_a_publicar) AS descripcion,
        CAST(NULL AS DECIMAL(10,2)) AS descuento_porcentaje,
        CAST(NULL AS DECIMAL(10,2)) AS descuento_fijo,
        CAST(cr.fecha_ini_vigencia AS DATETIME2) AS fecha_inicio,
        CAST(cr.fecha_fin_vigencia AS DATETIME2) AS fecha_fin,
        CASE WHEN cr.fecha_ini_vigencia IS NOT NULL AND cr.fecha_fin_vigencia IS NOT NULL 
             AND CAST(GETDATE() AS DATE) BETWEEN cr.fecha_ini_vigencia AND cr.fecha_fin_vigencia 
             THEN 'ACTIVA' ELSE 'INACTIVA' END AS estado,
        cr.imagen_promocional AS imagen_url,
        CAST(NULL AS INT) AS min_personas,
        CAST(NULL AS INT) AS max_personas,
        cr.cod_contenido_restaurante AS codigo_promocion,
        CAST(0 AS BIT) AS requiere_codigo
    FROM contenidos_restaurantes cr
    WHERE cr.nro_restaurante = @nroRestaurante
      AND cr.nro_idioma = @idioma  -- FILTRAR POR IDIOMA
      AND cr.contenido_promocional IS NOT NULL  -- Solo promociones (no contenidos generales)
      AND cr.fecha_ini_vigencia IS NOT NULL
      AND cr.fecha_fin_vigencia IS NOT NULL
      AND CAST(GETDATE() AS DATE) <= cr.fecha_fin_vigencia  -- Vigentes o futuras
    ORDER BY cr.fecha_ini_vigencia DESC;
END;
GO

-- =============================
-- Obtener sucursales de un restaurante
-- =============================
CREATE OR ALTER PROCEDURE sp_ObtenerSucursalesPorRestaurante
    @nroRestaurante VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        s.nro_restaurante,
        s.nro_sucursal,
        s.nom_sucursal AS nombre,
        LTRIM(RTRIM(
            ISNULL(s.calle,'') +
            CASE WHEN s.nro_calle IS NOT NULL THEN ' ' + CAST(s.nro_calle AS VARCHAR(10)) ELSE '' END +
            CASE WHEN s.barrio IS NOT NULL THEN ', ' + s.barrio ELSE '' END
        )) AS direccion,
        l.nom_localidad AS localidad,
        p.nom_provincia AS provincia,
        s.cod_postal AS codigo_postal,
        s.telefonos,
        s.total_comensales AS capacidad,
        s.min_tolerencia_reserva AS min_tolerancia_reserva
    FROM sucursales_restaurantes s
    LEFT JOIN localidades l ON s.nro_localidad = l.nro_localidad
    LEFT JOIN provincias p ON l.cod_provincia = p.cod_provincia
    WHERE s.nro_restaurante = @nroRestaurante
    ORDER BY s.nom_sucursal;
END;
GO

-- =============================
-- Obtener tipos de cocina de un restaurante
-- =============================
CREATE OR ALTER PROCEDURE sp_ObtenerTiposCocinaPorRestaurante
    @nroRestaurante VARCHAR(36),
    @nro_idioma INT = 0  -- Default: es-AR
AS
BEGIN
    SET NOCOUNT ON;

    -- Manejar NULL explícitamente usando el valor por defecto
    DECLARE @idioma INT = ISNULL(@nro_idioma, 0);

    SELECT 
        ISNULL(idcp.valor_dominio, dcp.nom_valor_dominio) AS tipo_cocina
    FROM preferencias_restaurantes pr
    JOIN categorias_preferencias cp ON pr.cod_categoria = cp.cod_categoria
    JOIN dominio_categorias_preferencias dcp ON pr.cod_categoria = dcp.cod_categoria 
      AND pr.nro_valor_dominio = dcp.nro_valor_dominio
    LEFT JOIN idiomas_dominio_cat_preferencias idcp
        ON dcp.cod_categoria = idcp.cod_categoria
        AND dcp.nro_valor_dominio = idcp.nro_valor_dominio
        AND idcp.nro_idioma = @idioma
    WHERE pr.nro_restaurante = @nroRestaurante 
      AND cp.nom_categoria = 'Tipo de comida'
      AND pr.nro_sucursal IS NULL  -- Solo preferencias del restaurante, no de sucursal específica
    ORDER BY pr.nro_preferencia;
END;
GO

-- =============================
-- Obtener promociones vigentes de un restaurante
-- =============================
CREATE OR ALTER PROCEDURE sp_ObtenerPromocionesPorRestaurante
    @nroRestaurante VARCHAR(36),
    @nro_idioma INT = 0  -- Default: es-AR
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        cr.nro_restaurante,
        cr.nro_idioma,
        cr.nro_contenido,
        LEFT(ISNULL(cr.contenido_promocional, cr.contenido_a_publicar), 100) AS titulo,
        ISNULL(cr.contenido_promocional, cr.contenido_a_publicar) AS descripcion,
        CAST(NULL AS DECIMAL(10,2)) AS descuento_porcentaje,
        CAST(NULL AS DECIMAL(10,2)) AS descuento_fijo,
        CAST(cr.fecha_ini_vigencia AS DATETIME2) AS fecha_inicio,
        CAST(cr.fecha_fin_vigencia AS DATETIME2) AS fecha_fin,
        CASE WHEN cr.fecha_ini_vigencia IS NOT NULL AND cr.fecha_fin_vigencia IS NOT NULL 
             AND CAST(GETDATE() AS DATE) BETWEEN cr.fecha_ini_vigencia AND cr.fecha_fin_vigencia 
             THEN 'ACTIVA' ELSE 'INACTIVA' END AS estado,
        -- Devolver directamente la URL de la imagen almacenada
        cr.imagen_promocional AS imagen_url,
        CAST(NULL AS INT) AS min_personas,
        CAST(NULL AS INT) AS max_personas,
        cr.cod_contenido_restaurante AS codigo_promocion,
        CAST(0 AS BIT) AS requiere_codigo
    FROM contenidos_restaurantes cr
    WHERE cr.nro_restaurante = @nroRestaurante
      AND cr.nro_idioma = @nro_idioma  -- FILTRAR POR IDIOMA
      AND cr.contenido_promocional IS NOT NULL  -- Solo promociones (no contenidos generales)
      AND cr.fecha_ini_vigencia IS NOT NULL
      AND cr.fecha_fin_vigencia IS NOT NULL
      AND CAST(GETDATE() AS DATE) <= cr.fecha_fin_vigencia  -- Vigentes o futuras
    ORDER BY cr.fecha_ini_vigencia DESC;
END;
GO

-- =============================
-- Búsqueda de restaurantes por NLP (lenguaje natural)
-- =============================
CREATE OR ALTER PROCEDURE sp_BuscarRestaurantesPorNLP
    @tiposComida NVARCHAR(MAX) = NULL,        -- JSON array o lista separada por comas
    @barrios NVARCHAR(MAX) = NULL,             -- JSON array o lista separada por comas
    @localidades NVARCHAR(MAX) = NULL,        -- JSON array o lista separada por comas
    @ambientes NVARCHAR(MAX) = NULL,           -- JSON array o lista separada por comas
    @rangosPrecio NVARCHAR(MAX) = NULL,       -- JSON array o lista separada por comas
    @palabrasClave NVARCHAR(MAX) = NULL,      -- Palabras clave para búsqueda en nombre/descripción
    @nroCliente VARCHAR(36) = NULL            -- UUID del cliente autenticado (opcional, solo si está autenticado)
AS
BEGIN
    SET NOCOUNT ON;

    ;WITH restaurantes_filtrados AS (
        SELECT DISTINCT
            r.nro_restaurante,
            r.razon_social,
            -- Calcular score de relevancia para ordenar
            -- Prioridad: configuracion_restaurantes (15) > preferencias_restaurantes (10) > match con preferencias_clientes (5) > palabras clave (5)
            CASE 
                -- Match en configuracion_restaurantes (mayor peso: 15) - match parcial
                WHEN EXISTS (
                    SELECT 1 FROM configuracion_restaurantes cr
                    JOIN atributos a ON cr.cod_atributo = a.cod_atributo
                    WHERE cr.nro_restaurante = r.nro_restaurante
                        AND a.nom_atributo = 'Tipo de cocina'
                        AND @tiposComida IS NOT NULL AND @tiposComida <> ''
                        AND EXISTS (
                            SELECT 1 FROM STRING_SPLIT(@tiposComida, ',') AS tipo
                            WHERE LTRIM(RTRIM(tipo.value)) <> ''
                            AND (
                                cr.valor LIKE '%' + LTRIM(RTRIM(tipo.value)) + '%'
                                OR LTRIM(RTRIM(tipo.value)) LIKE '%' + cr.valor + '%'
                            )
                        )
                ) THEN 15
                -- Match en preferencias_restaurantes (peso: 10) - match exacto
                WHEN EXISTS (
                    SELECT 1 FROM dominio_categorias_preferencias dcp2
                    JOIN categorias_preferencias cp2 ON dcp2.cod_categoria = cp2.cod_categoria
                    JOIN preferencias_restaurantes pr2 ON pr2.nro_restaurante = r.nro_restaurante 
                        AND pr2.nro_sucursal IS NULL
                        AND pr2.cod_categoria = dcp2.cod_categoria
                        AND pr2.nro_valor_dominio = dcp2.nro_valor_dominio
                    WHERE cp2.nom_categoria = 'Tipo de comida'
                        AND @tiposComida IS NOT NULL AND @tiposComida <> ''
                        AND dcp2.nom_valor_dominio IN (
                            SELECT value FROM STRING_SPLIT(@tiposComida, ',')
                        )
                ) THEN 10
                -- Match con preferencias del cliente autenticado (bonus: 5) - solo si está autenticado
                WHEN @nroCliente IS NOT NULL AND EXISTS (
                    SELECT 1 FROM preferencias_clientes pc
                    JOIN categorias_preferencias cp5 ON pc.cod_categoria = cp5.cod_categoria
                    JOIN preferencias_restaurantes pr5 ON pr5.nro_restaurante = r.nro_restaurante
                        AND pr5.nro_sucursal IS NULL
                        AND pr5.cod_categoria = pc.cod_categoria
                        AND pr5.nro_valor_dominio = pc.nro_valor_dominio
                    WHERE pc.nro_cliente = @nroCliente
                        AND cp5.nom_categoria IN ('Tipo de comida', 'Ambiente', 'Rango de precio')
                ) THEN 5
                -- Match por palabras clave (peso: 5)
                WHEN @palabrasClave IS NOT NULL AND @palabrasClave <> '' AND EXISTS (
                    SELECT 1 FROM STRING_SPLIT(@palabrasClave, ',') AS keyword
                    WHERE LTRIM(RTRIM(keyword.value)) <> ''
                    AND r.razon_social LIKE '%' + LTRIM(RTRIM(keyword.value)) + '%'
                ) THEN 5
                ELSE 0
            END AS relevancia
        FROM restaurantes r
        LEFT JOIN sucursales_restaurantes s ON s.nro_restaurante = r.nro_restaurante
        LEFT JOIN localidades l ON s.nro_localidad = l.nro_localidad
        LEFT JOIN preferencias_restaurantes pr ON pr.nro_restaurante = r.nro_restaurante AND pr.nro_sucursal IS NULL
        LEFT JOIN categorias_preferencias cp ON pr.cod_categoria = cp.cod_categoria
        LEFT JOIN dominio_categorias_preferencias dcp ON pr.cod_categoria = dcp.cod_categoria 
            AND pr.nro_valor_dominio = dcp.nro_valor_dominio
        LEFT JOIN configuracion_restaurantes cr_config ON cr_config.nro_restaurante = r.nro_restaurante
        LEFT JOIN atributos a_config ON cr_config.cod_atributo = a_config.cod_atributo
        WHERE 
            -- LÓGICA FLEXIBLE: Los filtros principales (tipo comida, localidad) deben cumplirse si se especifican
            -- Las palabras clave son opcionales y se usan como refuerzo, no como requisito obligatorio
            (
                -- Si se especifica tipo de comida, DEBE cumplirse (filtro principal)
                (@tiposComida IS NULL OR @tiposComida = '' OR (
                    EXISTS (
                        SELECT 1 FROM configuracion_restaurantes cr
                        JOIN atributos a ON cr.cod_atributo = a.cod_atributo
                        WHERE cr.nro_restaurante = r.nro_restaurante
                            AND a.nom_atributo = 'Tipo de cocina'
                            AND EXISTS (
                                SELECT 1 FROM STRING_SPLIT(@tiposComida, ',') AS tipo
                                WHERE LTRIM(RTRIM(tipo.value)) <> ''
                                AND (
                                    cr.valor LIKE '%' + LTRIM(RTRIM(tipo.value)) + '%'
                                    OR LTRIM(RTRIM(tipo.value)) LIKE '%' + cr.valor + '%'
                                )
                            )
                    )
                    OR EXISTS (
                        SELECT 1 FROM dominio_categorias_preferencias dcp2
                        JOIN categorias_preferencias cp2 ON dcp2.cod_categoria = cp2.cod_categoria
                        JOIN preferencias_restaurantes pr2 ON pr2.nro_restaurante = r.nro_restaurante
                            AND pr2.nro_sucursal IS NULL
                            AND pr2.cod_categoria = dcp2.cod_categoria
                            AND pr2.nro_valor_dominio = dcp2.nro_valor_dominio
                        WHERE cp2.nom_categoria = 'Tipo de comida'
                            AND dcp2.nom_valor_dominio IN (
                                SELECT value FROM STRING_SPLIT(@tiposComida, ',')
                            )
                    )
                ))
                -- Si se especifica barrio, DEBE cumplirse (filtro principal)
                AND (@barrios IS NULL OR @barrios = '' OR EXISTS (
                    SELECT 1 FROM sucursales_restaurantes s_sub 
                    WHERE s_sub.nro_restaurante = r.nro_restaurante 
                    AND s_sub.barrio IN (
                        SELECT value FROM STRING_SPLIT(@barrios, ',')
                    )
                ))
                -- Si se especifica localidad, DEBE cumplirse (filtro principal)
                AND (@localidades IS NULL OR @localidades = '' OR EXISTS (
                    SELECT 1 FROM sucursales_restaurantes s_sub
                    JOIN localidades l_sub ON s_sub.nro_localidad = l_sub.nro_localidad
                    WHERE s_sub.nro_restaurante = r.nro_restaurante 
                    AND l_sub.nom_localidad IN (
                        SELECT value FROM STRING_SPLIT(@localidades, ',')
                    )
                ))
                -- Si se especifica ambiente, DEBE cumplirse (filtro principal)
                AND (@ambientes IS NULL OR @ambientes = '' OR (
                    EXISTS (
                        SELECT 1 FROM configuracion_restaurantes cr
                        JOIN atributos a ON cr.cod_atributo = a.cod_atributo
                        WHERE cr.nro_restaurante = r.nro_restaurante
                            AND a.nom_atributo = 'Estilo'
                            AND EXISTS (
                                SELECT 1 FROM STRING_SPLIT(@ambientes, ',') AS ambiente
                                WHERE LTRIM(RTRIM(ambiente.value)) <> ''
                                AND (
                                    cr.valor LIKE '%' + LTRIM(RTRIM(ambiente.value)) + '%'
                                    OR LTRIM(RTRIM(ambiente.value)) LIKE '%' + cr.valor + '%'
                                )
                            )
                    )
                    OR EXISTS (
                        SELECT 1 FROM dominio_categorias_preferencias dcp3
                        JOIN categorias_preferencias cp3 ON dcp3.cod_categoria = cp3.cod_categoria
                        JOIN preferencias_restaurantes pr3 ON pr3.nro_restaurante = r.nro_restaurante
                            AND pr3.nro_sucursal IS NULL
                            AND pr3.cod_categoria = dcp3.cod_categoria
                            AND pr3.nro_valor_dominio = dcp3.nro_valor_dominio
                        WHERE cp3.nom_categoria = 'Ambiente'
                            AND dcp3.nom_valor_dominio IN (
                                SELECT value FROM STRING_SPLIT(@ambientes, ',')
                            )
                    )
                ))
                -- Si se especifica rango de precio, DEBE cumplirse (filtro principal)
                AND (@rangosPrecio IS NULL OR @rangosPrecio = '' OR (
                    EXISTS (
                        SELECT 1 FROM configuracion_restaurantes cr
                        JOIN atributos a ON cr.cod_atributo = a.cod_atributo
                        WHERE cr.nro_restaurante = r.nro_restaurante
                            AND a.nom_atributo = 'Nivel de precio'
                            AND EXISTS (
                                SELECT 1 FROM STRING_SPLIT(@rangosPrecio, ',') AS precio
                                WHERE LTRIM(RTRIM(precio.value)) <> ''
                                AND (
                                    cr.valor LIKE '%' + LTRIM(RTRIM(precio.value)) + '%'
                                    OR LTRIM(RTRIM(precio.value)) LIKE '%' + cr.valor + '%'
                                )
                            )
                    )
                    OR EXISTS (
                        SELECT 1 FROM dominio_categorias_preferencias dcp4
                        JOIN categorias_preferencias cp4 ON dcp4.cod_categoria = cp4.cod_categoria
                        JOIN preferencias_restaurantes pr4 ON pr4.nro_restaurante = r.nro_restaurante
                            AND pr4.nro_sucursal IS NULL
                            AND pr4.cod_categoria = dcp4.cod_categoria
                            AND pr4.nro_valor_dominio = dcp4.nro_valor_dominio
                        WHERE cp4.nom_categoria = 'Rango de precio'
                            AND dcp4.nom_valor_dominio IN (
                                SELECT value FROM STRING_SPLIT(@rangosPrecio, ',')
                            )
                    )
                ))
                -- Palabras clave: OPCIONALES - solo se usan si NO hay otros filtros principales
                -- Si hay tipo de comida o localidad, las palabras clave son opcionales (no bloquean resultados)
                AND (
                    -- Si hay filtros principales (tipo comida o localidad), las palabras clave son opcionales
                    (@tiposComida IS NOT NULL AND @tiposComida <> '') OR
                    (@localidades IS NOT NULL AND @localidades <> '') OR
                    (@barrios IS NOT NULL AND @barrios <> '') OR
                    (@ambientes IS NOT NULL AND @ambientes <> '') OR
                    (@rangosPrecio IS NOT NULL AND @rangosPrecio <> '') OR
                    -- Si NO hay filtros principales, entonces las palabras clave son obligatorias
                    (@palabrasClave IS NULL OR @palabrasClave = '' OR EXISTS (
                        SELECT 1 FROM STRING_SPLIT(@palabrasClave, ',') AS keyword
                        WHERE LTRIM(RTRIM(keyword.value)) <> ''
                        AND (
                            r.razon_social LIKE '%' + LTRIM(RTRIM(keyword.value)) + '%'
                            OR EXISTS (
                                SELECT 1 FROM contenidos_restaurantes cr
                                WHERE cr.nro_restaurante = r.nro_restaurante
                                    AND (
                                        cr.contenido_a_publicar LIKE '%' + LTRIM(RTRIM(keyword.value)) + '%'
                                        OR cr.contenido_promocional LIKE '%' + LTRIM(RTRIM(keyword.value)) + '%'
                                    )
                            )
                        )
                    ))
                )
                -- Requiere que haya AL MENOS un filtro activo (no devolver todos si no hay criterios)
                AND NOT (
                    (@tiposComida IS NULL OR @tiposComida = '')
                    AND (@barrios IS NULL OR @barrios = '')
                    AND (@localidades IS NULL OR @localidades = '')
                    AND (@ambientes IS NULL OR @ambientes = '')
                    AND (@rangosPrecio IS NULL OR @rangosPrecio = '')
                    AND (@palabrasClave IS NULL OR @palabrasClave = '')
                )
            )
    )
    SELECT 
        CAST(ROW_NUMBER() OVER (ORDER BY MAX(rf.relevancia) DESC, rf.razon_social) AS BIGINT) AS id,
        rf.nro_restaurante AS nro_restaurante,
        rf.razon_social AS nombre,
        LTRIM(RTRIM(
            ISNULL((SELECT TOP 1 s2.calle FROM sucursales_restaurantes s2 WHERE s2.nro_restaurante = rf.nro_restaurante), '') +
            CASE WHEN (SELECT TOP 1 s2.nro_calle FROM sucursales_restaurantes s2 WHERE s2.nro_restaurante = rf.nro_restaurante) IS NOT NULL 
                 THEN ' ' + CAST((SELECT TOP 1 s2.nro_calle FROM sucursales_restaurantes s2 WHERE s2.nro_restaurante = rf.nro_restaurante) AS VARCHAR(10)) 
                 ELSE '' END +
            CASE WHEN (SELECT TOP 1 s2.barrio FROM sucursales_restaurantes s2 WHERE s2.nro_restaurante = rf.nro_restaurante) IS NOT NULL 
                 THEN ', ' + (SELECT TOP 1 s2.barrio FROM sucursales_restaurantes s2 WHERE s2.nro_restaurante = rf.nro_restaurante) 
                 ELSE '' END
        )) AS direccion,
        (SELECT TOP 1 s2.telefonos FROM sucursales_restaurantes s2 WHERE s2.nro_restaurante = rf.nro_restaurante) AS telefono,
        (SELECT TOP 1 valor FROM configuracion_restaurantes cr 
         JOIN atributos a ON cr.cod_atributo = a.cod_atributo 
         WHERE cr.nro_restaurante = rf.nro_restaurante 
           AND a.nom_atributo = 'email') AS email,
        ISNULL((SELECT MAX(s2.total_comensales) FROM sucursales_restaurantes s2 WHERE s2.nro_restaurante = rf.nro_restaurante), 0) AS capacidad,
        ISNULL((SELECT MIN(t2.hora_desde) FROM turnos_sucursales_restaurantes t2 WHERE t2.nro_restaurante = rf.nro_restaurante), CAST('08:00:00' AS TIME(0))) AS horario_apertura,
        ISNULL((SELECT MAX(t2.hora_hasta) FROM turnos_sucursales_restaurantes t2 WHERE t2.nro_restaurante = rf.nro_restaurante), CAST('23:00:00' AS TIME(0))) AS horario_cierre,
        (SELECT TOP 1 dcp.nom_valor_dominio 
         FROM preferencias_restaurantes pr
         JOIN categorias_preferencias cp ON pr.cod_categoria = cp.cod_categoria
         JOIN dominio_categorias_preferencias dcp ON pr.cod_categoria = dcp.cod_categoria 
           AND pr.nro_valor_dominio = dcp.nro_valor_dominio
         WHERE pr.nro_restaurante = rf.nro_restaurante 
           AND cp.nom_categoria = 'Tipo de comida'
           AND pr.nro_sucursal IS NULL
         ORDER BY pr.nro_preferencia) AS categoria,
        CAST(4.0 AS FLOAT) AS calificacion,
        CAST(1 AS BIT) AS activo,
        CAST(NULL AS VARCHAR(255)) AS imagen_url
    FROM restaurantes_filtrados rf
    GROUP BY rf.nro_restaurante, rf.razon_social
    ORDER BY MAX(rf.relevancia) DESC, rf.razon_social;
END;
GO

-- =============================
-- Obtener sugerencias de restaurantes
-- Basado en preferencias del usuario o restaurantes populares
-- =============================
CREATE OR ALTER PROCEDURE sp_ObtenerSugerenciasRestaurantes
    @excluirRestaurantes NVARCHAR(MAX) = NULL,  -- Lista de UUIDs separados por comas a excluir
    @nroCliente VARCHAR(36) = NULL,              -- UUID del cliente autenticado (opcional)
    @limite INT = 10                            -- Cantidad máxima de sugerencias
AS
BEGIN
    SET NOCOUNT ON;

    ;WITH sugerencias AS (
        SELECT DISTINCT
            r.nro_restaurante,
            r.razon_social,
            -- Calcular score de relevancia para ordenar
            -- Prioridad: preferencias del usuario (20) > restaurantes con promociones (10) > aleatorios (0)
            CASE 
                -- Match con preferencias del cliente autenticado (mayor peso: 20)
                WHEN @nroCliente IS NOT NULL AND EXISTS (
                    SELECT 1 FROM preferencias_clientes pc
                    JOIN categorias_preferencias cp ON pc.cod_categoria = cp.cod_categoria
                    JOIN preferencias_restaurantes pr ON pr.nro_restaurante = r.nro_restaurante
                        AND pr.nro_sucursal IS NULL
                        AND pr.cod_categoria = pc.cod_categoria
                        AND pr.nro_valor_dominio = pc.nro_valor_dominio
                    WHERE pc.nro_cliente = @nroCliente
                        AND cp.nom_categoria IN ('Tipo de comida', 'Ambiente', 'Rango de precio')
                ) THEN 20
                -- Restaurantes con promociones vigentes (peso: 10)
                WHEN EXISTS (
                    SELECT 1 FROM contenidos_restaurantes cr
                    WHERE cr.nro_restaurante = r.nro_restaurante
                        AND cr.contenido_promocional IS NOT NULL
                        AND cr.fecha_ini_vigencia IS NOT NULL
                        AND cr.fecha_fin_vigencia IS NOT NULL
                        AND CAST(GETDATE() AS DATE) BETWEEN cr.fecha_ini_vigencia AND cr.fecha_fin_vigencia
                ) THEN 10
                ELSE 0
            END AS relevancia,
            -- Para ordenar aleatoriamente cuando no hay preferencias
            NEWID() AS orden_aleatorio
        FROM restaurantes r
        WHERE 
            -- Excluir restaurantes que ya están en resultados exactos
            (@excluirRestaurantes IS NULL OR @excluirRestaurantes = '' 
             OR r.nro_restaurante NOT IN (
                 SELECT value FROM STRING_SPLIT(@excluirRestaurantes, ',')
                 WHERE LTRIM(RTRIM(value)) <> ''
             ))
    )
    SELECT TOP (@limite)
        CAST(ROW_NUMBER() OVER (ORDER BY MAX(s.relevancia) DESC, s.orden_aleatorio) AS BIGINT) AS id,
        s.nro_restaurante AS nro_restaurante,
        s.razon_social AS nombre,
        LTRIM(RTRIM(
            ISNULL((SELECT TOP 1 sr2.calle FROM sucursales_restaurantes sr2 WHERE sr2.nro_restaurante = s.nro_restaurante), '') +
            CASE WHEN (SELECT TOP 1 sr2.nro_calle FROM sucursales_restaurantes sr2 WHERE sr2.nro_restaurante = s.nro_restaurante) IS NOT NULL 
                 THEN ' ' + CAST((SELECT TOP 1 sr2.nro_calle FROM sucursales_restaurantes sr2 WHERE sr2.nro_restaurante = s.nro_restaurante) AS VARCHAR(10)) 
                 ELSE '' END +
            CASE WHEN (SELECT TOP 1 sr2.barrio FROM sucursales_restaurantes sr2 WHERE sr2.nro_restaurante = s.nro_restaurante) IS NOT NULL 
                 THEN ', ' + (SELECT TOP 1 sr2.barrio FROM sucursales_restaurantes sr2 WHERE sr2.nro_restaurante = s.nro_restaurante) 
                 ELSE '' END
        )) AS direccion,
        (SELECT TOP 1 sr2.telefonos FROM sucursales_restaurantes sr2 WHERE sr2.nro_restaurante = s.nro_restaurante) AS telefono,
        (SELECT TOP 1 valor FROM configuracion_restaurantes cr 
         JOIN atributos a ON cr.cod_atributo = a.cod_atributo 
         WHERE cr.nro_restaurante = s.nro_restaurante 
           AND a.nom_atributo = 'email') AS email,
        ISNULL((SELECT MAX(sr2.total_comensales) FROM sucursales_restaurantes sr2 WHERE sr2.nro_restaurante = s.nro_restaurante), 0) AS capacidad,
        ISNULL((SELECT MIN(ts2.hora_desde) FROM turnos_sucursales_restaurantes ts2 WHERE ts2.nro_restaurante = s.nro_restaurante), CAST('08:00:00' AS TIME(0))) AS horario_apertura,
        ISNULL((SELECT MAX(ts2.hora_hasta) FROM turnos_sucursales_restaurantes ts2 WHERE ts2.nro_restaurante = s.nro_restaurante), CAST('23:00:00' AS TIME(0))) AS horario_cierre,
        (SELECT TOP 1 dcp.nom_valor_dominio 
         FROM preferencias_restaurantes pr
         JOIN categorias_preferencias cp ON pr.cod_categoria = cp.cod_categoria
         JOIN dominio_categorias_preferencias dcp ON pr.cod_categoria = dcp.cod_categoria 
           AND pr.nro_valor_dominio = dcp.nro_valor_dominio
         WHERE pr.nro_restaurante = s.nro_restaurante 
           AND cp.nom_categoria = 'Tipo de comida'
           AND pr.nro_sucursal IS NULL
         ORDER BY pr.nro_preferencia) AS categoria,
        CAST(4.0 AS FLOAT) AS calificacion,
        CAST(1 AS BIT) AS activo,
        CAST(NULL AS VARCHAR(255)) AS imagen_url
    FROM sugerencias s
    GROUP BY s.nro_restaurante, s.razon_social, s.orden_aleatorio
    ORDER BY MAX(s.relevancia) DESC, s.orden_aleatorio;
END;
GO

-- =============================
-- Promociones (mínimo para listar)
-- =============================
CREATE OR ALTER PROCEDURE sp_ObtenerTodasLasPromociones
    @nro_idioma INT = 0  -- Default: es-AR
AS
BEGIN
    SET NOCOUNT ON;
    SELECT 
        cr.nro_restaurante,
        cr.nro_idioma,
        cr.nro_contenido,
        LEFT(ISNULL(cr.contenido_promocional, cr.contenido_a_publicar), 100) AS titulo,
        ISNULL(cr.contenido_promocional, cr.contenido_a_publicar) AS descripcion,
        CAST(NULL AS DECIMAL(10,2)) AS descuento_porcentaje,
        CAST(NULL AS DECIMAL(10,2)) AS descuento_fijo,
        CAST(cr.fecha_ini_vigencia AS DATETIME2) AS fecha_inicio,
        CAST(cr.fecha_fin_vigencia AS DATETIME2) AS fecha_fin,
        CASE 
            WHEN cr.fecha_ini_vigencia IS NOT NULL AND cr.fecha_fin_vigencia IS NOT NULL 
                 AND CAST(GETDATE() AS DATE) BETWEEN cr.fecha_ini_vigencia AND cr.fecha_fin_vigencia 
            THEN 'ACTIVA' ELSE 'INACTIVA' END AS estado,
        -- Devolver directamente la URL de la imagen almacenada
        cr.imagen_promocional AS imagen_url,
        CAST(NULL AS INT) AS min_personas,
        CAST(NULL AS INT) AS max_personas,
        cr.cod_contenido_restaurante AS codigo_promocion,
        CAST(0 AS BIT) AS requiere_codigo
    FROM contenidos_restaurantes cr
    WHERE cr.nro_idioma = @nro_idioma  -- FILTRAR POR IDIOMA
      AND cr.fecha_fin_vigencia IS NOT NULL
      AND CAST(GETDATE() AS DATE) <= cr.fecha_fin_vigencia;
END;
GO

-- =====================================================
-- STORED PROCEDURE: sp_RegistrarClickPromocion
-- Registra un click en una promoción/contenido
-- =====================================================
CREATE OR ALTER PROCEDURE sp_RegistrarClickPromocion
    @nro_restaurante VARCHAR(36),
    @nro_idioma INT,
    @nro_contenido VARCHAR(36),
    @nro_cliente VARCHAR(36) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @nro_click VARCHAR(36) = NEWID();
    DECLARE @costo_click DECIMAL(12,2);
    
    -- Obtener el costo del click desde la tabla contenidos_restaurantes
    SELECT @costo_click = costo_click
    FROM contenidos_restaurantes
    WHERE nro_restaurante = @nro_restaurante
      AND nro_idioma = @nro_idioma
      AND nro_contenido = @nro_contenido;
    
    -- Insertar el registro del click
    INSERT INTO clicks_contenidos_restaurantes (
        nro_restaurante,
        nro_idioma,
        nro_contenido,
        nro_click,
        fecha_hora_registro,
        nro_cliente,
        costo_click,
        notificado
    )
    VALUES (
        @nro_restaurante,
        @nro_idioma,
        @nro_contenido,
        @nro_click,
        SYSDATETIME(),
        @nro_cliente,
        @costo_click,
        0
    );
    
    -- Retornar el click registrado
    SELECT 
        nro_click,
        nro_restaurante,
        nro_idioma,
        nro_contenido,
        fecha_hora_registro,
        nro_cliente,
        costo_click,
        notificado
    FROM clicks_contenidos_restaurantes
    WHERE nro_click = @nro_click;
END;
GO

-- =====================================================
-- STORED PROCEDURE: sp_GuardarContenidoGenerado
-- Guarda contenido publicitario generado por IA
-- Vigencia: 1 mes desde la fecha actual
-- =====================================================
CREATE OR ALTER PROCEDURE sp_GuardarContenidoGenerado
    @nro_restaurante VARCHAR(36),
    @nro_sucursal VARCHAR(36) = NULL,
    @nro_idioma INT,
    @contenido_generado NVARCHAR(MAX),
    @cod_contenido_restaurante VARCHAR(40) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @nro_contenido VARCHAR(36) = NEWID();
    DECLARE @fecha_ini DATE = CAST(GETDATE() AS DATE);
    DECLARE @fecha_fin DATE = DATEADD(MONTH, 1, @fecha_ini);
    DECLARE @nro_sucursal_validado VARCHAR(36) = NULL;
    DECLARE @cod_contenido_final VARCHAR(40);
    DECLARE @costo_click_final DECIMAL(12,2) = NULL;
    
    -- Obtener el costo de click activo desde la tabla costos (tipo_costo = 'CLICK')
    SELECT TOP 1 @costo_click_final = monto
    FROM costos
    WHERE tipo_costo = 'CLICK'
      AND fecha_ini_vigencia <= CAST(GETDATE() AS DATE)
      AND (fecha_fin_vigencia IS NULL OR fecha_fin_vigencia >= CAST(GETDATE() AS DATE))
    ORDER BY fecha_ini_vigencia DESC;
    
    -- Validar y normalizar nro_sucursal
    -- Si es NULL, cadena vacía o no existe en la base de datos, establecer a NULL
    IF @nro_sucursal IS NOT NULL AND LTRIM(RTRIM(@nro_sucursal)) != ''
    BEGIN
        -- Verificar que la sucursal existe para este restaurante
        IF EXISTS (
            SELECT 1 
            FROM sucursales_restaurantes 
            WHERE nro_restaurante = @nro_restaurante 
              AND nro_sucursal = @nro_sucursal
        )
        BEGIN
            SET @nro_sucursal_validado = @nro_sucursal;
        END
        ELSE
        BEGIN
            -- Si la sucursal no existe, establecer a NULL para evitar error de foreign key
            SET @nro_sucursal_validado = NULL;
        END
    END
    
    -- Determinar el cod_contenido_restaurante
    -- Si se proporciona, usarlo; si no, generar uno con prefijo AI_
    IF @cod_contenido_restaurante IS NOT NULL AND LTRIM(RTRIM(@cod_contenido_restaurante)) != ''
    BEGIN
        SET @cod_contenido_final = @cod_contenido_restaurante;
    END
    ELSE
    BEGIN
        SET @cod_contenido_final = 'AI_' + CONVERT(VARCHAR(36), NEWID());
    END
    
    -- Insertar el contenido generado
    INSERT INTO contenidos_restaurantes (
        nro_restaurante,
        nro_idioma,
        nro_contenido,
        nro_sucursal,
        contenido_promocional,
        imagen_promocional,
        contenido_a_publicar,
        fecha_ini_vigencia,
        fecha_fin_vigencia,
        costo_click,
        cod_contenido_restaurante
    )
    VALUES (
        @nro_restaurante,
        @nro_idioma,
        @nro_contenido,
        @nro_sucursal_validado,
        NULL, -- contenido_promocional (null por ahora)
        NULL, -- imagen_promocional (null por ahora, será URL de internet)
        @contenido_generado,
        @fecha_ini,
        @fecha_fin,
        @costo_click_final,
        @cod_contenido_final
    );
    
    -- Retornar el contenido guardado
    SELECT 
        nro_restaurante,
        nro_sucursal,
        nro_idioma,
        nro_contenido,
        contenido_a_publicar,
        fecha_ini_vigencia,
        fecha_fin_vigencia,
        costo_click
    FROM contenidos_restaurantes
    WHERE nro_contenido = @nro_contenido;
END;
GO

-- =====================================================
-- STORED PROCEDURE: sp_ActualizarCodContenidoRestaurante
-- Actualiza el cod_contenido_restaurante después de registrar en SOAP
-- =====================================================
CREATE OR ALTER PROCEDURE sp_ActualizarCodContenidoRestaurante
    @nro_restaurante VARCHAR(36),
    @nro_idioma INT,
    @nro_contenido VARCHAR(36),
    @cod_contenido_restaurante VARCHAR(40)
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Actualizar cod_contenido_restaurante con el nro_contenido del sistema SOAP
    -- Este valor viene del sistema das-restaurante-soap y se usa para identificar
    -- clicks que deben ser notificados
    UPDATE contenidos_restaurantes
    SET cod_contenido_restaurante = @cod_contenido_restaurante
    WHERE nro_restaurante = @nro_restaurante
      AND nro_idioma = @nro_idioma
      AND nro_contenido = @nro_contenido;
    
    -- Verificar si se actualizó correctamente
    IF @@ROWCOUNT = 0
    BEGIN
        -- Si no se encontró el registro, puede ser un problema de tipos o valores
        RAISERROR('No se encontró el contenido para actualizar. Verificar nro_restaurante, nro_idioma y nro_contenido.', 16, 1);
    END
    
    -- Retornar el registro actualizado
    SELECT 
        nro_restaurante,
        nro_idioma,
        nro_contenido,
        cod_contenido_restaurante
    FROM contenidos_restaurantes
    WHERE nro_restaurante = @nro_restaurante
      AND nro_idioma = @nro_idioma
      AND nro_contenido = @nro_contenido;
END;
GO

CREATE OR ALTER PROCEDURE sp_ObtenerClicksNoNotificados
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        c.nro_restaurante,
        c.nro_idioma,
        c.nro_contenido,
        c.nro_click,
        c.fecha_hora_registro,
        c.nro_cliente,
        c.costo_click,
        cr.cod_contenido_restaurante
    FROM clicks_contenidos_restaurantes c
    INNER JOIN contenidos_restaurantes cr
        ON c.nro_restaurante = cr.nro_restaurante
        AND c.nro_idioma = cr.nro_idioma
        AND c.nro_contenido = cr.nro_contenido
    WHERE c.notificado = 0
        AND cr.cod_contenido_restaurante IS NOT NULL
        AND cr.cod_contenido_restaurante NOT LIKE 'AI_%'
    ORDER BY c.fecha_hora_registro;
END;
GO

CREATE OR ALTER PROCEDURE sp_MarcarClickComoNotificado
    @nro_restaurante VARCHAR(36),
    @nro_idioma INT,
    @nro_contenido VARCHAR(36),
    @nro_click VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE clicks_contenidos_restaurantes
    SET notificado = 1
    WHERE nro_restaurante = @nro_restaurante
        AND nro_idioma = @nro_idioma
        AND nro_contenido = @nro_contenido
        AND nro_click = @nro_click;
    
    IF @@ROWCOUNT = 0
    BEGIN
        RAISERROR('No se encontró el click para marcar como notificado. Verificar parámetros: nro_restaurante=%s, nro_idioma=%d, nro_contenido=%s, nro_click=%s', 
                  16, 1, @nro_restaurante, @nro_idioma, @nro_contenido, @nro_click);
    END
    ELSE
    BEGIN
        SELECT @@ROWCOUNT AS filas_actualizadas;
    END
END;
GO

-- =====================================================
-- STORED PROCEDURES PARA LOCALIDADES
-- =====================================================

-- Obtener todas las localidades con su provincia
CREATE OR ALTER PROCEDURE sp_ObtenerTodasLasLocalidades
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        l.nro_localidad AS nroLocalidad,
        l.nom_localidad AS nombre,
        p.nom_provincia AS provincia
    FROM localidades l
    INNER JOIN provincias p ON l.cod_provincia = p.cod_provincia
    ORDER BY p.nom_provincia, l.nom_localidad;
END;
GO

-- =====================================================
-- STORED PROCEDURES PARA PREFERENCIAS GASTRONÓMICAS
-- =====================================================

-- Obtener todas las categorías de preferencias
CREATE OR ALTER PROCEDURE sp_ObtenerCategoriasPreferencias
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        cod_categoria AS codCategoria,
        nom_categoria AS nombre
    FROM categorias_preferencias
    ORDER BY nom_categoria;
END;
GO

-- Obtener todos los dominios de una categoría específica
CREATE OR ALTER PROCEDURE sp_ObtenerDominiosPorCategoria
    @cod_categoria VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        cod_categoria AS codCategoria,
        nro_valor_dominio AS nroValorDominio,
        nom_valor_dominio AS nombre
    FROM dominio_categorias_preferencias
    WHERE cod_categoria = @cod_categoria
    ORDER BY nro_valor_dominio;
END;
GO

-- Obtener todas las categorías con sus dominios (más eficiente para el frontend)
CREATE OR ALTER PROCEDURE sp_ObtenerTodasLasCategoriasConDominios
    @nro_idioma INT = 0  -- Default: es-AR
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Primero obtener las categorías traducidas
    SELECT 
        cp.cod_categoria AS codCategoria,
        ISNULL(icp.categoria, cp.nom_categoria) AS nombre
    FROM categorias_preferencias cp
    LEFT JOIN idiomas_categorias_preferencias icp 
        ON cp.cod_categoria = icp.cod_categoria 
        AND icp.nro_idioma = @nro_idioma
    ORDER BY ISNULL(icp.categoria, cp.nom_categoria);
    
    -- Luego obtener todos los dominios traducidos agrupados por categoría
    SELECT 
        dcp.cod_categoria AS codCategoria,
        dcp.nro_valor_dominio AS nroValorDominio,
        ISNULL(idcp.valor_dominio, dcp.nom_valor_dominio) AS nombre
    FROM dominio_categorias_preferencias dcp
    LEFT JOIN idiomas_dominio_cat_preferencias idcp
        ON dcp.cod_categoria = idcp.cod_categoria
        AND dcp.nro_valor_dominio = idcp.nro_valor_dominio
        AND idcp.nro_idioma = @nro_idioma
    ORDER BY dcp.cod_categoria, dcp.nro_valor_dominio;
END;
GO

-- Guardar preferencias de un cliente (reemplaza las existentes)
CREATE OR ALTER PROCEDURE sp_GuardarPreferenciasCliente
    @nro_cliente VARCHAR(36),
    @preferencias NVARCHAR(MAX)  -- JSON con array de {codCategoria, nroValorDominio, observaciones}
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRANSACTION;
    
    BEGIN TRY
        -- Eliminar preferencias existentes del cliente
        DELETE FROM preferencias_clientes
        WHERE nro_cliente = @nro_cliente;
        
        -- Parsear JSON y insertar nuevas preferencias
        -- El JSON debe tener formato: [{"codCategoria":"...","nroValorDominio":1,"observaciones":"..."}, ...]
        -- Usamos OPENJSON para parsear (SQL Server 2016+)
        INSERT INTO preferencias_clientes (nro_cliente, cod_categoria, nro_valor_dominio, observaciones)
        SELECT 
            @nro_cliente,
            codCategoria,
            nroValorDominio,
            observaciones
        FROM OPENJSON(@preferencias)
        WITH (
            codCategoria VARCHAR(36) '$.codCategoria',
            nroValorDominio INT '$.nroValorDominio',
            observaciones NVARCHAR(400) '$.observaciones'
        );
        
        COMMIT TRANSACTION;
        
        SELECT @@ROWCOUNT AS preferencias_guardadas;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

-- Obtener preferencias de un cliente
CREATE OR ALTER PROCEDURE sp_ObtenerPreferenciasCliente
    @nro_cliente VARCHAR(36),
    @nro_idioma INT = 0  -- Default: es-AR
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        pc.cod_categoria AS codCategoria,
        ISNULL(icp.categoria, cp.nom_categoria) AS nombreCategoria,
        pc.nro_valor_dominio AS nroValorDominio,
        ISNULL(idcp.valor_dominio, dcp.nom_valor_dominio) AS nombreDominio,
        pc.observaciones
    FROM preferencias_clientes pc
    INNER JOIN categorias_preferencias cp ON pc.cod_categoria = cp.cod_categoria
    LEFT JOIN idiomas_categorias_preferencias icp 
        ON cp.cod_categoria = icp.cod_categoria 
        AND icp.nro_idioma = @nro_idioma
    INNER JOIN dominio_categorias_preferencias dcp 
        ON pc.cod_categoria = dcp.cod_categoria 
        AND pc.nro_valor_dominio = dcp.nro_valor_dominio
    LEFT JOIN idiomas_dominio_cat_preferencias idcp
        ON dcp.cod_categoria = idcp.cod_categoria
        AND dcp.nro_valor_dominio = idcp.nro_valor_dominio
        AND idcp.nro_idioma = @nro_idioma
    WHERE pc.nro_cliente = @nro_cliente
    ORDER BY ISNULL(icp.categoria, cp.nom_categoria), ISNULL(idcp.valor_dominio, dcp.nom_valor_dominio);
END;
GO

-- =====================================================
-- STORED PROCEDURES PARA RESERVAS - NUEVOS
-- =====================================================

CREATE OR ALTER PROCEDURE sp_ObtenerCostoReserva
    @fecha_reserva DATE
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT TOP 1 monto
    FROM costos
    WHERE tipo_costo = 'RESERVA'
        AND fecha_ini_vigencia <= @fecha_reserva
        AND (fecha_fin_vigencia IS NULL OR fecha_fin_vigencia >= @fecha_reserva)
    ORDER BY fecha_ini_vigencia DESC;
END;
GO

CREATE OR ALTER PROCEDURE sp_GenerarCodigoReserva
    @codigo_reserva VARCHAR(20) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @anio VARCHAR(4) = CAST(YEAR(GETDATE()) AS VARCHAR(4));
    DECLARE @secuencial INT;
    DECLARE @prefijo VARCHAR(3) = 'RES';
    
    SELECT @secuencial = ISNULL(MAX(CAST(SUBSTRING(nro_reserva, 9, 6) AS INT)), 0) + 1
    FROM reservas_restaurantes
    WHERE nro_reserva LIKE @prefijo + '-' + @anio + '-%'
        AND LEN(nro_reserva) >= 15;
    
    DECLARE @secuencialStr VARCHAR(6) = RIGHT('000000' + CAST(@secuencial AS VARCHAR(6)), 6);
    DECLARE @randomStr VARCHAR(6) = UPPER(SUBSTRING(REPLACE(NEWID(), '-', ''), 1, 6));
    
    SET @codigo_reserva = @prefijo + '-' + @anio + '-' + @secuencialStr + '-' + @randomStr;
END;
GO

CREATE OR ALTER PROCEDURE sp_RegistrarReservaRistorino
    @nro_reserva VARCHAR(36) OUTPUT,
    @nro_restaurante VARCHAR(36),
    @nro_sucursal VARCHAR(36),
    @cod_zona VARCHAR(36),
    @fecha_reserva DATE,
    @hora_desde TIME(0),
    @nro_cliente VARCHAR(36),
    @cant_adultos SMALLINT,
    @cant_menores SMALLINT,
    @cod_estado VARCHAR(36),
    @costo_reserva DECIMAL(12,2),
    @notas NVARCHAR(400) = NULL,
    @cod_reserva_sucursal VARCHAR(36) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @codigo_legible VARCHAR(20);
    EXEC sp_GenerarCodigoReserva @codigo_legible OUTPUT;
    
    SET @nro_reserva = @codigo_legible;
    
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
        cod_estado,
        costo_reserva,
        notas,
        cod_reserva_sucursal
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
        0,
        SYSDATETIME(),
        @cod_estado,
        @costo_reserva,
        @notas,
        @cod_reserva_sucursal
    );
END;
GO

CREATE OR ALTER PROCEDURE sp_ActualizarCodReservaSucursal
    @nro_reserva VARCHAR(36),
    @cod_reserva_sucursal VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE reservas_restaurantes
    SET cod_reserva_sucursal = @cod_reserva_sucursal
    WHERE nro_reserva = @nro_reserva;
    
    IF @@ROWCOUNT = 0
    BEGIN
        RAISERROR('Reserva no encontrada: %s', 16, 1, @nro_reserva);
    END
END;
GO

CREATE OR ALTER PROCEDURE sp_ObtenerCodigoEstado
    @nom_estado NVARCHAR(80)
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT cod_estado AS codEstado
    FROM estados_reservas
    WHERE nom_estado = @nom_estado;
END;
GO

CREATE OR ALTER PROCEDURE sp_ObtenerResenas_sucursales
    @nro_restaurante VARCHAR(36),
    @nro_sucursal VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        CONCAT(c.nombre,' ',c.apellido) AS nombreCompleto,
        res.calificacion,
        res.comentario,
        res.fecha_hora_registro
    FROM dbo.resenas_sucursales_restaurantes res
    JOIN dbo.clientes c on res.nro_cliente = c.nro_cliente
    WHERE nro_restaurante = @nro_restaurante
      AND nro_sucursal = @nro_sucursal;
END;
GO

CREATE OR ALTER PROCEDURE sp_InsertarResena_sucursal
    @nro_restaurante VARCHAR(36),
    @nro_sucursal VARCHAR(36),
    @nro_cliente VARCHAR(36),
    @calificacion INT,
    @comentario NVARCHAR(1000)
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO resenas_sucursales_restaurantes (
        nro_restaurante,
        nro_sucursal,
        nro_cliente,
        calificacion,
        comentario,
        fecha_hora_registro
    )
    VALUES (
        @nro_restaurante,
        @nro_sucursal,
        @nro_cliente,
        @calificacion,
        @comentario,
        SYSDATETIME()
    );
END;
GO

create or alter procedure sp_CancelarReservaRistorino
    @nro_reserva VARCHAR(36)
    as
begin
    set nocount on;

    DECLARE @cod_estado_cancelada VARCHAR(36);
    SELECT @cod_estado_cancelada = cod_estado FROM estados_reservas WHERE nom_estado = 'Cancelada';

    update reservas_restaurantes
    set cancelada = 1, cod_estado = @cod_estado_cancelada, fecha_hora_cancelacion = GETDATE()
    where nro_reserva = @nro_reserva;

    if @@rowcount = 0
    begin
        raiserror('Reserva no encontrada: %s', 16, 1, @nro_reserva);
    end
end;
GO

CREATE OR ALTER PROCEDURE sp_ObtenerCancelacionReserva
    @nro_reserva VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        nro_restaurante,
        cod_reserva_sucursal
    FROM reservas_restaurantes
    WHERE nro_reserva = @nro_reserva;
END;

PRINT 'Stored procedures creados/actualizados exitosamente!';
