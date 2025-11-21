-- Inserta clientes de demo (idempotente), resolviendo localidad por nombre y provincia
SET NOCOUNT ON;

-------------------------------------------------------------
-- Parámetros de localidades (ajustables a tu dataset)
-------------------------------------------------------------
DECLARE 
  @ProvCba NVARCHAR(80)  = N'Córdoba',
  @LocCba  NVARCHAR(100) = N'Córdoba',
  @LocVcp  NVARCHAR(100) = N'Villa Carlos Paz',
  @ProvBsA NVARCHAR(80)  = N'Buenos Aires',
  @LocLPl  NVARCHAR(100) = N'La Plata';

-------------------------------------------------------------
-- Resolver IDs de localidades
-------------------------------------------------------------
DECLARE @cod_cba VARCHAR(36), @cod_ba VARCHAR(36);
SELECT @cod_cba = cod_provincia FROM provincias WHERE nom_provincia=@ProvCba;
SELECT @cod_ba  = cod_provincia FROM provincias WHERE nom_provincia=@ProvBsA;

DECLARE @loc_cba VARCHAR(36), @loc_vcp VARCHAR(36), @loc_lpl VARCHAR(36);
SELECT @loc_cba = nro_localidad FROM localidades WHERE nom_localidad=@LocCba AND cod_provincia=@cod_cba;
SELECT @loc_vcp = nro_localidad FROM localidades WHERE nom_localidad=@LocVcp AND cod_provincia=@cod_cba;
SELECT @loc_lpl = nro_localidad FROM localidades WHERE nom_localidad=@LocLPl AND cod_provincia=@cod_ba;

IF @loc_cba IS NULL OR @loc_vcp IS NULL OR @loc_lpl IS NULL
BEGIN
  RAISERROR('Faltan localidades base. Corré primero 01_insert_basicos.sql', 16, 1);
  RETURN;
END

-------------------------------------------------------------
-- Insert idempotente de clientes demo
-------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM clientes WHERE correo=N'ana.rodriguez@mail.com')
  INSERT INTO clientes (apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado)
  VALUES (N'Rodríguez', N'Ana', N'$2y$dummy$hash', N'ana.rodriguez@mail.com', N'351-555-1111', @loc_cba, 1);

IF NOT EXISTS (SELECT 1 FROM clientes WHERE correo=N'max.ferreyra@mail.com')
  INSERT INTO clientes (apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado)
  VALUES (N'Ferreyra', N'Maximiliano', N'$2y$dummy$hash', N'max.ferreyra@mail.com', N'351-555-2222', @loc_vcp, 1);

IF NOT EXISTS (SELECT 1 FROM clientes WHERE correo=N'carla.sosa@mail.com')
  INSERT INTO clientes (apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado)
  VALUES (N'Sosa', N'Carla', N'$2y$dummy$hash', N'carla.sosa@mail.com', N'221-555-3333', @loc_lpl, 1);

-------------------------------------------------------------
-- Verificación
-------------------------------------------------------------
SELECT TOP 50 nro_cliente, apellido, nombre, correo, telefonos
FROM clientes
ORDER BY apellido, nombre;
