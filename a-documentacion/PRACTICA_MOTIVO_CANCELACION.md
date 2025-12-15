# Práctica: Agregar motivoCancelacion end-to-end

Este ejercicio te guía para agregar el campo `motivoCancelacion` en el flujo de cancelación de reservas, atravesando API REST de Ristorino, clientes SOAP/REST hacia restaurante y (opcionalmente) persistencia en BD.

## Objetivo
- Aceptar un motivo de cancelación desde el frontend/API.
- Propagarlo al sistema del restaurante vía REST o SOAP.
- (Opcional) Guardarlo en la base de datos de Ristorino.

## Visión general del flujo
- Ristorino API: expone `POST /api/reservas/cancelar/{nroReserva}` con body `{ motivoCancelacion }`.
- `ReservaService` recibe el motivo y lo pasa al `RestauranteClient`.
- Cliente SOAP/REST serializa `motivoCancelacion` en el JSON enviado al restaurante.
- (Opcional) Ristorino guarda `motivoCancelacion` en su propia tabla.

---

## Cambios ya implementados (guía de referencia)
Usa estos diffs como guía si querés rehacerlos desde cero.

- DTO para enviar a restaurante (se agregó el campo y se arregló el constructor):
  - [das-restaurante/das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/dto/restaurante/CancelarReservaJsonDto.java](das-restaurante/das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/dto/restaurante/CancelarReservaJsonDto.java)

- Interfaz del cliente a restaurantes (nuevo overload con motivo, compatible hacia atrás):
  - [das-restaurante/das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/client/RestauranteClient.java](das-restaurante/das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/client/RestauranteClient.java)

- Cliente SOAP (implementa el overload y serializa `motivoCancelacion`):
  - [das-restaurante/das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/client/soap/RestauranteSoapClientImpl.java](das-restaurante/das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/client/soap/RestauranteSoapClientImpl.java)

- Cliente REST (implementa el overload y envía body con motivo):
  - [das-restaurante/das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/client/rest/RestauranteRestClient.java](das-restaurante/das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/client/rest/RestauranteRestClient.java)

- API de Ristorino: nuevo endpoint `POST` con body opcional y DTO de request:
  - [das-restaurante/das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/resources/ReservaResource.java](das-restaurante/das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/resources/ReservaResource.java)
  - [das-restaurante/das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/dto/response/CancelarReservaRequestDto.java](das-restaurante/das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/dto/response/CancelarReservaRequestDto.java)

- Servicio: overload que recibe el motivo y lo propaga al cliente:
  - [das-restaurante/das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/service/ReservaService.java](das-restaurante/das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/service/ReservaService.java)

---

## Paso a paso para practicar desde cero

1) Crear el DTO para el body de la API
- Archivo: `backend/src/main/java/.../dto/response/CancelarReservaRequestDto.java`
- Campos: `String motivoCancelacion` (con `@Size(max = 200)`).

2) Agregar endpoint REST para cancelar con motivo
- Archivo: `backend/src/main/java/.../resources/ReservaResource.java`
- Endpoint: `POST /api/reservas/cancelar/{nroReserva}`.
- Body opcional: `CancelarReservaRequestDto`.
- Llama a `reservaService.cancelarReserva(nroReserva, motivo)`.
- Mantener el GET existente para compatibilidad.

3) Extender el servicio
- Archivo: `backend/src/main/java/.../service/ReservaService.java`
- Agregar overload `cancelarReserva(String nroReserva, String motivo)`.
- Obtener datos con `reservaRepository.obtenerDatosCancelarReservaDto(...)`.
- Llamar a `RestauranteClient.cancelarReserva(nroRestaurante, codReservaSucursal, motivo)`.
- Registrar cancelación localmente (SP actual) hasta tener la parte de BD lista.

4) Extender el contrato del cliente a restaurantes
- Archivo: `backend/src/main/java/.../client/RestauranteClient.java`
- Añadir un método default overload con `motivoCancelacion` para no romper implementaciones existentes.

5) Implementar en clientes SOAP y REST
- SOAP: `RestauranteSoapClientImpl.cancelarReserva(nroRestaurante, nroReserva, motivo)`
  - Crear `CancelarReservaJsonDto(nroReserva, motivo)` → `gson.toJson(...)`.
  - Enviar como `jsonData` a la operación `cancelarReservaRequest`.
- REST: `RestauranteRestClient.cancelarReserva(nroRestaurante, nroReserva, motivo)`
  - POST a `/restaurantes/{nroRestaurante}/reservas/{nroReserva}/cancelar` con body `{ "motivoCancelacion": "..." }`.

6) Persistir el motivo en BD de Ristorino
- Ejecutá el script: `scripts/sql/06_add_motivo_cancelacion.sql` (SQL Server)
- Repositorio: usá `ReservaRepository.cancelarReserva(String nroReserva, String motivo)` para invocar el SP con el motivo.

---

## Cómo probar rápidamente

- Cancelación sin motivo (compatibilidad):
```bash
curl -i http://localhost:8080/api/reservas/cancelar/RES-123
```

- Cancelación con motivo (nuevo POST):
```bash
curl -i -X POST http://localhost:8080/api/reservas/cancelar/RES-123 \
  -H "Content-Type: application/json" \
  -d '{"motivoCancelacion":"No puedo asistir"}'
```

- Si estás usando Windows PowerShell, escapa comillas así:
```powershell
curl -Method Post "http://localhost:8080/api/reservas/cancelar/RES-123" `
  -Headers @{"Content-Type"="application/json"} `
  -Body '{"motivoCancelacion":"No puedo asistir"}'
```

- Verifica que el cliente seleccionado (SOAP/REST) del restaurante reciba el JSON con `motivoCancelacion`.

---

## Checklist de validación
- [ ] El endpoint POST responde 204 No Content al cancelar.
- [ ] En logs del backend se ve el motivo recibido (podés agregar un `logger.info`).
- [ ] El restaurante (SOAP/REST) recibe el motivo en su JSON.
- [ ] (Opcional) La BD de Ristorino muestra el motivo en `reservas.motivo_cancelacion`.

---

## Errores comunes y tips
- Constructor del DTO sin asignar campos → revisá `CancelarReservaJsonDto`.
- Contractos con restaurantes: si el servicio remoto no soporta motivo aún, el overload default del `RestauranteClient` hace fallback sin motivo.
- CORS/seguridad: si llamás desde frontend, confirmá cabeceras y autenticación.
- BD: si el SP no acepta el parámetro nuevo, la actualización no se verá reflejada.

---

## Paso extra (Frontend)
- Agregar textarea "Motivo de cancelación" en el modal/página de cancelación.
- Si hay motivo, llamar al POST; si no, podés mantener el GET para compatibilidad.

---

## Qué practicar para el examen
1) Agregar columna en tabla y modificar SP.
2) Añadir campo en DTOs/requests/responses.
3) Propagar al cliente (SOAP/REST) y verificar el contrato.
4) Probar con cURL/Postman y (opcional) UI.

> Consejo: practicá con un timer de 30–45 minutos y evaluá el flujo completo (código + DB + prueba).
