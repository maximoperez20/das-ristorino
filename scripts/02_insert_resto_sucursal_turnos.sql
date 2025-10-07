-- Crea/asegura el restaurante, su sucursal y genera turnos cada 2 horas hasta 00:00 (idempotente)
SET NOCOUNT ON;

-------------------------------------------------------------
-- Parámetros (ajustables)
-------------------------------------------------------------
DECLARE 
  @RazonSocial NVARCHAR(150) = N'Los Aroza SRL',
  @CUIT        VARCHAR(11)   = '30700987654',
  @NomSucursal NVARCHAR(120) = N'Los Aroza - Centro',
  @Calle       NVARCHAR(120) = N'Av. Colón',
  @NroCalle    INT           = 950,
  @Barrio      NVARCHAR(120) = N'Centro',
  @Provincia   NVARCHAR(80)  = N'Córdoba',
  @Localidad   NVARCHAR(100) = N'Córdoba',
  @CP          NVARCHAR(10)  = N'5000',
  @Telefonos   NVARCHAR(120) = N'351-555-1234',
  @CapTotal    INT           = 140,
  @MinTol      INT           = 15,
  @Categoria   NVARCHAR(40)  = N'Media',
  @Apertura    TIME          = '16:00';   -- → 16,18,20,22 → 00:00

-------------------------------------------------------------
-- 1) Restaurante
-------------------------------------------------------------
DECLARE @nro_restaurante VARCHAR(36);
IF NOT EXISTS (SELECT 1 FROM restaurantes WHERE cuit=@CUIT)
  INSERT INTO restaurantes (razon_social, cuit) VALUES (@RazonSocial, @CUIT);

SELECT @nro_restaurante = nro_restaurante
FROM restaurantes WHERE cuit=@CUIT;

-------------------------------------------------------------
-- 2) Resolver localidad y categoría (por nombre)
-------------------------------------------------------------
DECLARE @cod_provincia VARCHAR(36), @nro_localidad VARCHAR(36), @nro_categoria VARCHAR(36);

SELECT @cod_provincia = cod_provincia FROM provincias WHERE nom_provincia=@Provincia;
SELECT @nro_localidad = nro_localidad 
FROM localidades WHERE nom_localidad=@Localidad AND cod_provincia=@cod_provincia;

-- Si manejás categorías en otra tabla, resolvela aquí; si no aplica, comentá la línea siguiente:
-- SELECT @nro_categoria = nro_categoria FROM categorias_precios WHERE nom_categoria=@Categoria;

-------------------------------------------------------------
-- 3) Sucursal (idempotente)
-------------------------------------------------------------
DECLARE @nro_sucursal VARCHAR(36);
IF NOT EXISTS (
  SELECT 1 FROM sucursales_restaurantes 
  WHERE nro_restaurante=@nro_restaurante AND nom_sucursal=@NomSucursal
)
BEGIN
  INSERT INTO sucursales_restaurantes (
    nro_restaurante, nom_sucursal, calle, nro_calle, barrio,
    nro_localidad, cod_postal, telefonos, total_comensales,
    min_tolerencia_reserva, cod_sucursal_restaurante
  ) VALUES (
    @nro_restaurante, @NomSucursal, @Calle, @NroCalle, @Barrio,
    @nro_localidad, @CP, @Telefonos, @CapTotal,
    @MinTol, NULL
  );
END

SELECT TOP 1 @nro_sucursal = nro_sucursal
FROM sucursales_restaurantes
WHERE nro_restaurante=@nro_restaurante AND nom_sucursal=@NomSucursal;

-------------------------------------------------------------
-- 4) Turnos cada 120 min desde @Apertura hasta 00:00
-------------------------------------------------------------
DECLARE @t TIME = @Apertura, @hHasta TIME, @i INT = 0;

WHILE (@i < 12)
BEGIN
  SET @hHasta = CAST(DATEADD(MINUTE, 120, CAST(@t AS datetime2(0))) AS TIME);

  IF NOT EXISTS (
    SELECT 1 FROM turnos_sucursales_restaurantes
    WHERE nro_restaurante=@nro_restaurante AND nro_sucursal=@nro_sucursal AND hora_desde=@t
  )
    INSERT INTO turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
    VALUES (@nro_restaurante, @nro_sucursal, @t, @hHasta, 1);

  IF (@hHasta = '00:00') BREAK;
  SET @t = @hHasta; SET @i += 1;
END

-------------------------------------------------------------
-- 5) Verificación rápida
-------------------------------------------------------------
SELECT r.razon_social AS Restaurante, s.nom_sucursal, t.hora_desde, t.hora_hasta, t.habilitado
FROM turnos_sucursales_restaurantes t
JOIN sucursales_restaurantes s ON s.nro_restaurante=t.nro_restaurante AND s.nro_sucursal=t.nro_sucursal
JOIN restaurantes r            ON r.nro_restaurante=s.nro_restaurante
WHERE r.cuit=@CUIT
ORDER BY s.nom_sucursal, t.hora_desde;
