/* =========================================================================================
   INSERT DE DATOS BÁSICOS - das_ristorino
   Incluye SOLO catálogos base: provincias, localidades, idiomas
   NOTA: Los restaurantes se insertan con el script 12_insert_restaurantes_examen_final.sql
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

-- Localidades (barrios de Córdoba para los 4 restaurantes del examen final)
DECLARE @cod_cba VARCHAR(36);
SELECT @cod_cba = cod_provincia FROM provincias WHERE nom_provincia = N'Córdoba';

IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'Alta Córdoba' AND cod_provincia=@cod_cba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Alta Córdoba', @cod_cba);
IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'General Paz' AND cod_provincia=@cod_cba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'General Paz', @cod_cba);
IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'Nueva Córdoba' AND cod_provincia=@cod_cba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Nueva Córdoba', @cod_cba);
IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'Güemes' AND cod_provincia=@cod_cba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Güemes', @cod_cba);
IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'Cerro de las Rosas' AND cod_provincia=@cod_cba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Cerro de las Rosas', @cod_cba);
IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'Centro' AND cod_provincia=@cod_cba)
    INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Centro', @cod_cba);

-- Idiomas
IF NOT EXISTS (SELECT 1 FROM idiomas WHERE cod_idioma=N'es-AR')
    INSERT INTO idiomas (nom_idioma, cod_idioma) VALUES (N'Español (Argentina)', N'es-AR');
IF NOT EXISTS (SELECT 1 FROM idiomas WHERE cod_idioma=N'en-US')
    INSERT INTO idiomas (nom_idioma, cod_idioma) VALUES (N'English (United States)', N'en-US');
IF NOT EXISTS (SELECT 1 FROM idiomas WHERE cod_idioma=N'pt-BR')
    INSERT INTO idiomas (nom_idioma, cod_idioma) VALUES (N'Português (Brasil)', N'pt-BR');

PRINT 'Catálogos base insertados exitosamente en das_ristorino';
PRINT 'Los restaurantes se insertan con el script: 12_insert_restaurantes_examen_final.sql';
GO

