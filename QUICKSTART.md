# 🚀 Quick Start - das-ristorino

## Configuración en 3 pasos

### 1. Clonar y configurar
```bash
git clone <repository-url>
cd das-ristorino
./setup-database.sh
```

### 2. Verificar
```bash
./verify-setup.sh
```

### 3. Ejecutar
```bash
cd backend
./mvnw spring-boot:run
```

## ✅ Verificar que funciona

Abrir en el navegador: `http://localhost:8080/api/reservas`

Deberías ver: `[]` (array vacío, pero sin errores)

## 🧪 Probar crear una reserva

```bash
curl -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "nombreCliente": "Juan Pérez",
    "email": "juan@email.com",
    "telefono": "123456789",
    "fechaHora": "2025-12-25T20:00:00",
    "cantidadPersonas": 4,
    "observaciones": "Mesa cerca de la ventana"
  }'
```

## ❌ Si algo falla

1. **Docker no corre**: Iniciar Docker Desktop
2. **Error de base de datos**: `./setup-database.sh`
3. **Error de compilación**: Verificar Java 21
4. **Puerto ocupado**: Cambiar `server.port=8081` en `application.properties`

## 📋 Endpoints disponibles

- `GET /api/reservas` - Ver todas las reservas
- `POST /api/reservas` - Crear reserva
- `GET /api/reservas/estadisticas` - Ver estadísticas

¡Listo! 🎉
