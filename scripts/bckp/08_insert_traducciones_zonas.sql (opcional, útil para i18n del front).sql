	-- Inserta traducciones de zonas por sucursal (idempotente). Requiere idiomas + zonas cargadas.
SET NOCOUNT ON;

DECLARE 
  @CUIT        VARCHAR(11)   = '30700987654',
  @NomSucursal NVARCHAR(120) = N'Los Aroza - Centro',
  @IdiomaCod   NVARCHAR(16)  = N'en-US';  -- traducimos a inglés (ejemplo)

-- Resolver IDs
DECLARE @nro_restaurante VARCHAR(36), @nro_sucursal VARCHAR(36), @nro_idioma VARCHAR(36);
SELECT @nro_restaurante = nro_restaurante FROM restaurantes WHERE cuit=@CUIT;
IF @nro_restaurante IS NULL BEGIN RAISERROR('Restaurante no encontrado por CUIT.',16,1); RETURN; END

SELECT @nro_sucursal = nro_sucursal
FROM sucursales_restaurantes
WHERE nro_restaurante=@nro_restaurante AND nom_sucursal=@NomSucursal;
IF @nro_sucursal IS NULL BEGIN RAISERROR('Sucursal no encontrada.',16,1); RETURN; END

SELECT @nro_idioma = nro_idioma FROM idiomas WHERE cod_idioma=@IdiomaCod;
IF @nro_idioma IS NULL BEGIN RAISERROR('Idioma no encontrado. Corré 01_insert_basicos.sql',16,1); RETURN; END

-- Traducciones (ejemplos)
;WITH z AS (
  SELECT cod_zona, desc_zona
  FROM zonas_sucursales_restaurantes
  WHERE nro_restaurante=@nro_restaurante AND nro_sucursal=@nro_sucursal
)
INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
SELECT @nro_restaurante, @nro_sucursal, z.cod_zona, @nro_idioma,
       CASE z.desc_zona
         WHEN N'Salón Principal' THEN N'Main Hall'
         WHEN N'Terraza'         THEN N'Terrace'
         ELSE z.desc_zona
       END AS zona,
       NULL
FROM z
WHERE NOT EXISTS (
  SELECT 1 FROM idiomas_zonas_suc_restaurantes t
  WHERE t.nro_restaurante=@nro_restaurante AND t.nro_sucursal=@nro_sucursal
    AND t.cod_zona=z.cod_zona AND t.nro_idioma=@nro_idioma
);

-- Verificación
SELECT i.cod_idioma, t.zona AS zona_traducida, z.desc_zona AS zona_original
FROM idiomas_zonas_suc_restaurantes t
JOIN idiomas i ON i.nro_idioma=t.nro_idioma
JOIN zonas_sucursales_restaurantes z 
  ON z.nro_restaurante=t.nro_restaurante AND z.nro_sucursal=t.nro_sucursal AND z.cod_zona=t.cod_zona
WHERE t.nro_restaurante=@nro_restaurante AND t.nro_sucursal=@nro_sucursal AND t.nro_idioma=@nro_idioma
ORDER BY zona_traducida;
