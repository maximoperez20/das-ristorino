package ar.edu.ubp.das.backend.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class StoredProceduresSetup {
    
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=das_ristorino;encrypt=true;trustServerCertificate=true";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "DB_Password";
    
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            
            System.out.println("🔧 Creando stored procedures...");
            
            // 1. Obtener todas las reservas
            createProcedure(statement, "sp_ObtenerTodasLasReservas", """
                CREATE PROCEDURE sp_ObtenerTodasLasReservas
                AS
                BEGIN
                    SET NOCOUNT ON;
                    SELECT 
                        id, nombre_cliente, email, telefono, fecha_hora, 
                        cantidad_personas, estado, observaciones, fecha_creacion, fecha_actualizacion
                    FROM reservas
                    ORDER BY fecha_hora ASC;
                END
                """);
            
            // 2. Obtener reserva por ID
            createProcedure(statement, "sp_ObtenerReservaPorId", """
                CREATE PROCEDURE sp_ObtenerReservaPorId
                    @id BIGINT
                AS
                BEGIN
                    SET NOCOUNT ON;
                    SELECT 
                        id, nombre_cliente, email, telefono, fecha_hora, 
                        cantidad_personas, estado, observaciones, fecha_creacion, fecha_actualizacion
                    FROM reservas
                    WHERE id = @id;
                END
                """);
            
            // 3. Crear nueva reserva
            createProcedure(statement, "sp_CrearReserva", """
                CREATE PROCEDURE sp_CrearReserva
                    @nombre_cliente NVARCHAR(100),
                    @email NVARCHAR(100),
                    @telefono NVARCHAR(20),
                    @fecha_hora DATETIME2,
                    @cantidad_personas INT,
                    @observaciones NVARCHAR(500),
                    @nuevo_id BIGINT OUTPUT
                AS
                BEGIN
                    SET NOCOUNT ON;
                    INSERT INTO reservas (
                        nombre_cliente, email, telefono, fecha_hora, 
                        cantidad_personas, estado, observaciones, fecha_creacion
                    )
                    VALUES (
                        @nombre_cliente, @email, @telefono, @fecha_hora,
                        @cantidad_personas, 'PENDIENTE', @observaciones, GETDATE()
                    );
                    SET @nuevo_id = SCOPE_IDENTITY();
                END
                """);
            
            // 4. Actualizar reserva existente
            createProcedure(statement, "sp_ActualizarReserva", """
                CREATE PROCEDURE sp_ActualizarReserva
                    @id BIGINT,
                    @nombre_cliente NVARCHAR(100),
                    @email NVARCHAR(100),
                    @telefono NVARCHAR(20),
                    @fecha_hora DATETIME2,
                    @cantidad_personas INT,
                    @estado NVARCHAR(20),
                    @observaciones NVARCHAR(500)
                AS
                BEGIN
                    SET NOCOUNT ON;
                    UPDATE reservas 
                    SET 
                        nombre_cliente = @nombre_cliente,
                        email = @email,
                        telefono = @telefono,
                        fecha_hora = @fecha_hora,
                        cantidad_personas = @cantidad_personas,
                        estado = @estado,
                        observaciones = @observaciones,
                        fecha_actualizacion = GETDATE()
                    WHERE id = @id;
                    SELECT @@ROWCOUNT;
                END
                """);
            
            // 5. Eliminar reserva
            createProcedure(statement, "sp_EliminarReserva", """
                CREATE PROCEDURE sp_EliminarReserva
                    @id BIGINT
                AS
                BEGIN
                    SET NOCOUNT ON;
                    DELETE FROM reservas WHERE id = @id;
                    SELECT @@ROWCOUNT;
                END
                """);
            
            // 6. Obtener reservas por estado
            createProcedure(statement, "sp_ObtenerReservasPorEstado", """
                CREATE PROCEDURE sp_ObtenerReservasPorEstado
                    @estado NVARCHAR(20)
                AS
                BEGIN
                    SET NOCOUNT ON;
                    SELECT 
                        id, nombre_cliente, email, telefono, fecha_hora, 
                        cantidad_personas, estado, observaciones, fecha_creacion, fecha_actualizacion
                    FROM reservas
                    WHERE estado = @estado
                    ORDER BY fecha_hora ASC;
                END
                """);
            
            // 7. Cambiar estado de una reserva
            createProcedure(statement, "sp_CambiarEstadoReserva", """
                CREATE PROCEDURE sp_CambiarEstadoReserva
                    @id BIGINT,
                    @nuevo_estado NVARCHAR(20)
                AS
                BEGIN
                    SET NOCOUNT ON;
                    UPDATE reservas 
                    SET 
                        estado = @nuevo_estado,
                        fecha_actualizacion = GETDATE()
                    WHERE id = @id;
                    SELECT @@ROWCOUNT;
                END
                """);
            
            // 8. Obtener reservas por email del cliente
            createProcedure(statement, "sp_ObtenerReservasPorCliente", """
                CREATE PROCEDURE sp_ObtenerReservasPorCliente
                    @email NVARCHAR(100)
                AS
                BEGIN
                    SET NOCOUNT ON;
                    SELECT 
                        id, nombre_cliente, email, telefono, fecha_hora, 
                        cantidad_personas, estado, observaciones, fecha_creacion, fecha_actualizacion
                    FROM reservas
                    WHERE email = @email
                    ORDER BY fecha_hora ASC;
                END
                """);
            
            // 9. Contar total de reservas
            createProcedure(statement, "sp_ContarReservas", """
                CREATE PROCEDURE sp_ContarReservas
                AS
                BEGIN
                    SET NOCOUNT ON;
                    SELECT COUNT(*) FROM reservas;
                END
                """);
            
            // 10. Verificar si existe una reserva
            createProcedure(statement, "sp_ExisteReserva", """
                CREATE PROCEDURE sp_ExisteReserva
                    @id BIGINT
                AS
                BEGIN
                    SET NOCOUNT ON;
                    SELECT COUNT(*) FROM reservas WHERE id = @id;
                END
                """);
            
            // 11. Obtener reservas por rango de fechas
            createProcedure(statement, "sp_ObtenerReservasPorRangoFechas", """
                CREATE PROCEDURE sp_ObtenerReservasPorRangoFechas
                    @fecha_inicio DATETIME2,
                    @fecha_fin DATETIME2
                AS
                BEGIN
                    SET NOCOUNT ON;
                    SELECT 
                        id, nombre_cliente, email, telefono, fecha_hora, 
                        cantidad_personas, estado, observaciones, fecha_creacion, fecha_actualizacion
                    FROM reservas
                    WHERE fecha_hora BETWEEN @fecha_inicio AND @fecha_fin
                    ORDER BY fecha_hora ASC;
                END
                """);
            
            // 12. Obtener estadísticas de reservas
            createProcedure(statement, "sp_ObtenerEstadisticasReservas", """
                CREATE PROCEDURE sp_ObtenerEstadisticasReservas
                AS
                BEGIN
                    SET NOCOUNT ON;
                    SELECT 
                        COUNT(*) as total_reservas,
                        COUNT(CASE WHEN estado = 'CONFIRMADA' THEN 1 END) as reservas_confirmadas,
                        COUNT(CASE WHEN estado = 'PENDIENTE' THEN 1 END) as reservas_pendientes,
                        COUNT(CASE WHEN estado = 'CANCELADA' THEN 1 END) as reservas_canceladas,
                        AVG(CAST(cantidad_personas AS FLOAT)) as promedio_personas,
                        SUM(cantidad_personas) as total_personas
                    FROM reservas;
                END
                """);
            
            System.out.println("✅ Todos los stored procedures han sido creados exitosamente!");
            
            statement.close();
            connection.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Error al crear stored procedures: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createProcedure(Statement statement, String procedureName, String procedureBody) throws SQLException {
        try {
            // Eliminar el stored procedure si existe
            String dropSql = "IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = '" + procedureName + "') DROP PROCEDURE " + procedureName;
            statement.execute(dropSql);
            
            // Crear el stored procedure
            statement.execute(procedureBody);
            System.out.println("✅ " + procedureName + " creado exitosamente");
            
        } catch (SQLException e) {
            System.err.println("❌ Error al crear " + procedureName + ": " + e.getMessage());
            throw e;
        }
    }
}