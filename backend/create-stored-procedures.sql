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
        CAST(NULL AS VARCHAR(500)) AS descripcion,
        CAST(NULL AS VARCHAR(100)) AS categoria,
        CAST(4.0 AS FLOAT) AS calificacion,
        CAST(1 AS BIT) AS activo,
        CAST(NULL AS VARCHAR(255)) AS imagen_url
    FROM datos
    ORDER BY nombre;
END;
GO

CREATE OR ALTER PROCEDURE sp_ObtenerRestaurantePorId
    @nroRestaurante VARCHAR(36)
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
        -- Descripción desde contenidos_restaurantes (contenido general, no de sucursal específica)
        (SELECT TOP 1 contenido_a_publicar FROM contenidos_restaurantes 
         WHERE nro_restaurante = @nroRestaurante 
           AND nro_sucursal IS NULL 
           AND contenido_a_publicar IS NOT NULL 
         ORDER BY fecha_ini_vigencia DESC) AS descripcion,
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
    @nroRestaurante VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        dcp.nom_valor_dominio AS tipo_cocina
    FROM preferencias_restaurantes pr
    JOIN categorias_preferencias cp ON pr.cod_categoria = cp.cod_categoria
    JOIN dominio_categorias_preferencias dcp ON pr.cod_categoria = dcp.cod_categoria 
      AND pr.nro_valor_dominio = dcp.nro_valor_dominio
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
    @nroRestaurante VARCHAR(36)
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
        CAST(NULL AS NVARCHAR(255)) AS imagen_url,
        CAST(NULL AS INT) AS min_personas,
        CAST(NULL AS INT) AS max_personas,
        cr.cod_contenido_restaurante AS codigo_promocion,
        CAST(0 AS BIT) AS requiere_codigo
    FROM contenidos_restaurantes cr
    WHERE cr.nro_restaurante = @nroRestaurante
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
    @palabrasClave NVARCHAR(MAX) = NULL       -- Palabras clave para búsqueda en nombre/descripción
AS
BEGIN
    SET NOCOUNT ON;

    ;WITH restaurantes_filtrados AS (
        SELECT DISTINCT
            r.nro_restaurante,
            r.razon_social
        FROM restaurantes r
        LEFT JOIN sucursales_restaurantes s ON s.nro_restaurante = r.nro_restaurante
        LEFT JOIN localidades l ON s.nro_localidad = l.nro_localidad
        LEFT JOIN preferencias_restaurantes pr ON pr.nro_restaurante = r.nro_restaurante AND pr.nro_sucursal IS NULL
        LEFT JOIN categorias_preferencias cp ON pr.cod_categoria = cp.cod_categoria
        LEFT JOIN dominio_categorias_preferencias dcp ON pr.cod_categoria = dcp.cod_categoria 
            AND pr.nro_valor_dominio = dcp.nro_valor_dominio
        WHERE 
            -- Filtro por tipos de comida
            (@tiposComida IS NULL OR @tiposComida = '' OR EXISTS (
                SELECT 1 FROM dominio_categorias_preferencias dcp2
                JOIN categorias_preferencias cp2 ON dcp2.cod_categoria = cp2.cod_categoria
                WHERE cp2.nom_categoria = 'Tipo de comida'
                    AND dcp2.nom_valor_dominio IN (
                        SELECT value FROM STRING_SPLIT(@tiposComida, ',')
                    )
                    AND pr.cod_categoria = dcp2.cod_categoria
                    AND pr.nro_valor_dominio = dcp2.nro_valor_dominio
            ))
            -- Filtro por barrios
            AND (@barrios IS NULL OR @barrios = '' OR s.barrio IN (
                SELECT value FROM STRING_SPLIT(@barrios, ',')
            ))
            -- Filtro por localidades
            AND (@localidades IS NULL OR @localidades = '' OR l.nom_localidad IN (
                SELECT value FROM STRING_SPLIT(@localidades, ',')
            ))
            -- Filtro por ambientes
            AND (@ambientes IS NULL OR @ambientes = '' OR EXISTS (
                SELECT 1 FROM dominio_categorias_preferencias dcp3
                JOIN categorias_preferencias cp3 ON dcp3.cod_categoria = cp3.cod_categoria
                WHERE cp3.nom_categoria = 'Ambiente'
                    AND dcp3.nom_valor_dominio IN (
                        SELECT value FROM STRING_SPLIT(@ambientes, ',')
                    )
                    AND pr.cod_categoria = dcp3.cod_categoria
                    AND pr.nro_valor_dominio = dcp3.nro_valor_dominio
            ))
            -- Filtro por rangos de precio
            AND (@rangosPrecio IS NULL OR @rangosPrecio = '' OR EXISTS (
                SELECT 1 FROM dominio_categorias_preferencias dcp4
                JOIN categorias_preferencias cp4 ON dcp4.cod_categoria = cp4.cod_categoria
                WHERE cp4.nom_categoria = 'Rango de precio'
                    AND dcp4.nom_valor_dominio IN (
                        SELECT value FROM STRING_SPLIT(@rangosPrecio, ',')
                    )
                    AND pr.cod_categoria = dcp4.cod_categoria
                    AND pr.nro_valor_dominio = dcp4.nro_valor_dominio
            ))
            -- Filtro por palabras clave en nombre o descripción (búsqueda por cualquier palabra)
            AND (@palabrasClave IS NULL OR @palabrasClave = '' OR 
                EXISTS (
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
                )
            )
    )
    SELECT 
        CAST(ROW_NUMBER() OVER (ORDER BY razon_social) AS BIGINT) AS id,
        rf.razon_social AS nombre,
        LTRIM(RTRIM(
            ISNULL(MIN(s.calle), '') +
            CASE WHEN MIN(s.nro_calle) IS NOT NULL THEN ' ' + CAST(MIN(s.nro_calle) AS VARCHAR(10)) ELSE '' END +
            CASE WHEN MIN(s.barrio) IS NOT NULL THEN ', ' + MIN(s.barrio) ELSE '' END
        )) AS direccion,
        MIN(s.telefonos) AS telefono,
        (SELECT TOP 1 valor FROM configuracion_restaurantes cr 
         JOIN atributos a ON cr.cod_atributo = a.cod_atributo 
         WHERE cr.nro_restaurante = rf.nro_restaurante 
           AND a.nom_atributo = 'email') AS email,
        ISNULL(MAX(s.total_comensales), 0) AS capacidad,
        ISNULL(MIN(t.hora_desde), CAST('08:00:00' AS TIME(0))) AS horario_apertura,
        ISNULL(MAX(t.hora_hasta), CAST('23:00:00' AS TIME(0))) AS horario_cierre,
        (SELECT TOP 1 contenido_a_publicar FROM contenidos_restaurantes 
         WHERE nro_restaurante = rf.nro_restaurante 
           AND nro_sucursal IS NULL 
           AND contenido_a_publicar IS NOT NULL 
         ORDER BY fecha_ini_vigencia DESC) AS descripcion,
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
    LEFT JOIN sucursales_restaurantes s ON s.nro_restaurante = rf.nro_restaurante
    LEFT JOIN turnos_sucursales_restaurantes t ON t.nro_restaurante = rf.nro_restaurante
    GROUP BY rf.nro_restaurante, rf.razon_social
    ORDER BY rf.razon_social;
END;
GO

-- =============================
-- Promociones (mínimo para listar)
-- =============================
CREATE OR ALTER PROCEDURE sp_ObtenerTodasLasPromociones
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
        CAST(NULL AS NVARCHAR(255)) AS imagen_url,
        CAST(NULL AS INT) AS min_personas,
        CAST(NULL AS INT) AS max_personas,
        cr.cod_contenido_restaurante AS codigo_promocion,
        CAST(0 AS BIT) AS requiere_codigo
    FROM contenidos_restaurantes cr;
END;
GO

-- =====================================================
-- STORED PROCEDURE: sp_RegistrarClickPromocion
-- Registra un click en una promoción/contenido
-- =====================================================
CREATE OR ALTER PROCEDURE sp_RegistrarClickPromocion
    @nro_restaurante VARCHAR(36),
    @nro_idioma VARCHAR(36),
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
    @nro_idioma VARCHAR(36),
    @contenido_generado VARCHAR(MAX)
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @nro_contenido VARCHAR(36) = NEWID();
    DECLARE @fecha_ini DATE = CAST(GETDATE() AS DATE);
    DECLARE @fecha_fin DATE = DATEADD(MONTH, 1, @fecha_ini);
    DECLARE @costo_click DECIMAL(12,2) = 0.00; -- Costo por defecto
    
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
        @nro_sucursal,
        NULL, -- contenido_promocional (null por ahora)
        NULL, -- imagen_promocional (null por ahora)
        @contenido_generado,
        @fecha_ini,
        @fecha_fin,
        @costo_click,
        'AI_' + CONVERT(VARCHAR(36), NEWID()) -- Código único generado
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
    @nro_idioma VARCHAR(36),
    @nro_contenido VARCHAR(36),
    @cod_contenido_restaurante VARCHAR(40)
AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE contenidos_restaurantes
    SET cod_contenido_restaurante = @cod_contenido_restaurante
    WHERE nro_restaurante = @nro_restaurante
      AND nro_idioma = @nro_idioma
      AND nro_contenido = @nro_contenido;
    
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

-- =====================================================
-- STORED PROCEDURE: sp_ObtenerClicksNoNotificados
-- Obtiene clicks no notificados con cod_contenido_restaurante
-- =====================================================
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

-- =====================================================
-- STORED PROCEDURE: sp_MarcarClickComoNotificado
-- Marca un click como notificado
-- =====================================================
CREATE OR ALTER PROCEDURE sp_MarcarClickComoNotificado
    @nro_restaurante VARCHAR(36),
    @nro_idioma VARCHAR(36),
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
    
    SELECT @@ROWCOUNT AS filas_actualizadas;
END;
GO

PRINT 'Stored procedures creados/actualizados exitosamente!';
