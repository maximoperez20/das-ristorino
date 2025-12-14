# Verificación de Datos - Coincidencia entre Scripts

## ✅ Resumen de Verificación

Todos los datos principales **COINCIDEN** entre:
- `das-ristorino/scripts/sql/12_insert_restaurantes_examen_final.sql`
- Scripts individuales en `das-restaurante/scripts/`

---

## 1. La Bella Pizza

### ✅ Datos del Restaurante
| Campo | das-ristorino | das-restaurante | Estado |
|-------|---------------|-----------------|--------|
| **UUID** | `BELLA-PIZZA-1111-1111-1111-111111111111` | `BELLA-PIZZA-1111-1111-1111-111111111111` | ✅ |
| **Razón Social** | `La Bella Pizza SRL` | `La Bella Pizza SRL` | ✅ |
| **CUIT** | `30123456789` | `30123456789` | ✅ |
| **Protocolo** | `REST` | `REST` | ✅ |
| **URL Servicio** | `http://localhost:8082/api` | `http://localhost:8082/api` | ✅ |

### ✅ Sucursales
| Sucursal | das-ristorino | das-restaurante | Estado |
|----------|---------------|-----------------|--------|
| **Alta Córdoba** | Nombre: `La Bella Pizza - Alta Córdoba`<br>Código: `BELLA-PIZZA-ALTA-CORDOBA-001` | Nombre: `La Bella Pizza - Alta Córdoba`<br>UUID: Generado | ✅ |
| **General Paz** | Nombre: `La Bella Pizza - General Paz`<br>Código: `BELLA-PIZZA-GENERAL-PAZ-002` | Nombre: `La Bella Pizza - General Paz`<br>UUID: Generado | ✅ |

**Nota**: `das-restaurante` usa UUIDs para `nro_sucursal`, mientras que `das-ristorino` usa códigos alfanuméricos en `cod_sucursal_restaurante`. Esto es correcto porque son sistemas diferentes.

---

## 2. Perukai

### ✅ Datos del Restaurante
| Campo | das-ristorino | das-restaurante | Estado |
|-------|---------------|-----------------|--------|
| **UUID** | `PERUKAI-2222-2222-2222-222222222222` | `PERUKAI-2222-2222-2222-222222222222` | ✅ |
| **Razón Social** | `Perukai S.A.` | `Perukai S.A.` | ✅ |
| **CUIT** | `30234567890` | `30234567890` | ✅ |
| **Protocolo** | `SOAP` | `SOAP` | ✅ |
| **URL Servicio** | `http://localhost:8081/ws/restaurantes.wsdl` | `http://localhost:8081/ws/restaurantes.wsdl` | ✅ |

### ✅ Sucursales
| Sucursal | das-ristorino | das-restaurante | Estado |
|----------|---------------|-----------------|--------|
| **Nueva Córdoba** | Nombre: `Perukai - Nueva Córdoba`<br>Código: `PERUKAI-NUEVA-CORDOBA-001` | Nombre: `Perukai - Nueva Córdoba`<br>UUID: Generado | ✅ |
| **Güemes** | Nombre: `Perukai - Güemes`<br>Código: `PERUKAI-GUEMES-002` | Nombre: `Perukai - Güemes`<br>UUID: Generado | ✅ |

---

## 3. La Fábrica Burger

### ✅ Datos del Restaurante
| Campo | das-ristorino | das-restaurante | Estado |
|-------|---------------|-----------------|--------|
| **UUID** | `FABRICA-BURGER-3333-3333-3333-333333333333` | `FABRICA-BURGER-3333-3333-3333-333333333333` | ✅ |
| **Razón Social** | `La Fábrica Burger SRL` | `La Fábrica Burger SRL` | ✅ |
| **CUIT** | `30345678901` | `30345678901` | ✅ |
| **Protocolo** | `REST` | `REST` | ✅ |
| **URL Servicio** | `http://localhost:8082/api` | `http://localhost:8082/api` | ✅ |

### ✅ Sucursales
| Sucursal | das-ristorino | das-restaurante | Estado |
|----------|---------------|-----------------|--------|
| **Cerro de las Rosas** | Nombre: `La Fábrica Burger - Cerro de las Rosas`<br>Código: `FABRICA-BURGER-CERRO-001` | Nombre: `La Fábrica Burger - Cerro de las Rosas`<br>UUID: Generado | ✅ |

---

## 4. Sabores del Norte

### ✅ Datos del Restaurante
| Campo | das-ristorino | das-restaurante | Estado |
|-------|---------------|-----------------|--------|
| **UUID** | `SABORES-NORTE-4444-4444-4444-444444444444` | `SABORES-NORTE-4444-4444-4444-444444444444` | ✅ |
| **Razón Social** | `Sabores del Norte S.A.` | `Sabores del Norte S.A.` | ✅ |
| **CUIT** | `30456789012` | `30456789012` | ✅ |
| **Protocolo** | `SOAP` | `SOAP` | ✅ |
| **URL Servicio** | `http://localhost:8081/ws/restaurantes.wsdl` | `http://localhost:8081/ws/restaurantes.wsdl` | ✅ |

### ✅ Sucursales
| Sucursal | das-ristorino | das-restaurante | Estado |
|----------|---------------|-----------------|--------|
| **Centro** | Nombre: `Sabores del Norte - Centro`<br>Código: `SABORES-NORTE-CENTRO-001` | Nombre: `Sabores del Norte - Centro`<br>UUID: Generado | ✅ |
| **Cerro de las Rosas** | Nombre: `Sabores del Norte - Cerro de las Rosas`<br>Código: `SABORES-NORTE-CERRO-002` | Nombre: `Sabores del Norte - Cerro de las Rosas`<br>UUID: Generado | ✅ |

---

## 📋 Códigos de Sucursales y Zonas

### ⚠️ Diferencia Importante

**das-ristorino** usa códigos alfanuméricos (`cod_sucursal_restaurante`, `cod_zona_restaurante`) que deben coincidir con los códigos que usa el sistema SOAP/REST de cada restaurante.

**das-restaurante** usa UUIDs (`nro_sucursal`, `cod_zona`) que son internos a cada base de datos.

### Códigos en das-ristorino (para comunicación con sistemas externos):

#### La Bella Pizza (REST)
- Sucursal Alta Córdoba: `BELLA-PIZZA-ALTA-CORDOBA-001`
- Sucursal General Paz: `BELLA-PIZZA-GENERAL-PAZ-002`
- Zonas: `SALON-PRINCIPAL-001`, `TERRAZA-001`, `SALON-PRINCIPAL-002`, `PATIO-001`

#### Perukai (SOAP)
- Sucursal Nueva Córdoba: `PERUKAI-NUEVA-CORDOBA-001`
- Sucursal Güemes: `PERUKAI-GUEMES-002`
- Zonas: `SALON-PRINCIPAL-003`, `BARRA-001`, `SALON-PRINCIPAL-004`, `TERRAZA-002`

#### La Fábrica Burger (REST)
- Sucursal Cerro de las Rosas: `FABRICA-BURGER-CERRO-001`
- Zonas: `SALON-PRINCIPAL-005`, `PATIO-002`

#### Sabores del Norte (SOAP)
- Sucursal Centro: `SABORES-NORTE-CENTRO-001`
- Sucursal Cerro de las Rosas: `SABORES-NORTE-CERRO-002`
- Zonas: `SALON-PRINCIPAL-006`, `PATIO-CUBIERTO-001`, `SALON-PRINCIPAL-007`, `TERRAZA-003`

---

## ✅ Conclusión

**TODOS LOS DATOS COINCIDEN CORRECTAMENTE**

- ✅ UUIDs de restaurantes: **COINCIDEN**
- ✅ CUITs: **COINCIDEN**
- ✅ Razones sociales: **COINCIDEN**
- ✅ Protocolos (SOAP/REST): **COINCIDEN**
- ✅ URLs de servicio: **COINCIDEN**
- ✅ Nombres de sucursales: **COINCIDEN**

**Nota sobre códigos**: Los códigos `cod_sucursal_restaurante` y `cod_zona_restaurante` en `das-ristorino` son para comunicación con los sistemas externos (SOAP/REST). Los sistemas internos de cada restaurante (`das-restaurante`) usan UUIDs, lo cual es correcto y esperado.

---

## 🔍 Verificación Adicional Recomendada

Para asegurar que los códigos externos funcionen correctamente, verificar que:

1. Los sistemas SOAP/REST de cada restaurante reconozcan los códigos definidos en `das-ristorino`
2. Los stored procedures en `das-restaurante` que reciben `cod_sucursal_restaurante` y `cod_zona_restaurante` los mapeen correctamente a los UUIDs internos

