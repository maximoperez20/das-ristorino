# das-ristorino
Repositorio de app Ristorino - Materia DAS - UBP 2025

## 📋 Descripción
Sistema de gestión de reservas para restaurantes desarrollado con Spring Boot y SQL Server.

## 🛠️ Tecnologías
- **Backend**: Spring Boot 3.5.5, Java 21
- **Base de Datos**: Microsoft SQL Server 2022
- **Build Tool**: Maven
- **Contenedores**: Docker

## 🚀 Configuración Rápida

### Prerrequisitos
- Java 21 o superior
- Maven 3.6+
- Docker Desktop
- Git

### 1. Clonar y Configurar
```bash
git clone <repository-url>
cd das-ristorino

# Configurar base de datos automáticamente
./setup-database.sh

# Verificar que todo esté funcionando
./verify-setup.sh
```

### 2. Ejecutar la Aplicación
```bash
cd backend
./mvnw spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 📊 Estructura de la Base de Datos

### Tablas Principales
- **provincias**: Catálogo de provincias argentinas
- **localidades**: Catálogo de localidades por provincia  
- **restaurantes**: Información de restaurantes
- **sucursales_restaurantes**: Sucursales de cada restaurante
- **clientes**: Información de clientes
- **reservas_restaurantes**: Reservas realizadas
- **zonas_sucursales_restaurantes**: Zonas dentro de cada sucursal
- **turnos_sucursales_restaurantes**: Horarios de atención
- **categorias_preferencias**: Categorías de preferencias
- **contenidos_restaurantes**: Promociones y contenidos

### Stored Procedures Disponibles
- `sp_ObtenerTodasLasReservas`: Obtiene todas las reservas
- `sp_ObtenerReservaPorId`: Obtiene una reserva por ID
- `sp_CrearReserva`: Crea una nueva reserva
- `sp_ActualizarReserva`: Actualiza una reserva existente
- `sp_EliminarReserva`: Elimina una reserva
- `sp_ObtenerReservasPorEstado`: Filtra reservas por estado
- `sp_CambiarEstadoReserva`: Cambia el estado de una reserva
- `sp_ObtenerReservasPorCliente`: Obtiene reservas por email del cliente
- `sp_ContarReservas`: Cuenta el total de reservas
- `sp_ExisteReserva`: Verifica si existe una reserva
- `sp_ObtenerReservasPorRangoFechas`: Filtra reservas por rango de fechas
- `sp_ObtenerEstadisticasReservas`: Obtiene estadísticas de reservas

## 🔌 API Endpoints

### Reservas
- `GET /api/reservas` - Obtener todas las reservas
- `GET /api/reservas/{id}` - Obtener reserva por ID
- `POST /api/reservas` - Crear nueva reserva
- `PUT /api/reservas/{id}` - Actualizar reserva
- `DELETE /api/reservas/{id}` - Eliminar reserva
- `GET /api/reservas/estado/{estado}` - Obtener reservas por estado
- `PUT /api/reservas/{id}/estado` - Cambiar estado de reserva
- `GET /api/reservas/cliente/{email}` - Obtener reservas por cliente
- `GET /api/reservas/estadisticas` - Obtener estadísticas

### Ejemplo de Crear Reserva
```bash
curl -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "nombreCliente": "Juan Pérez",
    "email": "juan@email.com",
    "telefono": "123456789",
    "fechaHora": "2025-10-20T19:00:00",
    "cantidadPersonas": 4,
    "observaciones": "Mesa cerca de la ventana"
  }'
```

## 📁 Estructura del Proyecto
```
das-ristorino/
├── backend/
│   ├── src/main/java/ar/edu/ubp/das/backend/
│   │   ├── components/          # SimpleJdbcCallFactory
│   │   ├── config/             # Configuraciones
│   │   ├── dto/                # Data Transfer Objects
│   │   ├── repository/         # Capa de acceso a datos
│   │   ├── resources/          # Controladores REST
│   │   └── service/            # Lógica de negocio
│   ├── src/main/resources/
│   │   └── application.properties
│   └── create-stored-procedures.sql
├── scripts/                    # Scripts SQL de inicialización
│   ├── 00_risto.sql           # Creación de tablas
│   ├── 01_insert_basicos.sql  # Datos básicos
│   ├── 02_insert_resto_sucursal_turnos.sql
│   ├── 03_insert_zonas_sucursal.sql
│   ├── 04_insert_zonas_x_turno.sql
│   ├── 05_insert_categorias_y_dominios.sql
│   ├── 06_insert_clientes_demo.sql
│   ├── 07_insert_contenidos_demo.sql
│   └── 09_insert_preferencias_restaurante.sql
├── setup-database.sh          # Script de configuración automática
├── verify-setup.sh            # Script de verificación
└── README.md
```

## 🔧 Configuración Manual (si es necesaria)

### Configurar SQL Server con Docker
```bash
# Ejecutar SQL Server en Docker
docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=DB_Password" \
   -p 1433:1433 --name SQL_Server_Docker \
   -d mcr.microsoft.com/mssql/server:2022-latest
```

### Configurar la Base de Datos Manualmente
```bash
# Crear la base de datos
docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password \
   -Q "CREATE DATABASE das_ristorino;"

# Ejecutar scripts de creación de tablas
docker cp scripts/00_risto.sql SQL_Server_Docker:/tmp/
docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password -d das_ristorino -i /tmp/00_risto.sql

# Crear stored procedures
docker cp backend/create-stored-procedures.sql SQL_Server_Docker:/tmp/
docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password -d das_ristorino -i /tmp/create-stored-procedures.sql
```

### application.properties
```properties
spring.application.name=backend
# Configuración de SQL Server
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=das_ristorino;encrypt=false
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.username=sa
spring.datasource.password=DB_Password
# Configuración del pool de conexiones
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
# Configuración del servidor
server.port=8080
# Logging para debugging
logging.level.org.springframework.jdbc=DEBUG
logging.level.com.zaxxer.hikari=DEBUG
```

## 🐛 Troubleshooting

### Error de Conexión a Base de Datos
- Verificar que el contenedor SQL Server esté corriendo: `docker ps`
- Verificar que la base de datos `das_ristorino` exista
- Verificar credenciales en `application.properties`

### Error de Puerto en Uso
- Cambiar el puerto en `application.properties`: `server.port=8081`
- O matar el proceso que usa el puerto 8080

### Error de Stored Procedures
- Verificar que se ejecutaron todos los scripts en orden
- Re-ejecutar `create-stored-procedures.sql`

### Verificar Configuración
```bash
# Ejecutar script de verificación
./verify-setup.sh

# Si hay errores, reconfigurar
./setup-database.sh
```

## 🧪 Testing

### Probar Endpoints Básicos
```bash
# Obtener todas las reservas
curl http://localhost:8080/api/reservas

# Obtener estadísticas
curl http://localhost:8080/api/reservas/estadisticas

# Crear una reserva
curl -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "nombreCliente": "Test User",
    "email": "test@email.com",
    "telefono": "123456789",
    "fechaHora": "2025-12-25T20:00:00",
    "cantidadPersonas": 2,
    "observaciones": "Mesa romántica"
  }'
```

## 📝 Notas Importantes

### Estado Actual del Proyecto
- ✅ **Base de datos**: Completamente configurada con 24 tablas
- ✅ **Stored Procedures**: 12 procedures funcionando
- ✅ **API Endpoints**: Estructura completa implementada
- ⚠️ **Tipos de ID**: Hay inconsistencias entre `Long` y `String` que necesitan corrección
- ⚠️ **Datos de prueba**: Algunos scripts de datos pueden tener errores menores

### Próximos Pasos
1. Corregir tipos de ID en DTOs y servicios
2. Probar operaciones CRUD completas
3. Implementar validaciones de negocio
4. Agregar manejo de errores mejorado

## 👥 Contribución
1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## 📞 Soporte
Para dudas o problemas:
1. Ejecutar `./verify-setup.sh` para diagnóstico
2. Revisar logs de la aplicación
3. Contactar al equipo de desarrollo

---

**Desarrollado por el equipo DAS - UBP 2025**