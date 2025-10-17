#!/bin/bash

# Script de verificación para das-ristorino
# Verifica que la base de datos esté configurada correctamente

echo "🔍 Verificando configuración de das-ristorino..."

# Verificar que Docker esté corriendo
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker no está corriendo"
    exit 1
fi

# Verificar que el contenedor SQL Server esté corriendo
if ! docker ps | grep -q "SQL_Server_Docker"; then
    echo "❌ Contenedor SQL Server no está corriendo"
    echo "   Ejecuta: docker start SQL_Server_Docker"
    exit 1
fi

echo "✅ Docker y SQL Server están corriendo"

# Verificar conexión a la base de datos
if ! docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password -d das_ristorino \
   -Q "SELECT 1;" > /dev/null 2>&1; then
    echo "❌ No se puede conectar a la base de datos das_ristorino"
    echo "   Ejecuta: ./setup-database.sh"
    exit 1
fi

echo "✅ Conexión a base de datos exitosa"

# Verificar tablas
tables_count=$(docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password -d das_ristorino \
   -Q "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE';" \
   -h -1 | tr -d ' \n')

if [ "$tables_count" -lt 20 ]; then
    echo "❌ Faltan tablas en la base de datos ($tables_count encontradas)"
    echo "   Ejecuta: ./setup-database.sh"
    exit 1
fi

echo "✅ Tablas configuradas correctamente ($tables_count tablas)"

# Verificar stored procedures
procedures_count=$(docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password -d das_ristorino \
   -Q "SELECT COUNT(*) FROM sys.procedures WHERE name LIKE 'sp_%';" \
   -h -1 | tr -d ' \n')

if [ "$procedures_count" -lt 10 ]; then
    echo "❌ Faltan stored procedures ($procedures_count encontrados)"
    echo "   Ejecuta: ./setup-database.sh"
    exit 1
fi

echo "✅ Stored procedures configurados correctamente ($procedures_count procedures)"

# Verificar datos básicos
provincias_count=$(docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password -d das_ristorino \
   -Q "SELECT COUNT(*) FROM provincias;" \
   -h -1 | tr -d ' \n')

restaurantes_count=$(docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password -d das_ristorino \
   -Q "SELECT COUNT(*) FROM restaurantes;" \
   -h -1 | tr -d ' \n')

echo "✅ Datos básicos: $provincias_count provincias, $restaurantes_count restaurantes"

# Verificar que la aplicación pueda compilar
if [ -d "backend" ]; then
    cd backend
    if ./mvnw compile > /dev/null 2>&1; then
        echo "✅ Aplicación compila correctamente"
    else
        echo "❌ Error al compilar la aplicación"
        echo "   Verifica que tengas Java 21 instalado"
        exit 1
    fi
    cd ..
else
    echo "⚠️ Directorio backend no encontrado"
fi

echo ""
echo "🎉 ¡Todo está configurado correctamente!"
echo ""
echo "🚀 Para ejecutar la aplicación:"
echo "   cd backend"
echo "   ./mvnw spring-boot:run"
echo ""
echo "🌐 La aplicación estará disponible en: http://localhost:8080"
echo ""
echo "📋 Endpoints disponibles:"
echo "   GET  /api/reservas              - Obtener todas las reservas"
echo "   POST /api/reservas              - Crear nueva reserva"
echo "   GET  /api/reservas/{id}         - Obtener reserva por ID"
echo "   GET  /api/reservas/estadisticas - Obtener estadísticas"
