/* =========================================================================================
   INSERT DE TRADUCCIONES PARA TABLAS idiomas_* - das_ristorino
   Este script popula las tablas de traducciones para es-AR (nro_idioma=0) y en-US (nro_idioma=1)
   ========================================================================================= */

SET NOCOUNT ON;
GO

USE das_ristorino;
GO

PRINT '========================================';
PRINT 'Insertando traducciones de idiomas';
PRINT '========================================';

-- Obtener nro_idioma para es-AR y en-US
DECLARE @nro_idioma_es INT, @nro_idioma_en INT;
SELECT @nro_idioma_es = nro_idioma FROM idiomas WHERE cod_idioma = N'es-AR';
SELECT @nro_idioma_en = nro_idioma FROM idiomas WHERE cod_idioma = N'en-US';

IF @nro_idioma_es IS NULL
BEGIN
    RAISERROR('Idioma es-AR no encontrado. Ejecutar primero 03_insert_datos_basicos.sql', 16, 1);
    RETURN;
END

IF @nro_idioma_en IS NULL
BEGIN
    RAISERROR('Idioma en-US no encontrado. Ejecutar primero 03_insert_datos_basicos.sql', 16, 1);
    RETURN;
END

PRINT 'Idiomas encontrados: es-AR (nro=' + CAST(@nro_idioma_es AS VARCHAR) + '), en-US (nro=' + CAST(@nro_idioma_en AS VARCHAR) + ')';

/* =========================================
   1) Traducciones de Categorías de Preferencias
   ========================================= */

DECLARE @cat_tipo VARCHAR(36), @cat_amb VARCHAR(36), @cat_precio VARCHAR(36);
SELECT @cat_tipo = cod_categoria FROM categorias_preferencias WHERE nom_categoria = N'Tipo de comida';
SELECT @cat_amb = cod_categoria FROM categorias_preferencias WHERE nom_categoria = N'Ambiente';
SELECT @cat_precio = cod_categoria FROM categorias_preferencias WHERE nom_categoria = N'Rango de precio';

IF @cat_tipo IS NULL OR @cat_amb IS NULL OR @cat_precio IS NULL
BEGIN
    RAISERROR('Categorías de preferencias no encontradas. Ejecutar primero 05_insert_categorias_preferencias.sql', 16, 1);
    RETURN;
END

-- Categoría: Tipo de comida
IF NOT EXISTS (SELECT 1 FROM idiomas_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nro_idioma = @nro_idioma_es)
    INSERT INTO idiomas_categorias_preferencias (cod_categoria, nro_idioma, categoria) 
    VALUES (@cat_tipo, @nro_idioma_es, N'Tipo de Comida');

IF NOT EXISTS (SELECT 1 FROM idiomas_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nro_idioma = @nro_idioma_en)
    INSERT INTO idiomas_categorias_preferencias (cod_categoria, nro_idioma, categoria) 
    VALUES (@cat_tipo, @nro_idioma_en, N'Food Type');

-- Categoría: Ambiente
IF NOT EXISTS (SELECT 1 FROM idiomas_categorias_preferencias WHERE cod_categoria = @cat_amb AND nro_idioma = @nro_idioma_es)
    INSERT INTO idiomas_categorias_preferencias (cod_categoria, nro_idioma, categoria) 
    VALUES (@cat_amb, @nro_idioma_es, N'Ambiente');

IF NOT EXISTS (SELECT 1 FROM idiomas_categorias_preferencias WHERE cod_categoria = @cat_amb AND nro_idioma = @nro_idioma_en)
    INSERT INTO idiomas_categorias_preferencias (cod_categoria, nro_idioma, categoria) 
    VALUES (@cat_amb, @nro_idioma_en, N'Ambience');

-- Categoría: Rango de precio
IF NOT EXISTS (SELECT 1 FROM idiomas_categorias_preferencias WHERE cod_categoria = @cat_precio AND nro_idioma = @nro_idioma_es)
    INSERT INTO idiomas_categorias_preferencias (cod_categoria, nro_idioma, categoria) 
    VALUES (@cat_precio, @nro_idioma_es, N'Rango Precio');

IF NOT EXISTS (SELECT 1 FROM idiomas_categorias_preferencias WHERE cod_categoria = @cat_precio AND nro_idioma = @nro_idioma_en)
    INSERT INTO idiomas_categorias_preferencias (cod_categoria, nro_idioma, categoria) 
    VALUES (@cat_precio, @nro_idioma_en, N'Price Range');

PRINT 'Traducciones de categorías insertadas';

/* =========================================
   2) Traducciones de Dominios de Preferencias
   ========================================= */

-- Obtener todos los dominios existentes
DECLARE @cod_cat VARCHAR(36), @nro_dom INT, @nom_dom NVARCHAR(120);

-- Tipo de comida - Dominios
DECLARE cur_tipo CURSOR LOCAL FAST_FORWARD FOR
    SELECT cod_categoria, nro_valor_dominio, nom_valor_dominio
    FROM dominio_categorias_preferencias
    WHERE cod_categoria = @cat_tipo
    ORDER BY nro_valor_dominio;

OPEN cur_tipo;
FETCH NEXT FROM cur_tipo INTO @cod_cat, @nro_dom, @nom_dom;

WHILE @@FETCH_STATUS = 0
BEGIN
    -- Traducción es-AR (mantener el nombre original)
    IF NOT EXISTS (SELECT 1 FROM idiomas_dominio_cat_preferencias 
                   WHERE cod_categoria = @cod_cat AND nro_valor_dominio = @nro_dom AND nro_idioma = @nro_idioma_es)
    BEGIN
        INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio)
        VALUES (@cod_cat, @nro_dom, @nro_idioma_es, @nom_dom);
    END
    
    -- Traducción en-US
    DECLARE @traduccion_en NVARCHAR(120);
    SET @traduccion_en = CASE @nom_dom
        WHEN N'Parrilla' THEN N'Grill'
        WHEN N'Pizzería' THEN N'Pizzeria'
        WHEN N'Sushi' THEN N'Sushi'
        WHEN N'Vegano' THEN N'Vegan'
        WHEN N'Italiana' THEN N'Italian'
        WHEN N'Mexicana' THEN N'Mexican'
        WHEN N'Asiática' THEN N'Asian'
        ELSE @nom_dom  -- Si no hay traducción, usar el original
    END;
    
    IF NOT EXISTS (SELECT 1 FROM idiomas_dominio_cat_preferencias 
                   WHERE cod_categoria = @cod_cat AND nro_valor_dominio = @nro_dom AND nro_idioma = @nro_idioma_en)
    BEGIN
        INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio)
        VALUES (@cod_cat, @nro_dom, @nro_idioma_en, @traduccion_en);
    END
    
    FETCH NEXT FROM cur_tipo INTO @cod_cat, @nro_dom, @nom_dom;
END

CLOSE cur_tipo;
DEALLOCATE cur_tipo;

-- Ambiente - Dominios
DECLARE cur_amb CURSOR LOCAL FAST_FORWARD FOR
    SELECT cod_categoria, nro_valor_dominio, nom_valor_dominio
    FROM dominio_categorias_preferencias
    WHERE cod_categoria = @cat_amb
    ORDER BY nro_valor_dominio;

OPEN cur_amb;
FETCH NEXT FROM cur_amb INTO @cod_cat, @nro_dom, @nom_dom;

WHILE @@FETCH_STATUS = 0
BEGIN
    -- Traducción es-AR
    IF NOT EXISTS (SELECT 1 FROM idiomas_dominio_cat_preferencias 
                   WHERE cod_categoria = @cod_cat AND nro_valor_dominio = @nro_dom AND nro_idioma = @nro_idioma_es)
    BEGIN
        INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio)
        VALUES (@cod_cat, @nro_dom, @nro_idioma_es, @nom_dom);
    END
    
    -- Traducción en-US
    SET @traduccion_en = CASE @nom_dom
        WHEN N'Familiar' THEN N'Family'
        WHEN N'Romántico' THEN N'Romantic'
        WHEN N'Gourmet' THEN N'Gourmet'
        WHEN N'Casual' THEN N'Casual'
        WHEN N'Deportivo' THEN N'Sports'
        WHEN N'Elegante' THEN N'Elegant'
        ELSE @nom_dom
    END;
    
    IF NOT EXISTS (SELECT 1 FROM idiomas_dominio_cat_preferencias 
                   WHERE cod_categoria = @cod_cat AND nro_valor_dominio = @nro_dom AND nro_idioma = @nro_idioma_en)
    BEGIN
        INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio)
        VALUES (@cod_cat, @nro_dom, @nro_idioma_en, @traduccion_en);
    END
    
    FETCH NEXT FROM cur_amb INTO @cod_cat, @nro_dom, @nom_dom;
END

CLOSE cur_amb;
DEALLOCATE cur_amb;

-- Rango de precio - Dominios
DECLARE cur_precio CURSOR LOCAL FAST_FORWARD FOR
    SELECT cod_categoria, nro_valor_dominio, nom_valor_dominio
    FROM dominio_categorias_preferencias
    WHERE cod_categoria = @cat_precio
    ORDER BY nro_valor_dominio;

OPEN cur_precio;
FETCH NEXT FROM cur_precio INTO @cod_cat, @nro_dom, @nom_dom;

WHILE @@FETCH_STATUS = 0
BEGIN
    -- Traducción es-AR
    IF NOT EXISTS (SELECT 1 FROM idiomas_dominio_cat_preferencias 
                   WHERE cod_categoria = @cod_cat AND nro_valor_dominio = @nro_dom AND nro_idioma = @nro_idioma_es)
    BEGIN
        INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio)
        VALUES (@cod_cat, @nro_dom, @nro_idioma_es, @nom_dom);
    END
    
    -- Traducción en-US
    SET @traduccion_en = CASE @nom_dom
        WHEN N'Económico' THEN N'Economy'
        WHEN N'Medio' THEN N'Medium'
        WHEN N'Premium' THEN N'Premium'
        ELSE @nom_dom
    END;
    
    IF NOT EXISTS (SELECT 1 FROM idiomas_dominio_cat_preferencias 
                   WHERE cod_categoria = @cod_cat AND nro_valor_dominio = @nro_dom AND nro_idioma = @nro_idioma_en)
    BEGIN
        INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio)
        VALUES (@cod_cat, @nro_dom, @nro_idioma_en, @traduccion_en);
    END
    
    FETCH NEXT FROM cur_precio INTO @cod_cat, @nro_dom, @nom_dom;
END

CLOSE cur_precio;
DEALLOCATE cur_precio;

PRINT 'Traducciones de dominios insertadas';

/* =========================================
   3) Traducciones de Estados de Reservas
   ========================================= */

DECLARE @cod_estado VARCHAR(36);

-- Pendiente
SELECT @cod_estado = cod_estado FROM estados_reservas WHERE nom_estado = N'Pendiente';
IF @cod_estado IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM idiomas_estados_reservas WHERE cod_estado = @cod_estado AND nro_idioma = @nro_idioma_es)
        INSERT INTO idiomas_estados_reservas (cod_estado, nro_idioma, estado) VALUES (@cod_estado, @nro_idioma_es, N'Pendiente');
    
    IF NOT EXISTS (SELECT 1 FROM idiomas_estados_reservas WHERE cod_estado = @cod_estado AND nro_idioma = @nro_idioma_en)
        INSERT INTO idiomas_estados_reservas (cod_estado, nro_idioma, estado) VALUES (@cod_estado, @nro_idioma_en, N'Pending');
END

-- Confirmada
SELECT @cod_estado = cod_estado FROM estados_reservas WHERE nom_estado = N'Confirmada';
IF @cod_estado IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM idiomas_estados_reservas WHERE cod_estado = @cod_estado AND nro_idioma = @nro_idioma_es)
        INSERT INTO idiomas_estados_reservas (cod_estado, nro_idioma, estado) VALUES (@cod_estado, @nro_idioma_es, N'Confirmada');
    
    IF NOT EXISTS (SELECT 1 FROM idiomas_estados_reservas WHERE cod_estado = @cod_estado AND nro_idioma = @nro_idioma_en)
        INSERT INTO idiomas_estados_reservas (cod_estado, nro_idioma, estado) VALUES (@cod_estado, @nro_idioma_en, N'Confirmed');
END

-- En curso
SELECT @cod_estado = cod_estado FROM estados_reservas WHERE nom_estado = N'En curso';
IF @cod_estado IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM idiomas_estados_reservas WHERE cod_estado = @cod_estado AND nro_idioma = @nro_idioma_es)
        INSERT INTO idiomas_estados_reservas (cod_estado, nro_idioma, estado) VALUES (@cod_estado, @nro_idioma_es, N'En curso');
    
    IF NOT EXISTS (SELECT 1 FROM idiomas_estados_reservas WHERE cod_estado = @cod_estado AND nro_idioma = @nro_idioma_en)
        INSERT INTO idiomas_estados_reservas (cod_estado, nro_idioma, estado) VALUES (@cod_estado, @nro_idioma_en, N'In Progress');
END

-- Finalizada
SELECT @cod_estado = cod_estado FROM estados_reservas WHERE nom_estado = N'Finalizada';
IF @cod_estado IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM idiomas_estados_reservas WHERE cod_estado = @cod_estado AND nro_idioma = @nro_idioma_es)
        INSERT INTO idiomas_estados_reservas (cod_estado, nro_idioma, estado) VALUES (@cod_estado, @nro_idioma_es, N'Finalizada');
    
    IF NOT EXISTS (SELECT 1 FROM idiomas_estados_reservas WHERE cod_estado = @cod_estado AND nro_idioma = @nro_idioma_en)
        INSERT INTO idiomas_estados_reservas (cod_estado, nro_idioma, estado) VALUES (@cod_estado, @nro_idioma_en, N'Completed');
END

-- Cancelada
SELECT @cod_estado = cod_estado FROM estados_reservas WHERE nom_estado = N'Cancelada';
IF @cod_estado IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM idiomas_estados_reservas WHERE cod_estado = @cod_estado AND nro_idioma = @nro_idioma_es)
        INSERT INTO idiomas_estados_reservas (cod_estado, nro_idioma, estado) VALUES (@cod_estado, @nro_idioma_es, N'Cancelada');
    
    IF NOT EXISTS (SELECT 1 FROM idiomas_estados_reservas WHERE cod_estado = @cod_estado AND nro_idioma = @nro_idioma_en)
        INSERT INTO idiomas_estados_reservas (cod_estado, nro_idioma, estado) VALUES (@cod_estado, @nro_idioma_en, N'Cancelled');
END

PRINT 'Traducciones de estados de reservas insertadas';

/* =========================================
   4) Traducciones de Zonas de Sucursales
   ========================================= */

-- Obtener todas las zonas existentes
DECLARE @nro_restaurante VARCHAR(36), @nro_sucursal VARCHAR(36), @cod_zona VARCHAR(36), @desc_zona NVARCHAR(200);

DECLARE cur_zonas CURSOR LOCAL FAST_FORWARD FOR
    SELECT nro_restaurante, nro_sucursal, cod_zona, desc_zona
    FROM zonas_sucursales_restaurantes
    ORDER BY nro_restaurante, nro_sucursal, cod_zona;

OPEN cur_zonas;
FETCH NEXT FROM cur_zonas INTO @nro_restaurante, @nro_sucursal, @cod_zona, @desc_zona;

WHILE @@FETCH_STATUS = 0
BEGIN
    -- Traducción es-AR (usar desc_zona como base)
    IF NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                   WHERE nro_restaurante = @nro_restaurante 
                   AND nro_sucursal = @nro_sucursal 
                   AND cod_zona = @cod_zona 
                   AND nro_idioma = @nro_idioma_es)
    BEGIN
        DECLARE @zona_es NVARCHAR(120);
        SET @zona_es = CASE 
            WHEN @desc_zona LIKE N'%Salón Principal%' OR @desc_zona LIKE N'%Salon Principal%' THEN N'Salón Principal'
            WHEN @desc_zona LIKE N'%Terraza%' THEN N'Terraza'
            ELSE ISNULL(@desc_zona, N'Zona')
        END;
        
        INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
        VALUES (@nro_restaurante, @nro_sucursal, @cod_zona, @nro_idioma_es, @zona_es, @desc_zona);
    END
    
    -- Traducción en-US
    IF NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                   WHERE nro_restaurante = @nro_restaurante 
                   AND nro_sucursal = @nro_sucursal 
                   AND cod_zona = @cod_zona 
                   AND nro_idioma = @nro_idioma_en)
    BEGIN
        DECLARE @zona_en NVARCHAR(120), @desc_zona_en NVARCHAR(400);
        SET @zona_en = CASE 
            WHEN @desc_zona LIKE N'%Salón Principal%' OR @desc_zona LIKE N'%Salon Principal%' THEN N'Main Hall'
            WHEN @desc_zona LIKE N'%Terraza%' THEN N'Terrace'
            ELSE ISNULL(@desc_zona, N'Zone')
        END;
        
        SET @desc_zona_en = CASE 
            WHEN @desc_zona LIKE N'%Salón Principal%' OR @desc_zona LIKE N'%Salon Principal%' THEN 
                REPLACE(REPLACE(@desc_zona, N'Salón Principal', N'Main Hall'), N'Salon Principal', N'Main Hall')
            WHEN @desc_zona LIKE N'%Terraza%' THEN 
                REPLACE(@desc_zona, N'Terraza', N'Terrace')
            ELSE @desc_zona
        END;
        
        INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
        VALUES (@nro_restaurante, @nro_sucursal, @cod_zona, @nro_idioma_en, @zona_en, @desc_zona_en);
    END
    
    FETCH NEXT FROM cur_zonas INTO @nro_restaurante, @nro_sucursal, @cod_zona, @desc_zona;
END

CLOSE cur_zonas;
DEALLOCATE cur_zonas;

PRINT 'Traducciones de zonas insertadas';

/* =========================================
   Resumen
   ========================================= */

PRINT '========================================';
PRINT 'Traducciones insertadas exitosamente';
PRINT '========================================';
PRINT '- Categorías: 3 categorías x 2 idiomas = 6 traducciones';
PRINT '- Dominios: Todos los dominios x 2 idiomas';
PRINT '- Estados de reservas: 5 estados x 2 idiomas = 10 traducciones';
PRINT '- Zonas: Todas las zonas x 2 idiomas';
PRINT '========================================';

GO


