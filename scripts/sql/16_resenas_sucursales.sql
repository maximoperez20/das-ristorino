use das_ristorino

IF OBJECT_ID('dbo.resenas_sucursales', 'U') IS NOT NULL DROP TABLE dbo.resenas_sucursales;

CREATE TABLE dbo.resenas_sucursales (
  cod_resena		VARCHAR(36)   NOT NULL DEFAULT NEWID(), 
  nro_restaurante   VARCHAR(36)  NOT NULL,
  nro_sucursal      VARCHAR(36)  NOT NULL,
  nro_cliente       VARCHAR(36)  NOT NULL,
  comentario		NVARCHAR(400) NOT NULL,
  valoracion		SMALLINT	NOT NULL,
  CONSTRAINT PK_resenas PRIMARY KEY (cod_resena),
  CONSTRAINT FK_resena_rest_suc
    FOREIGN KEY (nro_restaurante, nro_sucursal)
    REFERENCES dbo.sucursales_restaurantes(nro_restaurante, nro_sucursal)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_resena_rest_cliente
    FOREIGN KEY (nro_cliente) REFERENCES dbo.clientes(nro_cliente)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT CK_resenas_valoracion CHECK (valoracion >= 0 AND valoracion <= 5),
);

GO
CREATE OR ALTER PROCEDURE dbo.get_resenas_x_sucursales
  @nro_restaurante VARCHAR(36),
  @nro_sucursal    VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;

    IF NOT EXISTS (SELECT 1 FROM restaurantes WHERE nro_restaurante = @nro_restaurante)
    BEGIN
        SELECT 'Restaurante no encontrado' AS mensaje;
        RETURN;
    END

    IF NOT EXISTS (
        SELECT 1 
        FROM sucursales_restaurantes 
        WHERE nro_restaurante = @nro_restaurante 
          AND nro_sucursal = @nro_sucursal
    )
    BEGIN
        SELECT 'Sucursal no encontrada' AS mensaje;
        RETURN;
    END

    SELECT
        r.cod_resena,
        r.comentario, 
        r.nro_restaurante, 
        r.nro_sucursal,
        r.valoracion, 
        r.nro_cliente,
				c.nombre + ' ' + c.apellido as nombre_cliente 
    FROM resenas_sucursales r
			inner join clientes c
				on r.nro_cliente = c.nro_cliente
    WHERE r.nro_restaurante = @nro_restaurante
      AND r.nro_sucursal    = @nro_sucursal;
END

GO

CREATE OR ALTER PROCEDURE dbo.sp_insertar_resena_sucursal
  @nro_restaurante   VARCHAR(36),
  @nro_sucursal      VARCHAR(36),
  @nro_cliente       VARCHAR(36),
  @comentario		NVARCHAR(400),
  @valoracion		SMALLINT
AS
BEGIN
  SET NOCOUNT ON;
  
  DECLARE @exitoso BIT = 0;
  DECLARE @mensaje NVARCHAR(200) = '';

  BEGIN TRY
      
    IF NOT EXISTS (SELECT 1 FROM restaurantes WHERE nro_restaurante = @nro_restaurante)
    BEGIN
        SELECT 'Restaurante no encontrado' AS mensaje;
        RETURN;
    END

    IF NOT EXISTS (
        SELECT 1 
        FROM sucursales_restaurantes 
        WHERE nro_restaurante = @nro_restaurante 
          AND nro_sucursal = @nro_sucursal
    )
    BEGIN
        SELECT 'Sucursal no encontrada' AS mensaje;
        RETURN;
    END


    INSERT INTO resenas_sucursales(
      nro_restaurante,
      nro_sucursal,
      nro_cliente,
      comentario,
      valoracion      
    )
    VALUES (
      @nro_restaurante,
      @nro_sucursal,
      @nro_cliente,
      @comentario,
      @valoracion
    );

    SET @exitoso = 1;
    SET @mensaje = 'Reseña registrada exitosamente';

  END TRY
  BEGIN CATCH
    SET @mensaje = ERROR_MESSAGE();
  END CATCH
  
END
GO

		
		
EXEC dbo.sp_insertar_resena_sucursal
    @nro_restaurante = 'BELLA-PIZZA-1111-1111-1111-111111111',
    @nro_sucursal = '986340FE-9EAA-4325-990D-3D15EF2EDA78',
		@nro_cliente = 'E79190F4-967E-402D-A90F-54C13902497B',
		@comentario = 'Muy buen ambiente, el servicio fue estupendo. ',
		@valoracion = 5


EXEC dbo.get_resenas_x_sucursales 
    @nro_restaurante = 'BELLA-PIZZA-1111-1111-1111-111111111',
    @nro_sucursal = '986340FE-9EAA-4325-990D-3D15EF2EDA78';

