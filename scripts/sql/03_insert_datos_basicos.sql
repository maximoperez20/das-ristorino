/* =========================================================================================
   INSERT DE DATOS BÁSICOS - das_ristorino
   Incluye: provincias, localidades, idiomas, y 3 restaurantes (1 compartido con restaurante-soap)
   ========================================================================================= */

SET NOCOUNT ON;
GO

USE das_ristorino;
GO

/* =========================================
   1) Catálogos base
   ========================================= */

-- Provincias
IF NOT EXISTS (SELECT 1 FROM provincias WHERE nom_provincia = N'Córdoba')
    INSERT INTO provincias (nom_provincia) VALUES (N'Córdoba');
IF NOT EXISTS (SELECT 1 FROM provincias WHERE nom_provincia = N'Buenos Aires')
    INSERT INTO provincias (nom_provincia) VALUES (N'Buenos Aires');
IF NOT EXISTS (SELECT 1 FROM provincias WHERE nom_provincia = N'Santa Fe')
    INSERT INTO provincias (nom_provincia) VALUES (N'Santa Fe');

-- Localidades
DECLARE @cod_cba VARCHAR(36), @cod_ba VARCHAR(36), @cod_sf VARCHAR(36);
SELECT @cod_cba = cod_provincia FROM provincias WHERE nom_provincia = N'Córdoba';
SELECT @cod_ba = cod_provincia FROM provincias WHERE nom_provincia = N'Buenos Aires';
SELECT @cod_sf = cod_provincia FROM provincias WHERE nom_provincia = N'Santa Fe';

IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'Córdoba' AND cod_provincia=@cod_cba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Córdoba', @cod_cba);
IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'Villa Carlos Paz' AND cod_provincia=@cod_cba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Villa Carlos Paz', @cod_cba);
IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'Río Cuarto' AND cod_provincia=@cod_cba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Río Cuarto', @cod_cba);

IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'La Plata' AND cod_provincia=@cod_ba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'La Plata', @cod_ba);
IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'Mar del Plata' AND cod_provincia=@cod_ba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Mar del Plata', @cod_ba);

IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'Rosario' AND cod_provincia=@cod_sf)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Rosario', @cod_sf);

-- Idiomas
IF NOT EXISTS (SELECT 1 FROM idiomas WHERE cod_idioma=N'es-AR')
    INSERT INTO idiomas (nom_idioma, cod_idioma) VALUES (N'Español (Argentina)', N'es-AR');
IF NOT EXISTS (SELECT 1 FROM idiomas WHERE cod_idioma=N'en-US')
    INSERT INTO idiomas (nom_idioma, cod_idioma) VALUES (N'English (United States)', N'en-US');
IF NOT EXISTS (SELECT 1 FROM idiomas WHERE cod_idioma=N'pt-BR')
    INSERT INTO idiomas (nom_idioma, cod_idioma) VALUES (N'Português (Brasil)', N'pt-BR');

/* =========================================
   2) Restaurantes (3 total)
   ========================================= */

-- Restaurante 1: COMPARTIDO con das-restaurante-soap (mismo UUID)
DECLARE @restaurante_compartido_uuid VARCHAR(36) = '12345678-1234-1234-1234-123456789abc';
DECLARE @nro_localidad_cordoba VARCHAR(36);
SELECT @nro_localidad_cordoba = nro_localidad FROM localidades WHERE nom_localidad=N'Córdoba' AND cod_provincia=@cod_cba;

IF NOT EXISTS (SELECT 1 FROM restaurantes WHERE nro_restaurante = @restaurante_compartido_uuid)
BEGIN
    INSERT INTO restaurantes (nro_restaurante, razon_social, cuit)
    VALUES (@restaurante_compartido_uuid, 'Los Aroza SRL', '30700987654');
    PRINT 'Restaurante 1 (COMPARTIDO) insertado: ' + @restaurante_compartido_uuid;
END
ELSE
    PRINT 'Restaurante 1 (COMPARTIDO) ya existe: ' + @restaurante_compartido_uuid;

-- Sucursal del restaurante compartido
DECLARE @nro_sucursal_1 VARCHAR(36);
IF NOT EXISTS (SELECT 1 FROM sucursales_restaurantes WHERE nro_restaurante = @restaurante_compartido_uuid AND nom_sucursal = 'Los Aroza - Centro')
BEGIN
    SET @nro_sucursal_1 = NEWID();
    INSERT INTO sucursales_restaurantes (
        nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio,
        nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva
    )
    VALUES (
        @restaurante_compartido_uuid, @nro_sucursal_1, 'Los Aroza - Centro',
        'Av. Colón', 950, 'Centro',
        @nro_localidad_cordoba, '5000', '351-555-1234', 140, 15
    );
    PRINT 'Sucursal 1 insertada: ' + @nro_sucursal_1;
END
ELSE
BEGIN
    SELECT @nro_sucursal_1 = nro_sucursal FROM sucursales_restaurantes WHERE nro_restaurante = @restaurante_compartido_uuid AND nom_sucursal = 'Los Aroza - Centro';
    PRINT 'Sucursal 1 ya existe: ' + @nro_sucursal_1;
END

-- Restaurante 2: Parrilla La Esquina
DECLARE @restaurante_2_uuid VARCHAR(36) = '22345678-2234-2234-2234-223456789abc';
DECLARE @nro_localidad_vcp VARCHAR(36);
SELECT @nro_localidad_vcp = nro_localidad FROM localidades WHERE nom_localidad=N'Villa Carlos Paz' AND cod_provincia=@cod_cba;

IF NOT EXISTS (SELECT 1 FROM restaurantes WHERE nro_restaurante = @restaurante_2_uuid)
BEGIN
    INSERT INTO restaurantes (nro_restaurante, razon_social, cuit)
    VALUES (@restaurante_2_uuid, 'Parrilla La Esquina SRL', '30987654321');
    PRINT 'Restaurante 2 insertado: ' + @restaurante_2_uuid;
END
ELSE
    PRINT 'Restaurante 2 ya existe: ' + @restaurante_2_uuid;

-- Sucursal del restaurante 2
DECLARE @nro_sucursal_2 VARCHAR(36);
IF NOT EXISTS (SELECT 1 FROM sucursales_restaurantes WHERE nro_restaurante = @restaurante_2_uuid AND nom_sucursal = 'Parrilla La Esquina - VCP')
BEGIN
    SET @nro_sucursal_2 = NEWID();
    INSERT INTO sucursales_restaurantes (
        nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio,
        nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva
    )
    VALUES (
        @restaurante_2_uuid, @nro_sucursal_2, 'Parrilla La Esquina - VCP',
        'Av. San Martín', 500, 'Centro',
        @nro_localidad_vcp, '5152', '3541-444-5678', 100, 20
    );
    PRINT 'Sucursal 2 insertada: ' + @nro_sucursal_2;
END
ELSE
BEGIN
    SELECT @nro_sucursal_2 = nro_sucursal FROM sucursales_restaurantes WHERE nro_restaurante = @restaurante_2_uuid AND nom_sucursal = 'Parrilla La Esquina - VCP';
    PRINT 'Sucursal 2 ya existe: ' + @nro_sucursal_2;
END

-- Restaurante 3: Sushi House
DECLARE @restaurante_3_uuid VARCHAR(36) = '32345678-3234-3234-3234-323456789abc';
DECLARE @nro_localidad_laplata VARCHAR(36);
SELECT @nro_localidad_laplata = nro_localidad FROM localidades WHERE nom_localidad=N'La Plata' AND cod_provincia=@cod_ba;

IF NOT EXISTS (SELECT 1 FROM restaurantes WHERE nro_restaurante = @restaurante_3_uuid)
BEGIN
    INSERT INTO restaurantes (nro_restaurante, razon_social, cuit)
    VALUES (@restaurante_3_uuid, 'Sushi House S.A.', '30123456789');
    PRINT 'Restaurante 3 insertado: ' + @restaurante_3_uuid;
END
ELSE
    PRINT 'Restaurante 3 ya existe: ' + @restaurante_3_uuid;

-- Sucursal del restaurante 3
DECLARE @nro_sucursal_3 VARCHAR(36);
IF NOT EXISTS (SELECT 1 FROM sucursales_restaurantes WHERE nro_restaurante = @restaurante_3_uuid AND nom_sucursal = 'Sushi House - Centro')
BEGIN
    SET @nro_sucursal_3 = NEWID();
    INSERT INTO sucursales_restaurantes (
        nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio,
        nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva
    )
    VALUES (
        @restaurante_3_uuid, @nro_sucursal_3, 'Sushi House - Centro',
        'Calle 50', 1200, 'Centro',
        @nro_localidad_laplata, '1900', '0221-555-9876', 80, 10
    );
    PRINT 'Sucursal 3 insertada: ' + @nro_sucursal_3;
END
ELSE
BEGIN
    SELECT @nro_sucursal_3 = nro_sucursal FROM sucursales_restaurantes WHERE nro_restaurante = @restaurante_3_uuid AND nom_sucursal = 'Sushi House - Centro';
    PRINT 'Sucursal 3 ya existe: ' + @nro_sucursal_3;
END

/* =========================================
   3) Turnos básicos para las sucursales
   ========================================= */

-- Turnos para sucursal 1 (restaurante compartido)
DECLARE @hora TIME = '16:00';
DECLARE @hora_hasta TIME;
DECLARE @i INT = 0;

WHILE @i < 4
BEGIN
    SET @hora_hasta = CAST(DATEADD(MINUTE, 120, CAST(@hora AS DATETIME)) AS TIME);
    
    IF NOT EXISTS (SELECT 1 FROM turnos_sucursales_restaurantes WHERE nro_restaurante = @restaurante_compartido_uuid AND nro_sucursal = @nro_sucursal_1 AND hora_desde = @hora)
    BEGIN
        INSERT INTO turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
        VALUES (@restaurante_compartido_uuid, @nro_sucursal_1, @hora, @hora_hasta, 1);
    END
    
    SET @hora = @hora_hasta;
    SET @i = @i + 1;
END

-- Turnos para sucursal 2
SET @hora = '19:00';
SET @i = 0;
WHILE @i < 3
BEGIN
    SET @hora_hasta = CAST(DATEADD(MINUTE, 120, CAST(@hora AS DATETIME)) AS TIME);
    
    IF NOT EXISTS (SELECT 1 FROM turnos_sucursales_restaurantes WHERE nro_restaurante = @restaurante_2_uuid AND nro_sucursal = @nro_sucursal_2 AND hora_desde = @hora)
    BEGIN
        INSERT INTO turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
        VALUES (@restaurante_2_uuid, @nro_sucursal_2, @hora, @hora_hasta, 1);
    END
    
    SET @hora = @hora_hasta;
    SET @i = @i + 1;
END

-- Turnos para sucursal 3
SET @hora = '20:00';
SET @i = 0;
WHILE @i < 2
BEGIN
    SET @hora_hasta = CAST(DATEADD(MINUTE, 120, CAST(@hora AS DATETIME)) AS TIME);
    
    IF NOT EXISTS (SELECT 1 FROM turnos_sucursales_restaurantes WHERE nro_restaurante = @restaurante_3_uuid AND nro_sucursal = @nro_sucursal_3 AND hora_desde = @hora)
    BEGIN
        INSERT INTO turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
        VALUES (@restaurante_3_uuid, @nro_sucursal_3, @hora, @hora_hasta, 1);
    END
    
    SET @hora = @hora_hasta;
    SET @i = @i + 1;
END

PRINT 'Datos básicos insertados exitosamente en das_ristorino';
PRINT 'Restaurante 1 (COMPARTIDO) UUID: 12345678-1234-1234-1234-123456789abc';
PRINT 'Restaurante 2 UUID: 22345678-2234-2234-2234-223456789abc';
PRINT 'Restaurante 3 UUID: 32345678-3234-3234-3234-323456789abc';
GO

