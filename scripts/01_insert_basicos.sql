-- Inserta catálogos base: provincias, localidades y idiomas (idempotente)
SET NOCOUNT ON;

/* Provincias */
IF NOT EXISTS (SELECT 1 FROM provincias WHERE nom_provincia = N'Córdoba')
  INSERT INTO provincias (nom_provincia) VALUES (N'Córdoba');
IF NOT EXISTS (SELECT 1 FROM provincias WHERE nom_provincia = N'Buenos Aires')
  INSERT INTO provincias (nom_provincia) VALUES (N'Buenos Aires');
IF NOT EXISTS (SELECT 1 FROM provincias WHERE nom_provincia = N'Santa Fe')
  INSERT INTO provincias (nom_provincia) VALUES (N'Santa Fe');

/* Localidades (ligadas a su provincia por nombre) */
DECLARE @cod_cba VARCHAR(36), @cod_ba VARCHAR(36), @cod_sf VARCHAR(36);
SELECT @cod_cba = cod_provincia FROM provincias WHERE nom_provincia = N'Córdoba';
SELECT @cod_ba  = cod_provincia FROM provincias WHERE nom_provincia = N'Buenos Aires';
SELECT @cod_sf  = cod_provincia FROM provincias WHERE nom_provincia = N'Santa Fe';

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

/* Idiomas */
IF NOT EXISTS (SELECT 1 FROM idiomas WHERE cod_idioma=N'es-AR')
  INSERT INTO idiomas (nom_idioma, cod_idioma) VALUES (N'Español (Argentina)', N'es-AR');
IF NOT EXISTS (SELECT 1 FROM idiomas WHERE cod_idioma=N'en-US')
  INSERT INTO idiomas (nom_idioma, cod_idioma) VALUES (N'English (United States)', N'en-US');
IF NOT EXISTS (SELECT 1 FROM idiomas WHERE cod_idioma=N'pt-BR')
  INSERT INTO idiomas (nom_idioma, cod_idioma) VALUES (N'Português (Brasil)', N'pt-BR');
