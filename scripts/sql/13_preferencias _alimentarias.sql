-- Script para insertar nueva categoría de preferencias: Especialidades Alimentarias
-- Fecha: 2025-11-30

GO

-- 1. Insertar la nueva categoría de preferencias
INSERT INTO categorias_preferencias (nom_categoria, descripcion)
VALUES ('Especialidades Alimentarias', 'Preferencias relacionadas con especialidades y restricciones alimentarias');


-- Obtener el ID de la categoría insertada
DECLARE @cod_categoria varchar(36);
SELECT @cod_categoria = cod_categoria 
FROM categorias_preferencias 
WHERE nom_categoria = 'Especialidades Alimentarias';

PRINT 'Categoría "Especialidades Alimentarias" insertada con código: ' + CAST(@cod_categoria AS VARCHAR);

-- 2. Insertar los dominios de la categoría
INSERT INTO dominio_categorias_preferencias (cod_categoria, nom_valor_dominio,nro_valor_dominio)
VALUES 
    (@cod_categoria, 'Vegetariana', 1),
    (@cod_categoria, 'Vegana', 2),
    (@cod_categoria, 'Sin gluten / Celíaco', 3),
    (@cod_categoria, 'Sin lactosa', 4),
    (@cod_categoria, 'Baja en calorías', 5),
    (@cod_categoria, 'Orgánica', 6),
    (@cod_categoria, 'Diabéticos (sin azúcar añadida)', 7);

PRINT 'Se insertaron 7 preferencias alimentarias';

-- 3. Verificar los datos insertados
PRINT '';
PRINT '===== VERIFICACIÓN DE DATOS INSERTADOS =====';
PRINT '';

SELECT 
    cp.cod_categoria,
    cp.nom_categoria,
    dcp.nom_valor_dominio
FROM categorias_preferencias cp
LEFT JOIN dominio_categorias_preferencias dcp ON cp.cod_categoria = dcp.cod_categoria
WHERE cp.nom_categoria = 'Especialidades Alimentarias'
ORDER BY dcp.nom_valor_dominio;

PRINT '';
PRINT 'Script ejecutado exitosamente';
