# 📚 Guía Completa de Desarrollo - das-ristorino

## 🎯 Objetivo
Esta guía te permitirá hacer cambios en las aplicaciones **das-ristorino** y **das-restaurante** sin usar IA. Incluye patrones, ejemplos prácticos y pasos detallados.

---

## 📋 Tabla de Contenidos

1. [Arquitectura del Sistema](#arquitectura-del-sistema)
2. [Estructura de Carpetas](#estructura-de-carpetas)
3. [Patrones de Código](#patrones-de-código)
4. [Agregar Nueva Funcionalidad (Backend)](#agregar-nueva-funcionalidad-backend)
5. [Agregar Nueva Funcionalidad (Frontend)](#agregar-nueva-funcionalidad-frontend)
6. [Trabajar con Stored Procedures](#trabajar-con-stored-procedures)
7. [Trabajar con DTOs](#trabajar-con-dtos)
8. [Integración SOAP/REST](#integración-soaprest)
9. [Ejemplos Prácticos](#ejemplos-prácticos)
10. [Checklist para Examen](#checklist-para-examen)

---

## 🏗️ Arquitectura del Sistema

### Componentes Principales

```
┌─────────────────────────────────────────┐
│   das-ristorino (Puerto 8080)          │
│   ┌─────────────────────────────────┐  │
│   │  Backend Spring Boot            │  │
│   │  - Resources (Controllers)      │  │
│   │  - Services (Lógica Negocio)    │  │
│   │  - Repositories (Acceso Datos)  │  │
│   └─────────────────────────────────┘  │
│   ┌─────────────────────────────────┐  │
│   │  Frontend Angular               │  │
│   │  - Pages                         │  │
│   │  - Components                    │  │
│   │  - Services                      │  │
│   └─────────────────────────────────┘  │
└─────────────────────────────────────────┘
              │
    ┌─────────┴─────────┐
    │                   │
    ▼                   ▼
┌──────────┐      ┌──────────┐
│ SOAP     │      │ REST     │
│ :8081    │      │ :8082    │
└──────────┘      └──────────┘
```

### Flujo de Datos

1. **Frontend** → Hace request HTTP al **Backend**
2. **Backend** → **Resource** recibe request
3. **Resource** → Llama a **Service**
4. **Service** → Llama a **Repository**
5. **Repository** → Ejecuta **Stored Procedure** en SQL Server
6. **Repository** → Retorna DTO al **Service**
7. **Service** → Retorna DTO al **Resource**
8. **Resource** → Retorna JSON al **Frontend**

---

## 📁 Estructura de Carpetas

### Backend (Spring Boot)

```
backend/src/main/java/ar/edu/ubp/das/backend/
├── resources/          # Controllers REST (@RestController)
│   ├── ReservaResource.java
│   ├── RestauranteResource.java
│   └── ...
├── service/            # Lógica de negocio (@Service)
│   ├── ReservaService.java
│   ├── RestauranteService.java
│   └── ...
├── repository/         # Acceso a datos (@Repository)
│   ├── ReservaRepository.java
│   ├── RestauranteRepository.java
│   └── ...
├── dto/                # Data Transfer Objects
│   ├── ReservaResponseDto.java
│   ├── CrearReservaDto.java
│   └── ...
├── config/             # Configuración
│   └── SecurityConfig.java
└── components/         # Componentes reutilizables
    └── SimpleJdbcCallFactory.java
```

### Frontend (Angular)

```
frontend/das-ristorino-frontend/src/app/
├── main/
│   ├── pages/          # Páginas completas
│   │   ├── home/
│   │   ├── restaurantes/
│   │   └── ...
│   ├── components/     # Componentes reutilizables
│   │   ├── detalle-restaurante/
│   │   └── ...
│   ├── api/
│   │   ├── resources/  # Clientes HTTP
│   │   │   ├── reserva-resource.ts
│   │   │   └── ...
│   │   └── models/     # Interfaces TypeScript
│   │       ├── i-reserva.ts
│   │       └── ...
│   └── services/       # Servicios Angular
│       └── ...
└── core/               # Módulo core
```

---

## 🔧 Patrones de Código

### 1. Patrón Repository (Backend)

**Propósito**: Acceso a datos mediante Stored Procedures

```java
@Repository
public class MiEntidadRepository {
    
    @Autowired
    private SimpleJdbcCallFactory jdbcCallFactory;
    
    // Obtener lista (con parámetros)
    public List<MiDto> obtenerTodos(String parametro) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("parametro", parametro);
        
        return jdbcCallFactory.executeQuery(
            "sp_ObtenerMiEntidad",  // Nombre del SP
            "dbo",                   // Schema
            params,                  // Parámetros
            "resultado",            // Nombre del ResultSet
            MiDto.class             // Clase DTO
        );
    }
    
    // Obtener lista (sin parámetros)
    public List<MiDto> obtenerTodos() {
        return jdbcCallFactory.executeQuery(
            "sp_ObtenerMiEntidad",
            "dbo",
            "resultado",
            MiDto.class
        );
    }
    
    // Obtener uno (Optional)
    public Optional<MiDto> obtenerPorId(String id) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);
        
        List<MiDto> resultados = jdbcCallFactory.executeQuery(
            "sp_ObtenerMiEntidadPorId",
            "dbo",
            params,
            "resultado",
            MiDto.class
        );
        
        return resultados.isEmpty() 
            ? Optional.empty() 
            : Optional.of(resultados.get(0));
    }
    
    // Crear (con OUTPUT)
    public String crear(MiRequestDto dto) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("campo1", dto.getCampo1())
                .addValue("campo2", dto.getCampo2())
                .addValue("nuevo_id", null, Types.VARCHAR); // OUTPUT
        
        Map<String, Object> result = jdbcCallFactory.executeWithOutputs(
            "sp_CrearMiEntidad",
            "dbo",
            params
        );
        
        return result.get("nuevo_id").toString();
    }
    
    // Actualizar (sin OUTPUT)
    public void actualizar(String id, MiRequestDto dto) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("campo1", dto.getCampo1())
                .addValue("campo2", dto.getCampo2());
        
        jdbcCallFactory.execute(
            "sp_ActualizarMiEntidad",
            "dbo",
            params
        );
    }
    
    // Eliminar
    public void eliminar(String id) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);
        
        jdbcCallFactory.execute(
            "sp_EliminarMiEntidad",
            "dbo",
            params
        );
    }
}
```

### 2. Patrón Service (Backend)

**Propósito**: Lógica de negocio y orquestación

```java
@Service
public class MiEntidadService {
    
    private final MiEntidadRepository repository;
    private final OtroRepository otroRepository;
    
    // Inyección por constructor (recomendado)
    public MiEntidadService(
            MiEntidadRepository repository,
            OtroRepository otroRepository) {
        this.repository = repository;
        this.otroRepository = otroRepository;
    }
    
    // Método simple: delegar al repository
    public List<MiDto> obtenerTodos() {
        return repository.obtenerTodos();
    }
    
    // Método con lógica de negocio
    public MiDto crear(MiRequestDto request) {
        // 1. Validaciones
        if (request.getCampo1() == null) {
            throw new RuntimeException("Campo1 es requerido");
        }
        
        // 2. Lógica de negocio
        String nuevoId = repository.crear(request);
        
        // 3. Obtener resultado
        return repository.obtenerPorId(nuevoId)
                .orElseThrow(() -> new RuntimeException("No se pudo crear"));
    }
    
    // Método con integración externa
    public MiDto crearConIntegracion(MiRequestDto request) {
        // 1. Crear en base de datos local
        String nuevoId = repository.crear(request);
        
        // 2. Integrar con servicio externo
        try {
            otroRepository.notificarCreacion(nuevoId);
        } catch (Exception e) {
            // Rollback si falla
            repository.eliminar(nuevoId);
            throw new RuntimeException("Error en integración", e);
        }
        
        return repository.obtenerPorId(nuevoId)
                .orElseThrow();
    }
}
```

### 3. Patrón Resource (Controller) (Backend)

**Propósito**: Endpoints REST API

```java
@RestController
@RequestMapping("/api/mi-entidad")
@CrossOrigin(origins = "*")
public class MiEntidadResource {
    
    private static final Logger logger = LoggerFactory.getLogger(MiEntidadResource.class);
    
    private final MiEntidadService service;
    private final LanguageService languageService;
    
    public MiEntidadResource(
            MiEntidadService service,
            LanguageService languageService) {
        this.service = service;
        this.languageService = languageService;
    }
    
    // GET - Listar todos
    @GetMapping
    public ResponseEntity<List<MiDto>> obtenerTodos() {
        List<MiDto> resultados = service.obtenerTodos();
        return ResponseEntity.ok(resultados);
    }
    
    // GET - Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<MiDto> obtenerPorId(@PathVariable String id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // POST - Crear nuevo
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody MiRequestDto request) {
        try {
            MiDto creado = service.crear(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (RuntimeException e) {
            logger.warn("Error al crear: {}", e.getMessage());
            return ResponseHelper.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado", e);
            return ResponseHelper.internalServerError("Error: " + e.getMessage());
        }
    }
    
    // PUT - Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable String id,
            @Valid @RequestBody MiRequestDto request) {
        try {
            MiDto actualizado = service.actualizar(id, request);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            logger.warn("Error al actualizar: {}", e.getMessage());
            return ResponseHelper.badRequest(e.getMessage());
        }
    }
    
    // DELETE - Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // GET - Con autenticación JWT
    @GetMapping("/mis-entidades")
    public ResponseEntity<?> obtenerMisEntidades(Authentication authentication) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
                return ResponseHelper.unauthorized("No autenticado");
            }
            
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String nroCliente = jwt.getClaimAsString("nroCliente");
            
            List<MiDto> resultados = service.obtenerPorCliente(nroCliente);
            return ResponseEntity.ok(resultados);
        } catch (Exception e) {
            logger.error("Error", e);
            return ResponseHelper.internalServerError("Error: " + e.getMessage());
        }
    }
}
```

### 4. Patrón DTO (Data Transfer Object)

**Propósito**: Transferir datos entre capas

```java
// DTO de Request (lo que recibe el API)
public class CrearMiEntidadDto {
    private String campo1;
    private Integer campo2;
    
    // Constructor sin argumentos (requerido por Jackson)
    public CrearMiEntidadDto() {}
    
    // Getters y Setters
    public String getCampo1() { return campo1; }
    public void setCampo1(String campo1) { this.campo1 = campo1; }
    
    public Integer getCampo2() { return campo2; }
    public void setCampo2(Integer campo2) { this.campo2 = campo2; }
}

// DTO de Response (lo que retorna el API)
public class MiEntidadDto {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("campo1")
    private String campo1;
    
    @JsonProperty("campo2")
    private Integer campo2;
    
    @JsonProperty("fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    // Constructor sin argumentos
    public MiEntidadDto() {}
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    // ... resto de getters/setters
}
```

---

## ➕ Agregar Nueva Funcionalidad (Backend)

### Paso a Paso: Agregar "Gestión de Reseñas"

#### Paso 1: Crear Stored Procedures (SQL)

```sql
-- En scripts/sql/02_create_stored_procedures.sql

-- Obtener reseñas por restaurante
CREATE OR ALTER PROCEDURE sp_ObtenerResenasPorRestaurante
    @nro_restaurante VARCHAR(36),
    @nro_sucursal VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        r.nro_reserva,
        c.nombre + ' ' + c.apellido as nombre_cliente,
        r.calificacion,
        r.comentario,
        r.fecha_resena as fechaResena
    FROM resenas r
    INNER JOIN reservas_restaurantes rr ON r.nro_reserva = rr.nro_reserva
    INNER JOIN clientes c ON rr.nro_cliente = c.nro_cliente
    WHERE rr.nro_restaurante = @nro_restaurante
      AND rr.nro_sucursal = @nro_sucursal
    ORDER BY r.fecha_resena DESC;
END;
GO

-- Insertar reseña
CREATE OR ALTER PROCEDURE sp_InsertarResena
    @nro_reserva VARCHAR(36),
    @comentario NVARCHAR(500),
    @calificacion INT
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO resenas (nro_reserva, comentario, calificacion, fecha_resena)
    VALUES (@nro_reserva, @comentario, @calificacion, GETDATE());
END;
GO
```

#### Paso 2: Crear DTOs

```java
// dto/ResenaDto.java
package ar.edu.ubp.das.backend.dto;

public class ResenaDto {
    private String nroReserva;
    private String nombreCliente;
    private int calificacion;
    private String comentario;
    private Date fechaResena;
    
    // Constructor, getters y setters
    public ResenaDto() {}
    
    public String getNroReserva() { return nroReserva; }
    public void setNroReserva(String nroReserva) { this.nroReserva = nroReserva; }
    
    // ... resto de getters/setters
}

// dto/ResenaRequestDto.java
package ar.edu.ubp.das.backend.dto;

public class ResenaRequestDto {
    private String nroReserva;
    private String comentario;
    private int calificacion;
    
    // Constructor, getters y setters
    public ResenaRequestDto() {}
    
    public String getNroReserva() { return nroReserva; }
    public void setNroReserva(String nroReserva) { this.nroReserva = nroReserva; }
    
    // ... resto
}
```

#### Paso 3: Crear Repository

```java
// repository/ResenaRepository.java
package ar.edu.ubp.das.backend.repository;

import ar.edu.ubp.das.backend.dto.ResenaDto;
import ar.edu.ubp.das.backend.dto.ResenaRequestDto;
import ar.edu.ubp.das.backend.components.SimpleJdbcCallFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ArrayList;

@Repository
public class ResenaRepository {
    
    @Autowired
    private SimpleJdbcCallFactory jdbcCallFactory;
    
    public List<ResenaDto> obtenerResenasPorRestaurante(
            String nroRestaurante, 
            String nroSucursal) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", nroRestaurante)
                .addValue("nro_sucursal", nroSucursal);
        
        List<ResenaDto> resenas = jdbcCallFactory.executeQuery(
            "sp_ObtenerResenasPorRestaurante",
            "dbo",
            params,
            "resenas",
            ResenaDto.class
        );
        
        return resenas != null && !resenas.isEmpty() 
            ? resenas 
            : new ArrayList<>();
    }
    
    public void crearResena(ResenaRequestDto resenaRequestDto) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_reserva", resenaRequestDto.getNroReserva())
                .addValue("comentario", resenaRequestDto.getComentario())
                .addValue("calificacion", resenaRequestDto.getCalificacion());
        
        jdbcCallFactory.execute(
            "sp_InsertarResena",
            "dbo",
            params
        );
    }
}
```

#### Paso 4: Crear Service

```java
// service/ResenaService.java
package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.dto.ResenaDto;
import ar.edu.ubp.das.backend.dto.ResenaRequestDto;
import ar.edu.ubp.das.backend.repository.ResenaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResenaService {
    
    private final ResenaRepository resenaRepository;
    
    public ResenaService(ResenaRepository resenaRepository) {
        this.resenaRepository = resenaRepository;
    }
    
    public List<ResenaDto> obtenerResenasPorRestaurante(
            String nroRestaurante, 
            String nroSucursal) {
        return resenaRepository.obtenerResenasPorRestaurante(
            nroRestaurante, 
            nroSucursal
        );
    }
    
    public void crearResena(ResenaRequestDto resenaRequestDto) {
        // Validaciones
        if (resenaRequestDto.getCalificacion() < 1 || 
            resenaRequestDto.getCalificacion() > 5) {
            throw new RuntimeException("La calificación debe estar entre 1 y 5");
        }
        
        if (resenaRequestDto.getComentario() == null || 
            resenaRequestDto.getComentario().trim().isEmpty()) {
            throw new RuntimeException("El comentario es requerido");
        }
        
        resenaRepository.crearResena(resenaRequestDto);
    }
}
```

#### Paso 5: Crear Resource (Controller)

```java
// resources/ResenaResource.java
package ar.edu.ubp.das.backend.resources;

import ar.edu.ubp.das.backend.dto.ResenaDto;
import ar.edu.ubp.das.backend.dto.ResenaRequestDto;
import ar.edu.ubp.das.backend.resources.util.ResponseHelper;
import ar.edu.ubp.das.backend.service.ResenaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
@CrossOrigin(origins = "*")
public class ResenaResource {
    
    private static final Logger logger = LoggerFactory.getLogger(ResenaResource.class);
    
    private final ResenaService resenaService;
    
    public ResenaResource(ResenaService resenaService) {
        this.resenaService = resenaService;
    }
    
    @GetMapping("/restaurante/{nroRestaurante}/sucursal/{nroSucursal}")
    public ResponseEntity<List<ResenaDto>> obtenerResenas(
            @PathVariable String nroRestaurante,
            @PathVariable String nroSucursal) {
        List<ResenaDto> resenas = resenaService.obtenerResenasPorRestaurante(
            nroRestaurante, 
            nroSucursal
        );
        return ResponseEntity.ok(resenas);
    }
    
    @PostMapping
    public ResponseEntity<?> crearResena(@Valid @RequestBody ResenaRequestDto request) {
        try {
            resenaService.crearResena(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            logger.warn("Error al crear reseña: {}", e.getMessage());
            return ResponseHelper.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado", e);
            return ResponseHelper.internalServerError("Error: " + e.getMessage());
        }
    }
}
```

#### Paso 6: Configurar Seguridad (si es necesario)

```java
// config/SecurityConfig.java
// Agregar en permitAll() si es público:
.requestMatchers("/api/resenas/**").permitAll()

// O dejar autenticado si requiere login
```

---

## 🎨 Agregar Nueva Funcionalidad (Frontend)

### Paso a Paso: Agregar "Ver Reseñas"

#### Paso 1: Crear Interface (Model)

```typescript
// main/api/models/i-resena.ts
export interface IResena {
  nroReserva: string;
  nombreCliente: string;
  calificacion: number;
  comentario: string;
  fechaResena: string;
}

export interface IResenaRequest {
  nroReserva: string;
  comentario: string;
  calificacion: number;
}
```

#### Paso 2: Crear Resource (Cliente HTTP)

```typescript
// main/api/resources/resena-resource.ts
import { Resource } from '@ngx-resource/core';
import { Injectable } from '@angular/core';
import { IResena, IResenaRequest } from '../models/i-resena';
import { environment } from '../../../../environments/environment';

@Injectable()
export class ResenaResource extends Resource {
  
  // Obtener reseñas por restaurante y sucursal
  obtenerResenas(nroRestaurante: string, nroSucursal: string) {
    return this.$get<IResena[]>(
      `${environment.apiUrl}/resenas/restaurante/${nroRestaurante}/sucursal/${nroSucursal}`
    );
  }
  
  // Crear reseña
  crearResena(resena: IResenaRequest) {
    return this.$post<IResena>(
      `${environment.apiUrl}/resenas`,
      null,
      resena
    );
  }
}
```

#### Paso 3: Crear Componente

```typescript
// main/components/resenas/resenas.ts
import { Component, Input, OnInit } from '@angular/core';
import { ResenaResource } from '../../api/resources/resena-resource';
import { IResena } from '../../api/models/i-resena';

@Component({
  selector: 'app-resenas',
  templateUrl: './resenas.html',
  styleUrls: ['./resenas.scss']
})
export class ResenasComponent implements OnInit {
  
  @Input() nroRestaurante!: string;
  @Input() nroSucursal!: string;
  
  resenas: IResena[] = [];
  cargando = false;
  
  constructor(private resenaResource: ResenaResource) {}
  
  ngOnInit() {
    this.cargarResenas();
  }
  
  cargarResenas() {
    this.cargando = true;
    this.resenaResource.obtenerResenas(
      this.nroRestaurante,
      this.nroSucursal
    ).$promise.then(
      (resenas) => {
        this.resenas = resenas;
        this.cargando = false;
      },
      (error) => {
        console.error('Error al cargar reseñas', error);
        this.cargando = false;
      }
    );
  }
}
```

```html
<!-- main/components/resenas/resenas.html -->
<div class="resenas-container">
  <h3>Reseñas</h3>
  
  <div *ngIf="cargando" class="text-center">
    <p>Cargando reseñas...</p>
  </div>
  
  <div *ngIf="!cargando && resenas.length === 0" class="text-center">
    <p>No hay reseñas aún</p>
  </div>
  
  <div *ngFor="let resena of resenas" class="resena-card">
    <div class="resena-header">
      <strong>{{ resena.nombreCliente }}</strong>
      <span class="calificacion">
        ⭐ {{ resena.calificacion }}/5
      </span>
    </div>
    <p class="comentario">{{ resena.comentario }}</p>
    <small class="fecha">{{ resena.fechaResena | date }}</small>
  </div>
</div>
```

#### Paso 4: Usar en una Página

```typescript
// main/pages/detalle-restaurante/detalle-restaurante.ts
// Agregar en el componente existente:

import { ResenaResource } from '../../api/resources/resena-resource';

// En providers del componente:
providers: [ResenaResource]

// En el template:
<app-resenas 
  [nroRestaurante]="restaurante.nroRestaurante"
  [nroSucursal]="sucursalSeleccionada">
</app-resenas>
```

---

## 💾 Trabajar con Stored Procedures

### Tipos de Stored Procedures

#### 1. SP que retorna ResultSet (SELECT)

```sql
CREATE OR ALTER PROCEDURE sp_ObtenerAlgo
    @parametro VARCHAR(36)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT campo1, campo2, campo3
    FROM tabla
    WHERE campo = @parametro;
END;
```

**Uso en Java:**
```java
List<MiDto> resultados = jdbcCallFactory.executeQuery(
    "sp_ObtenerAlgo",
    "dbo",
    params,
    "resultado",  // Nombre del ResultSet
    MiDto.class
);
```

#### 2. SP con OUTPUT

```sql
CREATE OR ALTER PROCEDURE sp_CrearAlgo
    @campo1 VARCHAR(100),
    @nuevo_id VARCHAR(36) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET @nuevo_id = NEWID();
    INSERT INTO tabla (id, campo1) VALUES (@nuevo_id, @campo1);
END;
```

**Uso en Java:**
```java
SqlParameterSource params = new MapSqlParameterSource()
        .addValue("campo1", "valor")
        .addValue("nuevo_id", null, Types.VARCHAR); // OUTPUT

Map<String, Object> result = jdbcCallFactory.executeWithOutputs(
    "sp_CrearAlgo",
    "dbo",
    params
);

String nuevoId = result.get("nuevo_id").toString();
```

#### 3. SP sin retorno (INSERT/UPDATE/DELETE)

```sql
CREATE OR ALTER PROCEDURE sp_ActualizarAlgo
    @id VARCHAR(36),
    @campo1 VARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tabla SET campo1 = @campo1 WHERE id = @id;
END;
```

**Uso en Java:**
```java
jdbcCallFactory.execute(
    "sp_ActualizarAlgo",
    "dbo",
    params
);
```

### Convenciones de Nombres

- `sp_Obtener...` → SELECT (retorna lista)
- `sp_Obtener...PorId` → SELECT uno (retorna Optional)
- `sp_Crear...` → INSERT (con OUTPUT del ID)
- `sp_Actualizar...` → UPDATE
- `sp_Eliminar...` → DELETE
- `sp_Insertar...` → INSERT simple (sin OUTPUT)

---

## 🔄 Trabajar con DTOs

### Mapeo de Campos

**SQL → Java:**
- `snake_case` en SQL → `camelCase` en Java
- `fecha_reserva` → `fechaReserva`
- `nro_cliente` → `nroCliente`

**Java → JSON:**
- Usar `@JsonProperty` para mantener snake_case en JSON:
```java
@JsonProperty("fecha_reserva")
private LocalDate fechaReserva;
```

### Conversión de Tipos

```java
// String → Date
java.sql.Date.valueOf(localDate)

// Date → String
date.toString()

// LocalDateTime → Timestamp
java.sql.Timestamp.valueOf(localDateTime)

// Timestamp → LocalDateTime
timestamp.toLocalDateTime()
```

---

## 🔌 Integración SOAP/REST

### Patrón Factory para Selección Dinámica

```java
// El sistema consulta la BD para saber qué protocolo usar
RestauranteClient client = restauranteClientFactory.getClient(nroRestaurante);

// Si el restaurante usa SOAP → RestauranteSoapClientImpl
// Si el restaurante usa REST → RestauranteRestClient
```

### Llamar a Servicio Externo

```java
// En el Service:
RestauranteClient client = restauranteClientFactory.getClient(nroRestaurante);

// Obtener horarios disponibles
List<HorarioDisponibleDto> horarios = client.getHorariosDisponibles(
    nroRestaurante,
    codSucursal,
    codZona,
    fechaReserva,
    cantidadPersonas
);

// Registrar reserva
String codReservaRestaurante = client.registrarReserva(
    nroCliente,
    apellido,
    nombre,
    email,
    telefono,
    nroRestaurante,
    codSucursal,
    codZona,
    fechaReserva,
    horaDesde,
    cantAdultos,
    cantMenores
);
```

---

## 📝 Ejemplos Prácticos

### Ejemplo 1: Agregar campo "notas" a Reserva

1. **SQL**: Agregar columna a tabla
```sql
ALTER TABLE reservas_restaurantes
ADD notas NVARCHAR(500) NULL;
```

2. **SP**: Actualizar stored procedure
```sql
-- En sp_ObtenerReservaPorId, agregar:
rr.notas as notas
```

3. **DTO**: Agregar campo
```java
@JsonProperty("notas")
private String notas;
```

4. **Repository**: Ya funciona automáticamente (mapeo por nombre)

### Ejemplo 2: Agregar validación en Service

```java
public void crearResena(ResenaRequestDto request) {
    // Validar que la reserva existe
    if (!reservaRepository.existsById(request.getNroReserva())) {
        throw new RuntimeException("La reserva no existe");
    }
    
    // Validar que no haya reseña previa
    List<ResenaDto> existentes = resenaRepository
        .obtenerResenasPorReserva(request.getNroReserva());
    if (!existentes.isEmpty()) {
        throw new RuntimeException("Ya existe una reseña para esta reserva");
    }
    
    resenaRepository.crearResena(request);
}
```

### Ejemplo 3: Agregar filtro en Frontend

```typescript
// En el componente:
filtroCalificacion: number | null = null;

filtrarResenas() {
  if (this.filtroCalificacion === null) {
    this.resenasFiltradas = this.resenas;
  } else {
    this.resenasFiltradas = this.resenas.filter(
      r => r.calificacion === this.filtroCalificacion
    );
  }
}
```

---

## ✅ Checklist para Examen

### Antes de Empezar
- [ ] Entender qué se pide exactamente
- [ ] Identificar qué capas necesito modificar (SQL, Backend, Frontend)
- [ ] Revisar código similar existente

### Backend
- [ ] Crear/Modificar Stored Procedure en SQL
- [ ] Crear/Modificar DTOs si es necesario
- [ ] Crear/Modificar Repository
- [ ] Crear/Modificar Service
- [ ] Crear/Modificar Resource (Controller)
- [ ] Configurar Security si es necesario
- [ ] Probar con Postman/curl

### Frontend
- [ ] Crear/Modificar Interfaces (models)
- [ ] Crear/Modificar Resource (cliente HTTP)
- [ ] Crear/Modificar Componente o Página
- [ ] Agregar ruta si es nueva página
- [ ] Probar en navegador

### Validaciones
- [ ] Validar datos de entrada (@Valid)
- [ ] Manejar errores correctamente
- [ ] Logs apropiados
- [ ] Respuestas HTTP correctas (200, 201, 400, 404, 500)

### Testing Manual
- [ ] Probar caso exitoso
- [ ] Probar caso con error
- [ ] Probar validaciones
- [ ] Probar autenticación (si aplica)

---

## 🎓 Consejos para el Examen

1. **Lee el código existente primero**: Entiende los patrones antes de escribir
2. **Copia y adapta**: Usa código similar como plantilla
3. **Prueba paso a paso**: No escribas todo de una vez
4. **Revisa nombres**: Usa las convenciones del proyecto
5. **Maneja errores**: Siempre usa try-catch y retorna respuestas apropiadas
6. **Logs útiles**: Agrega logs para debugging
7. **Comentarios claros**: Explica lógica compleja

---

## 📚 Referencias Rápidas

### Anotaciones Spring Boot
- `@RestController` → Controller REST
- `@Service` → Lógica de negocio
- `@Repository` → Acceso a datos
- `@Autowired` → Inyección de dependencias
- `@RequestMapping("/api/...")` → Ruta base
- `@GetMapping`, `@PostMapping`, etc. → Métodos HTTP
- `@PathVariable` → Parámetro en URL
- `@RequestBody` → Body del request
- `@RequestHeader` → Header del request
- `@Valid` → Validación de DTO

### Métodos HTTP
- `GET` → Obtener datos
- `POST` → Crear nuevo
- `PUT` → Actualizar completo
- `PATCH` → Actualizar parcial
- `DELETE` → Eliminar

### Códigos HTTP
- `200 OK` → Éxito
- `201 Created` → Creado exitosamente
- `400 Bad Request` → Error de validación
- `401 Unauthorized` → No autenticado
- `404 Not Found` → No encontrado
- `500 Internal Server Error` → Error del servidor

---

**¡Buena suerte en tu examen! 🚀**
