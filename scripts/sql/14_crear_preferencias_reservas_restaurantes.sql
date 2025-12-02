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

GO

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
      iddcp.nro_valor_dominio as nroValorDominio
    FROM preferencias_restaurantes pr
    JOIN idiomas_categorias_preferencias idcp ON pr.cod_categoria = idcp.cod_categoria
    JOIN idiomas_dominio_cat_preferencias iddcp ON pr.cod_categoria = iddcp.cod_categoria AND pr.nro_valor_dominio = iddcp.nro_valor_dominio
    WHERE nro_restaurante = @nro_restaurante AND pr.cod_categoria = @nro_categoria AND idcp.nro_idioma = @nro_idioma AND iddcp.nro_idioma = @nro_idioma
END;

GO
-- Stored procedure para insertar preferencias de reservas de restaurantes
CREATE OR ALTER PROCEDURE sp_InsertarPreferenciasReserva
    @nro_reserva VARCHAR(36),
    @nro_cliente VARCHAR(36),
    @nro_restaurante VARCHAR(36),
    @preferencias NVARCHAR(MAX) -- JSON array de nro_valor_dominio: [1, 2, 3]
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRANSACTION;
    
    BEGIN TRY
        -- Obtener el cod_categoria de "Especialidades alimentarias"
        DECLARE @cod_categoria VARCHAR(36);
        SELECT @cod_categoria = cod_categoria 
        FROM categorias_preferencias 
        WHERE nom_categoria = 'Especialidades alimentarias';
        
        IF @cod_categoria IS NULL
        BEGIN
            RAISERROR('Categoría "Especialidades alimentarias" no encontrada', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END
        
        -- Parsear JSON y insertar preferencias
        -- El JSON debe tener formato: [1, 2, 3] (array de nro_valor_dominio)
        INSERT INTO preferencias_reservas_restaurantes (
            nro_reserva,
            nro_cliente,
            nro_restaurante,
            cod_categoria,
            nro_valor_dominio,
            nro_preferencia,
            observaciones
        )
        SELECT 
            @nro_reserva,
            @nro_cliente,
            @nro_restaurante,
            @cod_categoria,
            CAST(oj.value AS INT) AS nro_valor_dominio,
            MIN(pr.nro_preferencia) AS nro_preferencia, -- Usar el primer nro_preferencia si hay múltiples
            NULL AS observaciones
        FROM OPENJSON(@preferencias) oj
        INNER JOIN preferencias_restaurantes pr
            ON pr.nro_restaurante = @nro_restaurante
            AND pr.cod_categoria = @cod_categoria
            AND pr.nro_valor_dominio = CAST(oj.value AS INT)
            AND pr.nro_sucursal IS NULL -- Solo preferencias del restaurante, no de sucursal específica
        GROUP BY CAST(oj.value AS INT)
        
        COMMIT TRANSACTION;
        
        SELECT @@ROWCOUNT AS preferencias_insertadas;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

-- Stored procedure para obtener preferencias de reservas de restaurantes
CREATE OR ALTER PROCEDURE sp_ObtenerPreferenciasReserva
    @nro_reserva VARCHAR(36),
    @nro_idioma INT = 0
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        prr.cod_categoria AS codCategoria,
        ISNULL(icp.categoria, cp.nom_categoria) AS nombreCategoria,
        prr.nro_valor_dominio AS nroValorDominio,
        ISNULL(idcp.valor_dominio, dcp.nom_valor_dominio) AS nombreDominio,
        prr.nro_preferencia AS nroPreferencia,
        prr.observaciones
    FROM preferencias_reservas_restaurantes prr
    INNER JOIN categorias_preferencias cp ON prr.cod_categoria = cp.cod_categoria
    LEFT JOIN idiomas_categorias_preferencias icp 
        ON cp.cod_categoria = icp.cod_categoria 
        AND icp.nro_idioma = @nro_idioma
    INNER JOIN dominio_categorias_preferencias dcp 
        ON prr.cod_categoria = dcp.cod_categoria 
        AND prr.nro_valor_dominio = dcp.nro_valor_dominio
    LEFT JOIN idiomas_dominio_cat_preferencias idcp
        ON dcp.cod_categoria = idcp.cod_categoria
        AND dcp.nro_valor_dominio = idcp.nro_valor_dominio
        AND idcp.nro_idioma = @nro_idioma
    WHERE prr.nro_reserva = @nro_reserva
    ORDER BY ISNULL(icp.categoria, cp.nom_categoria), ISNULL(idcp.valor_dominio, dcp.nom_valor_dominio);
END;
GO