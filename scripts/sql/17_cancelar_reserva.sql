/* TABLA DE MOTIVOS DE CANCELACION */
use das_ristorino

IF OBJECT_ID('dbo.motivos_cancelacion', 'U') IS NOT NULL DROP TABLE dbo.motivos_cancelacion;

GO
CREATE TABLE motivos_cancelacion (
    cod_motivo_cancelacion VARCHAR(36) NOT NULL DEFAULT NEWID(),
    descripcion VARCHAR(255) NOT NULL,
		CONSTRAINT PK_motivos_cancelacion PRIMARY KEY (cod_motivo_cancelacion)
);
GO 

/* PROCEDURE GET MOTIVOS CANCELACION */
CREATE OR ALTER PROCEDURE sp_get_motivos_cancelacion
AS
BEGIN
    SELECT * FROM motivos_cancelacion;
END

GO

/* CAMPO EN TABLA RESERVAS */ 
ALTER TABLE reservas_restaurantes
ADD 
    cod_motivo_cancelacion VARCHAR(36)FOREIGN KEY REFERENCES motivos_cancelacion(cod_motivo_cancelacion);


GO
/* PROCEDURE CANCELAR RESERVA */

CREATE OR ALTER PROCEDURE sp_cancelar_reserva
    @nro_reserva varchar(36),
    @cod_motivo_cancelacion varchar(36),
    @notas VARCHAR(400) = NULL
AS
BEGIN
    DECLARE @cod_estado_nuevo VARCHAR(36);
    
    SELECT @cod_estado_nuevo = cod_estado 
    FROM estados_reservas 
    WHERE UPPER(LTRIM(RTRIM(nom_estado))) ='CANCELADA';

    UPDATE reservas_restaurantes
    SET cod_estado = @cod_estado_nuevo,
        cod_motivo_cancelacion = @cod_motivo_cancelacion,
        notas = @notas,
        fecha_hora_cancelacion = GETDATE()
    WHERE nro_reserva = @nro_reserva;    
    SELECT @@ROWCOUNT;
END

GO


/* DATOS INICIALES MOTIVOS CANCELACION */
INSERT INTO motivos_cancelacion (descripcion) VALUES
('Cambio de planes'),
('Enfermedad'),
('Clima adverso'),
('Problemas de transporte'),
('Error en la reserva'),
('Otro');
GO

/* LISTAR CON EL PROCEDURE LOS MOTIVOS DE CANCELACION */
EXEC sp_get_motivos_cancelacion;