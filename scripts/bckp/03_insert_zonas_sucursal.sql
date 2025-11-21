-- Crea (si faltan) las zonas de una sucursal con capacidad, permite_menores y habilitada
SET NOCOUNT ON;

-------------------------------------------------------------
-- Parámetros
-------------------------------------------------------------
DECLARE 
  @CUIT        VARCHAR(11)   = '30700987654',
  @NomSucursal NVARCHAR(120) = N'Los Aroza - Centro';

-- Zonas “semilla” (ajustá nombres/capacidades)
DECLARE @Zonas TABLE (
  desc_zona       NVARCHAR(200),
  cant_comensales INT,
  permite_menores BIT,
  habilitada      BIT
);
INSERT INTO @Zonas VALUES
(N'Salón Principal', 90, 1, 1),
(N'Terraza',         50, 1, 1);
-- (agregá más si querés, ej.: (N'Patio Cubierto', 40, 1, 1))

-------------------------------------------------------------
-- Resolver IDs restaurante y sucursal
-------------------------------------------------------------
DECLARE @nro_restaurante VARCHAR(36), @nro_sucursal VARCHAR(36);
SELECT @nro_restaurante = r.nro_restaurante FROM restaurantes r WHERE r.cuit = @CUIT;
IF @nro_restaurante IS NULL BEGIN RAISERROR('CUIT no encontrado.',16,1); RETURN; END

SELECT @nro_sucursal = s.nro_sucursal
FROM sucursales_restaurantes s
WHERE s.nro_restaurante=@nro_restaurante AND s.nom_sucursal=@NomSucursal;
IF @nro_sucursal IS NULL BEGIN RAISERROR('Sucursal no encontrada.',16,1); RETURN; END

-------------------------------------------------------------
-- Insert idempotente de zonas
-------------------------------------------------------------
DECLARE @dz NVARCHAR(200), @cap INT, @pm BIT, @hab BIT;

DECLARE curZ CURSOR LOCAL FAST_FORWARD FOR
  SELECT desc_zona, cant_comensales, permite_menores, habilitada FROM @Zonas;
OPEN curZ;
FETCH NEXT FROM curZ INTO @dz, @cap, @pm, @hab;
WHILE @@FETCH_STATUS = 0
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM zonas_sucursales_restaurantes
    WHERE nro_restaurante=@nro_restaurante AND nro_sucursal=@nro_sucursal AND desc_zona=@dz
  )
    INSERT INTO zonas_sucursales_restaurantes
      (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada)
    VALUES
      (@nro_restaurante, @nro_sucursal, @dz, @cap, @pm, @hab);

  FETCH NEXT FROM curZ INTO @dz, @cap, @pm, @hab;
END
CLOSE curZ; DEALLOCATE curZ;

-------------------------------------------------------------
-- Verificación
-------------------------------------------------------------
SELECT desc_zona, cant_comensales, permite_menores, habilitada
FROM zonas_sucursales_restaurantes
WHERE nro_restaurante=@nro_restaurante AND nro_sucursal=@nro_sucursal
ORDER BY desc_zona;
