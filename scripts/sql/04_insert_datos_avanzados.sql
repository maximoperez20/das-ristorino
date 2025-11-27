/* =========================================================================================
   INSERT DE DATOS AVANZADOS - das_ristorino
   Incluye SOLO catálogos base: estados de reservas, categorías de preferencias
   NOTA: Las zonas, zonas por turno y promociones se insertan con 12_insert_restaurantes_examen_final.sql
   ========================================================================================= */

SET NOCOUNT ON;
GO

USE das_ristorino;
GO

/* =========================================
   1) Estados de Reservas
   ========================================= */

-- Estados base
IF NOT EXISTS (SELECT 1 FROM estados_reservas WHERE nom_estado = N'Pendiente')
    INSERT INTO estados_reservas (nom_estado) VALUES (N'Pendiente');
IF NOT EXISTS (SELECT 1 FROM estados_reservas WHERE nom_estado = N'Confirmada')
    INSERT INTO estados_reservas (nom_estado) VALUES (N'Confirmada');
IF NOT EXISTS (SELECT 1 FROM estados_reservas WHERE nom_estado = N'En curso')
    INSERT INTO estados_reservas (nom_estado) VALUES (N'En curso');
IF NOT EXISTS (SELECT 1 FROM estados_reservas WHERE nom_estado = N'Finalizada')
    INSERT INTO estados_reservas (nom_estado) VALUES (N'Finalizada');
IF NOT EXISTS (SELECT 1 FROM estados_reservas WHERE nom_estado = N'Cancelada')
    INSERT INTO estados_reservas (nom_estado) VALUES (N'Cancelada');

-- Traducciones de estados (español)
DECLARE @nro_idioma_es INT;
SELECT @nro_idioma_es = nro_idioma FROM idiomas WHERE cod_idioma = N'es-AR';

IF @nro_idioma_es IS NOT NULL
BEGIN
    DECLARE @cod_estado VARCHAR(36);
    
    -- Pendiente
    SELECT @cod_estado = cod_estado FROM estados_reservas WHERE nom_estado = N'Pendiente';
    IF NOT EXISTS (SELECT 1 FROM idiomas_estados_reservas WHERE cod_estado = @cod_estado AND nro_idioma = @nro_idioma_es)
        INSERT INTO idiomas_estados_reservas (cod_estado, nro_idioma, estado) VALUES (@cod_estado, @nro_idioma_es, N'Pendiente');
    
    -- Confirmada
    SELECT @cod_estado = cod_estado FROM estados_reservas WHERE nom_estado = N'Confirmada';
    IF NOT EXISTS (SELECT 1 FROM idiomas_estados_reservas WHERE cod_estado = @cod_estado AND nro_idioma = @nro_idioma_es)
        INSERT INTO idiomas_estados_reservas (cod_estado, nro_idioma, estado) VALUES (@cod_estado, @nro_idioma_es, N'Confirmada');
    
    -- En curso
    SELECT @cod_estado = cod_estado FROM estados_reservas WHERE nom_estado = N'En curso';
    IF NOT EXISTS (SELECT 1 FROM idiomas_estados_reservas WHERE cod_estado = @cod_estado AND nro_idioma = @nro_idioma_es)
        INSERT INTO idiomas_estados_reservas (cod_estado, nro_idioma, estado) VALUES (@cod_estado, @nro_idioma_es, N'En curso');
    
    -- Finalizada
    SELECT @cod_estado = cod_estado FROM estados_reservas WHERE nom_estado = N'Finalizada';
    IF NOT EXISTS (SELECT 1 FROM idiomas_estados_reservas WHERE cod_estado = @cod_estado AND nro_idioma = @nro_idioma_es)
        INSERT INTO idiomas_estados_reservas (cod_estado, nro_idioma, estado) VALUES (@cod_estado, @nro_idioma_es, N'Finalizada');
    
    -- Cancelada
    SELECT @cod_estado = cod_estado FROM estados_reservas WHERE nom_estado = N'Cancelada';
    IF NOT EXISTS (SELECT 1 FROM idiomas_estados_reservas WHERE cod_estado = @cod_estado AND nro_idioma = @nro_idioma_es)
        INSERT INTO idiomas_estados_reservas (cod_estado, nro_idioma, estado) VALUES (@cod_estado, @nro_idioma_es, N'Cancelada');
END

PRINT 'Estados de reservas insertados';

/* =========================================
   2) Categorías de Preferencias y Dominios
   ========================================= */

-- Categorías base
IF NOT EXISTS (SELECT 1 FROM categorias_preferencias WHERE nom_categoria = N'Tipo de comida')
    INSERT INTO categorias_preferencias (nom_categoria) VALUES (N'Tipo de comida');
IF NOT EXISTS (SELECT 1 FROM categorias_preferencias WHERE nom_categoria = N'Ambiente')
    INSERT INTO categorias_preferencias (nom_categoria) VALUES (N'Ambiente');
IF NOT EXISTS (SELECT 1 FROM categorias_preferencias WHERE nom_categoria = N'Rango de precio')
    INSERT INTO categorias_preferencias (nom_categoria) VALUES (N'Rango de precio');

DECLARE @cat_tipo VARCHAR(36), @cat_amb VARCHAR(36), @cat_precio VARCHAR(36);
SELECT @cat_tipo = cod_categoria FROM categorias_preferencias WHERE nom_categoria = N'Tipo de comida';
SELECT @cat_amb = cod_categoria FROM categorias_preferencias WHERE nom_categoria = N'Ambiente';
SELECT @cat_precio = cod_categoria FROM categorias_preferencias WHERE nom_categoria = N'Rango de precio';

-- Dominios: Tipo de comida
DECLARE @prox INT;
IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Parrilla')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_tipo, @prox, N'Parrilla');
END
IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Pizzería')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_tipo, @prox, N'Pizzería');
END
IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Sushi')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_tipo, @prox, N'Sushi');
END
IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Vegano')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_tipo, @prox, N'Vegano');
END

-- Dominios: Ambiente
IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Familiar')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_amb, @prox, N'Familiar');
END
IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Romántico')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_amb, @prox, N'Romántico');
END
IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Gourmet')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_amb, @prox, N'Gourmet');
END
IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Casual')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_amb, @prox, N'Casual');
END

-- Dominios: Rango de precio
IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_precio AND nom_valor_dominio = N'Económico')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_precio;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_precio, @prox, N'Económico');
END
IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_precio AND nom_valor_dominio = N'Medio')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_precio;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_precio, @prox, N'Medio');
END
IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_precio AND nom_valor_dominio = N'Premium')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_precio;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_precio, @prox, N'Premium');
END

PRINT 'Categorías y dominios de preferencias insertados';

/* =========================================
   3) Clientes de Prueba (opcional, para testing)
   ========================================= */

DECLARE @cod_cba VARCHAR(36);
SELECT @cod_cba = cod_provincia FROM provincias WHERE nom_provincia = N'Córdoba';

DECLARE @loc_alta_cba VARCHAR(36);
SELECT @loc_alta_cba = nro_localidad FROM localidades WHERE nom_localidad = N'Alta Córdoba' AND cod_provincia = @cod_cba;

-- Cliente de prueba: Ana Rodríguez
IF @loc_alta_cba IS NOT NULL AND NOT EXISTS (SELECT 1 FROM clientes WHERE correo = N'ana.rodriguez@mail.com')
    INSERT INTO clientes (apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado)
    VALUES (N'Rodríguez', N'Ana', N'$2y$10$dummyhash1234567890123456789012345678901234567890123456789012', N'ana.rodriguez@mail.com', N'351-555-1111', @loc_alta_cba, 1);

PRINT 'Clientes de prueba insertados (opcional)';

/* =========================================
   4) Costos para contenidos
   ========================================= */

-- Insertar un costo activo por defecto para los contenidos generados con IA
-- Tipo: CLICK, vigente desde hoy, sin fecha de fin (vigente indefinidamente)
DECLARE @fecha_hoy DATE = CAST(GETDATE() AS DATE);

IF NOT EXISTS (SELECT 1 FROM costos 
               WHERE tipo_costo = N'CLICK' 
               AND fecha_ini_vigencia <= @fecha_hoy 
               AND (fecha_fin_vigencia IS NULL OR fecha_fin_vigencia >= @fecha_hoy))
BEGIN
    INSERT INTO costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto)
    VALUES (N'CLICK', @fecha_hoy, NULL, 0.50);
    PRINT '>> Costo CLICK insertado: $0.50 por click (vigente desde ' + CAST(@fecha_hoy AS VARCHAR(10)) + ')';
END
ELSE
BEGIN
    PRINT '>> Ya existe un costo CLICK activo en la tabla costos';
END

-- Opcional: Insertar costo para reservas (si no existe)
IF NOT EXISTS (SELECT 1 FROM costos 
               WHERE tipo_costo = N'RESERVA' 
               AND fecha_ini_vigencia <= @fecha_hoy 
               AND (fecha_fin_vigencia IS NULL OR fecha_fin_vigencia >= @fecha_hoy))
BEGIN
    INSERT INTO costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto)
    VALUES (N'RESERVA', @fecha_hoy, NULL, 100.00);
    PRINT '>> Costo RESERVA insertado: $100.00 por reserva (vigente desde ' + CAST(@fecha_hoy AS VARCHAR(10)) + ')';
END

PRINT '========================================';
PRINT 'Datos avanzados insertados exitosamente';
PRINT '========================================';
PRINT '- Estados de reservas: 5 estados creados';
PRINT '- Categorías de preferencias: 3 categorías con dominios';
PRINT '- Clientes de prueba: 1 cliente creado (opcional)';
PRINT '- Costos: CLICK y RESERVA configurados';
PRINT '';
PRINT 'NOTA: Las zonas, zonas por turno y promociones se insertan con:';
PRINT '  12_insert_restaurantes_examen_final.sql';
PRINT '========================================';
GO
