-- Habilita todas las zonas de la sucursal en todos sus turnos (cruce zonas×turnos), tomando permite_menores desde la zona
SET NOCOUNT ON;

-------------------------------------------------------------
-- Parámetros
-------------------------------------------------------------
DECLARE 
  @CUIT        VARCHAR(11)   = '30700987654',
  @NomSucursal NVARCHAR(120) = N'Los Aroza - Centro';

-------------------------------------------------------------
-- Resolver IDs
-------------------------------------------------------------
DECLARE @nro_restaurante VARCHAR(36), @nro_sucursal VARCHAR(36);
SELECT @nro_restaurante = r.nro_restaurante FROM restaurantes r WHERE r.cuit=@CUIT;
IF @nro_restaurante IS NULL BEGIN RAISERROR('CUIT no encontrado.',16,1); RETURN; END

SELECT @nro_sucursal = s.nro_sucursal
FROM sucursales_restaurantes s
WHERE s.nro_restaurante=@nro_restaurante AND s.nom_sucursal=@NomSucursal;
IF @nro_sucursal IS NULL BEGIN RAISERROR('Sucursal no encontrada.',16,1); RETURN; END

-------------------------------------------------------------
-- Insertar faltantes: zonas × turnos
-------------------------------------------------------------
INSERT INTO zonas_turnos_sucurales_restaurantes
       (nro_restaurante, nro_sucursal, cod_zona, hora_desde, permite_menores)
SELECT t.nro_restaurante, t.nro_sucursal, zsr.cod_zona, t.hora_desde, zsr.permite_menores
FROM turnos_sucursales_restaurantes t
JOIN zonas_sucursales_restaurantes zsr
  ON zsr.nro_restaurante=t.nro_restaurante AND zsr.nro_sucursal=t.nro_sucursal
LEFT JOIN zonas_turnos_sucurales_restaurantes ztr
  ON ztr.nro_restaurante=t.nro_restaurante AND ztr.nro_sucursal=t.nro_sucursal
 AND ztr.cod_zona=zsr.cod_zona AND ztr.hora_desde=t.hora_desde
WHERE t.nro_restaurante=@nro_restaurante AND t.nro_sucursal=@nro_sucursal
  AND ztr.nro_restaurante IS NULL;

-------------------------------------------------------------
-- Verificación
-------------------------------------------------------------
SELECT zsr.desc_zona, t.hora_desde, t.hora_hasta, ztr.permite_menores
FROM zonas_turnos_sucurales_restaurantes ztr
JOIN zonas_sucursales_restaurantes zsr
  ON zsr.nro_restaurante=ztr.nro_restaurante AND zsr.nro_sucursal=ztr.nro_sucursal AND zsr.cod_zona=ztr.cod_zona
JOIN turnos_sucursales_restaurantes t
  ON t.nro_restaurante=ztr.nro_restaurante AND t.nro_sucursal=ztr.nro_sucursal AND t.hora_desde=ztr.hora_desde
WHERE ztr.nro_restaurante=@nro_restaurante AND ztr.nro_sucursal=@nro_sucursal
ORDER BY t.hora_desde, zsr.desc_zona;
