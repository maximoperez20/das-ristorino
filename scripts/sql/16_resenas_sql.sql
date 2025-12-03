-- crear tabla
CREATE TABLE resenas (
  nro_resena VARCHAR(36) NOT NULL,
  nro_cliente VARCHAR(36) NOT NULL,
  nro_restaurante VARCHAR(36) NOT NULL,
  nro_sucursal VARCHAR(36) NOT NULL,
  fecha_resena DATE,
  comentario VARCHAR(255),
  calificacion INT NOT NULL,
)

-- crear foreign key
ALTER TABLE resenas
ADD FOREIGN KEY (nro_cliente) REFERENCES clientes(nro_cliente);

ALTER TABLE resenas
ADD FOREIGN KEY (nro_restaurante) REFERENCES restaurantes(nro_restaurante);

ALTER TABLE resenas
ADD FOREIGN KEY (nro_sucursal) REFERENCES reservas_sucursales(nro_sucursal);


-- procedimientos almacenados

-- Stored procedure para obtener las preferencias de un restaurante para una reserva por restaurante
CREATE OR ALTER PROCEDURE sp_ObtenerResenasPorRestaurante
    @nro_restaurante VARCHAR(36),
    @nro_sucursal VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
      clientes.nombre AS nombreCliente ,
      calificacion,
      comentario,
      fecha_resena AS fechaResena
    FROM resenas
    JOIN clientes ON resenas.nro_cliente = clientes.nro_cliente
    WHERE nro_restaurante = @nro_restaurante AND nro_sucursal = @nro_sucursal
END;
GO

CREATE OR ALTER PROCEDURE sp_InsertarResena
    @nro_resena VARCHAR(36),
    @nro_cliente VARCHAR(36),
    @nro_restaurante VARCHAR(36),
    @nro_sucursal VARCHAR(36),
    @comentario VARCHAR(255),
    @calificacion INT
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO resenas (nro_resena, nro_cliente, nro_restaurante, nro_sucursal, comentario, calificacion)
    VALUES (@nro_resena, @nro_cliente, @nro_restaurante, @nro_sucursal, @comentario, @calificacion);
END;
GO
