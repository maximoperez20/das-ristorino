#!/bin/bash

# Script para ejecutar todos los scripts SQL en orden
# Usando sqlcmd directamente en el contenedor Docker

echo "🔧 Ejecutando scripts SQL en orden..."

# Lista de scripts en orden
scripts=(
    "00_risto.sql"
    "01_insert_basicos.sql"
    "02_insert_resto_sucursal_turnos.sql"
    "03_insert_zonas_sucursal.sql"
    "04_insert_zonas_x_turno.sql"
    "05_insert_categorias_y_dominios.sql"
    "06_insert_clientes_demo.sql"
    "07_insert_contenidos_demo.sql"
    "08_insert_traducciones_zonas.sql (opcional, útil para i18n del front).sql"
    "09_insert_preferencias_restaurante.sql"
)

# Ejecutar cada script
for script in "${scripts[@]}"; do
    echo "🔧 Ejecutando script: $script"
    
    # Copiar el script al contenedor
    docker cp "../scripts/$script" SQL_Server_Docker:/tmp/
    
    # Ejecutar el script
    docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
        -S localhost \
        -U sa \
        -P DB_Password \
        -d das_ristorino \
        -i "/tmp/$script"
    
    if [ $? -eq 0 ]; then
        echo "✅ Script $script ejecutado exitosamente"
    else
        echo "❌ Error ejecutando script $script"
    fi
    
    echo ""
done

echo "🎉 Todos los scripts han sido ejecutados!"
