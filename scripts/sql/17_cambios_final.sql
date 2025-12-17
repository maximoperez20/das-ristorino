ALTER TABLE contenidos_restaurantes
ADD proposito_corto VARCHAR(20) NULL;


CREATE OR ALTER PROCEDURE sp_GuardarContenidoGenerado
    @nro_restaurante VARCHAR(36),
    @nro_sucursal VARCHAR(36) = NULL,
    @nro_idioma INT,
    @contenido_generado NVARCHAR(MAX),
    @cod_contenido_restaurante VARCHAR(40) = NULL,
    @proposito_corto VARCHAR(20) = NULL
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
        cod_contenido_restaurante,
        proposito_corto
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
        @cod_contenido_final,
        @proposito_corto
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
        costo_click,
        proposito_corto
    FROM contenidos_restaurantes
    WHERE nro_contenido = @nro_contenido;
END;
GO

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
        CAST(0 AS BIT) AS requiere_codigo,
        cr.proposito_corto
    FROM contenidos_restaurantes cr
    WHERE cr.nro_idioma = @nro_idioma  -- FILTRAR POR IDIOMA
      AND cr.fecha_fin_vigencia IS NOT NULL
      AND CAST(GETDATE() AS DATE) <= cr.fecha_fin_vigencia;
END;
GO