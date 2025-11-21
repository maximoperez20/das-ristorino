-- Inserta categorías de preferencias y sus dominios; nro_valor_dominio se incrementa por categoría
SET NOCOUNT ON;

-------------------------------------------------------------
-- Categorías base (idempotente)
-------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM categorias_preferencias WHERE nom_categoria=N'Tipo de comida')
  INSERT INTO categorias_preferencias (nom_categoria) VALUES (N'Tipo de comida');
IF NOT EXISTS (SELECT 1 FROM categorias_preferencias WHERE nom_categoria=N'Ambiente')
  INSERT INTO categorias_preferencias (nom_categoria) VALUES (N'Ambiente');
IF NOT EXISTS (SELECT 1 FROM categorias_preferencias WHERE nom_categoria=N'Rango de precio')
  INSERT INTO categorias_preferencias (nom_categoria) VALUES (N'Rango de precio');

DECLARE @cat_tipo  VARCHAR(36), @cat_amb VARCHAR(36), @cat_precio VARCHAR(36);
SELECT @cat_tipo  = cod_categoria FROM categorias_preferencias WHERE nom_categoria=N'Tipo de comida';
SELECT @cat_amb   = cod_categoria FROM categorias_preferencias WHERE nom_categoria=N'Ambiente';
SELECT @cat_precio= cod_categoria FROM categorias_preferencias WHERE nom_categoria=N'Rango de precio';

-------------------------------------------------------------
-- Helper: inserta dominio con nro_valor_dominio incremental por categoría
-------------------------------------------------------------
DECLARE @categoria VARCHAR(36), @nom_valor NVARCHAR(120), @prox INT;

-- Tipo de comida
DECLARE cur1 CURSOR LOCAL FAST_FORWARD FOR
  SELECT @cat_tipo AS categoria, v FROM (VALUES (N'Parrilla'),(N'Pizzería'),(N'Sushi'),(N'Vegano')) x(v);
OPEN cur1; FETCH NEXT FROM cur1 INTO @categoria, @nom_valor;
WHILE @@FETCH_STATUS=0
BEGIN
  IF NOT EXISTS (
      SELECT 1 FROM dominio_categorias_preferencias
      WHERE cod_categoria=@categoria AND nom_valor_dominio=@nom_valor
  )
  BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio),0) + 1
    FROM dominio_categorias_preferencias
    WHERE cod_categoria=@categoria;

    INSERT INTO dominio_categorias_preferencias
      (cod_categoria, nro_valor_dominio, nom_valor_dominio)
    VALUES (@categoria, @prox, @nom_valor);
  END
  FETCH NEXT FROM cur1 INTO @categoria, @nom_valor;
END
CLOSE cur1; DEALLOCATE cur1;

-- Ambiente
DECLARE cur2 CURSOR LOCAL FAST_FORWARD FOR
  SELECT @cat_amb AS categoria, v FROM (VALUES (N'Familiar'),(N'Romántico'),(N'Gourmet'),(N'Casual')) x(v);
OPEN cur2; FETCH NEXT FROM cur2 INTO @categoria, @nom_valor;
WHILE @@FETCH_STATUS=0
BEGIN
  IF NOT EXISTS (
      SELECT 1 FROM dominio_categorias_preferencias
      WHERE cod_categoria=@categoria AND nom_valor_dominio=@nom_valor
  )
  BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio),0) + 1
    FROM dominio_categorias_preferencias
    WHERE cod_categoria=@categoria;

    INSERT INTO dominio_categorias_preferencias
      (cod_categoria, nro_valor_dominio, nom_valor_dominio)
    VALUES (@categoria, @prox, @nom_valor);
  END
  FETCH NEXT FROM cur2 INTO @categoria, @nom_valor;
END
CLOSE cur2; DEALLOCATE cur2;

-- Rango de precio
DECLARE cur3 CURSOR LOCAL FAST_FORWARD FOR
  SELECT @cat_precio AS categoria, v FROM (VALUES (N'Económico'),(N'Medio'),(N'Premium')) x(v);
OPEN cur3; FETCH NEXT FROM cur3 INTO @categoria, @nom_valor;
WHILE @@FETCH_STATUS=0
BEGIN
  IF NOT EXISTS (
      SELECT 1 FROM dominio_categorias_preferencias
      WHERE cod_categoria=@categoria AND nom_valor_dominio=@nom_valor
  )
  BEGIN
    SELECT @prox = ISNULL(MAX(nro_valor_dominio),0) + 1
    FROM dominio_categorias_preferencias
    WHERE cod_categoria=@categoria;

    INSERT INTO dominio_categorias_preferencias
      (cod_categoria, nro_valor_dominio, nom_valor_dominio)
    VALUES (@categoria, @prox, @nom_valor);
  END
  FETCH NEXT FROM cur3 INTO @categoria, @nom_valor;
END
CLOSE cur3; DEALLOCATE cur3;

-------------------------------------------------------------
-- Verificación
-------------------------------------------------------------
SELECT c.nom_categoria, d.nro_valor_dominio, d.nom_valor_dominio
FROM dominio_categorias_preferencias d
JOIN categorias_preferencias c ON c.cod_categoria=d.cod_categoria
ORDER BY c.nom_categoria, d.nro_valor_dominio;
