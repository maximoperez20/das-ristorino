USE das_ristorino;
GO


-- Insertar categoría de preferencias de Especialidades alimentarias
INSERT INTO categorias_preferencias (nom_categoria) VALUES ('Especialidades alimentarias');

DECLARE @cat_alimentacion VARCHAR(36);
SELECT @cat_alimentacion = cod_categoria FROM categorias_preferencias WHERE nom_categoria = 'Especialidades alimentarias';


-- Insertar traducciones de categorias alimenticias
INSERT INTO idiomas_categorias_preferencias (cod_categoria, nro_idioma, categoria) VALUES (@cat_alimentacion, 0, 'Especialidades alimentarias');
INSERT INTO idiomas_categorias_preferencias (cod_categoria, nro_idioma, categoria) VALUES (@cat_alimentacion, 1, 'Food specialties');

-- Insertar dominios de categorias alimenticias
INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_alimentacion, 1, 'Vegetariano');
INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_alimentacion, 2, 'Vegano');
INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_alimentacion, 3, 'Sin gluten');
INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_alimentacion, 4, 'Sin lactosa');
INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_alimentacion, 5, 'Baja en calorías');
INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_alimentacion, 6, 'Organico');
INSERT INTO dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES (@cat_alimentacion, 7, 'Diabetico');

-- Insertar traducciones de dominios de categorias alimenticias
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 1, 1, 'Vegetarian');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 2, 1, 'Vegan');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 3, 1, 'Gluten free');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 4, 1, 'Lactose free');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 5, 1, 'Low calorie');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 6, 1, 'Organic');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 7, 1, 'Diabetic');

INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 1, 0, 'Vegetariano');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 2, 0, 'Vegano');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 3, 0, 'Sin gluten');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 4, 0, 'Sin lactosa');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 5, 0, 'Baja en calorías');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 6, 0, 'Orgánico');
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio) VALUES (@cat_alimentacion, 7, 0, 'Diabético');
