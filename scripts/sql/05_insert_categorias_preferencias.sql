-- Script para insertar categorías y dominios de preferencias gastronómicas
-- Este script debe ejecutarse después de 01_create_tables.sql

USE das_ristorino;
GO

PRINT '========================================';
PRINT 'Insertando categorías y dominios de preferencias';
PRINT '========================================';

/* =========================================
   Categorías de Preferencias
   ========================================= */

-- Categorías base
IF NOT EXISTS (SELECT 1 FROM categorias_preferencias WHERE nom_categoria = N'Tipo de comida')
    INSERT INTO categorias_preferencias (nom_categoria) VALUES (N'Tipo de comida');

IF NOT EXISTS (SELECT 1 FROM categorias_preferencias WHERE nom_categoria = N'Ambiente')
    INSERT INTO categorias_preferencias (nom_categoria) VALUES (N'Ambiente');

IF NOT EXISTS (SELECT 1 FROM categorias_preferencias WHERE nom_categoria = N'Rango de precio')
    INSERT INTO categorias_preferencias (nom_categoria) VALUES (N'Rango de precio');

DECLARE @cat_tipo VARCHAR(36), @cat_amb VARCHAR(36), @cat_precio VARCHAR(36);
SELECT @cat_tipo = cod_categoria FROM categorias_preferencias WHERE nom_categoria = N'Tipo de comida';
SELECT @cat_amb = cod_categoria FROM categorias_preferencias WHERE nom_categoria = N'Ambiente';
SELECT @cat_precio = cod_categoria FROM categorias_preferencias WHERE nom_categoria = N'Rango de precio';

PRINT 'Categorías creadas/verificadas';

/* =========================================
   Dominios: Tipo de comida
   ========================================= */

DECLARE @prox INT;

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Parrilla')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_tipo, @prox, N'Parrilla');
    PRINT 'Dominio insertado: Parrilla';
END

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Pizzería')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_tipo, @prox, N'Pizzería');
    PRINT 'Dominio insertado: Pizzería';
END

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Sushi')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_tipo, @prox, N'Sushi');
    PRINT 'Dominio insertado: Sushi';
END

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Vegano')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_tipo, @prox, N'Vegano');
    PRINT 'Dominio insertado: Vegano';
END

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Italiana')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_tipo, @prox, N'Italiana');
    PRINT 'Dominio insertado: Italiana';
END

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Mexicana')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_tipo, @prox, N'Mexicana');
    PRINT 'Dominio insertado: Mexicana';
END

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo AND nom_valor_dominio = N'Asiática')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_tipo;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_tipo, @prox, N'Asiática');
    PRINT 'Dominio insertado: Asiática';
END

/* =========================================
   Dominios: Ambiente
   ========================================= */

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Familiar')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_amb, @prox, N'Familiar');
    PRINT 'Dominio insertado: Familiar';
END

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Romántico')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_amb, @prox, N'Romántico');
    PRINT 'Dominio insertado: Romántico';
END

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Gourmet')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_amb, @prox, N'Gourmet');
    PRINT 'Dominio insertado: Gourmet';
END

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Casual')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_amb, @prox, N'Casual');
    PRINT 'Dominio insertado: Casual';
END

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Deportivo')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_amb, @prox, N'Deportivo');
    PRINT 'Dominio insertado: Deportivo';
END

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb AND nom_valor_dominio = N'Elegante')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_amb;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_amb, @prox, N'Elegante');
    PRINT 'Dominio insertado: Elegante';
END

/* =========================================
   Dominios: Rango de precio
   ========================================= */

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_precio AND nom_valor_dominio = N'Económico')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_precio;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_precio, @prox, N'Económico');
    PRINT 'Dominio insertado: Económico';
END

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_precio AND nom_valor_dominio = N'Medio')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_precio;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_precio, @prox, N'Medio');
    PRINT 'Dominio insertado: Medio';
END

IF NOT EXISTS (SELECT 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_precio AND nom_valor_dominio = N'Premium')
BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio), 0) + 1 FROM dominio_categorias_preferencias WHERE cod_categoria = @cat_precio;
    INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) 
    VALUES (@cat_precio, @prox, N'Premium');
    PRINT 'Dominio insertado: Premium';
END

/* =========================================
   Resumen
   ========================================= */

PRINT '========================================';
PRINT 'Categorías y dominios de preferencias insertados exitosamente';
PRINT '========================================';
PRINT '- Categorías: 3 (Tipo de comida, Ambiente, Rango de precio)';
PRINT '- Dominios Tipo de comida: 7 (Parrilla, Pizzería, Sushi, Vegano, Italiana, Mexicana, Asiática)';
PRINT '- Dominios Ambiente: 6 (Familiar, Romántico, Gourmet, Casual, Deportivo, Elegante)';
PRINT '- Dominios Rango de precio: 3 (Económico, Medio, Premium)';
PRINT '========================================';

GO

