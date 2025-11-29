/* =========================================================================================
   INSERT DE RESTAURANTES PARA EXAMEN FINAL
   Incluye: 4 restaurantes con protocolo (SOAP/REST), sucursales, preferencias e identidad
   ========================================================================================= */

SET NOCOUNT ON;
GO

USE das_ristorino;
GO

/* =========================================
   1) Verificar/crear localidades (barrios de Córdoba)
   ========================================= */

DECLARE @cod_cba VARCHAR(36);
SELECT @cod_cba = cod_provincia FROM provincias WHERE nom_provincia = N'Córdoba';

-- Barrios de Córdoba
IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad = N'Alta Córdoba' AND cod_provincia = @cod_cba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Alta Córdoba', @cod_cba);

IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad = N'General Paz' AND cod_provincia = @cod_cba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'General Paz', @cod_cba);

IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad = N'Nueva Córdoba' AND cod_provincia = @cod_cba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Nueva Córdoba', @cod_cba);

IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad = N'Güemes' AND cod_provincia = @cod_cba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Güemes', @cod_cba);

IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad = N'Cerro de las Rosas' AND cod_provincia = @cod_cba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Cerro de las Rosas', @cod_cba);

IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad = N'Centro' AND cod_provincia = @cod_cba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Centro', @cod_cba);

PRINT 'Localidades (barrios) verificadas/creadas';

/* =========================================
   2) Verificar/crear dominios de preferencias necesarios
   ========================================= */

DECLARE @cat_tipo VARCHAR(36), @cat_amb VARCHAR(36), @cat_precio VARCHAR(36);
SELECT @cat_tipo = cod_categoria FROM categorias_preferencias WHERE nom_categoria = N'Tipo de comida';
SELECT @cat_amb = cod_categoria FROM categorias_preferencias WHERE nom_categoria = N'Ambiente';
SELECT @cat_precio = cod_categoria FROM categorias_preferencias WHERE nom_categoria = N'Rango de precio';

DECLARE @prox INT;

-- Tipo de comida: Italiana tradicional, Fusión japonesa-peruana, Fast food gourmet, Regional del NOA
IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Italiana tradicional')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_tipo, @prox, N'Italiana tradicional');
END

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Fusión japonesa-peruana')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_tipo, @prox, N'Fusión japonesa-peruana');
END

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Fast food gourmet')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_tipo, @prox, N'Fast food gourmet');
END

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Regional del NOA')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_tipo, @prox, N'Regional del NOA');
END

PRINT 'Dominios de preferencias verificados/creados';

/* =========================================
   3) Verificar/crear atributos de identidad
   ========================================= */

DECLARE @cod_atributo_lenguaje VARCHAR(36);
IF NOT EXISTS (SELECT 1 FROM atributos WHERE nom_atributo = N'Lenguaje preferido')
BEGIN
    SET @cod_atributo_lenguaje = NEWID();
    INSERT INTO atributos (cod_atributo, nom_atributo, tipo_dato)
    VALUES (@cod_atributo_lenguaje, N'Lenguaje preferido', 'string');
    PRINT 'Atributo "Lenguaje preferido" creado';
END
ELSE
BEGIN
    SELECT @cod_atributo_lenguaje = cod_atributo FROM atributos WHERE nom_atributo = N'Lenguaje preferido';
END

-- Obtener atributos existentes
DECLARE @cod_atributo_tipo_cocina VARCHAR(36);
DECLARE @cod_atributo_estilo_atencion VARCHAR(36);
DECLARE @cod_atributo_platos_emblematicos VARCHAR(36);

SELECT @cod_atributo_tipo_cocina = cod_atributo FROM atributos WHERE nom_atributo = N'Tipo de cocina';
SELECT @cod_atributo_estilo_atencion = cod_atributo FROM atributos WHERE nom_atributo = N'Estilo de atención';
SELECT @cod_atributo_platos_emblematicos = cod_atributo FROM atributos WHERE nom_atributo = N'Platos emblemáticos';

PRINT 'Atributos de identidad verificados';

/* =========================================
   4) RESTAURANTE 1: La Bella Pizza (REST)
   ========================================= */

DECLARE @rest_1_uuid VARCHAR(36) = 'BELLA-PIZZA-1111-1111-1111-111111111111';
DECLARE @nro_localidad_alta_cordoba VARCHAR(36);
DECLARE @nro_localidad_general_paz VARCHAR(36);

SELECT @nro_localidad_alta_cordoba = nro_localidad FROM localidades WHERE nom_localidad = N'Alta Córdoba' AND cod_provincia = @cod_cba;
SELECT @nro_localidad_general_paz = nro_localidad FROM localidades WHERE nom_localidad = N'General Paz' AND cod_provincia = @cod_cba;

-- Insertar restaurante
IF NOT EXISTS (SELECT 1 FROM restaurantes WHERE nro_restaurante = @rest_1_uuid)
BEGIN
    INSERT INTO restaurantes (nro_restaurante, razon_social, cuit, tipo_protocolo, url_servicio)
    VALUES (@rest_1_uuid, N'La Bella Pizza SRL', '30123456789', 'REST', 'http://localhost:8082/api');
    PRINT 'Restaurante 1 (La Bella Pizza) insertado';
END
ELSE
BEGIN
    UPDATE restaurantes 
    SET tipo_protocolo = 'REST', url_servicio = 'http://localhost:8082/api'
    WHERE nro_restaurante = @rest_1_uuid;
    PRINT 'Restaurante 1 (La Bella Pizza) actualizado';
END

-- Sucursal 1: Alta Córdoba
-- UUID interno de ristorino (diferente del UUID del sistema del restaurante)
DECLARE @suc_1_1_uuid VARCHAR(36);
-- UUID del sistema del restaurante (das_restaurante) - DEBE COINCIDIR
DECLARE @suc_1_1_uuid_restaurante VARCHAR(36) = 'BELLA-PIZZA-SUC-0001-0001-0001-0001';
IF NOT EXISTS (SELECT 1 FROM sucursales_restaurantes WHERE nro_restaurante = @rest_1_uuid AND nom_sucursal = N'La Bella Pizza - Alta Córdoba')
BEGIN
    SET @suc_1_1_uuid = NEWID();
    INSERT INTO sucursales_restaurantes (
        nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio,
        nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva,
        cod_sucursal_restaurante
    )
    VALUES (
        @rest_1_uuid, @suc_1_1_uuid, N'La Bella Pizza - Alta Córdoba',
        N'Av. Colón', 2500, N'Alta Córdoba',
        @nro_localidad_alta_cordoba, '5000', '351-555-1001', 80, 15,
        @suc_1_1_uuid_restaurante  -- UUID fijo del sistema del restaurante
    );
    PRINT 'Sucursal 1.1 (Alta Córdoba) insertada';
END
ELSE
BEGIN
    SELECT @suc_1_1_uuid = nro_sucursal FROM sucursales_restaurantes 
    WHERE nro_restaurante = @rest_1_uuid AND nom_sucursal = N'La Bella Pizza - Alta Córdoba';
    -- Actualizar cod_sucursal_restaurante si no existe o es diferente
    UPDATE sucursales_restaurantes 
    SET cod_sucursal_restaurante = @suc_1_1_uuid_restaurante
    WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_1_uuid 
      AND (cod_sucursal_restaurante IS NULL OR cod_sucursal_restaurante != @suc_1_1_uuid_restaurante);
END

-- Sucursal 2: General Paz
DECLARE @suc_1_2_uuid VARCHAR(36);
DECLARE @suc_1_2_uuid_restaurante VARCHAR(36) = 'BELLA-PIZZA-SUC-0002-0002-0002-0002';
IF NOT EXISTS (SELECT 1 FROM sucursales_restaurantes WHERE nro_restaurante = @rest_1_uuid AND nom_sucursal = N'La Bella Pizza - General Paz')
BEGIN
    SET @suc_1_2_uuid = NEWID();
    INSERT INTO sucursales_restaurantes (
        nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio,
        nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva,
        cod_sucursal_restaurante
    )
    VALUES (
        @rest_1_uuid, @suc_1_2_uuid, N'La Bella Pizza - General Paz',
        N'Av. General Paz', 800, N'General Paz',
        @nro_localidad_general_paz, '5000', '351-555-1002', 60, 15,
        @suc_1_2_uuid_restaurante  -- UUID fijo del sistema del restaurante
    );
    PRINT 'Sucursal 1.2 (General Paz) insertada';
END
ELSE
BEGIN
    SELECT @suc_1_2_uuid = nro_sucursal FROM sucursales_restaurantes 
    WHERE nro_restaurante = @rest_1_uuid AND nom_sucursal = N'La Bella Pizza - General Paz';
    UPDATE sucursales_restaurantes 
    SET cod_sucursal_restaurante = @suc_1_2_uuid_restaurante
    WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_2_uuid 
      AND (cod_sucursal_restaurante IS NULL OR cod_sucursal_restaurante != @suc_1_2_uuid_restaurante);
END

-- Preferencia: Tipo de comida - Italiana tradicional
DECLARE @nro_valor_italiana_trad INT;
SELECT @nro_valor_italiana_trad = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Italiana tradicional';

IF @nro_valor_italiana_trad IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_1_uuid AND cod_categoria = @cat_tipo AND nro_valor_dominio = @nro_valor_italiana_trad)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_1_uuid, @cat_tipo, @nro_valor_italiana_trad, 1);
    END
END

-- Preferencia adicional: Tipo de comida - Pizzería (para que aparezca en búsquedas de "pizza")
DECLARE @nro_valor_pizzeria INT;
SELECT @nro_valor_pizzeria = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Pizzería';

IF @nro_valor_pizzeria IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_1_uuid AND cod_categoria = @cat_tipo AND nro_valor_dominio = @nro_valor_pizzeria)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_1_uuid, @cat_tipo, @nro_valor_pizzeria, 2);
    END
END

-- Atributos de identidad
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @rest_1_uuid AND cod_atributo = @cod_atributo_tipo_cocina)
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@rest_1_uuid, @cod_atributo_tipo_cocina, N'Italiana tradicional');

IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @rest_1_uuid AND cod_atributo = @cod_atributo_estilo_atencion)
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@rest_1_uuid, @cod_atributo_estilo_atencion, N'Casual y familiar, ambiente acogedor');

IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @rest_1_uuid AND cod_atributo = @cod_atributo_lenguaje)
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@rest_1_uuid, @cod_atributo_lenguaje, N'es-AR');

IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @rest_1_uuid AND cod_atributo = @cod_atributo_platos_emblematicos)
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@rest_1_uuid, @cod_atributo_platos_emblematicos, N'Pizza Margherita, Pizza Napolitana, Lasagna casera, Tiramisú');

PRINT 'Restaurante 1 (La Bella Pizza) configurado completamente';

/* =========================================
   5) RESTAURANTE 2: Perukai (SOAP)
   ========================================= */

DECLARE @rest_2_uuid VARCHAR(36) = 'PERUKAI-2222-2222-2222-222222222222';
DECLARE @nro_localidad_nueva_cordoba VARCHAR(36);
DECLARE @nro_localidad_guemes VARCHAR(36);

SELECT @nro_localidad_nueva_cordoba = nro_localidad FROM localidades WHERE nom_localidad = N'Nueva Córdoba' AND cod_provincia = @cod_cba;
SELECT @nro_localidad_guemes = nro_localidad FROM localidades WHERE nom_localidad = N'Güemes' AND cod_provincia = @cod_cba;

-- Insertar restaurante
IF NOT EXISTS (SELECT 1 FROM restaurantes WHERE nro_restaurante = @rest_2_uuid)
BEGIN
    INSERT INTO restaurantes (nro_restaurante, razon_social, cuit, tipo_protocolo, url_servicio)
    VALUES (@rest_2_uuid, N'Perukai S.A.', '30234567890', 'SOAP', 'http://localhost:8081/ws/restaurantes.wsdl');
    PRINT 'Restaurante 2 (Perukai) insertado';
END
ELSE
BEGIN
    UPDATE restaurantes 
    SET tipo_protocolo = 'SOAP', url_servicio = 'http://localhost:8081/ws/restaurantes.wsdl'
    WHERE nro_restaurante = @rest_2_uuid;
    PRINT 'Restaurante 2 (Perukai) actualizado';
END

-- Sucursal 1: Nueva Córdoba
DECLARE @suc_2_1_uuid VARCHAR(36);
DECLARE @suc_2_1_uuid_restaurante VARCHAR(36) = 'PERUKAI-SUC-0001-0001-0001-0001';
IF NOT EXISTS (SELECT 1 FROM sucursales_restaurantes WHERE nro_restaurante = @rest_2_uuid AND nom_sucursal = N'Perukai - Nueva Córdoba')
BEGIN
    SET @suc_2_1_uuid = NEWID();
    INSERT INTO sucursales_restaurantes (
        nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio,
        nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva,
        cod_sucursal_restaurante
    )
    VALUES (
        @rest_2_uuid, @suc_2_1_uuid, N'Perukai - Nueva Córdoba',
        N'Av. Humberto Primo', 450, N'Nueva Córdoba',
        @nro_localidad_nueva_cordoba, '5000', '351-555-2001', 100, 20,
        @suc_2_1_uuid_restaurante  -- UUID fijo del sistema del restaurante
    );
    PRINT 'Sucursal 2.1 (Nueva Córdoba) insertada';
END
ELSE
BEGIN
    SELECT @suc_2_1_uuid = nro_sucursal FROM sucursales_restaurantes 
    WHERE nro_restaurante = @rest_2_uuid AND nom_sucursal = N'Perukai - Nueva Córdoba';
    UPDATE sucursales_restaurantes 
    SET cod_sucursal_restaurante = @suc_2_1_uuid_restaurante
    WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_1_uuid 
      AND (cod_sucursal_restaurante IS NULL OR cod_sucursal_restaurante != @suc_2_1_uuid_restaurante);
END

-- Sucursal 2: Güemes
DECLARE @suc_2_2_uuid VARCHAR(36);
DECLARE @suc_2_2_uuid_restaurante VARCHAR(36) = 'PERUKAI-SUC-0002-0002-0002-0002';
IF NOT EXISTS (SELECT 1 FROM sucursales_restaurantes WHERE nro_restaurante = @rest_2_uuid AND nom_sucursal = N'Perukai - Güemes')
BEGIN
    SET @suc_2_2_uuid = NEWID();
    INSERT INTO sucursales_restaurantes (
        nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio,
        nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva,
        cod_sucursal_restaurante
    )
    VALUES (
        @rest_2_uuid, @suc_2_2_uuid, N'Perukai - Güemes',
        N'Belgrano', 700, N'Güemes',
        @nro_localidad_guemes, '5000', '351-555-2002', 70, 20,
        @suc_2_2_uuid_restaurante  -- UUID fijo del sistema del restaurante
    );
    PRINT 'Sucursal 2.2 (Güemes) insertada';
END
ELSE
BEGIN
    SELECT @suc_2_2_uuid = nro_sucursal FROM sucursales_restaurantes 
    WHERE nro_restaurante = @rest_2_uuid AND nom_sucursal = N'Perukai - Güemes';
    UPDATE sucursales_restaurantes 
    SET cod_sucursal_restaurante = @suc_2_2_uuid_restaurante
    WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_2_uuid 
      AND (cod_sucursal_restaurante IS NULL OR cod_sucursal_restaurante != @suc_2_2_uuid_restaurante);
END

-- Preferencia: Tipo de comida - Fusión japonesa-peruana
DECLARE @nro_valor_fusion INT;
SELECT @nro_valor_fusion = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Fusión japonesa-peruana';

IF @nro_valor_fusion IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_2_uuid AND cod_categoria = @cat_tipo AND nro_valor_dominio = @nro_valor_fusion)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_2_uuid, @cat_tipo, @nro_valor_fusion, 1);
    END
END

-- Atributos de identidad
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @rest_2_uuid AND cod_atributo = @cod_atributo_tipo_cocina)
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@rest_2_uuid, @cod_atributo_tipo_cocina, N'Fusión japonesa-peruana');

IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @rest_2_uuid AND cod_atributo = @cod_atributo_estilo_atencion)
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@rest_2_uuid, @cod_atributo_estilo_atencion, N'Moderno y sofisticado, experiencia gastronómica única');

IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @rest_2_uuid AND cod_atributo = @cod_atributo_lenguaje)
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@rest_2_uuid, @cod_atributo_lenguaje, N'es-AR');

IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @rest_2_uuid AND cod_atributo = @cod_atributo_platos_emblematicos)
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@rest_2_uuid, @cod_atributo_platos_emblematicos, N'Ceviche Nikkei, Tiradito de salmón, Roll Acevichado, Lomo saltado');

PRINT 'Restaurante 2 (Perukai) configurado completamente';

/* =========================================
   6) RESTAURANTE 3: La Fábrica Burger (REST)
   ========================================= */

DECLARE @rest_3_uuid VARCHAR(36) = 'FABRICA-BURGER-3333-3333-3333-333333333333';
DECLARE @nro_localidad_cerro VARCHAR(36);

SELECT @nro_localidad_cerro = nro_localidad FROM localidades WHERE nom_localidad = N'Cerro de las Rosas' AND cod_provincia = @cod_cba;

-- Insertar restaurante
IF NOT EXISTS (SELECT 1 FROM restaurantes WHERE nro_restaurante = @rest_3_uuid)
BEGIN
    INSERT INTO restaurantes (nro_restaurante, razon_social, cuit, tipo_protocolo, url_servicio)
    VALUES (@rest_3_uuid, N'La Fábrica Burger SRL', '30345678901', 'REST', 'http://localhost:8082/api');
    PRINT 'Restaurante 3 (La Fábrica Burger) insertado';
END
ELSE
BEGIN
    UPDATE restaurantes 
    SET tipo_protocolo = 'REST', url_servicio = 'http://localhost:8082/api'
    WHERE nro_restaurante = @rest_3_uuid;
    PRINT 'Restaurante 3 (La Fábrica Burger) actualizado';
END

-- Sucursal 1: Cerro de las Rosas
DECLARE @suc_3_1_uuid VARCHAR(36);
DECLARE @suc_3_1_uuid_restaurante VARCHAR(36) = 'FABRICA-BURGER-SUC-0001-0001-0001-0001';
IF NOT EXISTS (SELECT 1 FROM sucursales_restaurantes WHERE nro_restaurante = @rest_3_uuid AND nom_sucursal = N'La Fábrica Burger - Cerro de las Rosas')
BEGIN
    SET @suc_3_1_uuid = NEWID();
    INSERT INTO sucursales_restaurantes (
        nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio,
        nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva,
        cod_sucursal_restaurante
    )
    VALUES (
        @rest_3_uuid, @suc_3_1_uuid, N'La Fábrica Burger - Cerro de las Rosas',
        N'Av. Rafael Núñez', 3500, N'Cerro de las Rosas',
        @nro_localidad_cerro, '5009', '351-555-3001', 90, 10,
        @suc_3_1_uuid_restaurante  -- UUID fijo del sistema del restaurante
    );
    PRINT 'Sucursal 3.1 (Cerro de las Rosas) insertada';
END
ELSE
BEGIN
    SELECT @suc_3_1_uuid = nro_sucursal FROM sucursales_restaurantes 
    WHERE nro_restaurante = @rest_3_uuid AND nom_sucursal = N'La Fábrica Burger - Cerro de las Rosas';
    UPDATE sucursales_restaurantes 
    SET cod_sucursal_restaurante = @suc_3_1_uuid_restaurante
    WHERE nro_restaurante = @rest_3_uuid AND nro_sucursal = @suc_3_1_uuid 
      AND (cod_sucursal_restaurante IS NULL OR cod_sucursal_restaurante != @suc_3_1_uuid_restaurante);
END

-- Preferencia: Tipo de comida - Fast food gourmet
DECLARE @nro_valor_fastfood INT;
SELECT @nro_valor_fastfood = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Fast food gourmet';

IF @nro_valor_fastfood IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_3_uuid AND cod_categoria = @cat_tipo AND nro_valor_dominio = @nro_valor_fastfood)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_3_uuid, @cat_tipo, @nro_valor_fastfood, 1);
    END
END

-- Atributos de identidad
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @rest_3_uuid AND cod_atributo = @cod_atributo_tipo_cocina)
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@rest_3_uuid, @cod_atributo_tipo_cocina, N'Fast food gourmet');

IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @rest_3_uuid AND cod_atributo = @cod_atributo_estilo_atencion)
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@rest_3_uuid, @cod_atributo_estilo_atencion, N'Casual y dinámico, ambiente juvenil y moderno');

IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @rest_3_uuid AND cod_atributo = @cod_atributo_lenguaje)
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@rest_3_uuid, @cod_atributo_lenguaje, N'es-AR');

IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @rest_3_uuid AND cod_atributo = @cod_atributo_platos_emblematicos)
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@rest_3_uuid, @cod_atributo_platos_emblematicos, N'Burger Clásica, Burger BBQ, Papas artesanales, Milkshakes premium');

PRINT 'Restaurante 3 (La Fábrica Burger) configurado completamente';

/* =========================================
   7) RESTAURANTE 4: Sabores del Norte (SOAP)
   ========================================= */

DECLARE @rest_4_uuid VARCHAR(36) = 'SABORES-NORTE-4444-4444-4444-444444444444';
DECLARE @nro_localidad_centro VARCHAR(36);

SELECT @nro_localidad_centro = nro_localidad FROM localidades WHERE nom_localidad = N'Centro' AND cod_provincia = @cod_cba;

-- Insertar restaurante
IF NOT EXISTS (SELECT 1 FROM restaurantes WHERE nro_restaurante = @rest_4_uuid)
BEGIN
    INSERT INTO restaurantes (nro_restaurante, razon_social, cuit, tipo_protocolo, url_servicio)
    VALUES (@rest_4_uuid, N'Sabores del Norte S.A.', '30456789012', 'SOAP', 'http://localhost:8081/ws/restaurantes.wsdl');
    PRINT 'Restaurante 4 (Sabores del Norte) insertado';
END
ELSE
BEGIN
    UPDATE restaurantes 
    SET tipo_protocolo = 'SOAP', url_servicio = 'http://localhost:8081/ws/restaurantes.wsdl'
    WHERE nro_restaurante = @rest_4_uuid;
    PRINT 'Restaurante 4 (Sabores del Norte) actualizado';
END

-- Sucursal 1: Centro
DECLARE @suc_4_1_uuid VARCHAR(36);
DECLARE @suc_4_1_uuid_restaurante VARCHAR(36) = 'SABORES-NORTE-SUC-0001-0001-0001-0001';
IF NOT EXISTS (SELECT 1 FROM sucursales_restaurantes WHERE nro_restaurante = @rest_4_uuid AND nom_sucursal = N'Sabores del Norte - Centro')
BEGIN
    SET @suc_4_1_uuid = NEWID();
    INSERT INTO sucursales_restaurantes (
        nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio,
        nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva,
        cod_sucursal_restaurante
    )
    VALUES (
        @rest_4_uuid, @suc_4_1_uuid, N'Sabores del Norte - Centro',
        N'Av. Colón', 1200, N'Centro',
        @nro_localidad_centro, '5000', '351-555-4001', 110, 20,
        @suc_4_1_uuid_restaurante  -- UUID fijo del sistema del restaurante
    );
    PRINT 'Sucursal 4.1 (Centro) insertada';
END
ELSE
BEGIN
    SELECT @suc_4_1_uuid = nro_sucursal FROM sucursales_restaurantes 
    WHERE nro_restaurante = @rest_4_uuid AND nom_sucursal = N'Sabores del Norte - Centro';
    UPDATE sucursales_restaurantes 
    SET cod_sucursal_restaurante = @suc_4_1_uuid_restaurante
    WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_1_uuid 
      AND (cod_sucursal_restaurante IS NULL OR cod_sucursal_restaurante != @suc_4_1_uuid_restaurante);
END

-- Sucursal 2: Cerro de las Rosas
DECLARE @suc_4_2_uuid VARCHAR(36);
DECLARE @suc_4_2_uuid_restaurante VARCHAR(36) = 'SABORES-NORTE-SUC-0002-0002-0002-0002';
IF NOT EXISTS (SELECT 1 FROM sucursales_restaurantes WHERE nro_restaurante = @rest_4_uuid AND nom_sucursal = N'Sabores del Norte - Cerro de las Rosas')
BEGIN
    SET @suc_4_2_uuid = NEWID();
    INSERT INTO sucursales_restaurantes (
        nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio,
        nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva,
        cod_sucursal_restaurante
    )
    VALUES (
        @rest_4_uuid, @suc_4_2_uuid, N'Sabores del Norte - Cerro de las Rosas',
        N'Av. Rafael Núñez', 3800, N'Cerro de las Rosas',
        @nro_localidad_cerro, '5009', '351-555-4002', 85, 20,
        @suc_4_2_uuid_restaurante  -- UUID fijo del sistema del restaurante
    );
    PRINT 'Sucursal 4.2 (Cerro de las Rosas) insertada';
END
ELSE
BEGIN
    SELECT @suc_4_2_uuid = nro_sucursal FROM sucursales_restaurantes 
    WHERE nro_restaurante = @rest_4_uuid AND nom_sucursal = N'Sabores del Norte - Cerro de las Rosas';
    UPDATE sucursales_restaurantes 
    SET cod_sucursal_restaurante = @suc_4_2_uuid_restaurante
    WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_2_uuid 
      AND (cod_sucursal_restaurante IS NULL OR cod_sucursal_restaurante != @suc_4_2_uuid_restaurante);
END

-- Preferencia: Tipo de comida - Regional del NOA
DECLARE @nro_valor_regional_noa INT;
SELECT @nro_valor_regional_noa = nro_valor_dominio FROM dominio_categorias_preferencias 
WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Regional del NOA';

IF @nro_valor_regional_noa IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM preferencias_restaurantes 
                   WHERE nro_restaurante = @rest_4_uuid AND cod_categoria = @cat_tipo AND nro_valor_dominio = @nro_valor_regional_noa)
    BEGIN
        INSERT INTO preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        VALUES (@rest_4_uuid, @cat_tipo, @nro_valor_regional_noa, 1);
    END
END

-- Atributos de identidad
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @rest_4_uuid AND cod_atributo = @cod_atributo_tipo_cocina)
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@rest_4_uuid, @cod_atributo_tipo_cocina, N'Regional del NOA');

IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @rest_4_uuid AND cod_atributo = @cod_atributo_estilo_atencion)
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@rest_4_uuid, @cod_atributo_estilo_atencion, N'Tradicional y auténtico, ambiente cálido y familiar');

IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @rest_4_uuid AND cod_atributo = @cod_atributo_lenguaje)
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@rest_4_uuid, @cod_atributo_lenguaje, N'es-AR');

IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @rest_4_uuid AND cod_atributo = @cod_atributo_platos_emblematicos)
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@rest_4_uuid, @cod_atributo_platos_emblematicos, N'Locro, Empanadas salteñas, Humita, Tamales, Carbonada');

PRINT 'Restaurante 4 (Sabores del Norte) configurado completamente';

/* =========================================
   8) ZONAS PARA TODAS LAS SUCURSALES
   ========================================= */

-- Restaurante 1: La Bella Pizza
-- Sucursal 1.1: Alta Córdoba
-- UUIDs fijos de zonas del sistema del restaurante (deben coincidir con das_restaurante)
DECLARE @zona_salon_principal_uuid VARCHAR(36) = 'ZONA-SALON-PRINCIPAL-0001-0001-0001-0001';
DECLARE @zona_terraza_uuid VARCHAR(36) = 'ZONA-TERRAZA-0001-0001-0001-0001';
DECLARE @zona_patio_uuid VARCHAR(36) = 'ZONA-PATIO-0001-0001-0001-0001';

IF @suc_1_1_uuid IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_1_uuid AND desc_zona = N'Salón Principal')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada, cod_zona_restaurante)
        VALUES (@rest_1_uuid, @suc_1_1_uuid, N'Salón Principal', 50, 1, 1, @zona_salon_principal_uuid);
    END
    ELSE
    BEGIN
        UPDATE zonas_sucursales_restaurantes 
        SET cod_zona_restaurante = @zona_salon_principal_uuid
        WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_1_uuid 
          AND desc_zona = N'Salón Principal'
          AND (cod_zona_restaurante IS NULL OR cod_zona_restaurante != @zona_salon_principal_uuid);
    END
    
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_1_uuid AND desc_zona = N'Terraza')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada, cod_zona_restaurante)
        VALUES (@rest_1_uuid, @suc_1_1_uuid, N'Terraza', 30, 1, 1, @zona_terraza_uuid);
    END
    ELSE
    BEGIN
        UPDATE zonas_sucursales_restaurantes 
        SET cod_zona_restaurante = @zona_terraza_uuid
        WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_1_uuid 
          AND desc_zona = N'Terraza'
          AND (cod_zona_restaurante IS NULL OR cod_zona_restaurante != @zona_terraza_uuid);
    END
END

-- Sucursal 1.2: General Paz
IF @suc_1_2_uuid IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_2_uuid AND desc_zona = N'Salón Principal')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada, cod_zona_restaurante)
        VALUES (@rest_1_uuid, @suc_1_2_uuid, N'Salón Principal', 40, 1, 1, @zona_salon_principal_uuid);
    END
    ELSE
    BEGIN
        UPDATE zonas_sucursales_restaurantes 
        SET cod_zona_restaurante = @zona_salon_principal_uuid
        WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_2_uuid 
          AND desc_zona = N'Salón Principal'
          AND (cod_zona_restaurante IS NULL OR cod_zona_restaurante != @zona_salon_principal_uuid);
    END
    
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_2_uuid AND desc_zona = N'Patio')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada, cod_zona_restaurante)
        VALUES (@rest_1_uuid, @suc_1_2_uuid, N'Patio', 20, 1, 1, @zona_patio_uuid);
    END
    ELSE
    BEGIN
        UPDATE zonas_sucursales_restaurantes 
        SET cod_zona_restaurante = @zona_patio_uuid
        WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_2_uuid 
          AND desc_zona = N'Patio'
          AND (cod_zona_restaurante IS NULL OR cod_zona_restaurante != @zona_patio_uuid);
    END
END

-- Restaurante 2: Perukai
-- Sucursal 2.1: Nueva Córdoba
DECLARE @zona_barra_uuid VARCHAR(36) = 'ZONA-BARRA-0001-0001-0001-0001';

IF @suc_2_1_uuid IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_1_uuid AND desc_zona = N'Salón Principal')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada, cod_zona_restaurante)
        VALUES (@rest_2_uuid, @suc_2_1_uuid, N'Salón Principal', 70, 1, 1, @zona_salon_principal_uuid);
    END
    ELSE
    BEGIN
        UPDATE zonas_sucursales_restaurantes 
        SET cod_zona_restaurante = @zona_salon_principal_uuid
        WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_1_uuid 
          AND desc_zona = N'Salón Principal'
          AND (cod_zona_restaurante IS NULL OR cod_zona_restaurante != @zona_salon_principal_uuid);
    END
    
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_1_uuid AND desc_zona = N'Barra')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada, cod_zona_restaurante)
        VALUES (@rest_2_uuid, @suc_2_1_uuid, N'Barra', 30, 0, 1, @zona_barra_uuid);
    END
    ELSE
    BEGIN
        UPDATE zonas_sucursales_restaurantes 
        SET cod_zona_restaurante = @zona_barra_uuid
        WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_1_uuid 
          AND desc_zona = N'Barra'
          AND (cod_zona_restaurante IS NULL OR cod_zona_restaurante != @zona_barra_uuid);
    END
END

-- Sucursal 2.2: Güemes
IF @suc_2_2_uuid IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_2_uuid AND desc_zona = N'Salón Principal')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada, cod_zona_restaurante)
        VALUES (@rest_2_uuid, @suc_2_2_uuid, N'Salón Principal', 50, 1, 1, @zona_salon_principal_uuid);
    END
    ELSE
    BEGIN
        UPDATE zonas_sucursales_restaurantes 
        SET cod_zona_restaurante = @zona_salon_principal_uuid
        WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_2_uuid 
          AND desc_zona = N'Salón Principal'
          AND (cod_zona_restaurante IS NULL OR cod_zona_restaurante != @zona_salon_principal_uuid);
    END
    
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_2_uuid AND desc_zona = N'Terraza')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada, cod_zona_restaurante)
        VALUES (@rest_2_uuid, @suc_2_2_uuid, N'Terraza', 20, 1, 1, @zona_terraza_uuid);
    END
    ELSE
    BEGIN
        UPDATE zonas_sucursales_restaurantes 
        SET cod_zona_restaurante = @zona_terraza_uuid
        WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_2_uuid 
          AND desc_zona = N'Terraza'
          AND (cod_zona_restaurante IS NULL OR cod_zona_restaurante != @zona_terraza_uuid);
    END
END

-- Restaurante 3: La Fábrica Burger
-- Sucursal 3.1: Cerro de las Rosas
IF @suc_3_1_uuid IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @rest_3_uuid AND nro_sucursal = @suc_3_1_uuid AND desc_zona = N'Salón Principal')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada, cod_zona_restaurante)
        VALUES (@rest_3_uuid, @suc_3_1_uuid, N'Salón Principal', 60, 1, 1, @zona_salon_principal_uuid);
    END
    ELSE
    BEGIN
        UPDATE zonas_sucursales_restaurantes 
        SET cod_zona_restaurante = @zona_salon_principal_uuid
        WHERE nro_restaurante = @rest_3_uuid AND nro_sucursal = @suc_3_1_uuid 
          AND desc_zona = N'Salón Principal'
          AND (cod_zona_restaurante IS NULL OR cod_zona_restaurante != @zona_salon_principal_uuid);
    END
    
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @rest_3_uuid AND nro_sucursal = @suc_3_1_uuid AND desc_zona = N'Patio')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada, cod_zona_restaurante)
        VALUES (@rest_3_uuid, @suc_3_1_uuid, N'Patio', 30, 1, 1, @zona_patio_uuid);
    END
    ELSE
    BEGIN
        UPDATE zonas_sucursales_restaurantes 
        SET cod_zona_restaurante = @zona_patio_uuid
        WHERE nro_restaurante = @rest_3_uuid AND nro_sucursal = @suc_3_1_uuid 
          AND desc_zona = N'Patio'
          AND (cod_zona_restaurante IS NULL OR cod_zona_restaurante != @zona_patio_uuid);
    END
END

-- Restaurante 4: Sabores del Norte
-- Sucursal 4.1: Centro
DECLARE @zona_patio_cubierto_uuid VARCHAR(36) = 'ZONA-PATIO-CUBIERTO-0001-0001-0001-0001';

IF @suc_4_1_uuid IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_1_uuid AND desc_zona = N'Salón Principal')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada, cod_zona_restaurante)
        VALUES (@rest_4_uuid, @suc_4_1_uuid, N'Salón Principal', 80, 1, 1, @zona_salon_principal_uuid);
    END
    ELSE
    BEGIN
        UPDATE zonas_sucursales_restaurantes 
        SET cod_zona_restaurante = @zona_salon_principal_uuid
        WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_1_uuid 
          AND desc_zona = N'Salón Principal'
          AND (cod_zona_restaurante IS NULL OR cod_zona_restaurante != @zona_salon_principal_uuid);
    END
    
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_1_uuid AND desc_zona = N'Patio Cubierto')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada, cod_zona_restaurante)
        VALUES (@rest_4_uuid, @suc_4_1_uuid, N'Patio Cubierto', 30, 1, 1, @zona_patio_cubierto_uuid);
    END
    ELSE
    BEGIN
        UPDATE zonas_sucursales_restaurantes 
        SET cod_zona_restaurante = @zona_patio_cubierto_uuid
        WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_1_uuid 
          AND desc_zona = N'Patio Cubierto'
          AND (cod_zona_restaurante IS NULL OR cod_zona_restaurante != @zona_patio_cubierto_uuid);
    END
END

-- Sucursal 4.2: Cerro de las Rosas
IF @suc_4_2_uuid IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_2_uuid AND desc_zona = N'Salón Principal')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada, cod_zona_restaurante)
        VALUES (@rest_4_uuid, @suc_4_2_uuid, N'Salón Principal', 60, 1, 1, @zona_salon_principal_uuid);
    END
    ELSE
    BEGIN
        UPDATE zonas_sucursales_restaurantes 
        SET cod_zona_restaurante = @zona_salon_principal_uuid
        WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_2_uuid 
          AND desc_zona = N'Salón Principal'
          AND (cod_zona_restaurante IS NULL OR cod_zona_restaurante != @zona_salon_principal_uuid);
    END
    
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_2_uuid AND desc_zona = N'Terraza')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada, cod_zona_restaurante)
        VALUES (@rest_4_uuid, @suc_4_2_uuid, N'Terraza', 25, 1, 1, @zona_terraza_uuid);
    END
    ELSE
    BEGIN
        UPDATE zonas_sucursales_restaurantes 
        SET cod_zona_restaurante = @zona_terraza_uuid
        WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_2_uuid 
          AND desc_zona = N'Terraza'
          AND (cod_zona_restaurante IS NULL OR cod_zona_restaurante != @zona_terraza_uuid);
    END
END

PRINT 'Zonas insertadas para todas las sucursales';

/* =========================================
   9) TURNOS PARA TODAS LAS SUCURSALES
   ========================================= */

DECLARE @hora TIME;
DECLARE @hora_hasta TIME;
DECLARE @i INT;

-- Restaurante 1: La Bella Pizza - Sucursal 1.1 (Alta Córdoba)
IF @suc_1_1_uuid IS NOT NULL
BEGIN
    SET @hora = '19:00';
    SET @i = 0;
    WHILE @i < 4
    BEGIN
        SET @hora_hasta = CAST(DATEADD(MINUTE, 120, CAST(@hora AS DATETIME)) AS TIME);
        IF NOT EXISTS (SELECT 1 FROM turnos_sucursales_restaurantes 
                       WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_1_uuid AND hora_desde = @hora)
        BEGIN
            INSERT INTO turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
            VALUES (@rest_1_uuid, @suc_1_1_uuid, @hora, @hora_hasta, 1);
        END
        SET @hora = @hora_hasta;
        SET @i = @i + 1;
    END
END

-- Restaurante 1: La Bella Pizza - Sucursal 1.2 (General Paz)
IF @suc_1_2_uuid IS NOT NULL
BEGIN
    SET @hora = '19:00';
    SET @i = 0;
    WHILE @i < 3
    BEGIN
        SET @hora_hasta = CAST(DATEADD(MINUTE, 120, CAST(@hora AS DATETIME)) AS TIME);
        IF NOT EXISTS (SELECT 1 FROM turnos_sucursales_restaurantes 
                       WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_2_uuid AND hora_desde = @hora)
        BEGIN
            INSERT INTO turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
            VALUES (@rest_1_uuid, @suc_1_2_uuid, @hora, @hora_hasta, 1);
        END
        SET @hora = @hora_hasta;
        SET @i = @i + 1;
    END
END

-- Restaurante 2: Perukai - Sucursal 2.1 (Nueva Córdoba)
IF @suc_2_1_uuid IS NOT NULL
BEGIN
    SET @hora = '20:00';
    SET @i = 0;
    WHILE @i < 3
    BEGIN
        SET @hora_hasta = CAST(DATEADD(MINUTE, 120, CAST(@hora AS DATETIME)) AS TIME);
        IF NOT EXISTS (SELECT 1 FROM turnos_sucursales_restaurantes 
                       WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_1_uuid AND hora_desde = @hora)
        BEGIN
            INSERT INTO turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
            VALUES (@rest_2_uuid, @suc_2_1_uuid, @hora, @hora_hasta, 1);
        END
        SET @hora = @hora_hasta;
        SET @i = @i + 1;
    END
END

-- Restaurante 2: Perukai - Sucursal 2.2 (Güemes)
IF @suc_2_2_uuid IS NOT NULL
BEGIN
    SET @hora = '20:00';
    SET @i = 0;
    WHILE @i < 3
    BEGIN
        SET @hora_hasta = CAST(DATEADD(MINUTE, 120, CAST(@hora AS DATETIME)) AS TIME);
        IF NOT EXISTS (SELECT 1 FROM turnos_sucursales_restaurantes 
                       WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_2_uuid AND hora_desde = @hora)
        BEGIN
            INSERT INTO turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
            VALUES (@rest_2_uuid, @suc_2_2_uuid, @hora, @hora_hasta, 1);
        END
        SET @hora = @hora_hasta;
        SET @i = @i + 1;
    END
END

-- Restaurante 3: La Fábrica Burger - Sucursal 3.1 (Cerro de las Rosas)
IF @suc_3_1_uuid IS NOT NULL
BEGIN
    SET @hora = '19:00';
    SET @i = 0;
    WHILE @i < 4
    BEGIN
        SET @hora_hasta = CAST(DATEADD(MINUTE, 120, CAST(@hora AS DATETIME)) AS TIME);
        IF NOT EXISTS (SELECT 1 FROM turnos_sucursales_restaurantes 
                       WHERE nro_restaurante = @rest_3_uuid AND nro_sucursal = @suc_3_1_uuid AND hora_desde = @hora)
        BEGIN
            INSERT INTO turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
            VALUES (@rest_3_uuid, @suc_3_1_uuid, @hora, @hora_hasta, 1);
        END
        SET @hora = @hora_hasta;
        SET @i = @i + 1;
    END
END

-- Restaurante 4: Sabores del Norte - Sucursal 4.1 (Centro)
IF @suc_4_1_uuid IS NOT NULL
BEGIN
    SET @hora = '19:00';
    SET @i = 0;
    WHILE @i < 4
    BEGIN
        SET @hora_hasta = CAST(DATEADD(MINUTE, 120, CAST(@hora AS DATETIME)) AS TIME);
        IF NOT EXISTS (SELECT 1 FROM turnos_sucursales_restaurantes 
                       WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_1_uuid AND hora_desde = @hora)
        BEGIN
            INSERT INTO turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
            VALUES (@rest_4_uuid, @suc_4_1_uuid, @hora, @hora_hasta, 1);
        END
        SET @hora = @hora_hasta;
        SET @i = @i + 1;
    END
END

-- Restaurante 4: Sabores del Norte - Sucursal 4.2 (Cerro de las Rosas)
IF @suc_4_2_uuid IS NOT NULL
BEGIN
    SET @hora = '19:00';
    SET @i = 0;
    WHILE @i < 3
    BEGIN
        SET @hora_hasta = CAST(DATEADD(MINUTE, 120, CAST(@hora AS DATETIME)) AS TIME);
        IF NOT EXISTS (SELECT 1 FROM turnos_sucursales_restaurantes 
                       WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_2_uuid AND hora_desde = @hora)
        BEGIN
            INSERT INTO turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
            VALUES (@rest_4_uuid, @suc_4_2_uuid, @hora, @hora_hasta, 1);
        END
        SET @hora = @hora_hasta;
        SET @i = @i + 1;
    END
END

PRINT 'Turnos insertados para todas las sucursales';

/* =========================================
   10) ZONAS POR TURNO (Habilitar zonas en turnos)
   ========================================= */

-- Restaurante 1: La Bella Pizza - Sucursal 1.1
IF @suc_1_1_uuid IS NOT NULL
BEGIN
    INSERT INTO zonas_turnos_sucurales_restaurantes
           (nro_restaurante, nro_sucursal, cod_zona, hora_desde, permite_menores)
    SELECT t.nro_restaurante, t.nro_sucursal, zsr.cod_zona, t.hora_desde, zsr.permite_menores
    FROM turnos_sucursales_restaurantes t
    JOIN zonas_sucursales_restaurantes zsr
      ON zsr.nro_restaurante = t.nro_restaurante AND zsr.nro_sucursal = t.nro_sucursal
    LEFT JOIN zonas_turnos_sucurales_restaurantes ztr
      ON ztr.nro_restaurante = t.nro_restaurante 
     AND ztr.nro_sucursal = t.nro_sucursal
     AND ztr.cod_zona = zsr.cod_zona 
     AND ztr.hora_desde = t.hora_desde
    WHERE t.nro_restaurante = @rest_1_uuid 
      AND t.nro_sucursal = @suc_1_1_uuid
      AND ztr.nro_restaurante IS NULL;
END

-- Restaurante 1: La Bella Pizza - Sucursal 1.2
IF @suc_1_2_uuid IS NOT NULL
BEGIN
    INSERT INTO zonas_turnos_sucurales_restaurantes
           (nro_restaurante, nro_sucursal, cod_zona, hora_desde, permite_menores)
    SELECT t.nro_restaurante, t.nro_sucursal, zsr.cod_zona, t.hora_desde, zsr.permite_menores
    FROM turnos_sucursales_restaurantes t
    JOIN zonas_sucursales_restaurantes zsr
      ON zsr.nro_restaurante = t.nro_restaurante AND zsr.nro_sucursal = t.nro_sucursal
    LEFT JOIN zonas_turnos_sucurales_restaurantes ztr
      ON ztr.nro_restaurante = t.nro_restaurante 
     AND ztr.nro_sucursal = t.nro_sucursal
     AND ztr.cod_zona = zsr.cod_zona 
     AND ztr.hora_desde = t.hora_desde
    WHERE t.nro_restaurante = @rest_1_uuid 
      AND t.nro_sucursal = @suc_1_2_uuid
      AND ztr.nro_restaurante IS NULL;
END

-- Restaurante 2: Perukai - Sucursal 2.1
IF @suc_2_1_uuid IS NOT NULL
BEGIN
    INSERT INTO zonas_turnos_sucurales_restaurantes
           (nro_restaurante, nro_sucursal, cod_zona, hora_desde, permite_menores)
    SELECT t.nro_restaurante, t.nro_sucursal, zsr.cod_zona, t.hora_desde, zsr.permite_menores
    FROM turnos_sucursales_restaurantes t
    JOIN zonas_sucursales_restaurantes zsr
      ON zsr.nro_restaurante = t.nro_restaurante AND zsr.nro_sucursal = t.nro_sucursal
    LEFT JOIN zonas_turnos_sucurales_restaurantes ztr
      ON ztr.nro_restaurante = t.nro_restaurante 
     AND ztr.nro_sucursal = t.nro_sucursal
     AND ztr.cod_zona = zsr.cod_zona 
     AND ztr.hora_desde = t.hora_desde
    WHERE t.nro_restaurante = @rest_2_uuid 
      AND t.nro_sucursal = @suc_2_1_uuid
      AND ztr.nro_restaurante IS NULL;
END

-- Restaurante 2: Perukai - Sucursal 2.2
IF @suc_2_2_uuid IS NOT NULL
BEGIN
    INSERT INTO zonas_turnos_sucurales_restaurantes
           (nro_restaurante, nro_sucursal, cod_zona, hora_desde, permite_menores)
    SELECT t.nro_restaurante, t.nro_sucursal, zsr.cod_zona, t.hora_desde, zsr.permite_menores
    FROM turnos_sucursales_restaurantes t
    JOIN zonas_sucursales_restaurantes zsr
      ON zsr.nro_restaurante = t.nro_restaurante AND zsr.nro_sucursal = t.nro_sucursal
    LEFT JOIN zonas_turnos_sucurales_restaurantes ztr
      ON ztr.nro_restaurante = t.nro_restaurante 
     AND ztr.nro_sucursal = t.nro_sucursal
     AND ztr.cod_zona = zsr.cod_zona 
     AND ztr.hora_desde = t.hora_desde
    WHERE t.nro_restaurante = @rest_2_uuid 
      AND t.nro_sucursal = @suc_2_2_uuid
      AND ztr.nro_restaurante IS NULL;
END

-- Restaurante 3: La Fábrica Burger - Sucursal 3.1
IF @suc_3_1_uuid IS NOT NULL
BEGIN
    INSERT INTO zonas_turnos_sucurales_restaurantes
           (nro_restaurante, nro_sucursal, cod_zona, hora_desde, permite_menores)
    SELECT t.nro_restaurante, t.nro_sucursal, zsr.cod_zona, t.hora_desde, zsr.permite_menores
    FROM turnos_sucursales_restaurantes t
    JOIN zonas_sucursales_restaurantes zsr
      ON zsr.nro_restaurante = t.nro_restaurante AND zsr.nro_sucursal = t.nro_sucursal
    LEFT JOIN zonas_turnos_sucurales_restaurantes ztr
      ON ztr.nro_restaurante = t.nro_restaurante 
     AND ztr.nro_sucursal = t.nro_sucursal
     AND ztr.cod_zona = zsr.cod_zona 
     AND ztr.hora_desde = t.hora_desde
    WHERE t.nro_restaurante = @rest_3_uuid 
      AND t.nro_sucursal = @suc_3_1_uuid
      AND ztr.nro_restaurante IS NULL;
END

-- Restaurante 4: Sabores del Norte - Sucursal 4.1
IF @suc_4_1_uuid IS NOT NULL
BEGIN
    INSERT INTO zonas_turnos_sucurales_restaurantes
           (nro_restaurante, nro_sucursal, cod_zona, hora_desde, permite_menores)
    SELECT t.nro_restaurante, t.nro_sucursal, zsr.cod_zona, t.hora_desde, zsr.permite_menores
    FROM turnos_sucursales_restaurantes t
    JOIN zonas_sucursales_restaurantes zsr
      ON zsr.nro_restaurante = t.nro_restaurante AND zsr.nro_sucursal = t.nro_sucursal
    LEFT JOIN zonas_turnos_sucurales_restaurantes ztr
      ON ztr.nro_restaurante = t.nro_restaurante 
     AND ztr.nro_sucursal = t.nro_sucursal
     AND ztr.cod_zona = zsr.cod_zona 
     AND ztr.hora_desde = t.hora_desde
    WHERE t.nro_restaurante = @rest_4_uuid 
      AND t.nro_sucursal = @suc_4_1_uuid
      AND ztr.nro_restaurante IS NULL;
END

-- Restaurante 4: Sabores del Norte - Sucursal 4.2
IF @suc_4_2_uuid IS NOT NULL
BEGIN
    INSERT INTO zonas_turnos_sucurales_restaurantes
           (nro_restaurante, nro_sucursal, cod_zona, hora_desde, permite_menores)
    SELECT t.nro_restaurante, t.nro_sucursal, zsr.cod_zona, t.hora_desde, zsr.permite_menores
    FROM turnos_sucursales_restaurantes t
    JOIN zonas_sucursales_restaurantes zsr
      ON zsr.nro_restaurante = t.nro_restaurante AND zsr.nro_sucursal = t.nro_sucursal
    LEFT JOIN zonas_turnos_sucurales_restaurantes ztr
      ON ztr.nro_restaurante = t.nro_restaurante 
     AND ztr.nro_sucursal = t.nro_sucursal
     AND ztr.cod_zona = zsr.cod_zona 
     AND ztr.hora_desde = t.hora_desde
    WHERE t.nro_restaurante = @rest_4_uuid 
      AND t.nro_sucursal = @suc_4_2_uuid
      AND ztr.nro_restaurante IS NULL;
END

PRINT 'Zonas habilitadas en turnos para todas las sucursales';

/* =========================================
   11) TRADUCCIONES DE ZONAS (opcional pero útil para i18n)
   ========================================= */

DECLARE @nro_idioma_es INT;
SELECT @nro_idioma_es = nro_idioma FROM idiomas WHERE cod_idioma = N'es-AR';

IF @nro_idioma_es IS NOT NULL
BEGIN
    -- Traducciones para todas las zonas de todos los restaurantes
    DECLARE @cod_zona VARCHAR(36);
    
    -- Restaurante 1: La Bella Pizza - Sucursal 1.1
    IF @suc_1_1_uuid IS NOT NULL
    BEGIN
        SELECT @cod_zona = cod_zona FROM zonas_sucursales_restaurantes 
        WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_1_uuid AND desc_zona = N'Salón Principal';
        IF @cod_zona IS NOT NULL AND NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                                                 WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_1_uuid 
                                                 AND cod_zona = @cod_zona AND nro_idioma = @nro_idioma_es)
            INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
            VALUES (@rest_1_uuid, @suc_1_1_uuid, @cod_zona, @nro_idioma_es, N'Salón Principal', N'Salón principal con capacidad para 50 comensales');
        
        SELECT @cod_zona = cod_zona FROM zonas_sucursales_restaurantes 
        WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_1_uuid AND desc_zona = N'Terraza';
        IF @cod_zona IS NOT NULL AND NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                                                 WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_1_uuid 
                                                 AND cod_zona = @cod_zona AND nro_idioma = @nro_idioma_es)
            INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
            VALUES (@rest_1_uuid, @suc_1_1_uuid, @cod_zona, @nro_idioma_es, N'Terraza', N'Terraza al aire libre con capacidad para 30 comensales');
    END
    
    -- Restaurante 1: La Bella Pizza - Sucursal 1.2
    IF @suc_1_2_uuid IS NOT NULL
    BEGIN
        SELECT @cod_zona = cod_zona FROM zonas_sucursales_restaurantes 
        WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_2_uuid AND desc_zona = N'Salón Principal';
        IF @cod_zona IS NOT NULL AND NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                                                 WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_2_uuid 
                                                 AND cod_zona = @cod_zona AND nro_idioma = @nro_idioma_es)
            INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
            VALUES (@rest_1_uuid, @suc_1_2_uuid, @cod_zona, @nro_idioma_es, N'Salón Principal', N'Salón principal con capacidad para 40 comensales');
        
        SELECT @cod_zona = cod_zona FROM zonas_sucursales_restaurantes 
        WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_2_uuid AND desc_zona = N'Patio';
        IF @cod_zona IS NOT NULL AND NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                                                 WHERE nro_restaurante = @rest_1_uuid AND nro_sucursal = @suc_1_2_uuid 
                                                 AND cod_zona = @cod_zona AND nro_idioma = @nro_idioma_es)
            INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
            VALUES (@rest_1_uuid, @suc_1_2_uuid, @cod_zona, @nro_idioma_es, N'Patio', N'Patio con capacidad para 20 comensales');
    END
    
    -- Restaurante 2: Perukai - Sucursal 2.1
    IF @suc_2_1_uuid IS NOT NULL
    BEGIN
        SELECT @cod_zona = cod_zona FROM zonas_sucursales_restaurantes 
        WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_1_uuid AND desc_zona = N'Salón Principal';
        IF @cod_zona IS NOT NULL AND NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                                                 WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_1_uuid 
                                                 AND cod_zona = @cod_zona AND nro_idioma = @nro_idioma_es)
            INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
            VALUES (@rest_2_uuid, @suc_2_1_uuid, @cod_zona, @nro_idioma_es, N'Salón Principal', N'Salón principal con capacidad para 70 comensales');
        
        SELECT @cod_zona = cod_zona FROM zonas_sucursales_restaurantes 
        WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_1_uuid AND desc_zona = N'Barra';
        IF @cod_zona IS NOT NULL AND NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                                                 WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_1_uuid 
                                                 AND cod_zona = @cod_zona AND nro_idioma = @nro_idioma_es)
            INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
            VALUES (@rest_2_uuid, @suc_2_1_uuid, @cod_zona, @nro_idioma_es, N'Barra', N'Barra con capacidad para 30 comensales (solo adultos)');
    END
    
    -- Restaurante 2: Perukai - Sucursal 2.2
    IF @suc_2_2_uuid IS NOT NULL
    BEGIN
        SELECT @cod_zona = cod_zona FROM zonas_sucursales_restaurantes 
        WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_2_uuid AND desc_zona = N'Salón Principal';
        IF @cod_zona IS NOT NULL AND NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                                                 WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_2_uuid 
                                                 AND cod_zona = @cod_zona AND nro_idioma = @nro_idioma_es)
            INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
            VALUES (@rest_2_uuid, @suc_2_2_uuid, @cod_zona, @nro_idioma_es, N'Salón Principal', N'Salón principal con capacidad para 50 comensales');
        
        SELECT @cod_zona = cod_zona FROM zonas_sucursales_restaurantes 
        WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_2_uuid AND desc_zona = N'Terraza';
        IF @cod_zona IS NOT NULL AND NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                                                 WHERE nro_restaurante = @rest_2_uuid AND nro_sucursal = @suc_2_2_uuid 
                                                 AND cod_zona = @cod_zona AND nro_idioma = @nro_idioma_es)
            INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
            VALUES (@rest_2_uuid, @suc_2_2_uuid, @cod_zona, @nro_idioma_es, N'Terraza', N'Terraza al aire libre con capacidad para 20 comensales');
    END
    
    -- Restaurante 3: La Fábrica Burger - Sucursal 3.1
    IF @suc_3_1_uuid IS NOT NULL
    BEGIN
        SELECT @cod_zona = cod_zona FROM zonas_sucursales_restaurantes 
        WHERE nro_restaurante = @rest_3_uuid AND nro_sucursal = @suc_3_1_uuid AND desc_zona = N'Salón Principal';
        IF @cod_zona IS NOT NULL AND NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                                                 WHERE nro_restaurante = @rest_3_uuid AND nro_sucursal = @suc_3_1_uuid 
                                                 AND cod_zona = @cod_zona AND nro_idioma = @nro_idioma_es)
            INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
            VALUES (@rest_3_uuid, @suc_3_1_uuid, @cod_zona, @nro_idioma_es, N'Salón Principal', N'Salón principal con capacidad para 60 comensales');
        
        SELECT @cod_zona = cod_zona FROM zonas_sucursales_restaurantes 
        WHERE nro_restaurante = @rest_3_uuid AND nro_sucursal = @suc_3_1_uuid AND desc_zona = N'Patio';
        IF @cod_zona IS NOT NULL AND NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                                                 WHERE nro_restaurante = @rest_3_uuid AND nro_sucursal = @suc_3_1_uuid 
                                                 AND cod_zona = @cod_zona AND nro_idioma = @nro_idioma_es)
            INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
            VALUES (@rest_3_uuid, @suc_3_1_uuid, @cod_zona, @nro_idioma_es, N'Patio', N'Patio con capacidad para 30 comensales');
    END
    
    -- Restaurante 4: Sabores del Norte - Sucursal 4.1
    IF @suc_4_1_uuid IS NOT NULL
    BEGIN
        SELECT @cod_zona = cod_zona FROM zonas_sucursales_restaurantes 
        WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_1_uuid AND desc_zona = N'Salón Principal';
        IF @cod_zona IS NOT NULL AND NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                                                 WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_1_uuid 
                                                 AND cod_zona = @cod_zona AND nro_idioma = @nro_idioma_es)
            INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
            VALUES (@rest_4_uuid, @suc_4_1_uuid, @cod_zona, @nro_idioma_es, N'Salón Principal', N'Salón principal con capacidad para 80 comensales');
        
        SELECT @cod_zona = cod_zona FROM zonas_sucursales_restaurantes 
        WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_1_uuid AND desc_zona = N'Patio Cubierto';
        IF @cod_zona IS NOT NULL AND NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                                                 WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_1_uuid 
                                                 AND cod_zona = @cod_zona AND nro_idioma = @nro_idioma_es)
            INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
            VALUES (@rest_4_uuid, @suc_4_1_uuid, @cod_zona, @nro_idioma_es, N'Patio Cubierto', N'Patio cubierto con capacidad para 30 comensales');
    END
    
    -- Restaurante 4: Sabores del Norte - Sucursal 4.2
    IF @suc_4_2_uuid IS NOT NULL
    BEGIN
        SELECT @cod_zona = cod_zona FROM zonas_sucursales_restaurantes 
        WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_2_uuid AND desc_zona = N'Salón Principal';
        IF @cod_zona IS NOT NULL AND NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                                                 WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_2_uuid 
                                                 AND cod_zona = @cod_zona AND nro_idioma = @nro_idioma_es)
            INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
            VALUES (@rest_4_uuid, @suc_4_2_uuid, @cod_zona, @nro_idioma_es, N'Salón Principal', N'Salón principal con capacidad para 60 comensales');
        
        SELECT @cod_zona = cod_zona FROM zonas_sucursales_restaurantes 
        WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_2_uuid AND desc_zona = N'Terraza';
        IF @cod_zona IS NOT NULL AND NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                                                 WHERE nro_restaurante = @rest_4_uuid AND nro_sucursal = @suc_4_2_uuid 
                                                 AND cod_zona = @cod_zona AND nro_idioma = @nro_idioma_es)
            INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
            VALUES (@rest_4_uuid, @suc_4_2_uuid, @cod_zona, @nro_idioma_es, N'Terraza', N'Terraza al aire libre con capacidad para 25 comensales');
    END
END

PRINT 'Traducciones de zonas insertadas para todos los restaurantes';

/* =========================================
   Resumen
   ========================================= */

PRINT '';
PRINT '========================================';
PRINT 'RESTAURANTES DEL EXAMEN FINAL INSERTADOS';
PRINT '========================================';
PRINT '';
PRINT '1. La Bella Pizza (REST)';
PRINT '   - Protocolo: REST';
PRINT '   - URL: http://localhost:8082/api';
PRINT '   - Tipo de comida: Italiana tradicional';
PRINT '   - Sucursales: Alta Córdoba, General Paz';
PRINT '';
PRINT '2. Perukai (SOAP)';
PRINT '   - Protocolo: SOAP';
PRINT '   - URL: http://localhost:8081/ws/restaurantes.wsdl';
PRINT '   - Tipo de comida: Fusión japonesa-peruana';
PRINT '   - Sucursales: Nueva Córdoba, Güemes';
PRINT '';
PRINT '3. La Fábrica Burger (REST)';
PRINT '   - Protocolo: REST';
PRINT '   - URL: http://localhost:8082/api';
PRINT '   - Tipo de comida: Fast food gourmet';
PRINT '   - Sucursales: Cerro de las Rosas';
PRINT '';
PRINT '4. Sabores del Norte (SOAP)';
PRINT '   - Protocolo: SOAP';
PRINT '   - URL: http://localhost:8081/ws/restaurantes.wsdl';
PRINT '   - Tipo de comida: Regional del NOA';
PRINT '   - Sucursales: Centro, Cerro de las Rosas';
PRINT '';
PRINT 'Todos los restaurantes incluyen:';
PRINT '  - Identidad gastronómica (tipo de cocina, estilo de atención, platos emblemáticos)';
PRINT '  - Lenguaje preferido (es-AR)';
PRINT '  - Preferencias de tipo de comida';
PRINT '  - Sucursales con direcciones completas y cod_sucursal_restaurante';
PRINT '  - Zonas por sucursal (2 zonas por sucursal) con cod_zona_restaurante';
PRINT '  - Turnos habilitados (3-4 turnos por sucursal, 2 horas cada uno)';
PRINT '  - Zonas habilitadas en turnos';
PRINT '  - Traducciones de zonas completas (español)';
PRINT '========================================';
PRINT '';
PRINT 'Resumen de datos insertados:';
PRINT '  - 4 restaurantes (con tipo_protocolo y url_servicio)';
PRINT '  - 7 sucursales (con cod_sucursal_restaurante)';
PRINT '  - 14 zonas (con cod_zona_restaurante)';
PRINT '  - 24 turnos';
PRINT '  - 48 relaciones zona-turno';
PRINT '  - 14 traducciones de zonas (español)';
PRINT '';
PRINT 'IMPORTANTE:';
PRINT '  - cod_sucursal_restaurante: Código usado por el sistema SOAP/REST del restaurante';
PRINT '  - cod_zona_restaurante: Código usado por el sistema SOAP/REST del restaurante';
PRINT '  - Estos códigos deben coincidir con los códigos en el sistema externo del restaurante';
PRINT '========================================';
GO

