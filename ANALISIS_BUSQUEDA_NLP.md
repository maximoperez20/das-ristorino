# Análisis Completo del Sistema de Búsqueda NLP

## 📋 Resumen Ejecutivo

El sistema de búsqueda NLP permite a los usuarios buscar restaurantes usando lenguaje natural (ej: "quiero comer pizza en el centro"). Utiliza OpenAI para extraer intenciones y criterios de búsqueda, los valida contra catálogos de la base de datos, y ejecuta búsquedas inteligentes con scoring de relevancia.

---

## 🏗️ Arquitectura General

### Flujo de Datos

```
Usuario → Frontend → RestauranteResource → BusquedaNLPService
                                                      ↓
                                   1. Construir Contexto (catálogos BD)
                                                      ↓
                                   2. OpenAI (analizar consulta)
                                                      ↓
                                   3. Validar y Mapear (ValidacionCatalogoService)
                                                      ↓
                                   4. Buscar Exactos (sp_BuscarRestaurantesPorNLP)
                                                      ↓
                                   5. Obtener Sugerencias (sp_ObtenerSugerenciasRestaurantes)
                                                      ↓
                                   6. Retornar Resultados
```

---

## 🔍 Componentes Principales

### 1. **BusquedaNLPService** (Orquestador Principal)

**Responsabilidades:**
- Orquestar todo el flujo de búsqueda NLP
- Construir contexto con catálogos de BD
- Llamar a OpenAI para análisis
- Validar y mapear respuestas de IA
- Ejecutar búsquedas exactas y sugerencias
- Eliminar duplicados y filtrar resultados

**Métodos Clave:**
- `buscarRestaurantesPorNLP()` - Método principal
- `construirContexto()` - Obtiene catálogos de BD
- `parsearRespuestaOpenAI()` - Parsea JSON de OpenAI
- `extraerDeEstructuraAnidada()` - Maneja estructuras anidadas
- `extraerJSONDeTexto()` - Limpia respuesta de OpenAI

**Problemas Identificados:**
- ✅ **Bien estructurado**: Usa DTOs tipados
- ⚠️ **Parsing complejo**: Maneja múltiples formatos de respuesta de OpenAI
- ⚠️ **Lógica de negocio mezclada**: Validación, parsing y búsqueda en un solo servicio

---

### 2. **OpenAIService** (Integración con IA)

**Responsabilidades:**
- Comunicarse con OpenAI API
- Construir prompts con contexto
- Manejar respuestas de OpenAI

**Métodos Clave:**
- `analizarConsultaNLP()` - Analiza consulta con OpenAI
- `construirJSONParaBusquedaNLP()` - Construye contexto JSON

**Problemas Identificados:**
- ✅ **Bien encapsulado**: Separado del servicio principal
- ⚠️ **Prompt hardcodeado**: Instrucciones largas en código Java
- ⚠️ **Manejo de errores**: Podría ser más específico

---

### 3. **ValidacionCatalogoService** (Validación y Mapeo)

**Responsabilidades:**
- Validar valores devueltos por IA contra catálogos
- Mapear sinónimos a valores exactos
- Fuzzy matching con Levenshtein distance

**Métodos Clave:**
- `validarYMapar()` - Valida y mapea respuesta de IA
- `buscarValorExacto()` - Búsqueda con múltiples estrategias
- `buscarValorExactoTipoComida()` - Lógica especial para tipos de comida
- `buscarValorExactoLocalidad()` - Lógica estricta para localidades
- `calcularDistanciaLevenshtein()` - Algoritmo de distancia

**Problemas Identificados:**
- ✅ **Bien diseñado**: Separación de responsabilidades
- ✅ **Algoritmos inteligentes**: Fuzzy matching y sinónimos
- ⚠️ **Sinónimos hardcodeados**: Mapeos específicos en código
- ⚠️ **Podría usar DTOs**: Los catálogos se pasan como List<String>

---

### 4. **BusquedaRepository** (Acceso a Catálogos)

**Responsabilidades:**
- Obtener catálogos de la base de datos
- Consultas simples a tablas de dominio

**Métodos:**
- `obtenerTiposComida()`
- `obtenerBarrios()`
- `obtenerLocalidades()`
- `obtenerAmbientes()`
- `obtenerRangosPrecio()`

**Problemas Identificados:**
- ✅ **Simple y claro**: Cada método tiene una responsabilidad
- ⚠️ **Repetitivo**: Todos los métodos son muy similares
- ⚠️ **Sin caché**: Consulta BD cada vez (podría ser costoso)

---

### 5. **RestauranteRepository** (Búsqueda de Restaurantes)

**Responsabilidades:**
- Ejecutar stored procedures de búsqueda
- Mapear resultados a DTOs

**Métodos Clave:**
- `buscarPorNLP()` - Ejecuta sp_BuscarRestaurantesPorNLP
- `obtenerSugerencias()` - Ejecuta sp_ObtenerSugerenciasRestaurantes

**Problemas Identificados:**
- ⚠️ **Parámetros sueltos**: `buscarPorNLP()` tiene 7 parámetros
- ✅ **Usa DTOs para resultados**: Retorna `List<RestauranteDto>`

---

## 🗄️ Base de Datos

### Stored Procedures

#### 1. **sp_BuscarRestaurantesPorNLP**

**Parámetros:**
- `@tiposComida NVARCHAR(MAX)` - JSON array o lista separada por comas
- `@barrios NVARCHAR(MAX)` - JSON array o lista separada por comas
- `@localidades NVARCHAR(MAX)` - JSON array o lista separada por comas
- `@ambientes NVARCHAR(MAX)` - JSON array o lista separada por comas
- `@rangosPrecio NVARCHAR(MAX)` - JSON array o lista separada por comas
- `@palabrasClave NVARCHAR(MAX)` - Palabras clave para búsqueda
- `@nroCliente VARCHAR(36)` - UUID del cliente (opcional)

**Lógica:**
1. **Filtrado Flexible**: Los filtros principales (tipo comida, localidad) son obligatorios si se especifican
2. **Scoring de Relevancia**:
   - Configuración restaurantes: 15 puntos
   - Preferencias restaurantes: 10 puntos
   - Preferencias cliente: 5 puntos
   - Palabras clave: 5 puntos
3. **Ordenamiento**: Por relevancia descendente, luego por nombre

**Problemas Identificados:**
- ⚠️ **Muy complejo**: Más de 200 líneas de SQL
- ⚠️ **Múltiples subconsultas**: Puede ser lento con muchos datos
- ⚠️ **Lógica de negocio en SQL**: Difícil de mantener y testear
- ⚠️ **Parámetros como strings**: No hay validación de tipos

#### 2. **sp_ObtenerSugerenciasRestaurantes**

**Parámetros:**
- `@excluirRestaurantes NVARCHAR(MAX)` - Lista de UUIDs a excluir
- `@nroCliente VARCHAR(36)` - UUID del cliente (opcional)
- `@limite INT` - Cantidad máxima de sugerencias

**Lógica:**
1. **Scoring de Sugerencias**:
   - Preferencias del cliente: 20 puntos
   - Restaurantes con promociones: 10 puntos
   - Aleatorios: 0 puntos
2. **Ordenamiento**: Por relevancia, luego aleatorio

**Problemas Identificados:**
- ✅ **Más simple**: Menos complejo que el SP principal
- ⚠️ **Mismo patrón**: Parámetros como strings

---

## 📊 DTOs y Objetos

### DTOs Existentes

1. **BusquedaNLPRequestDto** ✅
   - Simple, bien diseñado
   - Validaciones con Bean Validation

2. **BusquedaNLPResponseDto** ✅
   - Representa respuesta de OpenAI
   - Campos bien tipados

3. **BusquedaContextoDto** ✅
   - Encapsula contexto para OpenAI
   - Clase interna `ContextoDto` bien estructurada

4. **BusquedaNLPResultadoDto** ✅
   - Encapsula resultados finales
   - Separación clara entre exactos y sugerencias

### Problemas de OOP Identificados

#### ❌ **RestauranteRepository.buscarPorNLP()** - 7 parámetros sueltos

```java
// ACTUAL (MALO)
public List<RestauranteDto> buscarPorNLP(
    List<String> tiposComida, 
    String barrio, 
    String localidad, 
    String ambiente, 
    String rangoPrecio, 
    List<String> palabrasClave,
    String nroCliente)
```

**Solución Propuesta:**
```java
// MEJOR (CON DTO)
public List<RestauranteDto> buscarPorNLP(BusquedaNLPParametrosDto parametros)
```

#### ⚠️ **BusquedaRepository** - Métodos repetitivos

Todos los métodos son muy similares. Podría usar genéricos o un método base.

#### ⚠️ **ValidacionCatalogoService** - Sinónimos hardcodeados

Los mapeos de sinónimos están en código Java. Podrían estar en BD o configuración.

---

## 🔧 Mejoras Propuestas

### 1. **Crear DTO para Parámetros de Búsqueda NLP**

```java
public class BusquedaNLPParametrosDto {
    private List<String> tiposComida;
    private String barrio;
    private String localidad;
    private String ambiente;
    private String rangoPrecio;
    private List<String> palabrasClave;
    private String nroCliente;
    
    // Método helper para convertir a formato SQL
    public MapSqlParameterSource toSqlParameterSource() {
        // ...
    }
}
```

### 2. **Crear DTO para Catálogos**

```java
public class CatalogosDto {
    private List<String> tiposComida;
    private List<String> barrios;
    private List<String> localidades;
    private List<String> ambientes;
    private List<String> rangosPrecio;
    
    // Métodos de utilidad
    public boolean tieneTipoComida(String tipo) { ... }
    public Optional<String> buscarTipoComidaSimilar(String tipo) { ... }
}
```

### 3. **Refactorizar BusquedaRepository**

```java
// Método genérico para obtener catálogos
private <T> List<T> obtenerCatalogo(String categoria, String campo, Class<T> tipo) {
    // ...
}

// O mejor aún, usar un enum
public enum TipoCatalogo {
    TIPOS_COMIDA("Tipo de comida", "nom_valor_dominio"),
    AMBIENTES("Ambiente", "nom_valor_dominio"),
    // ...
}
```

### 4. **Mover Sinónimos a Base de Datos**

Crear tabla `sinonimos_catalogos`:
```sql
CREATE TABLE sinonimos_catalogos (
    cod_categoria VARCHAR(36),
    valor_catalogo VARCHAR(120),
    sinonimo VARCHAR(120),
    PRIMARY KEY (cod_categoria, valor_catalogo, sinonimo)
);
```

### 5. **Separar Responsabilidades en BusquedaNLPService**

Crear servicios más pequeños:
- `ContextoService` - Construir contexto
- `OpenAIParsingService` - Parsear respuestas de OpenAI
- `ResultadoService` - Procesar y filtrar resultados

---

## 📈 Métricas y Performance

### Posibles Cuellos de Botella

1. **OpenAI API**: Llamada externa, puede ser lenta (1-3 segundos)
2. **Stored Procedure**: Muy complejo, puede ser lento con muchos datos
3. **Múltiples consultas**: BusquedaRepository hace 5 consultas separadas para catálogos
4. **Sin caché**: Catálogos se consultan cada vez

### Optimizaciones Sugeridas

1. **Caché de catálogos**: Redis o caché en memoria (Spring Cache)
2. **Índices en BD**: Asegurar índices en tablas de preferencias
3. **Timeout para OpenAI**: Ya existe, pero verificar valores
4. **Paginación**: Los resultados no están paginados

---

## 🐛 Problemas Potenciales

### 1. **Parsing de JSON de OpenAI**

- Maneja múltiples formatos (anidado, plano, con markdown)
- Puede fallar si OpenAI cambia formato
- **Solución**: Validación más estricta y tests

### 2. **Validación de Localidades**

- Lógica especial para evitar mapear "Córdoba" a "Alta Córdoba"
- Puede ser demasiado estricta
- **Solución**: Revisar umbrales de fuzzy matching

### 3. **Duplicados**

- Se eliminan duplicados después de la consulta
- El SP puede devolver duplicados
- **Solución**: Mejorar el SP para evitar duplicados

### 4. **Manejo de Errores**

- Si OpenAI falla, se lanza RuntimeException
- No hay fallback
- **Solución**: Implementar fallback a búsqueda por palabras clave

---

## ✅ Aspectos Positivos

1. ✅ **Bien estructurado**: Separación de responsabilidades clara
2. ✅ **Usa DTOs**: La mayoría del código usa objetos tipados
3. ✅ **Logging extensivo**: Muy bueno para debugging
4. ✅ **Validación inteligente**: Fuzzy matching y sinónimos
5. ✅ **Scoring de relevancia**: Resultados ordenados por relevancia
6. ✅ **Sugerencias personalizadas**: Usa preferencias del usuario

---

## 🎯 Recomendaciones Prioritarias

### Alta Prioridad

1. **Crear `BusquedaNLPParametrosDto`** para `buscarPorNLP()`
2. **Implementar caché de catálogos** (Spring Cache)
3. **Mover sinónimos a base de datos**

### Media Prioridad

4. **Refactorizar BusquedaRepository** con métodos genéricos
5. **Separar BusquedaNLPService** en servicios más pequeños
6. **Agregar paginación** a resultados

### Baja Prioridad

7. **Optimizar stored procedure** (revisar índices)
8. **Agregar tests unitarios** para validación
9. **Implementar fallback** si OpenAI falla

---

## 📝 Conclusión

El sistema de búsqueda NLP está **bien diseñado** pero tiene oportunidades de mejora en:

1. **OOP**: Reducir parámetros sueltos con DTOs
2. **Performance**: Implementar caché y optimizar consultas
3. **Mantenibilidad**: Separar responsabilidades y mover configuración a BD
4. **Robustez**: Mejor manejo de errores y fallbacks

El código sigue buenas prácticas en general, pero puede beneficiarse de las mejoras propuestas.

