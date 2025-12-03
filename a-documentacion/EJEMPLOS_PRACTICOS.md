# 💡 Ejemplos Prácticos Paso a Paso

## 📋 Ejemplos Completos para el Examen

Esta guía contiene ejemplos completos y funcionales que puedes usar como referencia durante el examen.

---

## 📝 Ejemplo 1: Agregar "Cancelar Reserva" (Backend Completo)

### Requisito
Agregar funcionalidad para cancelar una reserva con validación de que solo el dueño puede cancelarla.

### Paso 1: Stored Procedure

```sql
-- En scripts/sql/02_create_stored_procedures.sql

CREATE OR ALTER PROCEDURE sp_CancelarReserva
    @nro_reserva VARCHAR(36),
    @nro_cliente VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Validar que la reserva existe y pertenece al cliente
    IF NOT EXISTS (
        SELECT 1 
        FROM reservas_restaurantes 
        WHERE nro_reserva = @nro_reserva 
          AND nro_cliente = @nro_cliente
    )
    BEGIN
        RAISERROR('La reserva no existe o no pertenece al cliente', 16, 1);
        RETURN;
    END;
    
    -- Validar que no esté ya cancelada
    IF EXISTS (
        SELECT 1 
        FROM reservas_restaurantes 
        WHERE nro_reserva = @nro_reserva 
          AND cancelada = 1
    )
    BEGIN
        RAISERROR('La reserva ya está cancelada', 16, 1);
        RETURN;
    END;
    
    -- Cancelar la reserva
    UPDATE reservas_restaurantes
    SET cancelada = 1,
        fecha_hora_cancelacion = GETDATE(),
        cod_estado = (SELECT cod_estado FROM estados_reservas WHERE nom_estado = 'Cancelada')
    WHERE nro_reserva = @nro_reserva;
    
    -- Si tiene código de reserva en el restaurante, cancelar también allí
    DECLARE @cod_reserva_sucursal VARCHAR(36);
    SELECT @cod_reserva_sucursal = cod_reserva_sucursal
    FROM reservas_restaurantes
    WHERE nro_reserva = @nro_reserva;
    
    IF @cod_reserva_sucursal IS NOT NULL
    BEGIN
        -- Aquí se podría llamar al servicio del restaurante para cancelar
        -- Por ahora solo marcamos que necesita cancelación externa
    END;
END;
GO
```

### Paso 2: DTO (si necesitas request específico)

```java
// dto/CancelarReservaDto.java
package ar.edu.ubp.das.backend.dto;

public class CancelarReservaDto {
    private String motivoCancelacion;
    
    public CancelarReservaDto() {}
    
    public String getMotivoCancelacion() { return motivoCancelacion; }
    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }
}
```

### Paso 3: Repository

```java
// repository/ReservaRepository.java
// Agregar método:

public void cancelarReserva(String nroReserva, String nroCliente) {
    SqlParameterSource params = new MapSqlParameterSource()
            .addValue("nro_reserva", nroReserva)
            .addValue("nro_cliente", nroCliente);
    
    try {
        jdbcCallFactory.execute("sp_CancelarReserva", "dbo", params);
    } catch (Exception e) {
        throw new RuntimeException("Error al cancelar reserva: " + e.getMessage(), e);
    }
}
```

### Paso 4: Service

```java
// service/ReservaService.java
// Agregar método:

public void cancelarReserva(String nroReserva, String nroCliente) {
    // Validar que la reserva existe y pertenece al cliente
    Optional<ReservaResponseDto> reserva = reservaRepository.findById(nroReserva);
    
    if (reserva.isEmpty()) {
        throw new RuntimeException("La reserva no existe");
    }
    
    if (!reserva.get().getNroCliente().equals(nroCliente)) {
        throw new RuntimeException("No tiene permiso para cancelar esta reserva");
    }
    
    // Validar que no esté ya cancelada
    if ("Cancelada".equalsIgnoreCase(reserva.get().getEstado())) {
        throw new RuntimeException("La reserva ya está cancelada");
    }
    
    // Cancelar en base de datos
    reservaRepository.cancelarReserva(nroReserva, nroCliente);
    
    // Si tiene código de reserva en restaurante, cancelar también allí
    ReservaResponseDto reservaDto = reserva.get();
    if (reservaDto.getCodReservaSucursal() != null) {
        try {
            RestauranteClient client = restauranteClientFactory
                .getClient(reservaDto.getNroRestaurante());
            // Llamar método de cancelación del servicio externo si existe
            // client.cancelarReserva(reservaDto.getCodReservaSucursal());
        } catch (Exception e) {
            logger.warn("No se pudo cancelar en el servicio del restaurante", e);
            // Continuar de todas formas, ya está cancelada en nuestra BD
        }
    }
}
```

### Paso 5: Resource

```java
// resources/ReservaResource.java
// Agregar endpoint:

@PutMapping("/{id}/cancelar")
public ResponseEntity<?> cancelarReserva(
        @PathVariable String id,
        @RequestBody(required = false) CancelarReservaDto request,
        Authentication authentication) {
    try {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            return ResponseHelper.unauthorized("Debe estar autenticado");
        }
        
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String nroCliente = jwt.getClaimAsString("nroCliente");
        
        if (nroCliente == null || nroCliente.isEmpty()) {
            return ResponseHelper.badRequest("Token inválido");
        }
        
        reservaService.cancelarReserva(id, nroCliente);
        
        return ResponseEntity.ok().body(Map.of(
            "mensaje", "Reserva cancelada exitosamente",
            "id", id
        ));
        
    } catch (RuntimeException e) {
        logger.warn("Error al cancelar reserva: {}", e.getMessage());
        return ResponseHelper.badRequest(e.getMessage());
    } catch (Exception e) {
        logger.error("Error inesperado", e);
        return ResponseHelper.internalServerError("Error: " + e.getMessage());
    }
}
```

---

## 📝 Ejemplo 2: Agregar "Filtrar Restaurantes por Tipo de Cocina" (Backend + Frontend)

### Requisito
Agregar filtro para buscar restaurantes por tipo de cocina.

### Backend

#### Paso 1: Stored Procedure

```sql
CREATE OR ALTER PROCEDURE sp_ObtenerRestaurantesPorTipoCocina
    @tipo_cocina VARCHAR(100),
    @nro_idioma INT = 0
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        r.nro_restaurante,
        r.nom_restaurante,
        r.tipo_cocina,
        r.descripcion,
        r.url_imagen
    FROM restaurantes r
    WHERE r.tipo_cocina LIKE '%' + @tipo_cocina + '%'
    ORDER BY r.nom_restaurante;
END;
GO
```

#### Paso 2: Repository

```java
// repository/RestauranteRepository.java
// Agregar método:

public List<RestauranteDto> obtenerPorTipoCocina(String tipoCocina, Integer nroIdioma) {
    SqlParameterSource params = new MapSqlParameterSource()
            .addValue("tipo_cocina", tipoCocina)
            .addValue("nro_idioma", nroIdioma);
    
    return jdbcCallFactory.executeQuery(
        "sp_ObtenerRestaurantesPorTipoCocina",
        "dbo",
        params,
        "restaurantes",
        RestauranteDto.class
    );
}
```

#### Paso 3: Service

```java
// service/RestauranteService.java
// Agregar método:

public List<RestauranteDto> obtenerPorTipoCocina(String tipoCocina, Integer nroIdioma) {
    if (tipoCocina == null || tipoCocina.trim().isEmpty()) {
        return obtenerTodosLosRestaurantes();
    }
    
    return restauranteRepository.obtenerPorTipoCocina(tipoCocina, nroIdioma);
}
```

#### Paso 4: Resource

```java
// resources/RestauranteResource.java
// Agregar endpoint:

@GetMapping("/buscar")
public ResponseEntity<List<RestauranteDto>> buscarRestaurantes(
        @RequestParam(required = false) String tipoCocina,
        @RequestHeader(value = "X-Nro-Idioma", required = false) Integer nroIdiomaHeader) {
    
    Integer nroIdioma = languageService.getNroIdiomaFromRequest(nroIdiomaHeader);
    
    List<RestauranteDto> restaurantes;
    
    if (tipoCocina != null && !tipoCocina.trim().isEmpty()) {
        restaurantes = restauranteService.obtenerPorTipoCocina(tipoCocina, nroIdioma);
    } else {
        restaurantes = restauranteService.obtenerTodosLosRestaurantes();
    }
    
    return ResponseEntity.ok(restaurantes);
}
```

### Frontend

#### Paso 1: Resource

```typescript
// main/api/resources/restaurante-resource.ts
// Agregar método:

buscarRestaurantes(tipoCocina?: string) {
  let url = `${environment.apiUrl}/restaurantes/buscar`;
  if (tipoCocina) {
    url += `?tipoCocina=${encodeURIComponent(tipoCocina)}`;
  }
  return this.$get<IRestaurante[]>(url);
}
```

#### Paso 2: Componente

```typescript
// main/pages/restaurantes/restaurantes.ts
// Agregar propiedades:

tipoCocinaFiltro: string = '';
restaurantesFiltrados: IRestaurante[] = [];

// Agregar método:

filtrarPorTipoCocina() {
  if (!this.tipoCocinaFiltro || this.tipoCocinaFiltro.trim() === '') {
    this.restaurantesFiltrados = this.restaurantes;
    return;
  }
  
  this.restauranteResource.buscarRestaurantes(this.tipoCocinaFiltro)
    .$promise.then(
      (restaurantes) => {
        this.restaurantesFiltrados = restaurantes;
      },
      (error) => {
        console.error('Error al filtrar', error);
        this.restaurantesFiltrados = [];
      }
    );
}
```

#### Paso 3: Template

```html
<!-- main/pages/restaurantes/restaurantes.html -->
<div class="filtros">
  <input 
    type="text" 
    [(ngModel)]="tipoCocinaFiltro"
    placeholder="Tipo de cocina (ej: Italiana, Japonesa)"
    (keyup.enter)="filtrarPorTipoCocina()">
  <button (click)="filtrarPorTipoCocina()">Filtrar</button>
</div>

<div *ngFor="let restaurante of restaurantesFiltrados" class="restaurante-card">
  <!-- ... contenido del card ... -->
</div>
```

---

## 📝 Ejemplo 3: Agregar "Calcular Costo de Reserva" (Con Lógica de Negocio)

### Requisito
Calcular el costo de una reserva basado en fecha, cantidad de personas y tipo de restaurante.

### Paso 1: Stored Procedure

```sql
CREATE OR ALTER PROCEDURE sp_CalcularCostoReserva
    @nro_restaurante VARCHAR(36),
    @fecha_reserva DATE,
    @cant_adultos INT,
    @cant_menores INT,
    @costo_reserva DECIMAL(12,2) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @costo_base DECIMAL(12,2);
    DECLARE @costo_adulto DECIMAL(12,2);
    DECLARE @costo_menor DECIMAL(12,2);
    DECLARE @multiplicador_fecha DECIMAL(3,2);
    
    -- Obtener costos base del restaurante
    SELECT 
        @costo_base = costo_base,
        @costo_adulto = costo_adulto,
        @costo_menor = costo_menor
    FROM restaurantes
    WHERE nro_restaurante = @nro_restaurante;
    
    -- Calcular multiplicador según día de la semana
    DECLARE @dia_semana INT = DATEPART(WEEKDAY, @fecha_reserva);
    IF @dia_semana IN (1, 7) -- Sábado o Domingo
        SET @multiplicador_fecha = 1.2; -- 20% más caro
    ELSE
        SET @multiplicador_fecha = 1.0;
    
    -- Calcular costo total
    SET @costo_reserva = @costo_base + 
                        (@costo_adulto * @cant_adultos) + 
                        (@costo_menor * @cant_menores);
    SET @costo_reserva = @costo_reserva * @multiplicador_fecha;
END;
GO
```

### Paso 2: Repository

```java
// repository/ReservaRepository.java
// Agregar método:

public BigDecimal calcularCostoReserva(
        String nroRestaurante,
        LocalDate fechaReserva,
        Integer cantAdultos,
        Integer cantMenores) {
    
    SqlParameterSource params = new MapSqlParameterSource()
            .addValue("nro_restaurante", nroRestaurante)
            .addValue("fecha_reserva", java.sql.Date.valueOf(fechaReserva))
            .addValue("cant_adultos", cantAdultos)
            .addValue("cant_menores", cantMenores)
            .addValue("costo_reserva", null, Types.DECIMAL);
    
    Map<String, Object> result = jdbcCallFactory.executeWithOutputs(
        "sp_CalcularCostoReserva",
        "dbo",
        params,
        new SqlOutParameter("costo_reserva", Types.DECIMAL)
    );
    
    if (result != null && result.containsKey("costo_reserva")) {
        Object costo = result.get("costo_reserva");
        if (costo instanceof BigDecimal) {
            return (BigDecimal) costo;
        } else if (costo instanceof Double) {
            return BigDecimal.valueOf((Double) costo);
        }
    }
    
    return BigDecimal.ZERO;
}
```

### Paso 3: Service

```java
// service/ReservaService.java
// Agregar método:

public BigDecimal calcularCostoReserva(
        String nroRestaurante,
        LocalDate fechaReserva,
        Integer cantAdultos,
        Integer cantMenores) {
    
    // Validaciones
    if (fechaReserva.isBefore(LocalDate.now())) {
        throw new RuntimeException("La fecha no puede ser en el pasado");
    }
    
    if (cantAdultos < 1) {
        throw new RuntimeException("Debe haber al menos un adulto");
    }
    
    if (cantMenores < 0) {
        throw new RuntimeException("La cantidad de menores no puede ser negativa");
    }
    
    return reservaRepository.calcularCostoReserva(
        nroRestaurante,
        fechaReserva,
        cantAdultos,
        cantMenores
    );
}
```

### Paso 4: Resource

```java
// resources/ReservaResource.java
// Agregar endpoint:

@GetMapping("/calcular-costo")
public ResponseEntity<?> calcularCosto(
        @RequestParam String nroRestaurante,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaReserva,
        @RequestParam Integer cantAdultos,
        @RequestParam(required = false, defaultValue = "0") Integer cantMenores) {
    
    try {
        BigDecimal costo = reservaService.calcularCostoReserva(
            nroRestaurante,
            fechaReserva,
            cantAdultos,
            cantMenores
        );
        
        return ResponseEntity.ok(Map.of(
            "costo", costo,
            "moneda", "ARS"
        ));
        
    } catch (RuntimeException e) {
        return ResponseHelper.badRequest(e.getMessage());
    }
}
```

---

## 📝 Ejemplo 4: Agregar "Historial de Clicks de Promoción" (Con Agregación)

### Requisito
Mostrar estadísticas de clicks por promoción.

### Paso 1: Stored Procedure

```sql
CREATE OR ALTER PROCEDURE sp_ObtenerEstadisticasClicksPromocion
    @nro_promocion VARCHAR(36),
    @fecha_desde DATE = NULL,
    @fecha_hasta DATE = NULL
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        COUNT(*) as total_clicks,
        COUNT(DISTINCT nro_cliente) as clientes_unicos,
        SUM(costo_click) as costo_total,
        CAST(AVG(CAST(costo_click AS FLOAT)) AS DECIMAL(10,2)) as costo_promedio
    FROM clicks_promociones
    WHERE nro_promocion = @nro_promocion
      AND (@fecha_desde IS NULL OR fecha_hora_registro >= @fecha_desde)
      AND (@fecha_hasta IS NULL OR fecha_hora_registro <= @fecha_hasta);
END;
GO
```

### Paso 2: DTO

```java
// dto/EstadisticasClicksDto.java
package ar.edu.ubp.das.backend.dto;

import java.math.BigDecimal;

public class EstadisticasClicksDto {
    private Long totalClicks;
    private Long clientesUnicos;
    private BigDecimal costoTotal;
    private BigDecimal costoPromedio;
    
    // Constructores, getters y setters
}
```

### Paso 3: Repository

```java
// repository/PromocionRepository.java
// Agregar método:

public EstadisticasClicksDto obtenerEstadisticasClicks(
        String nroPromocion,
        LocalDate fechaDesde,
        LocalDate fechaHasta) {
    
    SqlParameterSource params = new MapSqlParameterSource()
            .addValue("nro_promocion", nroPromocion)
            .addValue("fecha_desde", fechaDesde != null ? 
                java.sql.Date.valueOf(fechaDesde) : null)
            .addValue("fecha_hasta", fechaHasta != null ? 
                java.sql.Date.valueOf(fechaHasta) : null);
    
    List<EstadisticasClicksDto> resultados = jdbcCallFactory.executeQuery(
        "sp_ObtenerEstadisticasClicksPromocion",
        "dbo",
        params,
        "estadisticas",
        EstadisticasClicksDto.class
    );
    
    return resultados != null && !resultados.isEmpty() 
        ? resultados.get(0) 
        : new EstadisticasClicksDto(); // Con valores en 0
}
```

---

## 🎯 Patrón: Validación Compleja

```java
public void crearReserva(CrearReservaDto request) {
    // 1. Validar fecha
    if (request.getFechaReserva().isBefore(LocalDate.now())) {
        throw new RuntimeException("La fecha no puede ser en el pasado");
    }
    
    // 2. Validar horario
    LocalTime horaMinima = LocalTime.of(12, 0);
    LocalTime horaMaxima = LocalTime.of(23, 0);
    if (request.getHoraDesde().isBefore(horaMinima) || 
        request.getHoraDesde().isAfter(horaMaxima)) {
        throw new RuntimeException("El horario debe estar entre 12:00 y 23:00");
    }
    
    // 3. Validar cantidad de personas
    int totalPersonas = request.getCantAdultos() + request.getCantMenores();
    if (totalPersonas < 1 || totalPersonas > 20) {
        throw new RuntimeException("La cantidad de personas debe estar entre 1 y 20");
    }
    
    // 4. Validar que el restaurante existe
    if (!restauranteRepository.existeRestaurante(request.getNroRestaurante())) {
        throw new RuntimeException("El restaurante no existe");
    }
    
    // 5. Proceder con la creación
    return reservaRepository.crear(request);
}
```

---

## 🎯 Patrón: Manejo de Errores con Información Útil

```java
@PostMapping
public ResponseEntity<?> crear(@Valid @RequestBody MiDto request) {
    try {
        MiDto creado = service.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        
    } catch (IllegalArgumentException e) {
        // Error de validación de negocio
        logger.warn("Validación fallida: {}", e.getMessage());
        return ResponseHelper.badRequest(e.getMessage());
        
    } catch (RuntimeException e) {
        // Error de lógica de negocio
        logger.warn("Error de negocio: {}", e.getMessage());
        return ResponseHelper.badRequest(e.getMessage());
        
    } catch (Exception e) {
        // Error inesperado
        logger.error("Error inesperado al crear", e);
        return ResponseHelper.internalServerError(
            "Error al crear. Por favor, intente nuevamente."
        );
    }
}
```

---

## 🎯 Patrón: Búsqueda con Múltiples Filtros

```java
@GetMapping("/buscar")
public ResponseEntity<List<RestauranteDto>> buscar(
        @RequestParam(required = false) String tipoCocina,
        @RequestParam(required = false) String localidad,
        @RequestParam(required = false) Integer calificacionMinima,
        @RequestParam(required = false) Boolean tienePromociones) {
    
    // Construir filtros dinámicamente
    Map<String, Object> filtros = new HashMap<>();
    if (tipoCocina != null && !tipoCocina.trim().isEmpty()) {
        filtros.put("tipo_cocina", tipoCocina);
    }
    if (localidad != null && !localidad.trim().isEmpty()) {
        filtros.put("localidad", localidad);
    }
    if (calificacionMinima != null) {
        filtros.put("calificacion_minima", calificacionMinima);
    }
    if (tienePromociones != null) {
        filtros.put("tiene_promociones", tienePromociones);
    }
    
    List<RestauranteDto> resultados = restauranteService.buscarConFiltros(filtros);
    return ResponseEntity.ok(resultados);
}
```

---

## ✅ Checklist Rápido para el Examen

### Antes de Codificar
- [ ] Leer el requerimiento completo
- [ ] Identificar qué capas necesito modificar
- [ ] Buscar código similar como referencia
- [ ] Planificar los pasos

### Durante el Desarrollo
- [ ] SQL → Stored Procedure
- [ ] Java → DTOs
- [ ] Java → Repository
- [ ] Java → Service
- [ ] Java → Resource
- [ ] TypeScript → Models (si aplica)
- [ ] TypeScript → Resource (si aplica)
- [ ] TypeScript → Component (si aplica)

### Después de Codificar
- [ ] Verificar nombres de métodos/SPs
- [ ] Verificar tipos de datos
- [ ] Agregar validaciones
- [ ] Agregar manejo de errores
- [ ] Probar caso exitoso
- [ ] Probar caso con error

---

**¡Estos ejemplos deberían cubrir la mayoría de los casos que te puedan pedir! 🚀**
