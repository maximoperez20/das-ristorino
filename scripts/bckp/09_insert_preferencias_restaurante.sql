-- Inserta preferencias del restaurante (idempotente) usando categorías y dominios ya cargados
SET NOCOUNT ON;

-------------------------------------------------------------
-- Parámetros
-------------------------------------------------------------
DECLARE 
  @CUIT VARCHAR(11) = '30700987654',
  @NomSucursal NVARCHAR(120) = N'Los Aroza - Centro';  -- opcional (NULL si aplica a todo el restaurante)

-------------------------------------------------------------
-- Resolver IDs
-------------------------------------------------------------
DECLARE @nro_restaurante VARCHAR(36), @nro_sucursal VARCHAR(36);

SELECT @nro_restaurante = nro_restaurante FROM restaurantes WHERE cuit=@CUIT;
IF @nro_restaurante IS NULL BEGIN RAISERROR('Restaurante no encontrado.',16,1); RETURN; END

SELECT @nro_sucursal = s.nro_sucursal
FROM sucursales_restaurantes s
WHERE s.nro_restaurante=@nro_restaurante AND s.nom_sucursal=@NomSucursal;

-------------------------------------------------------------
-- Resolver categorías y dominios
-------------------------------------------------------------
DECLARE @cat_tipo VARCHAR(36), @cat_amb VARCHAR(36), @cat_precio VARCHAR(36);
SELECT @cat_tipo   = cod_categoria FROM categorias_preferencias WHERE nom_categoria=N'Tipo de comida';
SELECT @cat_amb    = cod_categoria FROM categorias_preferencias WHERE nom_categoria=N'Ambiente';
SELECT @cat_precio = cod_categoria FROM categorias_preferencias WHERE nom_categoria=N'Rango de precio';

-- Valores de dominio
DECLARE @tipo_sushi INT, @amb_romantico INT, @precio_medio INT;
SELECT @tipo_sushi   = nro_valor_dominio FROM dominio_categorias_preferencias WHERE cod_categoria=@cat_tipo   AND nom_valor_dominio=N'Sushi';
SELECT @amb_romantico= nro_valor_dominio FROM dominio_categorias_preferencias WHERE cod_categoria=@cat_amb    AND nom_valor_dominio=N'Romántico';
SELECT @precio_medio = nro_valor_dominio FROM dominio_categorias_preferencias WHERE cod_categoria=@cat_precio AND nom_valor_dominio=N'Medio';

-------------------------------------------------------------
-- Inserción idempotente de preferencias del restaurante
-------------------------------------------------------------
-- Tipo de comida: Sushi
IF NOT EXISTS (
  SELECT 1 FROM preferencias_restaurantes
  WHERE nro_restaurante=@nro_restaurante AND cod_categoria=@cat_tipo AND nro_valor_dominio=@tipo_sushi AND nro_preferencia=1
)
  INSERT INTO preferencias_restaurantes (
    nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal
  )
  VALUES (
    @nro_restaurante, @cat_tipo, @tipo_sushi, 1, N'Especializados en cocina japonesa y sushi gourmet.', @nro_sucursal
  );

-- Ambiente: Romántico
IF NOT EXISTS (
  SELECT 1 FROM preferencias_restaurantes
  WHERE nro_restaurante=@nro_restaurante AND cod_categoria=@cat_amb AND nro_valor_dominio=@amb_romantico AND nro_preferencia=1
)
  INSERT INTO preferencias_restaurantes (
    nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal
  )
  VALUES (
    @nro_restaurante, @cat_amb, @amb_romantico, 1, N'Iluminación cálida y música suave para cenas en pareja.', @nro_sucursal
  );

-- Rango de precio: Medio
IF NOT EXISTS (
  SELECT 1 FROM preferencias_restaurantes
  WHERE nro_restaurante=@nro_restaurante AND cod_categoria=@cat_precio AND nro_valor_dominio=@precio_medio AND nro_preferencia=1
)
  INSERT INTO preferencias_restaurantes (
    nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal
  )
  VALUES (
    @nro_restaurante, @cat_precio, @precio_medio, 1, N'Precios medios con buena relación calidad/precio.', @nro_sucursal
  );

-------------------------------------------------------------
-- Verificación
-------------------------------------------------------------
SELECT 
  r.razon_social AS Restaurante,
  c.nom_categoria AS Categoria,
  d.nom_valor_dominio AS Valor,
  pr.observaciones,
  ISNULL(s.nom_sucursal,'[General]') AS Sucursal
FROM preferencias_restaurantes pr
JOIN restaurantes r ON r.nro_restaurante=pr.nro_restaurante
JOIN categorias_preferencias c ON c.cod_categoria=pr.cod_categoria
JOIN dominio_categorias_preferencias d 
  ON d.cod_categoria=pr.cod_categoria AND d.nro_valor_dominio=pr.nro_valor_dominio
LEFT JOIN sucursales_restaurantes s 
  ON s.nro_restaurante=pr.nro_restaurante AND s.nro_sucursal=pr.nro_sucursal
WHERE pr.nro_restaurante=@nro_restaurante
ORDER BY c.nom_categoria;
