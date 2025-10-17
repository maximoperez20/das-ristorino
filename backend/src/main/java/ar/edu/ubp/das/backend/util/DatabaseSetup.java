package ar.edu.ubp.das.backend.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {
    
    private static final String URL = "jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "DB_Password";
    private static final String DATABASE_NAME = "das_ristorino";
    
    public static void main(String[] args) {
        try {
            // Conectar al servidor SQL Server (sin especificar base de datos)
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            
            // Crear la base de datos si no existe
            String createDbSql = "IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = '" + DATABASE_NAME + "') " +
                               "CREATE DATABASE " + DATABASE_NAME;
            
            statement.execute(createDbSql);
            System.out.println("✅ Base de datos '" + DATABASE_NAME + "' creada o ya existe");
            
            // Cerrar conexión
            statement.close();
            connection.close();
            
            // Conectar a la base de datos específica
            String dbUrl = URL + ";databaseName=" + DATABASE_NAME;
            connection = DriverManager.getConnection(dbUrl, USERNAME, PASSWORD);
            statement = connection.createStatement();
            
            // Crear tabla de reservas
            String createTableSql = """
                IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='reservas' AND xtype='U')
                BEGIN
                    CREATE TABLE reservas (
                        id BIGINT IDENTITY(1,1) PRIMARY KEY,
                        nombre_cliente NVARCHAR(100) NOT NULL,
                        email NVARCHAR(100) NOT NULL,
                        telefono NVARCHAR(20) NOT NULL,
                        fecha_hora DATETIME2 NOT NULL,
                        cantidad_personas INT NOT NULL,
                        estado NVARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
                        observaciones NVARCHAR(500),
                        fecha_creacion DATETIME2 NOT NULL DEFAULT GETDATE(),
                        fecha_actualizacion DATETIME2
                    );
                    PRINT 'Tabla reservas creada exitosamente';
                END
                """;
            
            statement.execute(createTableSql);
            System.out.println("✅ Tabla 'reservas' creada o ya existe");
            
            // Insertar datos de ejemplo
            String insertDataSql = """
                IF NOT EXISTS (SELECT 1 FROM reservas)
                BEGIN
                    INSERT INTO reservas (nombre_cliente, email, telefono, fecha_hora, cantidad_personas, estado, observaciones)
                    VALUES 
                        ('Juan Pérez', 'juan@email.com', '123456789', DATEADD(day, 1, GETDATE()), 4, 'CONFIRMADA', 'Mesa cerca de la ventana'),
                        ('María García', 'maria@email.com', '987654321', DATEADD(day, 2, GETDATE()), 2, 'PENDIENTE', 'Cumpleaños'),
                        ('Carlos López', 'carlos@email.com', '555666777', DATEADD(day, 3, GETDATE()), 6, 'CONFIRMADA', 'Cena de negocios');
                    PRINT 'Datos de ejemplo insertados';
                END
                """;
            
            statement.execute(insertDataSql);
            System.out.println("✅ Datos de ejemplo insertados o ya existen");
            
            // Cerrar conexión
            statement.close();
            connection.close();
            
            System.out.println("🎉 Configuración de base de datos completada exitosamente!");
            
        } catch (SQLException e) {
            System.err.println("❌ Error al configurar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
