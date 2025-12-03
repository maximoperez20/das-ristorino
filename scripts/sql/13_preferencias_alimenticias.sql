USE das_ristorino;
GO


-- Insertar categoría de preferencias de Especialidades alimentarias
INSERT INTO categorias_preferencias (nom_categoria) VALUES ('Especialidades alimentarias');

DECLARE @cat_alimentacion VARCHAR(36);
SELECT @cat_alimentacion = cod_categoria FROM categorias_preferencias WHERE nom_categoria = 'Especialidades alimentarias';


-- Insertar traducciones de categorias alimenticias
INSERT INTO idiomas_categorias_preferencias (cod_categoria, nro_idioma, categoria) VALUES (@cat_alimentacion, 0, 'Especialidades alimentarias');
INSERT INTO idiomas_categorias_preferencias (cod_categoria, nro_idioma, categoria) VALUES (@cat_alimentacion, 1, 'Food specialties');

-- Insertar dominios de categorias alimenticias
INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_alimentacion, 1, 'Vegetariano');
INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_alimentacion, 2, 'Vegano');
INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_alimentacion, 3, 'Sin gluten');
INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_alimentacion, 4, 'Sin lactosa');
INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_alimentacion, 5, 'Baja en calorías');
INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_alimentacion, 6, 'Organico');
INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_alimentacion, 7, 'Diabetico');

-- Insertar traducciones de dominios de categorias alimenticias
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 1, 1, 'Vegetarian');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 2, 1, 'Vegan');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 3, 1, 'Gluten free');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 4, 1, 'Lactose free');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 5, 1, 'Low calorie');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 6, 1, 'Organic');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 7, 1, 'Diabetic');

INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 1, 0, 'Vegetariano');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 2, 0, 'Vegano');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 3, 0, 'Sin gluten');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 4, 0, 'Sin lactosa');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 5, 0, 'Baja en calorías');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 6, 0, 'Orgánico');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 7, 0, 'Diabético');







-- Insertar preferencias de restaurantes

USE das_ristorino;
GO

SET NOCOUNT ON;
GO

PRINT '========================================';
PRINT 'Agregando preferencias adicionales a restaurantes existentes';
PRINT '========================================';

-- Obtener categorías
DECLARE @cat_tipo VARCHAR(36), @cat_amb VARCHAR(36), @cat_precio VARCHAR(36), @cat_alimentacion VARCHAR(36);
SELECT @cat_tipo = cod_categoria FROM categorias_preferencias WHERE nom_categoria = N'Tipo de comida';
SELECT @cat_amb = cod_categoria FROM categorias_preferencias WHERE nom_categoria = N'Ambiente';
SELECT @cat_precio = cod_categoria FROM categorias_preferencias WHERE nom_categoria = N'Rango de precio';
SELECT @cat_alimentacion = cod_categoria FROM categorias_preferencias WHERE nom_categoria = N'Especialidades alimentarias';

-- Variables para restaurantes
DECLARE @rest_1_uuid VARCHAR(36) = 'BELLA-PIZZA-1111-1111-1111-111111111111'; -- La Bella Pizza
DECLARE @rest_2_uuid VARCHAR(36) = 'PERUKAI-2222-2222-2222-222222222222'; -- Perukai
DECLARE @rest_3_uuid VARCHAR(36) = 'FABRICA-BURGER-3333-3333-3333-333333333333'; -- La Fábrica Burger
DECLARE @rest_4_uuid VARCHAR(36) = 'SABORES-NORTE-4444-4444-4444-444444444444'; -- Sabores del Norte

-- Variables para valores de dominio
DECLARE @nro_valor INT;
DECLARE @nro_pref INT;

/* =========================================
   RESTAURANTE 1: La Bella Pizza
   ========================================= */
PRINT 'Agregando preferencias para La Bella Pizza...';

-- Ambiente: Casual
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Casual';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_1_uuid AND cod_categoria = @cat_amb;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_1_uuid AND cod_categoria = @cat_amb AND nro_valor_dominio = @nro_valor)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_1_uuid, @cat_amb, @nro_valor, @nro_pref);
    END
END

-- Ambiente: Familiar
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Familiar';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_1_uuid AND cod_categoria = @cat_amb;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_1_uuid AND cod_categoria = @cat_amb AND nro_valor_dominio = @nro_valor)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_1_uuid, @cat_amb, @nro_valor, @nro_pref);
    END
END

-- Ambiente: Romántico (pizzas románticas)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Romántico';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_1_uuid AND cod_categoria = @cat_amb;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_1_uuid AND cod_categoria = @cat_amb AND nro_valor_dominio = @nro_valor)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_1_uuid, @cat_amb, @nro_valor, @nro_pref);
    END
END

-- Rango de precio: Medio
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_precio AND nom_valor_dominio = N'Medio';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_1_uuid AND cod_categoria = @cat_precio;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_1_uuid AND cod_categoria = @cat_precio AND nro_valor_dominio = @nro_valor)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_1_uuid, @cat_precio, @nro_valor, @nro_pref);
    END
END

-- Especialidades: Vegetariano (1)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_alimentacion AND nom_valor_dominio = N'Vegetariano';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_1_uuid AND cod_categoria = @cat_alimentacion AND nro_sucursal IS NULL;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_1_uuid AND cod_categoria = @cat_alimentacion AND nro_valor_dominio = @nro_valor AND nro_sucursal IS NULL)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, nro_sucursal)
        VALUES (@rest_1_uuid, @cat_alimentacion, @nro_valor, @nro_pref, NULL);
    END
END

-- Especialidades: Sin lactosa (4)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_alimentacion AND nom_valor_dominio = N'Sin lactosa';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_1_uuid AND cod_categoria = @cat_alimentacion AND nro_sucursal IS NULL;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_1_uuid AND cod_categoria = @cat_alimentacion AND nro_valor_dominio = @nro_valor AND nro_sucursal IS NULL)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, nro_sucursal)
        VALUES (@rest_1_uuid, @cat_alimentacion, @nro_valor, @nro_pref, NULL);
    END
END

-- Especialidades: Orgánico (6)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_alimentacion AND nom_valor_dominio = N'Orgánico';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_1_uuid AND cod_categoria = @cat_alimentacion AND nro_sucursal IS NULL;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_1_uuid AND cod_categoria = @cat_alimentacion AND nro_valor_dominio = @nro_valor AND nro_sucursal IS NULL)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, nro_sucursal)
        VALUES (@rest_1_uuid, @cat_alimentacion, @nro_valor, @nro_pref, NULL);
    END
END

PRINT 'Preferencias de La Bella Pizza agregadas';

/* =========================================
   RESTAURANTE 2: Perukai
   ========================================= */
PRINT 'Agregando preferencias para Perukai...';

-- Ambiente: Gourmet
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Gourmet';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_2_uuid AND cod_categoria = @cat_amb;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_2_uuid AND cod_categoria = @cat_amb AND nro_valor_dominio = @nro_valor)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_2_uuid, @cat_amb, @nro_valor, @nro_pref);
    END
END

-- Ambiente: Romántico (experiencia gastronómica única)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Romántico';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_2_uuid AND cod_categoria = @cat_amb;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_2_uuid AND cod_categoria = @cat_amb AND nro_valor_dominio = @nro_valor)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_2_uuid, @cat_amb, @nro_valor, @nro_pref);
    END
END

-- Rango de precio: Premium
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_precio AND nom_valor_dominio = N'Premium';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_2_uuid AND cod_categoria = @cat_precio;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_2_uuid AND cod_categoria = @cat_precio AND nro_valor_dominio = @nro_valor)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_2_uuid, @cat_precio, @nro_valor, @nro_pref);
    END
END

-- Especialidades: Vegano (2)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_alimentacion AND nom_valor_dominio = N'Vegano';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_2_uuid AND cod_categoria = @cat_alimentacion AND nro_sucursal IS NULL;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_2_uuid AND cod_categoria = @cat_alimentacion AND nro_valor_dominio = @nro_valor AND nro_sucursal IS NULL)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, nro_sucursal)
        VALUES (@rest_2_uuid, @cat_alimentacion, @nro_valor, @nro_pref, NULL);
    END
END

-- Especialidades: Sin gluten (3)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_alimentacion AND nom_valor_dominio = N'Sin gluten';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_2_uuid AND cod_categoria = @cat_alimentacion AND nro_sucursal IS NULL;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_2_uuid AND cod_categoria = @cat_alimentacion AND nro_valor_dominio = @nro_valor AND nro_sucursal IS NULL)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, nro_sucursal)
        VALUES (@rest_2_uuid, @cat_alimentacion, @nro_valor, @nro_pref, NULL);
    END
END

-- Especialidades: Baja en calorías (5)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_alimentacion AND nom_valor_dominio = N'Baja en calorías';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_2_uuid AND cod_categoria = @cat_alimentacion AND nro_sucursal IS NULL;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_2_uuid AND cod_categoria = @cat_alimentacion AND nro_valor_dominio = @nro_valor AND nro_sucursal IS NULL)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, nro_sucursal)
        VALUES (@rest_2_uuid, @cat_alimentacion, @nro_valor, @nro_pref, NULL);
    END
END

PRINT 'Preferencias de Perukai agregadas';

/* =========================================
   RESTAURANTE 3: La Fábrica Burger
   ========================================= */
PRINT 'Agregando preferencias para La Fábrica Burger...';

-- Ambiente: Casual
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Casual';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_3_uuid AND cod_categoria = @cat_amb;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_3_uuid AND cod_categoria = @cat_amb AND nro_valor_dominio = @nro_valor)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_3_uuid, @cat_amb, @nro_valor, @nro_pref);
    END
END

-- Ambiente: Familiar (ambiente juvenil y moderno, pero también familiar)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Familiar';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_3_uuid AND cod_categoria = @cat_amb;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_3_uuid AND cod_categoria = @cat_amb AND nro_valor_dominio = @nro_valor)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_3_uuid, @cat_amb, @nro_valor, @nro_pref);
    END
END

-- Rango de precio: Medio
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_precio AND nom_valor_dominio = N'Medio';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_3_uuid AND cod_categoria = @cat_precio;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_3_uuid AND cod_categoria = @cat_precio AND nro_valor_dominio = @nro_valor)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_3_uuid, @cat_precio, @nro_valor, @nro_pref);
    END
END

-- Rango de precio: Económico (también tiene opciones económicas)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_precio AND nom_valor_dominio = N'Económico';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_3_uuid AND cod_categoria = @cat_precio;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_3_uuid AND cod_categoria = @cat_precio AND nro_valor_dominio = @nro_valor)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_3_uuid, @cat_precio, @nro_valor, @nro_pref);
    END
END

-- Especialidades: Vegetariano (1)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_alimentacion AND nom_valor_dominio = N'Vegetariano';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_3_uuid AND cod_categoria = @cat_alimentacion AND nro_sucursal IS NULL;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_3_uuid AND cod_categoria = @cat_alimentacion AND nro_valor_dominio = @nro_valor AND nro_sucursal IS NULL)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, nro_sucursal)
        VALUES (@rest_3_uuid, @cat_alimentacion, @nro_valor, @nro_pref, NULL);
    END
END

-- Especialidades: Baja en calorías (5)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_alimentacion AND nom_valor_dominio = N'Baja en calorías';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_3_uuid AND cod_categoria = @cat_alimentacion AND nro_sucursal IS NULL;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_3_uuid AND cod_categoria = @cat_alimentacion AND nro_valor_dominio = @nro_valor AND nro_sucursal IS NULL)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, nro_sucursal)
        VALUES (@rest_3_uuid, @cat_alimentacion, @nro_valor, @nro_pref, NULL);
    END
END

PRINT 'Preferencias de La Fábrica Burger agregadas';

/* =========================================
   RESTAURANTE 4: Sabores del Norte
   ========================================= */
PRINT 'Agregando preferencias para Sabores del Norte...';

-- Ambiente: Familiar
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Familiar';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_4_uuid AND cod_categoria = @cat_amb;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_4_uuid AND cod_categoria = @cat_amb AND nro_valor_dominio = @nro_valor)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_4_uuid, @cat_amb, @nro_valor, @nro_pref);
    END
END

-- Ambiente: Casual (tradicional pero casual)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Casual';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_4_uuid AND cod_categoria = @cat_amb;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_4_uuid AND cod_categoria = @cat_amb AND nro_valor_dominio = @nro_valor)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_4_uuid, @cat_amb, @nro_valor, @nro_pref);
    END
END

-- Rango de precio: Medio
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_precio AND nom_valor_dominio = N'Medio';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_4_uuid AND cod_categoria = @cat_precio;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_4_uuid AND cod_categoria = @cat_precio AND nro_valor_dominio = @nro_valor)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_4_uuid, @cat_precio, @nro_valor, @nro_pref);
    END
END

-- Rango de precio: Económico (también tiene opciones económicas)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_precio AND nom_valor_dominio = N'Económico';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_4_uuid AND cod_categoria = @cat_precio;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_4_uuid AND cod_categoria = @cat_precio AND nro_valor_dominio = @nro_valor)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_4_uuid, @cat_precio, @nro_valor, @nro_pref);
    END
END

-- Especialidades: Vegetariano (1)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_alimentacion AND nom_valor_dominio = N'Vegetariano';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_4_uuid AND cod_categoria = @cat_alimentacion AND nro_sucursal IS NULL;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_4_uuid AND cod_categoria = @cat_alimentacion AND nro_valor_dominio = @nro_valor AND nro_sucursal IS NULL)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, nro_sucursal)
        VALUES (@rest_4_uuid, @cat_alimentacion, @nro_valor, @nro_pref, NULL);
    END
END

-- Especialidades: Orgánico (6)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_alimentacion AND nom_valor_dominio = N'Orgánico';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_4_uuid AND cod_categoria = @cat_alimentacion AND nro_sucursal IS NULL;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_4_uuid AND cod_categoria = @cat_alimentacion AND nro_valor_dominio = @nro_valor AND nro_sucursal IS NULL)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, nro_sucursal)
        VALUES (@rest_4_uuid, @cat_alimentacion, @nro_valor, @nro_pref, NULL);
    END
END

-- Especialidades: Diabético (7)
SELECT @nro_valor = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_alimentacion AND nom_valor_dominio = N'Diabético';
IF @nro_valor IS NOT NULL
BEGIN
    SELECT @nro_pref = ISNULL(MAX(nro_preferencia), 0) + 1 
    FROM preferencias_restaurantes 
    WHERE nro_restaurante = @rest_4_uuid AND cod_categoria = @cat_alimentacion AND nro_sucursal IS NULL;
    
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_4_uuid AND cod_categoria = @cat_alimentacion AND nro_valor_dominio = @nro_valor AND nro_sucursal IS NULL)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, nro_sucursal)
        VALUES (@rest_4_uuid, @cat_alimentacion, @nro_valor, @nro_pref, NULL);
    END
END

PRINT 'Preferencias de Sabores del Norte agregadas';

PRINT '';
PRINT '========================================';
PRINT 'RESUMEN DE PREFERENCIAS AGREGADAS';
PRINT '========================================';
PRINT '';
PRINT 'La Bella Pizza:';
PRINT '  - Ambiente: Casual, Familiar, Romántico';
PRINT '  - Rango de precio: Medio';
PRINT '  - Especialidades: Vegetariano (1), Sin lactosa (4), Orgánico (6)';
PRINT '';
PRINT 'Perukai:';
PRINT '  - Ambiente: Gourmet, Romántico';
PRINT '  - Rango de precio: Premium';
PRINT '  - Especialidades: Vegano (2), Sin gluten (3), Baja en calorías (5)';
PRINT '';
PRINT 'La Fábrica Burger:';
PRINT '  - Ambiente: Casual, Familiar';
PRINT '  - Rango de precio: Medio, Económico';
PRINT '  - Especialidades: Vegetariano (1), Baja en calorías (5)';
PRINT '';
PRINT 'Sabores del Norte:';
PRINT '  - Ambiente: Familiar, Casual';
PRINT '  - Rango de precio: Medio, Económico';
PRINT '  - Especialidades: Vegetariano (1), Orgánico (6), Diabético (7)';
PRINT '';
PRINT '========================================';
GO