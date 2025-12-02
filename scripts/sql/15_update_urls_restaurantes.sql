/* =========================================================================================
   ACTUALIZAR URLs DE SERVICIOS DE RESTAURANTES
   Actualiza las URLs para apuntar a los nuevos puertos de las aplicaciones separadas
   ========================================================================================= */

SET NOCOUNT ON;
GO

USE das_ristorino;
GO

/* =========================================
   Actualizar URLs según nuevos puertos:
   - La Bella Pizza (REST): puerto 8082 → http://localhost:8082/api
   - Perukai (SOAP): puerto 8081 → http://localhost:8081/ws/restaurantes.wsdl
   - La Fábrica Burger (REST): puerto 8083 → http://localhost:8083/api
   - Sabores del Norte (SOAP): puerto 8084 → http://localhost:8084/ws/restaurantes.wsdl
   ========================================= */

-- La Bella Pizza (REST - puerto 8082)
UPDATE restaurantes
SET url_servicio = 'http://localhost:8082/api'
WHERE nro_restaurante = 'BELLA-PIZZA-1111-1111-1111-111111111'
  AND tipo_protocolo = 'REST';

IF @@ROWCOUNT > 0
    PRINT 'URL actualizada para La Bella Pizza (REST - puerto 8082)';
ELSE
    PRINT 'La Bella Pizza no encontrado o ya tiene la URL correcta';

-- Perukai (SOAP - puerto 8081)
UPDATE restaurantes
SET url_servicio = 'http://localhost:8081/ws/restaurantes.wsdl'
WHERE nro_restaurante = 'PERUKAI-2222-2222-2222-222222222222'															
  AND tipo_protocolo = 'SOAP';

IF @@ROWCOUNT > 0
    PRINT 'URL actualizada para Perukai (SOAP - puerto 8081)';
ELSE
    PRINT 'Perukai no encontrado o ya tiene la URL correcta';

-- La Fábrica Burger (REST - puerto 8083)
UPDATE restaurantes
SET url_servicio = 'http://localhost:8083/api'
WHERE nro_restaurante = 'FABRICA-BURGER-3333-3333-3333-333333'
  AND tipo_protocolo = 'REST';

IF @@ROWCOUNT > 0
    PRINT 'URL actualizada para La Fábrica Burger (REST - puerto 8083)';
ELSE
    PRINT 'La Fábrica Burger no encontrado o ya tiene la URL correcta';

-- Sabores del Norte (SOAP - puerto 8084)
UPDATE restaurantes
SET url_servicio = 'http://localhost:8084/ws/restaurantes.wsdl'
WHERE nro_restaurante = 'SABORES-NORTE-4444-4444-4444-4444444'
  AND tipo_protocolo = 'SOAP';

IF @@ROWCOUNT > 0
    PRINT 'URL actualizada para Sabores del Norte (SOAP - puerto 8084)';
ELSE
    PRINT 'Sabores del Norte no encontrado o ya tiene la URL correcta';

-- Verificar actualizaciones
PRINT '';
PRINT '========================================';
PRINT 'VERIFICACIÓN DE URLs ACTUALIZADAS';
PRINT '========================================';
SELECT 
    nro_restaurante,
    razon_social,
    tipo_protocolo,
    url_servicio
FROM restaurantes
WHERE nro_restaurante IN (
    'BELLA-PIZZA-1111-1111-1111-111111111',
    'PERUKAI-2222-2222-2222-222222222222',
    'FABRICA-BURGER-3333-3333-3333-333333',
    'SABORES-NORTE-4444-4444-4444-4444444'
)
ORDER BY razon_social;

PRINT '========================================';
GO
