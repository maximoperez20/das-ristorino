USE das_ristorino;
GO

-- Crear tabla de preferencias de reservas de restaurantes
CREATE TABLE dbo.preferencias_reservas_restaurantes (
  nro_reserva VARCHAR(36) NOT NULL,
  nro_cliente VARCHAR(36) NOT NULL,
  nro_restaurante VARCHAR(36) NOT NULL,
  cod_categoria VARCHAR(36) NOT NULL,
  nro_valor_dominio INT NOT NULL,
  nro_preferencia INT NOT NULL,
  observaciones NVARCHAR(400) NULL,
  CONSTRAINT PK_preferencias_reservas_restaurantes PRIMARY KEY (nro_reserva, nro_cliente, nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia),
  CONSTRAINT FK_preferencias_reservas_restaurantes_reserva FOREIGN KEY (nro_reserva) REFERENCES dbo.reservas_restaurantes(nro_reserva),
  CONSTRAINT FK_preferencias_reservas_restaurantes_cliente FOREIGN KEY (nro_cliente) REFERENCES dbo.clientes(nro_cliente),
  CONSTRAINT FK_preferencias_reservas_restaurantes_restaurante FOREIGN KEY (nro_restaurante) REFERENCES dbo.restaurantes(nro_restaurante),
  CONSTRAINT FK_preferencias_reservas_restaurantes_categoria FOREIGN KEY (cod_categoria) REFERENCES dbo.categorias_preferencias(cod_categoria),
  CONSTRAINT FK_preferencias_reservas_restaurantes_valor_dominio FOREIGN KEY (cod_categoria, nro_valor_dominio) REFERENCES dbo.dominio_categorias_preferencias(cod_categoria, nro_valor_dominio),
  CONSTRAINT FK_preferencias_reservas_restaurantes_preferencia FOREIGN KEY (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia) REFERENCES dbo.preferencias_restaurantes(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
);
