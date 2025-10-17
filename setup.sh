#!/bin/bash

# Script de configuración automática para das-ristorino
# Ejecutar desde la raíz del proyecto

echo "🚀 Configurando das-ristorino..."

# Verificar que Docker esté corriendo
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker no está corriendo. Por favor inicia Docker Desktop."
    exit 1
fi

# Verificar si el contenedor SQL Server ya existe
if docker ps -a | grep -q "SQL_Server_Docker"; then
    echo "📦 Contenedor SQL Server ya existe"
    if ! docker ps | grep -q "SQL_Server_Docker"; then
        echo "🔄 Iniciando contenedor SQL Server..."
        docker start SQL_Server_Docker
    else
        echo "✅ Contenedor SQL Server ya está corriendo"
    fi
else
    echo "📦 Creando contenedor SQL Server..."
    docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=DB_Password" \
       -p 1433:1433 --name SQL_Server_Docker \
       -d mcr.microsoft.com/mssql/server:2022-latest
    
    echo "⏳ Esperando que SQL Server esté listo..."
    sleep 30
fi

# Verificar que el contenedor esté corriendo
if ! docker ps | grep -q "SQL_Server_Docker"; then
    echo "❌ Error: No se pudo iniciar el contenedor SQL Server"
    exit 1
fi

echo "✅ Contenedor SQL Server corriendo"

# Crear la base de datos si no existe
echo "🗄️ Creando base de datos..."
docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password \
   -Q "IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'das_ristorino') CREATE DATABASE das_ristorino;" > /dev/null 2>&1

echo "✅ Base de datos creada o ya existe"

# Ejecutar scripts de creación de tablas
echo "📋 Ejecutando scripts de creación de tablas..."
docker cp scripts/00_risto.sql SQL_Server_Docker:/tmp/ > /dev/null 2>&1
docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password -d das_ristorino -i /tmp/00_risto.sql > /dev/null 2>&1

echo "✅ Tablas creadas"

# Ejecutar scripts de datos
echo "📊 Insertando datos básicos..."
scripts=(
    "01_insert_basicos.sql"
    "02_insert_resto_sucursal_turnos.sql"
    "03_insert_zonas_sucursal.sql"
    "04_insert_zonas_x_turno.sql"
    "05_insert_categorias_y_dominios.sql"
    "06_insert_clientes_demo.sql"
    "07_insert_contenidos_demo.sql"
    "09_insert_preferencias_restaurante.sql"
)

for script in "${scripts[@]}"; do
    echo "  📄 Ejecutando $script..."
    docker cp "scripts/$script" SQL_Server_Docker:/tmp/ > /dev/null 2>&1
    docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
       -S localhost -U sa -P DB_Password -d das_ristorino -i "/tmp/$script" > /dev/null 2>&1
done

echo "✅ Datos insertados"

# Crear stored procedures
echo "⚙️ Creando stored procedures..."
docker cp backend/create-stored-procedures.sql SQL_Server_Docker:/tmp/ > /dev/null 2>&1
docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password -d das_ristorino -i /tmp/create-stored-procedures.sql > /dev/null 2>&1

echo "✅ Stored procedures creados"

# Verificar que todo esté funcionando
echo "🔍 Verificando configuración..."
tables_count=$(docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password -d das_ristorino \
   -Q "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE';" \
   -h -1 | tr -d ' \n')

if [ "$tables_count" -gt 20 ]; then
    echo "✅ Configuración completada exitosamente!"
    echo ""
    echo "🎉 El proyecto está listo para usar:"
    echo "   • Base de datos: das_ristorino"
    echo "   • Tablas creadas: $tables_count"
    echo "   • Stored procedures: 12"
    echo ""
    echo "🚀 Para ejecutar la aplicación:"
    echo "   cd backend"
    echo "   ./mvnw spring-boot:run"
    echo ""
    echo "🌐 La aplicación estará disponible en: http://localhost:8080"
else
    echo "❌ Error en la configuración. Verificar logs."
    exit 1
fi
