# 📋 Plan Completo: Modificar una Reserva Ya Creada

## 🎯 Objetivo
Implementar la funcionalidad para modificar una reserva existente que ya fue confirmada en el sistema del restaurante. Esta funcionalidad requiere cambios en:
- **Frontend** (`das-ristorino-frontend`)
- **Backend Ristorino** (`das-ristorino` backend)
- **Backend Restaurante** (tanto SOAP como REST)

---

## ⚠️ Situación Actual

**Problema identificado:**
El método `actualizarReserva` existente en `ReservaService` solo modifica la base de datos local de Ristorino, pero **NO sincroniza** los cambios con el sistema del restaurante externo (SOAP/REST).

**Flujo actual:**
```
Frontend → PUT /reservas/{id} → ReservaService.actualizarReserva() 
→ Solo actualiza BD local de Ristorino ❌
```

**Flujo necesario:**
```
Frontend → PUT /reservas/{id}/modificar → ReservaService.modificarReserva() 
→ Actualiza BD local + Sincroniza con restaurante externo ✅
```

---

## 📐 Arquitectura de la Solución

```
┌─────────────────────────────────────────────────────────────┐
│  Frontend (Angular)                                        │
│  - ModificarReservaComponent                               │
│  - ReservaResource.modificarReserva()                      │
└────────────────────┬──────────────────────────────────────┘
                     │ PUT /reservas/{id}/modificar
                     ▼
┌─────────────────────────────────────────────────────────────┐
│  Backend Ristorino (Spring Boot)                          │
│  - ReservaResource.modificarReserva()                      │
│  - ReservaService.modificarReserva()                       │
│    ├─ Validaciones                                         │
│    ├─ Actualizar BD local (sp_ModificarReservaCompleta)   │
│    └─ Sincronizar con restaurante externo                  │
│       └─ RestauranteClient.modificarReserva()              │
└────────────────────┬──────────────────────────────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
        ▼                         ▼
┌──────────────┐         ┌──────────────┐
│ REST Client  │         │ SOAP Client  │
│ :8082        │         │ :8081        │
└──────┬───────┘         └──────┬───────┘
       │                        │
       ▼                        ▼
┌──────────────┐         ┌──────────────┐
│ PUT /api/    │         │ modificar    │
│ restaurantes/│         │ Reserva      │
│ {nro}/       │         │ Request      │
│ reservas/    │         │ (SOAP)       │
│ {cod}        │         │              │
└──────────────┘         └──────────────┘
```

---

## 🔧 FASE 1: Backend del Restaurante (Interfaz y Clientes)

### Paso 1.1: Agregar método en `RestauranteClient`

**Archivo:** `das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/client/RestauranteClient.java`

```java
/**
 * Modificar una reserva existente en el sistema del restaurante
 * @param codReservaSucursal Código de la reserva en el sistema del restaurante
 * @param nroRestaurante UUID del restaurante
 * @param codSucursalRestaurante Código de la sucursal en el sistema del restaurante
 * @param codZonaRestaurante Código de la zona en el sistema del restaurante
 * @param fechaReserva Nueva fecha de reserva
 * @param horaDesde Nueva hora de inicio
 * @param cantAdultos Nueva cantidad de adultos
 * @param cantMenores Nueva cantidad de menores
 * @return Código de reserva actualizado (puede ser el mismo o uno nuevo)
 */
String modificarReserva(
    String codReservaSucursal,
    String nroRestaurante,
    String codSucursalRestaurante,
    String codZonaRestaurante,
    java.time.LocalDate fechaReserva,
    java.time.LocalTime horaDesde,
    Integer cantAdultos,
    Integer cantMenores
);
```

### Paso 1.2: Implementar en `RestauranteRestClient`

**Archivo:** `das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/client/rest/RestauranteRestClient.java`

```java
@Override
public String modificarReserva(
    String codReservaSucursal,
    String nroRestaurante,
    String codSucursalRestaurante,
    String codZonaRestaurante,
    java.time.LocalDate fechaReserva,
    java.time.LocalTime horaDesde,
    Integer cantAdultos,
    Integer cantMenores
) {
    String url = baseUrl + "/api/restaurantes/" + nroRestaurante + 
                 "/reservas/" + codReservaSucursal;
    
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("cod_sucursal_restaurante", codSucursalRestaurante);
    requestBody.put("cod_zona_restaurante", codZonaRestaurante);
    requestBody.put("fecha_reserva", fechaReserva.toString());
    requestBody.put("hora_desde", horaDesde.toString());
    requestBody.put("cant_adultos", cantAdultos);
    requestBody.put("cant_menores", cantMenores);
    
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
    
    try {
        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.PUT,
            request,
            Map.class
        );
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return (String) response.getBody().get("cod_reserva");
        }
        throw new RuntimeException("Error al modificar reserva en restaurante REST");
    } catch (Exception e) {
        throw new RuntimeException("Error al modificar reserva: " + e.getMessage(), e);
    }
}
```

### Paso 1.3: Implementar en `RestauranteSoapClientImpl`

**Archivo:** `das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/client/soap/RestauranteSoapClientImpl.java`

Primero, crear el DTO para el JSON que va dentro del SOAP:

**Archivo:** `das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/dto/ModificarReservaJsonDto.java`

```java
package ar.edu.ubp.das.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalTime;

public class ModificarReservaJsonDto {
    
    @JsonProperty("cod_reserva_sucursal")
    private String codReservaSucursal;
    
    @JsonProperty("cod_sucursal_restaurante")
    private String codSucursalRestaurante;
    
    @JsonProperty("cod_zona_restaurante")
    private String codZonaRestaurante;
    
    @JsonProperty("fecha_reserva")
    private String fechaReserva;
    
    @JsonProperty("hora_desde")
    private String horaDesde;
    
    @JsonProperty("cant_adultos")
    private Integer cantAdultos;
    
    @JsonProperty("cant_menores")
    private Integer cantMenores;
    
    // Constructores, getters y setters
    public ModificarReservaJsonDto() {}
    
    public ModificarReservaJsonDto(String codReservaSucursal, String codSucursalRestaurante,
                                   String codZonaRestaurante, LocalDate fechaReserva,
                                   LocalTime horaDesde, Integer cantAdultos, Integer cantMenores) {
        this.codReservaSucursal = codReservaSucursal;
        this.codSucursalRestaurante = codSucursalRestaurante;
        this.codZonaRestaurante = codZonaRestaurante;
        this.fechaReserva = fechaReserva.toString();
        this.horaDesde = horaDesde.toString();
        this.cantAdultos = cantAdultos;
        this.cantMenores = cantMenores;
    }
    
    // Getters y setters...
    public String getCodReservaSucursal() { return codReservaSucursal; }
    public void setCodReservaSucursal(String codReservaSucursal) { this.codReservaSucursal = codReservaSucursal; }
    
    public String getCodSucursalRestaurante() { return codSucursalRestaurante; }
    public void setCodSucursalRestaurante(String codSucursalRestaurante) { this.codSucursalRestaurante = codSucursalRestaurante; }
    
    public String getCodZonaRestaurante() { return codZonaRestaurante; }
    public void setCodZonaRestaurante(String codZonaRestaurante) { this.codZonaRestaurante = codZonaRestaurante; }
    
    public String getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(String fechaReserva) { this.fechaReserva = fechaReserva; }
    
    public String getHoraDesde() { return horaDesde; }
    public void setHoraDesde(String horaDesde) { this.horaDesde = horaDesde; }
    
    public Integer getCantAdultos() { return cantAdultos; }
    public void setCantAdultos(Integer cantAdultos) { this.cantAdultos = cantAdultos; }
    
    public Integer getCantMenores() { return cantMenores; }
    public void setCantMenores(Integer cantMenores) { this.cantMenores = cantMenores; }
}
```

Ahora implementar el método en `RestauranteSoapClientImpl`:

```java
@Override
public String modificarReserva(
    String codReservaSucursal,
    String nroRestaurante,
    String codSucursalRestaurante,
    String codZonaRestaurante,
    java.time.LocalDate fechaReserva,
    java.time.LocalTime horaDesde,
    Integer cantAdultos,
    Integer cantMenores
) {
    try {
        ModificarReservaJsonDto jsonDto = new ModificarReservaJsonDto(
            codReservaSucursal,
            codSucursalRestaurante,
            codZonaRestaurante,
            fechaReserva,
            horaDesde,
            cantAdultos,
            cantMenores
        );
        
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(jsonDto);
        
        // Crear el request SOAP
        String soapRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                           "xmlns:res=\"http://restaurante.das.ubp.edu.ar/\">" +
                           "<soapenv:Header/>" +
                           "<soapenv:Body>" +
                           "<res:modificarReservaRequest>" +
                           "<res:json>" + jsonBody + "</res:json>" +
                           "</res:modificarReservaRequest>" +
                           "</soapenv:Body>" +
                           "</soapenv:Envelope>";
        
        // Enviar request SOAP (similar a registrarReserva)
        String response = soapClient.sendSoapRequest(wsdlUrl, soapRequest);
        
        // Parsear respuesta SOAP y extraer cod_reserva
        // (similar al parseo en registrarReserva)
        // ...
        
        return codReservaSucursal; // o el nuevo código si el restaurante lo genera
    } catch (Exception e) {
        throw new RuntimeException("Error al modificar reserva en restaurante SOAP: " + e.getMessage(), e);
    }
}
```

---

## 🔧 FASE 2: Backend Ristorino

### Paso 2.1: Crear `ModificarReservaDto`

**Archivo:** `das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/dto/ModificarReservaDto.java`

```java
package ar.edu.ubp.das.backend.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class ModificarReservaDto {
    
    @NotNull(message = "La fecha de reserva es obligatoria")
    @Future(message = "La fecha debe ser futura")
    private LocalDate fechaReserva;
    
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaDesde;
    
    @NotNull(message = "La cantidad de adultos es obligatoria")
    @Min(value = 1, message = "Debe haber al menos 1 adulto")
    private Integer cantAdultos;
    
    @NotNull(message = "La cantidad de menores es obligatoria")
    @Min(value = 0, message = "La cantidad de menores no puede ser negativa")
    private Integer cantMenores;
    
    @Size(max = 400, message = "Las observaciones no pueden exceder 400 caracteres")
    private String observaciones;
    
    // Constructores
    public ModificarReservaDto() {}
    
    public ModificarReservaDto(LocalDate fechaReserva, LocalTime horaDesde, 
                              Integer cantAdultos, Integer cantMenores, String observaciones) {
        this.fechaReserva = fechaReserva;
        this.horaDesde = horaDesde;
        this.cantAdultos = cantAdultos;
        this.cantMenores = cantMenores;
        this.observaciones = observaciones;
    }
    
    // Getters y Setters
    public LocalDate getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDate fechaReserva) { this.fechaReserva = fechaReserva; }
    
    public LocalTime getHoraDesde() { return horaDesde; }
    public void setHoraDesde(LocalTime horaDesde) { this.horaDesde = horaDesde; }
    
    public Integer getCantAdultos() { return cantAdultos; }
    public void setCantAdultos(Integer cantAdultos) { this.cantAdultos = cantAdultos; }
    
    public Integer getCantMenores() { return cantMenores; }
    public void setCantMenores(Integer cantMenores) { this.cantMenores = cantMenores; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
```

### Paso 2.2: Crear Stored Procedure `sp_ModificarReservaCompleta`

**Archivo:** `das-ristorino/scripts/sql/02_create_stored_procedures.sql`

```sql
-- Modificar reserva completa (incluye validaciones)
CREATE OR ALTER PROCEDURE sp_ModificarReservaCompleta
    @nro_reserva VARCHAR(36),
    @fecha_reserva DATE,
    @hora_desde TIME(0),
    @cant_adultos SMALLINT,
    @cant_menores SMALLINT,
    @notas NVARCHAR(400) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Validar que la reserva existe
    IF NOT EXISTS (SELECT 1 FROM reservas_restaurantes WHERE nro_reserva = @nro_reserva)
    BEGIN
        RAISERROR('La reserva no existe', 16, 1);
        RETURN;
    END;
    
    -- Validar que no esté cancelada
    IF EXISTS (SELECT 1 FROM reservas_restaurantes 
               WHERE nro_reserva = @nro_reserva AND cancelada = 1)
    BEGIN
        RAISERROR('No se puede modificar una reserva cancelada', 16, 1);
        RETURN;
    END;
    
    -- Validar que el turno existe y está habilitado
    DECLARE @nro_restaurante VARCHAR(36);
    DECLARE @nro_sucursal VARCHAR(36);
    
    SELECT @nro_restaurante = nro_restaurante, @nro_sucursal = nro_sucursal
    FROM reservas_restaurantes
    WHERE nro_reserva = @nro_reserva;
    
    IF NOT EXISTS (
        SELECT 1 
        FROM turnos_sucursales_restaurantes
        WHERE nro_restaurante = @nro_restaurante
          AND nro_sucursal = @nro_sucursal
          AND hora_desde = @hora_desde
          AND habilitado = 1
    )
    BEGIN
        RAISERROR('El turno especificado no está disponible', 16, 1);
        RETURN;
    END;
    
    -- Validar que la fecha no sea pasada
    IF @fecha_reserva < CAST(GETDATE() AS DATE)
    BEGIN
        RAISERROR('La fecha de reserva no puede ser pasada', 16, 1);
        RETURN;
    END;
    
    -- Actualizar la reserva
    UPDATE reservas_restaurantes
    SET
        fecha_reserva = @fecha_reserva,
        hora_desde = @hora_desde,
        cant_adultos = @cant_adultos,
        cant_menores = @cant_menores,
        notas = @notas
    WHERE nro_reserva = @nro_reserva;
    
    -- Devolver éxito
    SELECT @@ROWCOUNT AS filas_actualizadas;
END;
GO
```

### Paso 2.3: Agregar método en `ReservaRepository`

**Archivo:** `das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/repository/ReservaRepository.java`

```java
/**
 * Modificar reserva completa (con validaciones)
 * @param modificarDto DTO con los datos a modificar
 * @param nroReserva ID de la reserva a modificar
 * @return true si se modificó correctamente
 */
public boolean modificarReservaCompleta(ModificarReservaDto modificarDto, String nroReserva) {
    SqlParameterSource params = new MapSqlParameterSource()
            .addValue("nro_reserva", nroReserva)
            .addValue("fecha_reserva", java.sql.Date.valueOf(modificarDto.getFechaReserva()))
            .addValue("hora_desde", java.sql.Time.valueOf(modificarDto.getHoraDesde()))
            .addValue("cant_adultos", modificarDto.getCantAdultos())
            .addValue("cant_menores", modificarDto.getCantMenores())
            .addValue("notas", modificarDto.getObservaciones());
    
    try {
        Map<String, Object> result = jdbcCallFactory.executeWithOutputs(
            "sp_ModificarReservaCompleta", "dbo", params);
        
        if (result != null && result.containsKey("filas_actualizadas")) {
            Integer filas = (Integer) result.get("filas_actualizadas");
            return filas != null && filas > 0;
        }
        return false;
    } catch (Exception e) {
        throw new RuntimeException("Error al modificar reserva: " + e.getMessage(), e);
    }
}

/**
 * Obtener datos de una reserva para modificación (incluye códigos externos)
 */
public ReservaParaModificarDto obtenerReservaParaModificar(String nroReserva) {
    SqlParameterSource params = new MapSqlParameterSource()
            .addValue("nro_reserva", nroReserva);
    
    List<ReservaParaModificarDto> results = jdbcCallFactory.executeQuery(
        "sp_ObtenerReservaParaModificar", "dbo", params, "reserva", 
        ReservaParaModificarDto.class);
    
    if (results != null && !results.isEmpty()) {
        return results.get(0);
    }
    return null;
}
```

**Crear DTO auxiliar:** `ReservaParaModificarDto.java`

```java
package ar.edu.ubp.das.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaParaModificarDto {
    
    @JsonProperty("nro_reserva")
    private String nroReserva;
    
    @JsonProperty("nro_restaurante")
    private String nroRestaurante;
    
    @JsonProperty("nro_sucursal")
    private String nroSucursal;
    
    @JsonProperty("cod_zona")
    private String codZona;
    
    @JsonProperty("cod_reserva_sucursal")
    private String codReservaSucursal;
    
    @JsonProperty("cod_sucursal_restaurante")
    private String codSucursalRestaurante;
    
    @JsonProperty("cod_zona_restaurante")
    private String codZonaRestaurante;
    
    // Getters y setters...
}
```

**Stored Procedure auxiliar:** `sp_ObtenerReservaParaModificar`

```sql
CREATE OR ALTER PROCEDURE sp_ObtenerReservaParaModificar
    @nro_reserva VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT
        rr.nro_reserva,
        rr.nro_restaurante,
        rr.nro_sucursal,
        rr.cod_zona,
        rr.cod_reserva_sucursal,
        sr.cod_sucursal_restaurante,
        zsr.cod_zona_restaurante
    FROM reservas_restaurantes rr
    INNER JOIN sucursales_restaurantes sr 
        ON sr.nro_restaurante = rr.nro_restaurante 
        AND sr.nro_sucursal = rr.nro_sucursal
    INNER JOIN zonas_sucursales_restaurantes zsr
        ON zsr.nro_restaurante = rr.nro_restaurante
        AND zsr.nro_sucursal = rr.nro_sucursal
        AND zsr.cod_zona = rr.cod_zona
    WHERE rr.nro_reserva = @nro_reserva;
END;
GO
```

### Paso 2.4: Implementar método en `ReservaService`

**Archivo:** `das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/service/ReservaService.java`

```java
/**
 * Modificar una reserva existente (sincroniza con restaurante externo)
 * @param modificarDto DTO con los datos a modificar
 * @param nroReserva ID de la reserva
 * @param nroCliente ID del cliente (para validar propiedad)
 * @return ReservaResponseDto con los datos actualizados
 */
public ReservaResponseDto modificarReserva(
    ModificarReservaDto modificarDto, 
    String nroReserva, 
    String nroCliente
) {
    // 1. Validar que la reserva existe y pertenece al cliente
    ReservaResponseDto reservaActual = reservaRepository.findById(nroReserva);
    if (reservaActual == null) {
        throw new RuntimeException("La reserva no existe");
    }
    
    // Validar propiedad (si tienes nro_cliente en ReservaResponseDto)
    // O hacer una consulta adicional para validar
    
    // 2. Validar que no esté cancelada
    if ("CANCELADA".equals(reservaActual.getEstado())) {
        throw new RuntimeException("No se puede modificar una reserva cancelada");
    }
    
    // 3. Obtener datos necesarios para la sincronización externa
    ReservaParaModificarDto datosReserva = reservaRepository.obtenerReservaParaModificar(nroReserva);
    if (datosReserva == null) {
        throw new RuntimeException("No se pudieron obtener los datos de la reserva");
    }
    
    // Validar que tenga cod_reserva_sucursal (debe estar confirmada)
    if (datosReserva.getCodReservaSucursal() == null || 
        datosReserva.getCodReservaSucursal().trim().isEmpty()) {
        throw new RuntimeException("La reserva no está confirmada en el restaurante");
    }
    
    // 4. Obtener cliente del restaurante para validar disponibilidad
    RestauranteClient client = restauranteClientFactory.getClient(datosReserva.getNroRestaurante());
    
    // 5. Validar disponibilidad en el restaurante externo (opcional pero recomendado)
    // Puedes llamar a getHorariosDisponibles para verificar
    
    // 6. ACTUALIZAR EN BASE DE DATOS LOCAL (con transacción)
    boolean actualizadoLocal = false;
    try {
        actualizadoLocal = reservaRepository.modificarReservaCompleta(modificarDto, nroReserva);
        if (!actualizadoLocal) {
            throw new RuntimeException("Error al actualizar la reserva en la base de datos local");
        }
    } catch (Exception e) {
        throw new RuntimeException("Error al actualizar reserva local: " + e.getMessage(), e);
    }
    
    // 7. SINCRONIZAR CON RESTAURANTE EXTERNO
    String nuevoCodReserva = null;
    try {
        nuevoCodReserva = client.modificarReserva(
            datosReserva.getCodReservaSucursal(),
            datosReserva.getNroRestaurante(),
            datosReserva.getCodSucursalRestaurante(),
            datosReserva.getCodZonaRestaurante(),
            modificarDto.getFechaReserva(),
            modificarDto.getHoraDesde(),
            modificarDto.getCantAdultos(),
            modificarDto.getCantMenores()
        );
        
        // Si el restaurante devuelve un nuevo código, actualizarlo
        if (nuevoCodReserva != null && !nuevoCodReserva.equals(datosReserva.getCodReservaSucursal())) {
            reservaRepository.actualizarCodReservaSucursal(nroReserva, nuevoCodReserva);
        }
        
    } catch (Exception e) {
        // ROLLBACK: Revertir cambios locales si falla la sincronización externa
        // Opción 1: Llamar a un SP que revierta los cambios
        // Opción 2: Guardar estado anterior y restaurarlo
        // Por simplicidad, lanzamos excepción y dejamos que el usuario reintente
        throw new RuntimeException(
            "Error al sincronizar con el restaurante. Los cambios locales se mantendrán. " +
            "Por favor, contacte al restaurante directamente. Error: " + e.getMessage(), e);
    }
    
    // 8. Devolver reserva actualizada
    return reservaRepository.findById(nroReserva);
}
```

### Paso 2.5: Agregar endpoint en `ReservaResource`

**Archivo:** `das-ristorino/backend/src/main/java/ar/edu/ubp/das/backend/resources/ReservaResource.java`

```java
@PutMapping("/{id}/modificar")
@PreAuthorize("hasRole('CLIENTE')")
public ResponseEntity<ReservaResponseDto> modificarReserva(
    @PathVariable String id,
    @Valid @RequestBody ModificarReservaDto modificarDto,
    Authentication authentication
) {
    try {
        // Obtener nro_cliente del token JWT
        String nroCliente = authentication.getName(); // o como esté configurado tu JWT
        
        ReservaResponseDto reservaActualizada = reservaService.modificarReserva(
            modificarDto, 
            id, 
            nroCliente
        );
        
        return ResponseEntity.ok(reservaActualizada);
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest()
            .body(null); // O crear un DTO de error
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(null);
    }
}
```

---

## 🔧 FASE 3: Backend del Restaurante (Implementar Endpoints)

### Paso 3.1: Implementar endpoint REST

**Archivo:** `das-restaurante/das-restaurante-rest/src/main/java/.../controller/ReservaController.java`

```java
@PutMapping("/api/restaurantes/{nroRestaurante}/reservas/{codReserva}")
public ResponseEntity<Map<String, Object>> modificarReserva(
    @PathVariable String nroRestaurante,
    @PathVariable String codReserva,
    @RequestBody Map<String, Object> requestBody
) {
    try {
        // Validar que el restaurante existe
        // Validar que la reserva existe y pertenece al restaurante
        // Validar disponibilidad para la nueva fecha/hora
        // Actualizar reserva en BD
        // Devolver código de reserva
        
        Map<String, Object> response = new HashMap<>();
        response.put("cod_reserva", codReserva); // o nuevo código si se genera
        response.put("mensaje", "Reserva modificada exitosamente");
        
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", e.getMessage()));
    }
}
```

### Paso 3.2: Implementar operación SOAP

**Archivo:** `das-restaurante/das-restaurante-soap/src/main/java/.../endpoint/ReservaEndpoint.java`

```java
@PayloadRoot(namespace = "http://restaurante.das.ubp.edu.ar/", localPart = "modificarReservaRequest")
@ResponsePayload
public ModificarReservaResponse modificarReserva(@RequestPayload ModificarReservaRequest request) {
    try {
        // Parsear JSON del request
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonData = mapper.readValue(request.getJson(), Map.class);
        
        // Validar y actualizar reserva
        // ...
        
        ModificarReservaResponse response = new ModificarReservaResponse();
        response.setCodReserva((String) jsonData.get("cod_reserva_sucursal"));
        response.setMensaje("Reserva modificada exitosamente");
        
        return response;
    } catch (Exception e) {
        // Manejar error
        throw new RuntimeException("Error al modificar reserva: " + e.getMessage());
    }
}
```

**Actualizar XSD:** Agregar `modificarReservaRequest` y `modificarReservaResponse` en el XSD del servicio SOAP.

---

## 🎨 FASE 4: Frontend

### Paso 4.1: Crear interfaz `IModificarReserva`

**Archivo:** `das-ristorino-frontend/src/app/main/api/models/i-modificar-reserva.ts`

```typescript
export interface IModificarReserva {
  fecha_reserva: string; // YYYY-MM-DD
  hora_desde: string; // HH:mm
  cant_adultos: number;
  cant_menores: number;
  observaciones?: string | null;
}
```

### Paso 4.2: Agregar método en `ReservaResource` (Angular)

**Archivo:** `das-ristorino-frontend/src/app/main/api/resources/reserva-resource.ts`

```typescript
@ResourceAction({
  path: '/{id}/modificar',
  method: ResourceRequestMethod.Put,
})
declare modificarReserva: IResourceMethodObservable<{ id: string; data: IModificarReserva }, IReserva>;
```

**Nota:** Puede requerir ajustes según cómo `@ngx-resource/core` maneje los path parameters.

### Paso 4.3: Crear componente `ModificarReservaComponent`

**Archivo:** `das-ristorino-frontend/src/app/main/components/modificar-reserva/modificar-reserva.ts`

```typescript
import { Component, inject, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ReservaResource } from '../../api/resources/reserva-resource';
import { IReserva } from '../../api/models/i-reserva';
import { IModificarReserva } from '../../api/models/i-modificar-reserva';

@Component({
  selector: 'app-modificar-reserva',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './modificar-reserva.html',
  styleUrls: ['./modificar-reserva.scss']
})
export class ModificarReservaComponent {
  @Input() reserva!: IReserva;
  @Output() reservaModificada = new EventEmitter<IReserva>();
  @Output() cancelar = new EventEmitter<void>();

  modificarForm!: FormGroup;
  private _reservaResource = inject(ReservaResource);
  private _fb = inject(FormBuilder);

  ngOnInit() {
    // Inicializar formulario con datos actuales de la reserva
    const fechaHora = new Date(this.reserva.fecha_hora);
    const fechaStr = fechaHora.toISOString().split('T')[0];
    const horaStr = fechaHora.toTimeString().split(' ')[0].substring(0, 5);

    this.modificarForm = this._fb.group({
      fecha_reserva: [fechaStr, [Validators.required]],
      hora_desde: [horaStr, [Validators.required]],
      cant_adultos: [this.reserva.cant_adultos || 1, [Validators.required, Validators.min(1)]],
      cant_menores: [this.reserva.cant_menores || 0, [Validators.required, Validators.min(0)]],
      observaciones: [this.reserva.observaciones || '']
    });
  }

  onSubmit() {
    if (this.modificarForm.valid) {
      const datos: IModificarReserva = this.modificarForm.value;
      
      this._reservaResource.modificarReserva({
        id: this.reserva.id,
        data: datos
      }).subscribe({
        next: (reservaActualizada) => {
          this.reservaModificada.emit(reservaActualizada);
        },
        error: (error) => {
          console.error('Error al modificar reserva:', error);
          alert('Error al modificar la reserva. Por favor, intente nuevamente.');
        }
      });
    }
  }

  onCancelar() {
    this.cancelar.emit();
  }
}
```

**Template:** `modificar-reserva.html`

```html
<form [formGroup]="modificarForm" (ngSubmit)="onSubmit()">
  <div class="form-group">
    <label>Fecha de Reserva</label>
    <input type="date" formControlName="fecha_reserva" class="form-control" />
  </div>

  <div class="form-group">
    <label>Hora de Inicio</label>
    <input type="time" formControlName="hora_desde" class="form-control" />
  </div>

  <div class="form-group">
    <label>Cantidad de Adultos</label>
    <input type="number" formControlName="cant_adultos" class="form-control" min="1" />
  </div>

  <div class="form-group">
    <label>Cantidad de Menores</label>
    <input type="number" formControlName="cant_menores" class="form-control" min="0" />
  </div>

  <div class="form-group">
    <label>Observaciones</label>
    <textarea formControlName="observaciones" class="form-control" rows="3"></textarea>
  </div>

  <div class="form-actions">
    <button type="submit" [disabled]="modificarForm.invalid" class="btn btn-primary">
      Guardar Cambios
    </button>
    <button type="button" (click)="onCancelar()" class="btn btn-secondary">
      Cancelar
    </button>
  </div>
</form>
```

### Paso 4.4: Integrar en `MisReservasPage`

**Archivo:** `das-ristorino-frontend/src/app/main/pages/mis-reservas/mis-reservas.ts`

```typescript
// Agregar propiedades
reservaSeleccionadaParaModificar: IReserva | null = null;
mostrarFormularioModificar = false;

// Método para abrir formulario de modificación
abrirModificarReserva(reserva: IReserva) {
  // Validar que no esté cancelada
  if (reserva.estado === 'CANCELADA') {
    alert('No se puede modificar una reserva cancelada');
    return;
  }
  
  this.reservaSeleccionadaParaModificar = reserva;
  this.mostrarFormularioModificar = true;
}

// Método para manejar reserva modificada
onReservaModificada(reservaActualizada: IReserva) {
  // Actualizar la lista de reservas
  const index = this.reservas.findIndex(r => r.id === reservaActualizada.id);
  if (index !== -1) {
    this.reservas[index] = reservaActualizada;
  }
  
  // Reagrupar por día
  this.reservasPorDia = this.agruparReservasPorDia(this.reservas);
  
  // Cerrar formulario
  this.mostrarFormularioModificar = false;
  this.reservaSeleccionadaParaModificar = null;
  
  alert('Reserva modificada exitosamente');
}

// Método para cancelar modificación
onCancelarModificacion() {
  this.mostrarFormularioModificar = false;
  this.reservaSeleccionadaParaModificar = null;
}
```

**Template:** `mis-reservas.html` (agregar botón y formulario)

```html
<!-- En la lista de reservas, agregar botón -->
<button (click)="abrirModificarReserva(reserva)" 
        [disabled]="reserva.estado === 'CANCELADA'"
        class="btn btn-sm btn-primary">
  Modificar
</button>

<!-- Agregar componente de modificación (condicional) -->
<app-modificar-reserva
  *ngIf="mostrarFormularioModificar && reservaSeleccionadaParaModificar"
  [reserva]="reservaSeleccionadaParaModificar"
  (reservaModificada)="onReservaModificada($event)"
  (cancelar)="onCancelarModificacion()">
</app-modificar-reserva>
```

**Importar componente en `mis-reservas.ts`:**

```typescript
import { ModificarReservaComponent } from '../../components/modificar-reserva/modificar-reserva';

@Component({
  // ...
  imports: [CommonModule, RouterLink, ModificarReservaComponent],
  // ...
})
```

---

## ✅ Checklist de Implementación

### Backend Restaurante (SOAP/REST)
- [ ] Agregar método `modificarReserva` en `RestauranteClient`
- [ ] Implementar en `RestauranteRestClient`
- [ ] Implementar en `RestauranteSoapClientImpl`
- [ ] Crear `ModificarReservaJsonDto` para SOAP
- [ ] Implementar endpoint REST `PUT /api/restaurantes/{nro}/reservas/{cod}`
- [ ] Implementar operación SOAP `modificarReservaRequest`
- [ ] Actualizar XSD para SOAP

### Backend Ristorino
- [ ] Crear `ModificarReservaDto`
- [ ] Crear `ReservaParaModificarDto`
- [ ] Crear SP `sp_ModificarReservaCompleta`
- [ ] Crear SP `sp_ObtenerReservaParaModificar`
- [ ] Agregar método `modificarReservaCompleta` en `ReservaRepository`
- [ ] Agregar método `obtenerReservaParaModificar` en `ReservaRepository`
- [ ] Implementar `modificarReserva` en `ReservaService` (con sincronización y rollback)
- [ ] Agregar endpoint `PUT /reservas/{id}/modificar` en `ReservaResource`

### Frontend
- [ ] Crear interfaz `IModificarReserva`
- [ ] Agregar método `modificarReserva` en `ReservaResource` (Angular)
- [ ] Crear componente `ModificarReservaComponent`
- [ ] Crear template `modificar-reserva.html`
- [ ] Integrar componente en `MisReservasPage`
- [ ] Agregar botón "Modificar" en lista de reservas
- [ ] Manejar eventos de modificación y cancelación

### Testing
- [ ] Probar modificación con REST
- [ ] Probar modificación con SOAP
- [ ] Probar validaciones (reserva cancelada, fecha pasada, etc.)
- [ ] Probar rollback cuando falla sincronización externa
- [ ] Probar en frontend (formulario, validaciones, actualización de lista)

---

## 📝 Orden de Implementación Recomendado

1. **Backend Restaurante (REST)** - Implementar endpoint primero (más simple)
2. **Backend Ristorino** - Implementar lógica completa con REST
3. **Frontend** - Conectar con backend Ristorino
4. **Backend Restaurante (SOAP)** - Implementar operación SOAP
5. **Testing** - Probar ambos protocolos (REST y SOAP)

---

## ⚠️ Consideraciones Importantes

### Validaciones
- La reserva debe existir y pertenecer al cliente
- La reserva no debe estar cancelada
- La nueva fecha debe ser futura
- El nuevo turno debe estar habilitado
- Validar disponibilidad en el restaurante externo (opcional pero recomendado)

### Sincronización
- Si falla la sincronización externa, decidir si hacer rollback o mantener cambios locales
- Considerar implementar un sistema de reintentos
- Registrar logs de errores de sincronización

### Códigos de Mapeo
- Recordar convertir `cod_zona` interno a `cod_zona_restaurante` externo
- Recordar convertir `nro_sucursal` interno a `cod_sucursal_restaurante` externo
- Usar `cod_reserva_sucursal` para identificar la reserva en el restaurante externo

### Manejo de Errores
- Proporcionar mensajes de error claros al usuario
- Registrar errores en logs para debugging
- Considerar notificaciones al usuario si la sincronización falla

---

## 🔍 Referencias de Código Existente

- **Actualizar reserva local:** `ReservaService.actualizarReserva()` (solo BD local)
- **Confirmar reserva:** `ReservaService.confirmarReserva()` (ejemplo de sincronización)
- **Registrar reserva:** `RestauranteClient.registrarReserva()` (ejemplo de llamada externa)
- **Repository pattern:** `ReservaRepository.update()` (ejemplo de SP)
- **DTO mapping:** `ActualizarReservaDto` (ejemplo de estructura)

---

## 📚 Documentación Relacionada

- Ver `GUIA_DESARROLLO.md` para patrones generales
- Ver `GUIA_INTEGRACION_RESTAURANTES.md` para detalles de integración
- Ver `EJEMPLOS_PRACTICOS.md` para ejemplos similares
- Ver `REFERENCIA_RAPIDA.md` para snippets de código

---

**¡Éxito en tu examen! 🎓**
