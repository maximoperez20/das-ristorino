-- Inserta contenidos/promos de demo para un restaurante (idempotente) y opcionalmente para una sucursal
SET NOCOUNT ON;

-------------------------------------------------------------
-- Parámetros
-------------------------------------------------------------
DECLARE 
  @CUIT        VARCHAR(11)   = '30700987654',             -- restaurante demo
  @NomSucursal NVARCHAR(120) = N'Los Aroza - Centro',     -- opcional: puede no existir
  @IdiomaCod   NVARCHAR(16)  = N'es-AR';                  -- idioma base
-- Códigos legibles para evitar duplicados lógicos
DECLARE 
  @CodPromo1 NVARCHAR(40) = N'PRM-PRIMAVERA-2x1',
  @CodPromo2 NVARCHAR(40) = N'PRM-FINDE-KIDS';

-------------------------------------------------------------
-- Resolver IDs restaurante, sucursal (si existe) e idioma
-------------------------------------------------------------
DECLARE @nro_restaurante VARCHAR(36), @nro_sucursal VARCHAR(36), @nro_idioma VARCHAR(36);

SELECT @nro_restaurante = nro_restaurante FROM restaurantes WHERE cuit=@CUIT;
IF @nro_restaurante IS NULL BEGIN RAISERROR('Restaurante no encontrado por CUIT.',16,1); RETURN; END

SELECT @nro_sucursal = s.nro_sucursal
FROM sucursales_restaurantes s
WHERE s.nro_restaurante=@nro_restaurante AND s.nom_sucursal=@NomSucursal;  -- puede quedar NULL si no existe

SELECT @nro_idioma = nro_idioma FROM idiomas WHERE cod_idioma=@IdiomaCod;
IF @nro_idioma IS NULL BEGIN RAISERROR('Idioma base no encontrado (es-AR). Corré 01_insert_basicos.sql',16,1); RETURN; END

-------------------------------------------------------------
-- Inserciones idempotentes (usando cod_contenido_restaurante como “natural key”)
-------------------------------------------------------------
IF NOT EXISTS (
  SELECT 1 FROM contenidos_restaurantes 
  WHERE nro_restaurante=@nro_restaurante AND nro_idioma=@nro_idioma AND cod_contenido_restaurante=@CodPromo1
)
  INSERT INTO contenidos_restaurantes (
    nro_restaurante, nro_idioma, nro_sucursal,
    contenido_promocional, imagen_promocional, contenido_a_publicar,
    fecha_ini_vigencia, fecha_fin_vigencia, costo_click, cod_contenido_restaurante
  )
  VALUES (
    @nro_restaurante, @nro_idioma, @nro_sucursal,
    N'¡Promo Primavera! 2x1 en principales de 16:00 a 20:00.',
    NULL,
    N'Vení a probar nuestra carta de temporada con 2x1 en platos seleccionados.',
    CAST(GETDATE() AS date),
    DATEADD(DAY, 30, CAST(GETDATE() AS date)),
    50.00,
    @CodPromo1
  );

IF NOT EXISTS (
  SELECT 1 FROM contenidos_restaurantes 
  WHERE nro_restaurante=@nro_restaurante AND nro_idioma=@nro_idioma AND cod_contenido_restaurante=@CodPromo2
)
  INSERT INTO contenidos_restaurantes (
    nro_restaurante, nro_idioma, nro_sucursal,
    contenido_promocional, imagen_promocional, contenido_a_publicar,
    fecha_ini_vigencia, fecha_fin_vigencia, costo_click, cod_contenido_restaurante
  )
  VALUES (
    @nro_restaurante, @nro_idioma, @nro_sucursal,
    N'Finde en familia: Menú kids con postre incluido.',
    NULL,
    N'Menú especial para chicos disponible sábados y domingos.',
    CAST(GETDATE() AS date),
    DATEADD(DAY, 45, CAST(GETDATE() AS date)),
    35.00,
    @CodPromo2
  );

-------------------------------------------------------------
-- Verificación
-------------------------------------------------------------
SELECT 
  r.razon_social, i.cod_idioma, s.nom_sucursal, 
  c.cod_contenido_restaurante, c.fecha_ini_vigencia, c.fecha_fin_vigencia, c.costo_click
FROM contenidos_restaurantes c
JOIN restaurantes r ON r.nro_restaurante=c.nro_restaurante
JOIN idiomas i      ON i.nro_idioma=c.nro_idioma
LEFT JOIN sucursales_restaurantes s ON s.nro_restaurante=c.nro_restaurante AND s.nro_sucursal=c.nro_sucursal
WHERE c.nro_restaurante=@nro_restaurante AND c.nro_idioma=@nro_idioma
ORDER BY c.fecha_ini_vigencia DESC, c.cod_contenido_restaurante;
