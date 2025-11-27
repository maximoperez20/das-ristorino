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
PRINT '  - Lenguaje preferido (mantenido para compatibilidad)';
PRINT '';
PRINT 'NOTA: Las configuraciones de identidad para los restaurantes se insertan con:';
PRINT '  12_insert_restaurantes_examen_final.sql';
PRINT '';
PRINT 'NOTA: Los valores específicos para cada atributo están documentados en los comentarios del script.';
PRINT '      Estos valores pueden usarse al configurar restaurantes en la tabla configuracion_restaurantes.';
PRINT '';
GO
