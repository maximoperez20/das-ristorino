/* =========================================================================================
   INSERT DE ATRIBUTOS DE IDENTIDAD GASTRONÓMICA Y COMUNICACIONAL
   Incluye: atributos base y configuraciones de ejemplo para los restaurantes
   ========================================================================================= */

SET NOCOUNT ON;
GO

USE das_ristorino;
GO

/* =========================================
   1) Crear atributos base
   ========================================= */

DECLARE @cod_atributo_tipo_cocina VARCHAR(36);
DECLARE @cod_atributo_especialidades VARCHAR(36);
DECLARE @cod_atributo_estilo VARCHAR(36);
DECLARE @cod_atributo_nivel_precio VARCHAR(36);
DECLARE @cod_atributo_estilo_atencion VARCHAR(36);
DECLARE @cod_atributo_platos_emblematicos VARCHAR(36);

-- Atributo: Tipo de cocina
IF NOT EXISTS (SELECT 1 FROM atributos WHERE nom_atributo = N'Tipo de cocina')
BEGIN
    SET @cod_atributo_tipo_cocina = NEWID();
    INSERT INTO atributos (cod_atributo, nom_atributo, tipo_dato)
    VALUES (@cod_atributo_tipo_cocina, N'Tipo de cocina', 'string');
    PRINT 'Atributo "Tipo de cocina" creado: ' + @cod_atributo_tipo_cocina;
END
ELSE
BEGIN
    SELECT @cod_atributo_tipo_cocina = cod_atributo FROM atributos WHERE nom_atributo = N'Tipo de cocina';
    PRINT 'Atributo "Tipo de cocina" ya existe: ' + @cod_atributo_tipo_cocina;
END

-- Atributo: Especialidades alimentarias
IF NOT EXISTS (SELECT 1 FROM atributos WHERE nom_atributo = N'Especialidades alimentarias')
BEGIN
    SET @cod_atributo_especialidades = NEWID();
    INSERT INTO atributos (cod_atributo, nom_atributo, tipo_dato)
    VALUES (@cod_atributo_especialidades, N'Especialidades alimentarias', 'string');
    PRINT 'Atributo "Especialidades alimentarias" creado: ' + @cod_atributo_especialidades;
END
ELSE
BEGIN
    SELECT @cod_atributo_especialidades = cod_atributo FROM atributos WHERE nom_atributo = N'Especialidades alimentarias';
    PRINT 'Atributo "Especialidades alimentarias" ya existe: ' + @cod_atributo_especialidades;
END

-- Atributo: Estilo
IF NOT EXISTS (SELECT 1 FROM atributos WHERE nom_atributo = N'Estilo')
BEGIN
    SET @cod_atributo_estilo = NEWID();
    INSERT INTO atributos (cod_atributo, nom_atributo, tipo_dato)
    VALUES (@cod_atributo_estilo, N'Estilo', 'string');
    PRINT 'Atributo "Estilo" creado: ' + @cod_atributo_estilo;
END
ELSE
BEGIN
    SELECT @cod_atributo_estilo = cod_atributo FROM atributos WHERE nom_atributo = N'Estilo';
    PRINT 'Atributo "Estilo" ya existe: ' + @cod_atributo_estilo;
END

-- Atributo: Nivel de precio
IF NOT EXISTS (SELECT 1 FROM atributos WHERE nom_atributo = N'Nivel de precio')
BEGIN
    SET @cod_atributo_nivel_precio = NEWID();
    INSERT INTO atributos (cod_atributo, nom_atributo, tipo_dato)
    VALUES (@cod_atributo_nivel_precio, N'Nivel de precio', 'string');
    PRINT 'Atributo "Nivel de precio" creado: ' + @cod_atributo_nivel_precio;
END
ELSE
BEGIN
    SELECT @cod_atributo_nivel_precio = cod_atributo FROM atributos WHERE nom_atributo = N'Nivel de precio';
    PRINT 'Atributo "Nivel de precio" ya existe: ' + @cod_atributo_nivel_precio;
END

-- Atributo: Estilo de atención (mantener para compatibilidad)
IF NOT EXISTS (SELECT 1 FROM atributos WHERE nom_atributo = N'Estilo de atención')
BEGIN
    SET @cod_atributo_estilo_atencion = NEWID();
    INSERT INTO atributos (cod_atributo, nom_atributo, tipo_dato)
    VALUES (@cod_atributo_estilo_atencion, N'Estilo de atención', 'string');
    PRINT 'Atributo "Estilo de atención" creado: ' + @cod_atributo_estilo_atencion;
END
ELSE
BEGIN
    SELECT @cod_atributo_estilo_atencion = cod_atributo FROM atributos WHERE nom_atributo = N'Estilo de atención';
    PRINT 'Atributo "Estilo de atención" ya existe: ' + @cod_atributo_estilo_atencion;
END

-- Atributo: Platos emblemáticos (mantener para compatibilidad)
IF NOT EXISTS (SELECT 1 FROM atributos WHERE nom_atributo = N'Platos emblemáticos')
BEGIN
    SET @cod_atributo_platos_emblematicos = NEWID();
    INSERT INTO atributos (cod_atributo, nom_atributo, tipo_dato)
    VALUES (@cod_atributo_platos_emblematicos, N'Platos emblemáticos', 'string');
    PRINT 'Atributo "Platos emblemáticos" creado: ' + @cod_atributo_platos_emblematicos;
END
ELSE
BEGIN
    SELECT @cod_atributo_platos_emblematicos = cod_atributo FROM atributos WHERE nom_atributo = N'Platos emblemáticos';
    PRINT 'Atributo "Platos emblemáticos" ya existe: ' + @cod_atributo_platos_emblematicos;
END

/* =========================================
   2) Valores disponibles para cada atributo
   ========================================= */

/*
   VALORES DISPONIBLES PARA "Tipo de cocina":
   - Italiana
   - Mexicana
   - Española
   - Francesa
   - Japonesa
   - China
   - Tailandesa
   - India
   - Mediterránea
   - Argentina
   - Peruana
   - Árabe / Medio Oriente
   - Americana
   - Fusión
   - Internacional

   VALORES DISPONIBLES PARA "Especialidades alimentarias":
   - Vegetariana
   - Vegana
   - Sin gluten / Celíaco
   - Sin lactosa
   - Baja en calorías
   - Orgánica
   - Diabéticos (sin azúcar añadida)

   VALORES DISPONIBLES PARA "Estilo":
   - Gourmet
   - Casual
   - Comida rápida / Fast food
   - Buffet libre
   - Bistró
   - Food truck
   - Restaurante tradicional
   - Bar / Tapas
   - Cafetería
   - Delivery
   - Fine dining

   VALORES DISPONIBLES PARA "Nivel de precio":
   - Económico / Bajo
   - Medio
   - Alto / Premium
   - De lujo
*/

/* =========================================
   3) Configuraciones de ejemplo para restaurantes
   ========================================= */

-- Restaurante 1: Los Aroza SRL (Restaurante general/argentino)
DECLARE @restaurante_1_uuid VARCHAR(36) = '12345678-1234-1234-1234-123456789abc';

-- Tipo de cocina
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @restaurante_1_uuid AND cod_atributo = @cod_atributo_tipo_cocina)
BEGIN
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@restaurante_1_uuid, @cod_atributo_tipo_cocina, N'Argentina, Mediterránea');
    PRINT 'Configuración "Tipo de cocina" agregada para Los Aroza SRL';
END

-- Estilo
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @restaurante_1_uuid AND cod_atributo = @cod_atributo_estilo)
BEGIN
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@restaurante_1_uuid, @cod_atributo_estilo, N'Fine dining');
    PRINT 'Configuración "Estilo" agregada para Los Aroza SRL';
END

-- Nivel de precio
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @restaurante_1_uuid AND cod_atributo = @cod_atributo_nivel_precio)
BEGIN
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@restaurante_1_uuid, @cod_atributo_nivel_precio, N'Alto / Premium');
    PRINT 'Configuración "Nivel de precio" agregada para Los Aroza SRL';
END

-- Estilo de atención (mantener para compatibilidad)
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @restaurante_1_uuid AND cod_atributo = @cod_atributo_estilo_atencion)
BEGIN
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@restaurante_1_uuid, @cod_atributo_estilo_atencion, N'Formal y elegante, con servicio personalizado');
    PRINT 'Configuración "Estilo de atención" agregada para Los Aroza SRL';
END

-- Platos emblemáticos (mantener para compatibilidad)
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @restaurante_1_uuid AND cod_atributo = @cod_atributo_platos_emblematicos)
BEGIN
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@restaurante_1_uuid, @cod_atributo_platos_emblematicos, N'Bife de chorizo a la parrilla, Risotto de hongos, Tiramisú casero');
    PRINT 'Configuración "Platos emblemáticos" agregada para Los Aroza SRL';
END

-- Restaurante 2: Parrilla La Esquina SRL (Parrilla argentina)
DECLARE @restaurante_2_uuid VARCHAR(36) = '22345678-2234-2234-2234-223456789abc';

-- Tipo de cocina
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @restaurante_2_uuid AND cod_atributo = @cod_atributo_tipo_cocina)
BEGIN
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@restaurante_2_uuid, @cod_atributo_tipo_cocina, N'Argentina');
    PRINT 'Configuración "Tipo de cocina" agregada para Parrilla La Esquina SRL';
END

-- Estilo
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @restaurante_2_uuid AND cod_atributo = @cod_atributo_estilo)
BEGIN
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@restaurante_2_uuid, @cod_atributo_estilo, N'Restaurante tradicional');
    PRINT 'Configuración "Estilo" agregada para Parrilla La Esquina SRL';
END

-- Nivel de precio
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @restaurante_2_uuid AND cod_atributo = @cod_atributo_nivel_precio)
BEGIN
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@restaurante_2_uuid, @cod_atributo_nivel_precio, N'Medio');
    PRINT 'Configuración "Nivel de precio" agregada para Parrilla La Esquina SRL';
END

-- Estilo de atención (mantener para compatibilidad)
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @restaurante_2_uuid AND cod_atributo = @cod_atributo_estilo_atencion)
BEGIN
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@restaurante_2_uuid, @cod_atributo_estilo_atencion, N'Casual y familiar, ambiente relajado');
    PRINT 'Configuración "Estilo de atención" agregada para Parrilla La Esquina SRL';
END

-- Platos emblemáticos (mantener para compatibilidad)
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @restaurante_2_uuid AND cod_atributo = @cod_atributo_platos_emblematicos)
BEGIN
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@restaurante_2_uuid, @cod_atributo_platos_emblematicos, N'Asado de tira, Chorizo criollo, Provoleta a la parrilla, Ensalada rusa');
    PRINT 'Configuración "Platos emblemáticos" agregada para Parrilla La Esquina SRL';
END

-- Restaurante 3: Sushi House S.A. (Cocina japonesa)
DECLARE @restaurante_3_uuid VARCHAR(36) = '32345678-3234-3234-3234-323456789abc';

-- Tipo de cocina
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @restaurante_3_uuid AND cod_atributo = @cod_atributo_tipo_cocina)
BEGIN
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@restaurante_3_uuid, @cod_atributo_tipo_cocina, N'Japonesa');
    PRINT 'Configuración "Tipo de cocina" agregada para Sushi House S.A.';
END

-- Estilo
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @restaurante_3_uuid AND cod_atributo = @cod_atributo_estilo)
BEGIN
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@restaurante_3_uuid, @cod_atributo_estilo, N'Gourmet');
    PRINT 'Configuración "Estilo" agregada para Sushi House S.A.';
END

-- Nivel de precio
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @restaurante_3_uuid AND cod_atributo = @cod_atributo_nivel_precio)
BEGIN
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@restaurante_3_uuid, @cod_atributo_nivel_precio, N'Alto / Premium');
    PRINT 'Configuración "Nivel de precio" agregada para Sushi House S.A.';
END

-- Estilo de atención (mantener para compatibilidad)
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @restaurante_3_uuid AND cod_atributo = @cod_atributo_estilo_atencion)
BEGIN
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@restaurante_3_uuid, @cod_atributo_estilo_atencion, N'Moderno y sofisticado, atención detallista');
    PRINT 'Configuración "Estilo de atención" agregada para Sushi House S.A.';
END

-- Platos emblemáticos (mantener para compatibilidad)
IF NOT EXISTS (SELECT 1 FROM configuracion_restaurantes WHERE nro_restaurante = @restaurante_3_uuid AND cod_atributo = @cod_atributo_platos_emblematicos)
BEGIN
    INSERT INTO configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
    VALUES (@restaurante_3_uuid, @cod_atributo_platos_emblematicos, N'Roll California, Sashimi de salmón, Tempura de verduras, Ramen de cerdo');
    PRINT 'Configuración "Platos emblemáticos" agregada para Sushi House S.A.';
END

PRINT '';
PRINT '========================================';
PRINT 'Atributos de identidad gastronómica insertados exitosamente';
PRINT '========================================';
PRINT '';
PRINT 'Atributos creados:';
PRINT '  - Tipo de cocina (15 valores disponibles)';
PRINT '  - Especialidades alimentarias (7 valores disponibles)';
PRINT '  - Estilo (11 valores disponibles)';
PRINT '  - Nivel de precio (4 valores disponibles)';
PRINT '  - Estilo de atención (mantenido para compatibilidad)';
PRINT '  - Platos emblemáticos (mantenido para compatibilidad)';
PRINT '';
PRINT 'Configuraciones agregadas para:';
PRINT '  - Los Aroza SRL (Restaurante general)';
PRINT '  - Parrilla La Esquina SRL (Parrilla)';
PRINT '  - Sushi House S.A. (Cocina japonesa)';
PRINT '';
PRINT 'NOTA: Los valores específicos para cada atributo están documentados en los comentarios del script.';
PRINT '      Estos valores pueden usarse al configurar restaurantes en la tabla configuracion_restaurantes.';
PRINT '';
GO
