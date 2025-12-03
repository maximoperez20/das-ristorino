-- crear tabla
CREATE TABLE resenas (
  nro_resena VARCHAR(36) PRIMARY KEY DEFAULT NEWID(),
  nro_cliente VARCHAR(36) NOT NULL,
  nro_restaurante VARCHAR(36) NOT NULL,
  nro_sucursal VARCHAR(36) NOT NULL,
  fecha_resena DATE NOT NULL DEFAULT GETDATE(),
  comentario VARCHAR(255),
  calificacion INT NOT NULL CHECK (calificacion >= 1 AND calificacion <= 5),
  CONSTRAINT FK_resenas_cliente FOREIGN KEY (nro_cliente) REFERENCES clientes(nro_cliente),
  CONSTRAINT FK_resenas_restaurante FOREIGN KEY (nro_restaurante) REFERENCES restaurantes(nro_restaurante),
  CONSTRAINT FK_resenas_sucursal FOREIGN KEY (nro_restaurante, nro_sucursal) REFERENCES sucursales_restaurantes(nro_restaurante, nro_sucursal)
);
GO

-- Stored procedure para obtener reseñas por restaurante y sucursal
CREATE OR ALTER PROCEDURE sp_ObtenerResenasPorRestaurante
    @nro_restaurante VARCHAR(36),
    @nro_sucursal VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        c.nombre + ' ' + c.apellido AS nombreCliente,
        r.calificacion,
        r.comentario,
        r.fecha_resena AS fechaResena
    FROM resenas r
    JOIN clientes c ON r.nro_cliente = c.nro_cliente
    WHERE r.nro_restaurante = @nro_restaurante 
      AND r.nro_sucursal = @nro_sucursal
    ORDER BY r.fecha_resena DESC;
END;
GO

-- Stored procedure para insertar reseña (genera nro_resena automáticamente)
CREATE OR ALTER PROCEDURE sp_InsertarResena
    @nro_reserva VARCHAR(36),
    @comentario VARCHAR(255),
    @calificacion INT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @nro_resena VARCHAR(36) = NEWID();
    DECLARE @fecha_resena DATE = GETDATE();

    DECLARE @nro_cliente VARCHAR(36) = (SELECT nro_cliente FROM reservas_restaurantes WHERE nro_reserva = @nro_reserva);
    DECLARE @nro_restaurante VARCHAR(36) = (SELECT nro_restaurante FROM reservas_restaurantes WHERE nro_reserva = @nro_reserva);
    DECLARE @nro_sucursal VARCHAR(36) = (SELECT nro_sucursal FROM reservas_restaurantes WHERE nro_reserva = @nro_reserva);
    
    INSERT INTO resenas (nro_resena, nro_cliente, nro_restaurante, nro_sucursal, fecha_resena, comentario, calificacion)
    VALUES (@nro_resena, @nro_cliente, @nro_restaurante, @nro_sucursal, @fecha_resena, @comentario, @calificacion);
END;
GO
