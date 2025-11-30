USE das_ristorino;
GO

-- Crear tabla de preferencias de reservas de restaurantes
CREATE TABLE dbo.preferencias_reservas_restaurantes (
  nro_reserva VARCHAR(36) NOT NULL,
  nro_cliente VARCHAR(36) NOT NULL,
  nro_restaurante VARCHAR(36) NOT NULL,
  cod_categoria VARCHAR(36) NOT NULL,
  nro_valor_dominio INT NOT NULL,
  nro_preferencia INT NOT NULL,
  observaciones NVARCHAR(400) NULL,
  CONSTRAINT PK_preferencias_reservas_restaurantes PRIMARY KEY (nro_reserva, nro_cliente, nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia),
  CONSTRAINT FK_preferencias_reservas_restaurantes_reserva FOREIGN KEY (nro_reserva) REFERENCES dbo.reservas_restaurantes(nro_reserva),
  CONSTRAINT FK_preferencias_reservas_restaurantes_cliente FOREIGN KEY (nro_cliente) REFERENCES dbo.clientes(nro_cliente),
  CONSTRAINT FK_preferencias_reservas_restaurantes_restaurante FOREIGN KEY (nro_restaurante) REFERENCES dbo.restaurantes(nro_restaurante),
  CONSTRAINT FK_preferencias_reservas_restaurantes_categoria FOREIGN KEY (cod_categoria) REFERENCES dbo.categorias_preferencias(cod_categoria),
  CONSTRAINT FK_preferencias_reservas_restaurantes_valor_dominio FOREIGN KEY (cod_categoria, nro_valor_dominio) REFERENCES dbo.dominio_categorias_preferencias(cod_categoria, nro_valor_dominio),
  CONSTRAINT FK_preferencias_reservas_restaurantes_preferencia FOREIGN KEY (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia) REFERENCES dbo.preferencias_restaurantes(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
);

-- Stored procedure para obtener las preferencias de un restaurante para una reserva por restaurante
CREATE OR ALTER PROCEDURE sp_ObtenerPreferenciasDeRestaurantePorId
    @nro_restaurante VARCHAR(36),
    @nro_idioma INT = 0
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @nro_categoria VARCHAR(36)
    SELECT @nro_categoria = cod_categoria FROM categorias_preferencias WHERE nom_categoria = 'Especialidades alimentarias'

    SELECT pr.cod_categoria as codCategoria,
      iddcp.valor_dominio as nombre,
      iddcp.valor_dominio as nroValorDominio
    FROM preferencias_restaurantes pr
    JOIN idiomas_categorias_preferencias idcp ON pr.cod_categoria = idcp.cod_categoria
    JOIN idiomas_dominio_cat_preferencias iddcp ON pr.cod_categoria = iddcp.cod_categoria AND pr.nro_valor_dominio = iddcp.nro_valor_dominio
    WHERE nro_restaurante = @nro_restaurante AND pr.cod_categoria = @nro_categoria AND idcp.nro_idioma = @nro_idioma AND iddcp.nro_idioma = @nro_idioma
END;

-- Stored procedure para insertar preferencias de reservas de restaurantes

-- Stored procedure para obtener preferencias de reservas de restaurantes