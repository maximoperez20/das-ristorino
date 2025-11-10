/* =========================================================================================
   INSERT DE DATOS AVANZADOS - das_ristorino
   Incluye: estados de reservas, zonas, zonas por turno, categorías de preferencias,
   clientes de prueba, y traducciones básicas
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
   2) Zonas para las Sucursales
   ========================================= */

-- Restaurante compartido (Los Aroza)
DECLARE @restaurante_compartido_uuid VARCHAR(36) = '12345678-1234-1234-1234-123456789abc';
DECLARE @nro_sucursal_1 VARCHAR(36);
SELECT @nro_sucursal_1 = nro_sucursal FROM sucursales_restaurantes 
WHERE nro_restaurante = @restaurante_compartido_uuid AND nom_sucursal = N'Los Aroza - Centro';

IF @nro_sucursal_1 IS NOT NULL
BEGIN
    -- Zona 1: Salón Principal
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @restaurante_compartido_uuid 
                   AND nro_sucursal = @nro_sucursal_1 
                   AND desc_zona = N'Salón Principal')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada)
        VALUES 
            (@restaurante_compartido_uuid, @nro_sucursal_1, N'Salón Principal', 90, 1, 1);
    END
    
    -- Zona 2: Terraza
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @restaurante_compartido_uuid 
                   AND nro_sucursal = @nro_sucursal_1 
                   AND desc_zona = N'Terraza')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada)
        VALUES 
            (@restaurante_compartido_uuid, @nro_sucursal_1, N'Terraza', 50, 1, 1);
    END
END

-- Restaurante 2 (Parrilla La Esquina)
DECLARE @restaurante_2_uuid VARCHAR(36) = '22345678-2234-2234-2234-223456789abc';
DECLARE @nro_sucursal_2 VARCHAR(36);
SELECT @nro_sucursal_2 = nro_sucursal FROM sucursales_restaurantes 
WHERE nro_restaurante = @restaurante_2_uuid AND nom_sucursal = N'Parrilla La Esquina - VCP';

IF @nro_sucursal_2 IS NOT NULL
BEGIN
    -- Zona 1: Salón Principal
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @restaurante_2_uuid 
                   AND nro_sucursal = @nro_sucursal_2 
                   AND desc_zona = N'Salón Principal')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada)
        VALUES 
            (@restaurante_2_uuid, @nro_sucursal_2, N'Salón Principal', 70, 1, 1);
    END
    
    -- Zona 2: Patio Cubierto
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @restaurante_2_uuid 
                   AND nro_sucursal = @nro_sucursal_2 
                   AND desc_zona = N'Patio Cubierto')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada)
        VALUES 
            (@restaurante_2_uuid, @nro_sucursal_2, N'Patio Cubierto', 30, 1, 1);
    END
END

-- Restaurante 3 (Sushi House)
DECLARE @restaurante_3_uuid VARCHAR(36) = '32345678-3234-3234-3234-323456789abc';
DECLARE @nro_sucursal_3 VARCHAR(36);
SELECT @nro_sucursal_3 = nro_sucursal FROM sucursales_restaurantes 
WHERE nro_restaurante = @restaurante_3_uuid AND nom_sucursal = N'Sushi House - Centro';

IF @nro_sucursal_3 IS NOT NULL
BEGIN
    -- Zona 1: Salón Principal
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @restaurante_3_uuid 
                   AND nro_sucursal = @nro_sucursal_3 
                   AND desc_zona = N'Salón Principal')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada)
        VALUES 
            (@restaurante_3_uuid, @nro_sucursal_3, N'Salón Principal', 60, 1, 1);
    END
    
    -- Zona 2: Barra
    IF NOT EXISTS (SELECT 1 FROM zonas_sucursales_restaurantes 
                   WHERE nro_restaurante = @restaurante_3_uuid 
                   AND nro_sucursal = @nro_sucursal_3 
                   AND desc_zona = N'Barra')
    BEGIN
        INSERT INTO zonas_sucursales_restaurantes 
            (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada)
        VALUES 
            (@restaurante_3_uuid, @nro_sucursal_3, N'Barra', 20, 0, 1);
    END
END

PRINT 'Zonas insertadas para todas las sucursales';

/* =========================================
   3) Zonas por Turno (Habilitar zonas en turnos)
   ========================================= */

-- Para restaurante compartido
IF @nro_sucursal_1 IS NOT NULL
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
    WHERE t.nro_restaurante = @restaurante_compartido_uuid 
      AND t.nro_sucursal = @nro_sucursal_1
      AND ztr.nro_restaurante IS NULL;
END

-- Para restaurante 2
IF @nro_sucursal_2 IS NOT NULL
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
    WHERE t.nro_restaurante = @restaurante_2_uuid 
      AND t.nro_sucursal = @nro_sucursal_2
      AND ztr.nro_restaurante IS NULL;
END

-- Para restaurante 3
IF @nro_sucursal_3 IS NOT NULL
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
    WHERE t.nro_restaurante = @restaurante_3_uuid 
      AND t.nro_sucursal = @nro_sucursal_3
      AND ztr.nro_restaurante IS NULL;
END

PRINT 'Zonas habilitadas en turnos';

/* =========================================
   4) Categorías de Preferencias y Dominios
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
   5) Clientes de Prueba
   ========================================= */

DECLARE @cod_cba VARCHAR(36), @cod_ba VARCHAR(36);
SELECT @cod_cba = cod_provincia FROM provincias WHERE nom_provincia = N'Córdoba';
SELECT @cod_ba = cod_provincia FROM provincias WHERE nom_provincia = N'Buenos Aires';

DECLARE @loc_cba VARCHAR(36), @loc_vcp VARCHAR(36), @loc_lpl VARCHAR(36);
SELECT @loc_cba = nro_localidad FROM localidades WHERE nom_localidad = N'Córdoba' AND cod_provincia = @cod_cba;
SELECT @loc_vcp = nro_localidad FROM localidades WHERE nom_localidad = N'Villa Carlos Paz' AND cod_provincia = @cod_cba;
SELECT @loc_lpl = nro_localidad FROM localidades WHERE nom_localidad = N'La Plata' AND cod_provincia = @cod_ba;

-- Cliente 1: Ana Rodríguez
IF NOT EXISTS (SELECT 1 FROM clientes WHERE correo = N'ana.rodriguez@mail.com')
    INSERT INTO clientes (apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado)
    VALUES (N'Rodríguez', N'Ana', N'$2y$10$dummyhash1234567890123456789012345678901234567890123456789012', N'ana.rodriguez@mail.com', N'351-555-1111', @loc_cba, 1);

-- Cliente 2: Maximiliano Ferreyra
IF NOT EXISTS (SELECT 1 FROM clientes WHERE correo = N'max.ferreyra@mail.com')
    INSERT INTO clientes (apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado)
    VALUES (N'Ferreyra', N'Maximiliano', N'$2y$10$dummyhash1234567890123456789012345678901234567890123456789012', N'max.ferreyra@mail.com', N'351-555-2222', @loc_vcp, 1);

-- Cliente 3: Carla Sosa
IF NOT EXISTS (SELECT 1 FROM clientes WHERE correo = N'carla.sosa@mail.com')
    INSERT INTO clientes (apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado)
    VALUES (N'Sosa', N'Carla', N'$2y$10$dummyhash1234567890123456789012345678901234567890123456789012', N'carla.sosa@mail.com', N'221-555-3333', @loc_lpl, 1);

PRINT 'Clientes de prueba insertados';

/* =========================================
   6) Traducciones de Zonas (opcional, útil para i18n)
   ========================================= */

IF @nro_idioma_es IS NOT NULL AND @nro_sucursal_1 IS NOT NULL
BEGIN
    -- Traducciones para restaurante compartido
    DECLARE @cod_zona VARCHAR(36);
    
    -- Salón Principal
    SELECT @cod_zona = cod_zona FROM zonas_sucursales_restaurantes 
    WHERE nro_restaurante = @restaurante_compartido_uuid AND nro_sucursal = @nro_sucursal_1 AND desc_zona = N'Salón Principal';
    IF @cod_zona IS NOT NULL AND NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                                             WHERE nro_restaurante = @restaurante_compartido_uuid 
                                             AND nro_sucursal = @nro_sucursal_1 
                                             AND cod_zona = @cod_zona 
                                             AND nro_idioma = @nro_idioma_es)
        INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
        VALUES (@restaurante_compartido_uuid, @nro_sucursal_1, @cod_zona, @nro_idioma_es, N'Salón Principal', N'Salón principal del restaurante con capacidad para 90 comensales');
    
    -- Terraza
    SELECT @cod_zona = cod_zona FROM zonas_sucursales_restaurantes 
    WHERE nro_restaurante = @restaurante_compartido_uuid AND nro_sucursal = @nro_sucursal_1 AND desc_zona = N'Terraza';
    IF @cod_zona IS NOT NULL AND NOT EXISTS (SELECT 1 FROM idiomas_zonas_suc_restaurantes 
                                             WHERE nro_restaurante = @restaurante_compartido_uuid 
                                             AND nro_sucursal = @nro_sucursal_1 
                                             AND cod_zona = @cod_zona 
                                             AND nro_idioma = @nro_idioma_es)
        INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
        VALUES (@restaurante_compartido_uuid, @nro_sucursal_1, @cod_zona, @nro_idioma_es, N'Terraza', N'Terraza al aire libre con capacidad para 50 comensales');
END

PRINT 'Traducciones de zonas insertadas';

/* =========================================
   Resumen
   ========================================= */

PRINT '========================================';
PRINT 'Datos avanzados insertados exitosamente';
PRINT '========================================';
PRINT '- Estados de reservas: 5 estados creados';
PRINT '- Zonas: 2 zonas por sucursal (6 zonas totales)';
PRINT '- Zonas por turno: Todas las zonas habilitadas en sus turnos';
PRINT '- Categorías de preferencias: 3 categorías con 12 dominios';
PRINT '- Clientes de prueba: 3 clientes creados';
PRINT '- Traducciones: Zonas traducidas al español';
PRINT '========================================';
GO

