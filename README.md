# das-ristorino

Repositorio de app Ristorino - Materia DAS - UBP 2025

## 📋 Descripción

Sistema de gestión de reservas para restaurantes desarrollado con Spring Boot y SQL Server. Portal central REST para clientes que permite buscar restaurantes, ver promociones, generar contenidos con IA y gestionar reservas.

## 🛠️ Tecnologías

- **Backend**: Spring Boot 3.5.5, Java 17
- **Base de Datos**: Microsoft SQL Server (`das_ristorino`)
- **Puerto**: 8080
- **Protocolo**: REST/JSON
- **Autenticación**: JWT (OAuth2 Resource Server)
- **IA**: OpenAI API (generación de contenidos)
- **Build Tool**: Maven
- **Frontend**: Angular (separado)

## 🚀 Configuración Rápida

### Prerrequisitos

- Java 17 o superior
- Maven 3.6+
- SQL Server (local o Docker)
- Docker Desktop (opcional, para SQL Server)
- OpenAI API Key (opcional, para generación de contenidos)

### 1. Configurar Base de Datos

#### Opción A: SQL Server en Docker

```bash
# Ejecutar SQL Server en Docker
docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=DB_Password" \
   -p 1433:1433 --name SQL_Server_Docker \
   -d mcr.microsoft.com/mssql/server:2022-latest

# Esperar 10-15 segundos para que SQL Server inicie
```

#### Opción B: SQL Server Local

Asegúrate de tener SQL Server instalado y corriendo en `localhost:1433`.

### 2. Crear Base de Datos

```bash
# Conectar a SQL Server y crear la base de datos
docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password \
   -Q "CREATE DATABASE das_ristorino;"
```

O si usas SQL Server local:
```sql
CREATE DATABASE das_ristorino;
GO
```

### 3. Ejecutar Scripts SQL

**IMPORTANTE**: Ejecuta los scripts en el siguiente orden:

```bash
# 1. Crear tablas (usa 00_risto.sql completo)
docker cp scripts/00_risto.sql SQL_Server_Docker:/tmp/
docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password -d das_ristorino -i /tmp/00_risto.sql

# 2. Crear stored procedures
docker cp backend/create-stored-procedures.sql SQL_Server_Docker:/tmp/
docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password -d das_ristorino -i /tmp/create-stored-procedures.sql

# 3. Insertar datos básicos (provincias, localidades, idiomas)
docker cp scripts/01_insert_basicos.sql SQL_Server_Docker:/tmp/
docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password -d das_ristorino -i /tmp/01_insert_basicos.sql

# 4. Insertar restaurantes (3 restaurantes, 1 compartido con restaurante-soap)
docker cp scripts/03_insert_datos_basicos.sql SQL_Server_Docker:/tmp/
docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password -d das_ristorino -i /tmp/03_insert_datos_basicos.sql
```

**O usando SQL Server Management Studio (SSMS):**
1. Abre SSMS y conéctate a tu instancia de SQL Server
2. Abre y ejecuta `scripts/00_risto.sql` (crea todas las tablas)
3. Abre y ejecuta `backend/create-stored-procedures.sql` (crea stored procedures)
4. Abre y ejecuta `scripts/01_insert_basicos.sql` (inserta provincias, localidades, idiomas)
5. Abre y ejecuta `scripts/03_insert_datos_basicos.sql` (inserta 3 restaurantes)

### 4. Verificar Configuración

Verifica que la base de datos tenga datos:

```bash
docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password -d das_ristorino \
   -Q "SELECT COUNT(*) AS total_restaurantes FROM restaurantes;"
```

Deberías ver 3 restaurantes.

### 5. Configurar application.properties

Verifica que `backend/src/main/resources/application.properties` tenga:

```properties
spring.application.name=backend

# SQL Server
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=das_ristorino;encrypt=false
spring.datasource.username=sa
spring.datasource.password=DB_Password
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# Pool de conexiones
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000

# Servidor
server.port=8080

# JWT
security.jwt.secret=RISTORINO_BACKEND_2025_SEGURIDAD_SPRINGBOOT_SECRET_KEY

# OpenAI (opcional)
openai.api.key=${OPENAI_API_KEY:not-configured}
openai.model=gpt-5-nano
```

### 6. Configurar Cliente SOAP (das-restaurante-soap)

Verifica que `backend/src/main/resources/application.properties` tenga la configuración del servicio SOAP:

```properties
# SOAP Restaurante Service
soap.restaurante.wsdl=http://localhost:8081/ws/restaurantes.wsdl
soap.restaurante.namespace=http://das.ubp.edu.ar/restaurante
soap.restaurante.service=RestaurantePortService
soap.restaurante.port=RestaurantePortSoap11
```

**IMPORTANTE**: El servicio `das-restaurante-soap` debe estar corriendo en el puerto 8081.

### 7. Compilar y Ejecutar la Aplicación

```bash
cd backend

# Compilar
./mvnw clean install

# Ejecutar
./mvnw spring-boot:run
```

O desde tu IDE:
- Importa el proyecto como proyecto Maven
- Ejecuta la clase `BackendApplication`

### 8. Verificar que la Aplicación Funciona

La aplicación estará disponible en:
- **REST API**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Health Check**: `http://localhost:8080/actuator/health`

## 📊 Estructura de Scripts SQL

```
scripts/
├── 00_risto.sql                    # Crea todas las tablas (ejecutar primero)
├── 01_insert_basicos.sql          # Inserta provincias, localidades, idiomas
├── 03_insert_datos_basicos.sql    # Inserta 3 restaurantes (1 compartido)
└── ...

backend/
└── create-stored-procedures.sql   # Crea todos los stored procedures
```

## 📡 API Endpoints REST

### Autenticación (Público)
- `POST /api/clientes/register` - Registro de cliente
- `POST /api/clientes/login` - Login con JWT

### Restaurantes (Público)
- `GET /api/restaurantes` - Buscar restaurantes
- `GET /api/restaurantes/{id}` - Detalle de restaurante
- `GET /api/restaurantes/{id}/sucursales` - Sucursales
- `POST /api/restaurantes/busqueda-nlp` - Búsqueda con NLP

### Promociones (Público)
- `GET /api/promociones` - Listar promociones
- `GET /api/promociones/{id}` - Detalle de promoción
- `POST /api/promociones/{id}/click` - Registrar click

### Reservas (Protegido - JWT)
- `GET /api/reservas` - Mis reservas
- `GET /api/reservas/{id}` - Detalle de reserva
- `POST /api/reservas` - Crear reserva
- `PUT /api/reservas/{id}` - Actualizar reserva
- `PUT /api/reservas/{id}/cancelar` - Cancelar reserva

### Contenidos (Protegido - JWT)
- `POST /api/contenidos/generar` - Generar contenido con IA

## 🔧 Configuración Manual

### application.properties Completo

```properties
spring.application.name=backend

# SQL Server
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=das_ristorino;encrypt=false
spring.datasource.username=sa
spring.datasource.password=DB_Password
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# Pool de conexiones
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000

# Servidor
server.port=8080

# JWT
security.jwt.secret=RISTORINO_BACKEND_2025_SEGURIDAD_SPRINGBOOT_SECRET_KEY

# SOAP Restaurante Service
soap.restaurante.wsdl=http://localhost:8081/ws/restaurantes.wsdl
soap.restaurante.namespace=http://das.ubp.edu.ar/restaurante
soap.restaurante.service=RestaurantePortService
soap.restaurante.port=RestaurantePortSoap11

# OpenAI API (opcional)
openai.api.key=${OPENAI_API_KEY:not-configured}
openai.model=gpt-5-nano
openai.prompt.id=pmpt_68f93394cf6c8195955e0767742b9d7f05a21f383241fa79
```

## 🐛 Troubleshooting

### Error de Conexión a Base de Datos

```bash
# Verificar que SQL Server esté corriendo
docker ps | grep SQL_Server_Docker

# Verificar que la base de datos exista
docker exec -it SQL_Server_Docker /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U sa -P DB_Password \
   -Q "SELECT name FROM sys.databases WHERE name='das_ristorino';"
```

### Error de Puerto en Uso

```bash
# Ver qué proceso usa el puerto 8080
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Cambiar el puerto en application.properties
server.port=8082
```

### Error al Conectar con das-restaurante-soap

- Verifica que `das-restaurante-soap` esté corriendo en el puerto 8081
- Verifica la URL del WSDL en `application.properties`
- Prueba abrir el WSDL en el navegador: `http://localhost:8081/ws/restaurantes.wsdl`

### Error al Ejecutar Scripts SQL

- Verifica que ejecutaste los scripts en orden:
  1. `00_risto.sql` (crea tablas)
  2. `create-stored-procedures.sql` (crea stored procedures)
  3. `01_insert_basicos.sql` (datos básicos)
  4. `03_insert_datos_basicos.sql` (restaurantes)
- Verifica que la base de datos `das_ristorino` existe
- Revisa los logs de SQL Server para errores específicos

## 🧪 Testing

### Probar Endpoints Básicos

```bash
# Obtener todos los restaurantes
curl http://localhost:8080/api/restaurantes

# Obtener restaurante por ID
curl http://localhost:8080/api/restaurantes/12345678-1234-1234-1234-123456789abc

# Swagger UI
open http://localhost:8080/swagger-ui.html
```

### Registrar Cliente

```bash
curl -X POST http://localhost:8080/api/clientes/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Pérez",
    "correo": "juan@email.com",
    "password": "password123",
    "telefonos": "351-555-1234",
    "nroLocalidad": "<UUID_DE_LOCALIDAD>"
  }'
```

### Login y Obtener Token JWT

```bash
curl -X POST http://localhost:8080/api/clientes/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "juan@email.com",
    "password": "password123"
  }'
```

## 📝 Notas Importantes

### Restaurante Compartido

El script `03_insert_datos_basicos.sql` inserta 3 restaurantes, incluyendo uno compartido con `das-restaurante-soap`:
- **Restaurante 1 (COMPARTIDO)**: UUID `12345678-1234-1234-1234-123456789abc` - "Los Aroza SRL"
- **Restaurante 2**: UUID `22345678-2234-2234-2234-223456789abc` - "Parrilla La Esquina SRL"
- **Restaurante 3**: UUID `32345678-3234-3234-3234-323456789abc` - "Sushi House S.A."

**El restaurante compartido debe existir en ambas bases de datos con el mismo UUID** para que la integración funcione correctamente.

### Orden de Ejecución de Scripts

1. **00_risto.sql** - Crea todas las tablas (ejecutar primero)
2. **create-stored-procedures.sql** - Crea stored procedures
3. **01_insert_basicos.sql** - Inserta provincias, localidades, idiomas
4. **03_insert_datos_basicos.sql** - Inserta restaurantes y datos básicos

### Dependencia con das-restaurante-soap

Esta aplicación depende de `das-restaurante-soap` para:
- Consultar restaurantes y sucursales
- Registrar contenidos
- Notificar clicks

Asegúrate de que `das-restaurante-soap` esté corriendo antes de iniciar esta aplicación.

## 📁 Estructura del Proyecto

```
das-ristorino/
├── backend/
│   ├── src/main/java/ar/edu/ubp/das/backend/
│   │   ├── resources/          # Controllers REST
│   │   ├── service/            # Lógica de negocio
│   │   ├── repository/         # Acceso a datos
│   │   ├── client/             # Cliente SOAP
│   │   ├── dto/                # Data Transfer Objects
│   │   └── config/             # Configuración
│   ├── src/main/resources/
│   │   └── application.properties
│   └── create-stored-procedures.sql
├── scripts/
│   ├── 00_risto.sql
│   ├── 01_insert_basicos.sql
│   ├── 03_insert_datos_basicos.sql
│   └── ...
└── frontend/
    └── das-ristorino-frontend/  # Angular (separado)
```

## 👥 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

**Desarrollado por el equipo DAS - UBP 2025**
