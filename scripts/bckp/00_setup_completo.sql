/* ==========================================================
   RISTORINO - SETUP COMPLETO DE BASE DE DATOS
   Script consolidado que ejecuta toda la inicialización
   en el orden correcto.
   
   NOTA: Campos modificados de VARCHAR a NVARCHAR para soporte
   correcto de caracteres Unicode (acentos, ñ, etc.)
   ========================================================== */

USE das_ristorino;
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET NOCOUNT ON;
GO

PRINT '====================================================';
PRINT 'INICIANDO SETUP COMPLETO DE RISTORINO';
PRINT '====================================================';
GO

/* ==========================================================
   PASO 0: CREACIÓN DE TABLAS (00_risto.sql)
   ========================================================== */

PRINT '';
PRINT '>> PASO 0: Creando estructura de tablas...';
GO

/* ==========================================================
   LIMPIEZA PREVIA
   Elimina todas las tablas del esquema RISTORINO en orden inverso
   de dependencias (de las más dependientes a las más base)
   ========================================================== */

-- Tablas dependientes finales (reservas, traducciones, cruces)
IF OBJECT_ID('dbo.reservas_restaurantes', 'U') IS NOT NULL DROP TABLE dbo.reservas_restaurantes;
IF OBJECT_ID('dbo.idiomas_estados_reservas', 'U') IS NOT NULL DROP TABLE dbo.idiomas_estados_reservas;
IF OBJECT_ID('dbo.estados_reservas', 'U') IS NOT NULL DROP TABLE dbo.estados_reservas;

IF OBJECT_ID('dbo.zonas_turnos_sucurales_restaurantes', 'U') IS NOT NULL DROP TABLE dbo.zonas_turnos_sucurales_restaurantes;
IF OBJECT_ID('dbo.idiomas_zonas_suc_restaurantes', 'U') IS NOT NULL DROP TABLE dbo.idiomas_zonas_suc_restaurantes;
IF OBJECT_ID('dbo.zonas_sucursales_restaurantes', 'U') IS NOT NULL DROP TABLE dbo.zonas_sucursales_restaurantes;
IF OBJECT_ID('dbo.turnos_sucursales_restaurantes', 'U') IS NOT NULL DROP TABLE dbo.turnos_sucursales_restaurantes;

IF OBJECT_ID('dbo.clicks_contenidos_restaurantes', 'U') IS NOT NULL DROP TABLE dbo.clicks_contenidos_restaurantes;
IF OBJECT_ID('dbo.contenidos_restaurantes', 'U') IS NOT NULL DROP TABLE dbo.contenidos_restaurantes;

IF OBJECT_ID('dbo.preferencias_clientes', 'U') IS NOT NULL DROP TABLE dbo.preferencias_clientes;
IF OBJECT_ID('dbo.clientes', 'U') IS NOT NULL DROP TABLE dbo.clientes;
IF OBJECT_ID('dbo.preferencias_restaurantes', 'U') IS NOT NULL DROP TABLE dbo.preferencias_restaurantes;

IF OBJECT_ID('dbo.idiomas_dominio_cat_preferencias', 'U') IS NOT NULL DROP TABLE dbo.idiomas_dominio_cat_preferencias;
IF OBJECT_ID('dbo.idiomas_categorias_preferencias', 'U') IS NOT NULL DROP TABLE dbo.idiomas_categorias_preferencias;
IF OBJECT_ID('dbo.dominio_categorias_preferencias', 'U') IS NOT NULL DROP TABLE dbo.dominio_categorias_preferencias;
IF OBJECT_ID('dbo.categorias_preferencias', 'U') IS NOT NULL DROP TABLE dbo.categorias_preferencias;

IF OBJECT_ID('dbo.configuracion_restaurantes', 'U') IS NOT NULL DROP TABLE dbo.configuracion_restaurantes;
IF OBJECT_ID('dbo.atributos', 'U') IS NOT NULL DROP TABLE dbo.atributos;

IF OBJECT_ID('dbo.sucursales_restaurantes', 'U') IS NOT NULL DROP TABLE dbo.sucursales_restaurantes;
IF OBJECT_ID('dbo.restaurantes', 'U') IS NOT NULL DROP TABLE dbo.restaurantes;

IF OBJECT_ID('dbo.idiomas', 'U') IS NOT NULL DROP TABLE dbo.idiomas;
IF OBJECT_ID('dbo.localidades', 'U') IS NOT NULL DROP TABLE dbo.localidades;
IF OBJECT_ID('dbo.provincias', 'U') IS NOT NULL DROP TABLE dbo.provincias;

PRINT '>> Todas las tablas eliminadas correctamente (si existían).';
GO

/* ===========================
   Catálogos geográficos
   =========================== */
CREATE TABLE dbo.provincias (
  cod_provincia   VARCHAR(36)   NOT NULL DEFAULT NEWID(),
  nom_provincia   NVARCHAR(80)  NOT NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  CONSTRAINT PK_provincias PRIMARY KEY (cod_provincia),
  CONSTRAINT UQ_provincias_nom UNIQUE (nom_provincia)
);
GO

CREATE TABLE dbo.localidades (
  nro_localidad   VARCHAR(36)    NOT NULL DEFAULT NEWID(),
  nom_localidad   NVARCHAR(120)  NOT NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  cod_provincia   VARCHAR(36)    NOT NULL,
  CONSTRAINT PK_localidades PRIMARY KEY (nro_localidad),
  CONSTRAINT UQ_localidades_prov_loc UNIQUE (cod_provincia, nom_localidad),
  CONSTRAINT FK_localidades_provincias
    FOREIGN KEY (cod_provincia) REFERENCES dbo.provincias(cod_provincia)
    ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

/* ===========================
   Idiomas
   =========================== */
CREATE TABLE dbo.idiomas (
  nro_idioma  VARCHAR(36)   NOT NULL DEFAULT NEWID(),
  nom_idioma  NVARCHAR(80)  NOT NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  cod_idioma  VARCHAR(16)   NOT NULL,  -- Mantiene VARCHAR: es-AR, en-US (códigos ISO)
  CONSTRAINT PK_idiomas PRIMARY KEY (nro_idioma),
  CONSTRAINT UQ_idiomas_cod UNIQUE (cod_idioma)
);
GO

/* ===========================
   Restaurantes y sucursales
   =========================== */
CREATE TABLE dbo.restaurantes (
  nro_restaurante VARCHAR(36)    NOT NULL DEFAULT NEWID(),
  razon_social    NVARCHAR(150)  NOT NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  cuit            VARCHAR(11)    NOT NULL,  -- Mantiene VARCHAR: solo números
  CONSTRAINT PK_restaurantes PRIMARY KEY (nro_restaurante),
  CONSTRAINT UQ_restaurantes_cuit UNIQUE (cuit)
);
GO

CREATE TABLE dbo.sucursales_restaurantes (
  nro_restaurante          VARCHAR(36)    NOT NULL,
  nro_sucursal             VARCHAR(36)    NOT NULL DEFAULT NEWID(),
  nom_sucursal             NVARCHAR(120)  NOT NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  calle                    NVARCHAR(120)  NULL,      -- CAMBIADO: VARCHAR → NVARCHAR
  nro_calle                INT            NULL,
  barrio                   NVARCHAR(120)  NULL,      -- CAMBIADO: VARCHAR → NVARCHAR
  nro_localidad            VARCHAR(36)    NOT NULL,
  cod_postal               VARCHAR(10)    NULL,      -- Mantiene VARCHAR: códigos postales
  telefonos                VARCHAR(120)   NULL,      -- Mantiene VARCHAR: solo números y símbolos
  total_comensales         INT            NOT NULL,
  min_tolerencia_reserva   INT            NOT NULL,  -- minutos
  cod_sucursal_restaurante VARCHAR(40)    NULL,      -- Mantiene VARCHAR: código técnico
  CONSTRAINT PK_sucursales_restaurantes PRIMARY KEY (nro_restaurante, nro_sucursal),
  CONSTRAINT UQ_sucursales_cod UNIQUE (nro_restaurante, cod_sucursal_restaurante),
  CONSTRAINT FK_suc_rest_restaurantes
    FOREIGN KEY (nro_restaurante) REFERENCES dbo.restaurantes(nro_restaurante)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_suc_rest_localidades
    FOREIGN KEY (nro_localidad) REFERENCES dbo.localidades(nro_localidad)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT CK_suc_total_pos CHECK (total_comensales > 0),
  CONSTRAINT CK_suc_tol_pos   CHECK (min_tolerencia_reserva >= 0)
);
GO

/* ===========================
   Atributos y configuración por restaurante
   =========================== */
CREATE TABLE dbo.atributos (
  cod_atributo VARCHAR(36)    NOT NULL DEFAULT NEWID(),
  nom_atributo NVARCHAR(120)  NOT NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  tipo_dato    VARCHAR(30)    NOT NULL,  -- Mantiene VARCHAR: valores técnicos
  CONSTRAINT PK_atributos PRIMARY KEY (cod_atributo),
  CONSTRAINT UQ_atributos_nom UNIQUE (nom_atributo),
  CONSTRAINT CK_atributos_tipo_dato
    CHECK (tipo_dato IN ('string','int','bool','time','decimal','date','datetime2'))
);
GO

CREATE TABLE dbo.configuracion_restaurantes (
  nro_restaurante VARCHAR(36)    NOT NULL,
  cod_atributo    VARCHAR(36)    NOT NULL,
  valor           NVARCHAR(500)  NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  CONSTRAINT PK_configuracion_rest PRIMARY KEY (nro_restaurante, cod_atributo),
  CONSTRAINT FK_conf_rest_rest
    FOREIGN KEY (nro_restaurante) REFERENCES dbo.restaurantes(nro_restaurante)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_conf_rest_attr
    FOREIGN KEY (cod_atributo) REFERENCES dbo.atributos(cod_atributo)
    ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

/* ===========================
   Preferencias (categorías y dominios)
   =========================== */
CREATE TABLE dbo.categorias_preferencias (
  cod_categoria VARCHAR(36)    NOT NULL DEFAULT NEWID(),
  nom_categoria NVARCHAR(120)  NOT NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  CONSTRAINT PK_categorias_preferencias PRIMARY KEY (cod_categoria),
  CONSTRAINT UQ_categorias_nom UNIQUE (nom_categoria)
);
GO

CREATE TABLE dbo.dominio_categorias_preferencias (
  cod_categoria     VARCHAR(36)    NOT NULL,
  nro_valor_dominio INT            NOT NULL,
  nom_valor_dominio NVARCHAR(120)  NOT NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  CONSTRAINT PK_dom_cat PRIMARY KEY (cod_categoria, nro_valor_dominio),
  CONSTRAINT FK_dom_cat_categoria
    FOREIGN KEY (cod_categoria) REFERENCES dbo.categorias_preferencias(cod_categoria)
    ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

CREATE TABLE dbo.idiomas_categorias_preferencias (
  cod_categoria  VARCHAR(36)    NOT NULL,
  nro_idioma     VARCHAR(36)    NOT NULL,
  categoria      NVARCHAR(120)  NOT NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  desc_categoria NVARCHAR(400)  NULL,      -- CAMBIADO: VARCHAR → NVARCHAR
  CONSTRAINT PK_idiomas_cat PRIMARY KEY (cod_categoria, nro_idioma),
  CONSTRAINT FK_idiomas_cat_categoria
    FOREIGN KEY (cod_categoria) REFERENCES dbo.categorias_preferencias(cod_categoria)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_idiomas_cat_idioma
    FOREIGN KEY (nro_idioma) REFERENCES dbo.idiomas(nro_idioma)
    ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

CREATE TABLE dbo.idiomas_dominio_cat_preferencias (
  cod_categoria      VARCHAR(36)    NOT NULL,
  nro_valor_dominio  INT            NOT NULL,
  nro_idioma         VARCHAR(36)    NOT NULL,
  valor_dominio      NVARCHAR(120)  NOT NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  desc_valor_dominio NVARCHAR(400)  NULL,      -- CAMBIADO: VARCHAR → NVARCHAR
  CONSTRAINT PK_idiomas_dom PRIMARY KEY (cod_categoria, nro_valor_dominio, nro_idioma),
  CONSTRAINT FK_idiomas_dom_dom
    FOREIGN KEY (cod_categoria, nro_valor_dominio)
    REFERENCES dbo.dominio_categorias_preferencias(cod_categoria, nro_valor_dominio)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_idiomas_dom_idioma
    FOREIGN KEY (nro_idioma) REFERENCES dbo.idiomas(nro_idioma)
    ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

/* ===========================
   Preferencias por restaurante y por cliente
   =========================== */
CREATE TABLE dbo.preferencias_restaurantes (
  nro_restaurante   VARCHAR(36)    NOT NULL,
  cod_categoria     VARCHAR(36)    NOT NULL,
  nro_valor_dominio INT            NOT NULL,
  nro_preferencia   INT            NOT NULL,
  observaciones     NVARCHAR(400)  NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  nro_sucursal      VARCHAR(36)    NULL,  -- opcional por sucursal
  CONSTRAINT PK_pref_rest PRIMARY KEY (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia),
  CONSTRAINT FK_pref_rest_rest
    FOREIGN KEY (nro_restaurante) REFERENCES dbo.restaurantes(nro_restaurante)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_pref_rest_cat
    FOREIGN KEY (cod_categoria) REFERENCES dbo.categorias_preferencias(cod_categoria)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_pref_rest_dom
    FOREIGN KEY (cod_categoria, nro_valor_dominio)
    REFERENCES dbo.dominio_categorias_preferencias(cod_categoria, nro_valor_dominio)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_pref_rest_suc
    FOREIGN KEY (nro_restaurante, nro_sucursal)
    REFERENCES dbo.sucursales_restaurantes(nro_restaurante, nro_sucursal)
    ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

CREATE TABLE dbo.clientes (
  nro_cliente   VARCHAR(36)    NOT NULL DEFAULT NEWID(),
  apellido      NVARCHAR(120)  NOT NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  nombre        NVARCHAR(120)  NOT NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  clave         VARCHAR(200)   NOT NULL,  -- Mantiene VARCHAR: hash de contraseña
  correo        VARCHAR(150)   NOT NULL,  -- Mantiene VARCHAR: emails no llevan acentos
  telefonos     VARCHAR(120)   NULL,      -- Mantiene VARCHAR: solo números
  nro_localidad VARCHAR(36)    NOT NULL,
  habilitado    BIT            NOT NULL DEFAULT 1,
  CONSTRAINT PK_clientes PRIMARY KEY (nro_cliente),
  CONSTRAINT UQ_clientes_correo UNIQUE (correo),
  CONSTRAINT FK_clientes_localidad
    FOREIGN KEY (nro_localidad) REFERENCES dbo.localidades(nro_localidad)
    ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

CREATE TABLE dbo.preferencias_clientes (
  nro_cliente       VARCHAR(36)    NOT NULL,
  cod_categoria     VARCHAR(36)    NOT NULL,
  nro_valor_dominio INT            NOT NULL,
  observaciones     NVARCHAR(400)  NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  CONSTRAINT PK_pref_clientes PRIMARY KEY (nro_cliente, cod_categoria, nro_valor_dominio),
  CONSTRAINT FK_pref_cli_cli
    FOREIGN KEY (nro_cliente) REFERENCES dbo.clientes(nro_cliente)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_pref_cli_cat
    FOREIGN KEY (cod_categoria) REFERENCES dbo.categorias_preferencias(cod_categoria)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_pref_cli_dom
    FOREIGN KEY (cod_categoria, nro_valor_dominio)
    REFERENCES dbo.dominio_categorias_preferencias(cod_categoria, nro_valor_dominio)
    ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

/* ===========================
   Contenidos/promos por restaurante
   =========================== */
CREATE TABLE dbo.contenidos_restaurantes (
  nro_restaurante           VARCHAR(36)     NOT NULL,
  nro_idioma                VARCHAR(36)     NOT NULL,
  nro_contenido             VARCHAR(36)     NOT NULL DEFAULT NEWID(),
  nro_sucursal              VARCHAR(36)     NULL,
  contenido_promocional     NVARCHAR(MAX)   NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  imagen_promocional        VARBINARY(MAX)  NULL,
  contenido_a_publicar      NVARCHAR(MAX)   NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  fecha_ini_vigencia        DATE            NULL,
  fecha_fin_vigencia        DATE            NULL,
  costo_click               DECIMAL(12,2)   NULL,
  cod_contenido_restaurante VARCHAR(40)     NULL,  -- Mantiene VARCHAR: código técnico
  CONSTRAINT PK_cont_rest PRIMARY KEY (nro_restaurante, nro_idioma, nro_contenido),
  CONSTRAINT FK_cont_rest_rest
    FOREIGN KEY (nro_restaurante) REFERENCES dbo.restaurantes(nro_restaurante)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_cont_rest_idioma
    FOREIGN KEY (nro_idioma) REFERENCES dbo.idiomas(nro_idioma)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_cont_rest_suc
    FOREIGN KEY (nro_restaurante, nro_sucursal)
    REFERENCES dbo.sucursales_restaurantes(nro_restaurante, nro_sucursal)
    ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

CREATE TABLE dbo.clicks_contenidos_restaurantes (
  nro_restaurante     VARCHAR(36)   NOT NULL,
  nro_idioma          VARCHAR(36)   NOT NULL,
  nro_contenido       VARCHAR(36)   NOT NULL,
  nro_click           VARCHAR(36)   NOT NULL DEFAULT NEWID(),
  fecha_hora_registro DATETIME2(3)  NOT NULL DEFAULT SYSDATETIME(),
  nro_cliente         VARCHAR(36)   NULL,
  costo_click         DECIMAL(12,2) NULL,
  notificado          BIT           NOT NULL DEFAULT 0,
  CONSTRAINT PK_clicks_cont PRIMARY KEY (nro_restaurante, nro_idioma, nro_contenido, nro_click),
  CONSTRAINT FK_clicks_cont_cont
    FOREIGN KEY (nro_restaurante, nro_idioma, nro_contenido)
    REFERENCES dbo.contenidos_restaurantes(nro_restaurante, nro_idioma, nro_contenido)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_clicks_cont_cli
    FOREIGN KEY (nro_cliente) REFERENCES dbo.clientes(nro_cliente)
    ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

/* ===========================
   Zonas y turnos por sucursal
   =========================== */
CREATE TABLE dbo.turnos_sucursales_restaurantes (
  nro_restaurante VARCHAR(36) NOT NULL,
  nro_sucursal    VARCHAR(36) NOT NULL,
  hora_desde      TIME(0)     NOT NULL,
  hora_hasta      TIME(0)     NOT NULL,
  habilitado      BIT         NOT NULL DEFAULT 1,
  CONSTRAINT PK_turnos_suc PRIMARY KEY (nro_restaurante, nro_sucursal, hora_desde),
  CONSTRAINT FK_turnos_suc_suc
    FOREIGN KEY (nro_restaurante, nro_sucursal)
    REFERENCES dbo.sucursales_restaurantes(nro_restaurante, nro_sucursal)
    ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

CREATE TABLE dbo.zonas_sucursales_restaurantes (
  nro_restaurante VARCHAR(36)    NOT NULL,
  nro_sucursal    VARCHAR(36)    NOT NULL,
  cod_zona        VARCHAR(36)    NOT NULL DEFAULT NEWID(),
  desc_zona       NVARCHAR(200)  NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  cant_comensales INT            NOT NULL,
  permite_menores BIT            NOT NULL DEFAULT 1,
  habilitada      BIT            NOT NULL DEFAULT 1,
  CONSTRAINT PK_zonas_suc PRIMARY KEY (nro_restaurante, nro_sucursal, cod_zona),
  CONSTRAINT FK_zonas_suc_suc
    FOREIGN KEY (nro_restaurante, nro_sucursal)
    REFERENCES dbo.sucursales_restaurantes(nro_restaurante, nro_sucursal)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT CK_zonas_cap_pos CHECK (cant_comensales > 0)
);
GO

CREATE TABLE dbo.idiomas_zonas_suc_restaurantes (
  nro_restaurante VARCHAR(36)    NOT NULL,
  nro_sucursal    VARCHAR(36)    NOT NULL,
  cod_zona        VARCHAR(36)    NOT NULL,
  nro_idioma      VARCHAR(36)    NOT NULL,
  zona            NVARCHAR(120)  NOT NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  desc_zona       NVARCHAR(400)  NULL,      -- CAMBIADO: VARCHAR → NVARCHAR
  CONSTRAINT PK_idiomas_zonas_suc PRIMARY KEY (nro_restaurante, nro_sucursal, cod_zona, nro_idioma),
  CONSTRAINT FK_idiomas_zonas_suc
    FOREIGN KEY (nro_restaurante, nro_sucursal, cod_zona)
    REFERENCES dbo.zonas_sucursales_restaurantes(nro_restaurante, nro_sucursal, cod_zona)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_idiomas_zonas_idioma
    FOREIGN KEY (nro_idioma) REFERENCES dbo.idiomas(nro_idioma)
    ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

CREATE TABLE dbo.zonas_turnos_sucurales_restaurantes (
  nro_restaurante VARCHAR(36) NOT NULL,
  nro_sucursal    VARCHAR(36) NOT NULL,
  cod_zona        VARCHAR(36) NOT NULL,
  hora_desde      TIME(0)     NOT NULL,
  permite_menores BIT         NOT NULL DEFAULT 1,
  CONSTRAINT PK_zonas_turnos PRIMARY KEY (nro_restaurante, nro_sucursal, cod_zona, hora_desde),
  CONSTRAINT FK_zonas_turnos_turno
    FOREIGN KEY (nro_restaurante, nro_sucursal, hora_desde)
    REFERENCES dbo.turnos_sucursales_restaurantes(nro_restaurante, nro_sucursal, hora_desde)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_zonas_turnos_zona
    FOREIGN KEY (nro_restaurante, nro_sucursal, cod_zona)
    REFERENCES dbo.zonas_sucursales_restaurantes(nro_restaurante, nro_sucursal, cod_zona)
    ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

/* ===========================
   Estados / Reservas
   =========================== */
CREATE TABLE dbo.estados_reservas (
  cod_estado VARCHAR(36)   NOT NULL DEFAULT NEWID(),
  nom_estado NVARCHAR(80)  NOT NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  CONSTRAINT PK_estados_reservas PRIMARY KEY (cod_estado),
  CONSTRAINT UQ_estados_nom UNIQUE (nom_estado)
);
GO

CREATE TABLE dbo.idiomas_estados_reservas (
  cod_estado VARCHAR(36)   NOT NULL,
  nro_idioma VARCHAR(36)   NOT NULL,
  estado     NVARCHAR(80)  NOT NULL,  -- CAMBIADO: VARCHAR → NVARCHAR
  CONSTRAINT PK_idiomas_estados PRIMARY KEY (cod_estado, nro_idioma),
  CONSTRAINT FK_idiomas_estados_estado
    FOREIGN KEY (cod_estado) REFERENCES dbo.estados_reservas(cod_estado)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_idiomas_estados_idioma
    FOREIGN KEY (nro_idioma) REFERENCES dbo.idiomas(nro_idioma)
    ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

CREATE TABLE dbo.reservas_restaurantes (
  nro_reserva            VARCHAR(36)   NOT NULL DEFAULT NEWID(),
  nro_restaurante        VARCHAR(36)   NOT NULL,
  nro_sucursal           VARCHAR(36)   NOT NULL,
  cod_zona               VARCHAR(36)   NOT NULL,
  fecha_reserva          DATE          NOT NULL,
  hora_desde             TIME(0)       NOT NULL,  -- FK al turno
  nro_cliente            VARCHAR(36)   NOT NULL,
  cant_adultos           SMALLINT      NOT NULL,
  cant_menores           SMALLINT      NOT NULL,
  cancelada              BIT           NOT NULL DEFAULT 0,
  fecha_hora_registro    DATETIME2(3)  NOT NULL DEFAULT SYSDATETIME(),
  fecha_hora_cancelacion DATETIME2(3)  NULL,
  cod_estado             VARCHAR(36)   NULL,      -- opcional según flujo
  costo_reserva          DECIMAL(12,2) NULL,
  notas                  NVARCHAR(400) NULL,      -- CAMBIADO: VARCHAR → NVARCHAR
  CONSTRAINT PK_reservas_restaurantes PRIMARY KEY (nro_reserva),
  CONSTRAINT FK_res_rest_suc
    FOREIGN KEY (nro_restaurante, nro_sucursal)
    REFERENCES dbo.sucursales_restaurantes(nro_restaurante, nro_sucursal)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_res_rest_zona
    FOREIGN KEY (nro_restaurante, nro_sucursal, cod_zona)
    REFERENCES dbo.zonas_sucursales_restaurantes(nro_restaurante, nro_sucursal, cod_zona)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_res_rest_turno
    FOREIGN KEY (nro_restaurante, nro_sucursal, hora_desde)
    REFERENCES dbo.turnos_sucursales_restaurantes(nro_restaurante, nro_sucursal, hora_desde)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_res_rest_cliente
    FOREIGN KEY (nro_cliente) REFERENCES dbo.clientes(nro_cliente)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FK_res_rest_estado
    FOREIGN KEY (cod_estado) REFERENCES dbo.estados_reservas(cod_estado)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT CK_reservas_cantidades CHECK (cant_adultos >= 0 AND cant_menores >= 0),
  CONSTRAINT CK_reservas_fecha_hora CHECK (hora_desde >= '00:00' AND hora_desde <= '23:59:59')
);
GO

/* ===========================
   Índices sugeridos
   =========================== */
CREATE INDEX IX_localidades_busq
  ON dbo.localidades (cod_provincia, nom_localidad);

CREATE INDEX IX_sucursales_rest_busq
  ON dbo.sucursales_restaurantes (nro_restaurante, nom_sucursal);

CREATE INDEX IX_turnos_busq
  ON dbo.turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde);

CREATE INDEX IX_zonas_suc_busq
  ON dbo.zonas_sucursales_restaurantes (nro_restaurante, nro_sucursal);

CREATE INDEX IX_reservas_busq
  ON dbo.reservas_restaurantes (nro_restaurante, nro_sucursal, cod_zona, fecha_reserva, hora_desde)
  INCLUDE (cant_adultos, cant_menores, cancelada);
GO

PRINT '>> Estructura de tablas creada exitosamente.';
GO

/* ==========================================================
   PASO 1: INSERTAR CATÁLOGOS BASE (01_insert_basicos.sql)
   ========================================================== */

PRINT '';
PRINT '>> PASO 1: Insertando catálogos base (provincias, localidades, idiomas)...';
GO

/* Provincias */
IF NOT EXISTS (SELECT 1 FROM provincias WHERE nom_provincia = N'Córdoba')
  INSERT INTO provincias (nom_provincia) VALUES (N'Córdoba');
IF NOT EXISTS (SELECT 1 FROM provincias WHERE nom_provincia = N'Buenos Aires')
  INSERT INTO provincias (nom_provincia) VALUES (N'Buenos Aires');
IF NOT EXISTS (SELECT 1 FROM provincias WHERE nom_provincia = N'Santa Fe')
  INSERT INTO provincias (nom_provincia) VALUES (N'Santa Fe');

/* Localidades (ligadas a su provincia por nombre) */
DECLARE @cod_cba VARCHAR(36), @cod_ba VARCHAR(36), @cod_sf VARCHAR(36);
SELECT @cod_cba = cod_provincia FROM provincias WHERE nom_provincia = N'Córdoba';
SELECT @cod_ba  = cod_provincia FROM provincias WHERE nom_provincia = N'Buenos Aires';
SELECT @cod_sf  = cod_provincia FROM provincias WHERE nom_provincia = N'Santa Fe';

IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'Córdoba' AND cod_provincia=@cod_cba)
  INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Córdoba', @cod_cba);
IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'Villa Carlos Paz' AND cod_provincia=@cod_cba)
  INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Villa Carlos Paz', @cod_cba);
IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'Río Cuarto' AND cod_provincia=@cod_cba)
  INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Río Cuarto', @cod_cba);

IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'La Plata' AND cod_provincia=@cod_ba)
  INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'La Plata', @cod_ba);
IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'Mar del Plata' AND cod_provincia=@cod_ba)
  INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Mar del Plata', @cod_ba);

IF NOT EXISTS (SELECT 1 FROM localidades WHERE nom_localidad=N'Rosario' AND cod_provincia=@cod_sf)
  INSERT INTO localidades (nom_localidad, cod_provincia) VALUES (N'Rosario', @cod_sf);

/* Idiomas */
IF NOT EXISTS (SELECT 1 FROM idiomas WHERE cod_idioma=N'es-AR')
  INSERT INTO idiomas (nom_idioma, cod_idioma) VALUES (N'Español (Argentina)', N'es-AR');
IF NOT EXISTS (SELECT 1 FROM idiomas WHERE cod_idioma=N'en-US')
  INSERT INTO idiomas (nom_idioma, cod_idioma) VALUES (N'English (United States)', N'en-US');
IF NOT EXISTS (SELECT 1 FROM idiomas WHERE cod_idioma=N'pt-BR')
  INSERT INTO idiomas (nom_idioma, cod_idioma) VALUES (N'Português (Brasil)', N'pt-BR');

PRINT '>> Catálogos base insertados exitosamente.';
GO

/* ==========================================================
   PASO 2: CREAR RESTAURANTE, SUCURSAL Y TURNOS (02_insert_resto_sucursal_turnos.sql)
   ========================================================== */

PRINT '';
PRINT '>> PASO 2: Creando restaurante, sucursal y turnos...';
GO

-------------------------------------------------------------
-- Parámetros (ajustables)
-------------------------------------------------------------
DECLARE 
  @RazonSocial NVARCHAR(150) = N'Los Aroza SRL',
  @CUIT        VARCHAR(11)   = '30700987654',
  @NomSucursal NVARCHAR(120) = N'Los Aroza - Centro',
  @Calle       NVARCHAR(120) = N'Av. Colón',
  @NroCalle    INT           = 950,
  @Barrio      NVARCHAR(120) = N'Centro',
  @Provincia   NVARCHAR(80)  = N'Córdoba',
  @Localidad   NVARCHAR(100) = N'Córdoba',
  @CP          NVARCHAR(10)  = N'5000',
  @Telefonos   NVARCHAR(120) = N'351-555-1234',
  @CapTotal    INT           = 140,
  @MinTol      INT           = 15,
  @Categoria   NVARCHAR(40)  = N'Media',
  @Apertura    TIME          = '16:00';   -- → 16,18,20,22 → 00:00

-------------------------------------------------------------
-- 1) Restaurante
-------------------------------------------------------------
DECLARE @nro_restaurante VARCHAR(36);
IF NOT EXISTS (SELECT 1 FROM restaurantes WHERE cuit=@CUIT)
  INSERT INTO restaurantes (razon_social, cuit) VALUES (@RazonSocial, @CUIT);

SELECT @nro_restaurante = nro_restaurante
FROM restaurantes WHERE cuit=@CUIT;

-------------------------------------------------------------
-- 2) Resolver localidad y categoría (por nombre)
-------------------------------------------------------------
DECLARE @cod_provincia VARCHAR(36), @nro_localidad VARCHAR(36), @nro_categoria VARCHAR(36);

SELECT @cod_provincia = cod_provincia FROM provincias WHERE nom_provincia=@Provincia;
SELECT @nro_localidad = nro_localidad 
FROM localidades WHERE nom_localidad=@Localidad AND cod_provincia=@cod_provincia;

-------------------------------------------------------------
-- 3) Sucursal (idempotente)
-------------------------------------------------------------
DECLARE @nro_sucursal VARCHAR(36);
IF NOT EXISTS (
  SELECT 1 FROM sucursales_restaurantes 
  WHERE nro_restaurante=@nro_restaurante AND nom_sucursal=@NomSucursal
)
BEGIN
  INSERT INTO sucursales_restaurantes (
    nro_restaurante, nom_sucursal, calle, nro_calle, barrio,
    nro_localidad, cod_postal, telefonos, total_comensales,
    min_tolerencia_reserva, cod_sucursal_restaurante
  ) VALUES (
    @nro_restaurante, @NomSucursal, @Calle, @NroCalle, @Barrio,
    @nro_localidad, @CP, @Telefonos, @CapTotal,
    @MinTol, NULL
  );
END

SELECT TOP 1 @nro_sucursal = nro_sucursal
FROM sucursales_restaurantes
WHERE nro_restaurante=@nro_restaurante AND nom_sucursal=@NomSucursal;

-------------------------------------------------------------
-- 4) Turnos cada 120 min desde @Apertura hasta 00:00
-------------------------------------------------------------
DECLARE @t TIME = @Apertura, @hHasta TIME, @i INT = 0;

WHILE (@i < 12)
BEGIN
  SET @hHasta = CAST(DATEADD(MINUTE, 120, CAST(@t AS datetime2(0))) AS TIME);

  IF NOT EXISTS (
    SELECT 1 FROM turnos_sucursales_restaurantes
    WHERE nro_restaurante=@nro_restaurante AND nro_sucursal=@nro_sucursal AND hora_desde=@t
  )
    INSERT INTO turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
    VALUES (@nro_restaurante, @nro_sucursal, @t, @hHasta, 1);

  IF (@hHasta = '00:00') BREAK;
  SET @t = @hHasta; SET @i += 1;
END

PRINT '>> Restaurante, sucursal y turnos creados exitosamente.';
GO

/* ==========================================================
   PASO 3: CREAR ZONAS DE LA SUCURSAL (03_insert_zonas_sucursal.sql)
   ========================================================== */

PRINT '';
PRINT '>> PASO 3: Creando zonas de la sucursal...';
GO

-------------------------------------------------------------
-- Parámetros
-------------------------------------------------------------
DECLARE 
  @CUIT        VARCHAR(11)   = '30700987654',
  @NomSucursal NVARCHAR(120) = N'Los Aroza - Centro';

-- Zonas "semilla" (ajustá nombres/capacidades)
DECLARE @Zonas TABLE (
  desc_zona       NVARCHAR(200),
  cant_comensales INT,
  permite_menores BIT,
  habilitada      BIT
);
INSERT INTO @Zonas VALUES
(N'Salón Principal', 90, 1, 1),
(N'Terraza',         50, 1, 1);

-------------------------------------------------------------
-- Resolver IDs restaurante y sucursal
-------------------------------------------------------------
DECLARE @nro_restaurante VARCHAR(36), @nro_sucursal VARCHAR(36);
SELECT @nro_restaurante = r.nro_restaurante FROM restaurantes r WHERE r.cuit = @CUIT;
IF @nro_restaurante IS NULL BEGIN RAISERROR('CUIT no encontrado.',16,1); RETURN; END

SELECT @nro_sucursal = s.nro_sucursal
FROM sucursales_restaurantes s
WHERE s.nro_restaurante=@nro_restaurante AND s.nom_sucursal=@NomSucursal;
IF @nro_sucursal IS NULL BEGIN RAISERROR('Sucursal no encontrada.',16,1); RETURN; END

-------------------------------------------------------------
-- Insert idempotente de zonas
-------------------------------------------------------------
DECLARE @dz NVARCHAR(200), @cap INT, @pm BIT, @hab BIT;

DECLARE curZ CURSOR LOCAL FAST_FORWARD FOR
  SELECT desc_zona, cant_comensales, permite_menores, habilitada FROM @Zonas;
OPEN curZ;
FETCH NEXT FROM curZ INTO @dz, @cap, @pm, @hab;
WHILE @@FETCH_STATUS = 0
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM zonas_sucursales_restaurantes
    WHERE nro_restaurante=@nro_restaurante AND nro_sucursal=@nro_sucursal AND desc_zona=@dz
  )
    INSERT INTO zonas_sucursales_restaurantes
      (nro_restaurante, nro_sucursal, desc_zona, cant_comensales, permite_menores, habilitada)
    VALUES
      (@nro_restaurante, @nro_sucursal, @dz, @cap, @pm, @hab);

  FETCH NEXT FROM curZ INTO @dz, @cap, @pm, @hab;
END
CLOSE curZ; DEALLOCATE curZ;

PRINT '>> Zonas de la sucursal creadas exitosamente.';
GO

/* ==========================================================
   PASO 4: CRUZAR ZONAS CON TURNOS (04_insert_zonas_x_turno.sql)
   ========================================================== */

PRINT '';
PRINT '>> PASO 4: Habilitando zonas en todos los turnos...';
GO

-------------------------------------------------------------
-- Parámetros
-------------------------------------------------------------
DECLARE 
  @CUIT        VARCHAR(11)   = '30700987654',
  @NomSucursal NVARCHAR(120) = N'Los Aroza - Centro';

-------------------------------------------------------------
-- Resolver IDs
-------------------------------------------------------------
DECLARE @nro_restaurante VARCHAR(36), @nro_sucursal VARCHAR(36);
SELECT @nro_restaurante = r.nro_restaurante FROM restaurantes r WHERE r.cuit=@CUIT;
IF @nro_restaurante IS NULL BEGIN RAISERROR('CUIT no encontrado.',16,1); RETURN; END

SELECT @nro_sucursal = s.nro_sucursal
FROM sucursales_restaurantes s
WHERE s.nro_restaurante=@nro_restaurante AND s.nom_sucursal=@NomSucursal;
IF @nro_sucursal IS NULL BEGIN RAISERROR('Sucursal no encontrada.',16,1); RETURN; END

-------------------------------------------------------------
-- Insertar faltantes: zonas × turnos
-------------------------------------------------------------
INSERT INTO zonas_turnos_sucurales_restaurantes
       (nro_restaurante, nro_sucursal, cod_zona, hora_desde, permite_menores)
SELECT t.nro_restaurante, t.nro_sucursal, zsr.cod_zona, t.hora_desde, zsr.permite_menores
FROM turnos_sucursales_restaurantes t
JOIN zonas_sucursales_restaurantes zsr
  ON zsr.nro_restaurante=t.nro_restaurante AND zsr.nro_sucursal=t.nro_sucursal
LEFT JOIN zonas_turnos_sucurales_restaurantes ztr
  ON ztr.nro_restaurante=t.nro_restaurante AND ztr.nro_sucursal=t.nro_sucursal
 AND ztr.cod_zona=zsr.cod_zona AND ztr.hora_desde=t.hora_desde
WHERE t.nro_restaurante=@nro_restaurante AND t.nro_sucursal=@nro_sucursal
  AND ztr.nro_restaurante IS NULL;

PRINT '>> Zonas habilitadas en turnos exitosamente.';
GO

/* ==========================================================
   PASO 5: CREAR CATEGORÍAS Y DOMINIOS DE PREFERENCIAS (05_insert_categorias_y_dominios.sql)
   ========================================================== */

PRINT '';
PRINT '>> PASO 5: Creando categorías de preferencias y dominios...';
GO

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

PRINT '>> Categorías y dominios de preferencias creados exitosamente.';
GO

/* ==========================================================
   PASO 6: INSERTAR CLIENTES DEMO (06_insert_clientes_demo.sql)
   ========================================================== */

PRINT '';
PRINT '>> PASO 6: Insertando clientes de demostración...';
GO

-------------------------------------------------------------
-- Parámetros de localidades (ajustables a tu dataset)
-------------------------------------------------------------
DECLARE 
  @ProvCba NVARCHAR(80)  = N'Córdoba',
  @LocCba  NVARCHAR(100) = N'Córdoba',
  @LocVcp  NVARCHAR(100) = N'Villa Carlos Paz',
  @ProvBsA NVARCHAR(80)  = N'Buenos Aires',
  @LocLPl  NVARCHAR(100) = N'La Plata';

-------------------------------------------------------------
-- Resolver IDs de localidades
-------------------------------------------------------------
DECLARE @cod_cba VARCHAR(36), @cod_ba VARCHAR(36);
SELECT @cod_cba = cod_provincia FROM provincias WHERE nom_provincia=@ProvCba;
SELECT @cod_ba  = cod_provincia FROM provincias WHERE nom_provincia=@ProvBsA;

DECLARE @loc_cba VARCHAR(36), @loc_vcp VARCHAR(36), @loc_lpl VARCHAR(36);
SELECT @loc_cba = nro_localidad FROM localidades WHERE nom_localidad=@LocCba AND cod_provincia=@cod_cba;
SELECT @loc_vcp = nro_localidad FROM localidades WHERE nom_localidad=@LocVcp AND cod_provincia=@cod_cba;
SELECT @loc_lpl = nro_localidad FROM localidades WHERE nom_localidad=@LocLPl AND cod_provincia=@cod_ba;

IF @loc_cba IS NULL OR @loc_vcp IS NULL OR @loc_lpl IS NULL
BEGIN
  RAISERROR('Faltan localidades base. Corré primero 01_insert_basicos.sql', 16, 1);
  RETURN;
END

-------------------------------------------------------------
-- Insert idempotente de clientes demo
-------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM clientes WHERE correo=N'ana.rodriguez@mail.com')
  INSERT INTO clientes (apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado)
  VALUES (N'Rodríguez', N'Ana', N'$2y$dummy$hash', N'ana.rodriguez@mail.com', N'351-555-1111', @loc_cba, 1);

IF NOT EXISTS (SELECT 1 FROM clientes WHERE correo=N'max.ferreyra@mail.com')
  INSERT INTO clientes (apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado)
  VALUES (N'Ferreyra', N'Maximiliano', N'$2y$dummy$hash', N'max.ferreyra@mail.com', N'351-555-2222', @loc_vcp, 1);

IF NOT EXISTS (SELECT 1 FROM clientes WHERE correo=N'carla.sosa@mail.com')
  INSERT INTO clientes (apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado)
  VALUES (N'Sosa', N'Carla', N'$2y$dummy$hash', N'carla.sosa@mail.com', N'221-555-3333', @loc_lpl, 1);

PRINT '>> Clientes de demostración insertados exitosamente.';
GO

/* ==========================================================
   PASO 7: INSERTAR CONTENIDOS/PROMOCIONES DEMO (07_insert_contenidos_demo.sql)
   ========================================================== */

PRINT '';
PRINT '>> PASO 7: Insertando contenidos y promociones de demostración...';
GO

-------------------------------------------------------------
-- Parámetros
-------------------------------------------------------------
DECLARE 
  @CUIT        VARCHAR(11)   = '30700987654',
  @NomSucursal NVARCHAR(120) = N'Los Aroza - Centro',
  @IdiomaCod   NVARCHAR(16)  = N'es-AR';
-- Códigos legibles para evitar duplicados lógicos
DECLARE 
  @CodPromo1 NVARCHAR(40) = N'PRM-PRIMAVERA-2x1',
  @CodPromo2 NVARCHAR(40) = N'PRM-FINDE-KIDS';

-------------------------------------------------------------
-- Resolver IDs restaurante, sucursal (si existe) e idioma
-------------------------------------------------------------
DECLARE @nro_restaurante VARCHAR(36), @nro_sucursal VARCHAR(36), @nro_idioma VARCHAR(36);

SELECT @nro_restaurante = nro_restaurante FROM restaurantes WHERE cuit=@CUIT;
IF @nro_restaurante IS NULL BEGIN RAISERROR('Restaurante no encontrado por CUIT.',16,1); RETURN; END

SELECT @nro_sucursal = s.nro_sucursal
FROM sucursales_restaurantes s
WHERE s.nro_restaurante=@nro_restaurante AND s.nom_sucursal=@NomSucursal;

SELECT @nro_idioma = nro_idioma FROM idiomas WHERE cod_idioma=@IdiomaCod;
IF @nro_idioma IS NULL BEGIN RAISERROR('Idioma base no encontrado (es-AR). Corré 01_insert_basicos.sql',16,1); RETURN; END

-------------------------------------------------------------
-- Inserciones idempotentes (usando cod_contenido_restaurante como "natural key")
-------------------------------------------------------------
IF NOT EXISTS (
  SELECT 1 FROM contenidos_restaurantes 
  WHERE nro_restaurante=@nro_restaurante AND nro_idioma=@nro_idioma AND cod_contenido_restaurante=@CodPromo1
)
  INSERT INTO contenidos_restaurantes (
    nro_restaurante, nro_idioma, nro_sucursal,
    contenido_promocional, imagen_promocional, contenido_a_publicar,
    fecha_ini_vigencia, fecha_fin_vigencia, costo_click, cod_contenido_restaurante
  )
  VALUES (
    @nro_restaurante, @nro_idioma, @nro_sucursal,
    N'¡Promo Primavera! 2x1 en principales de 16:00 a 20:00.',
    NULL,
    N'Vení a probar nuestra carta de temporada con 2x1 en platos seleccionados.',
    CAST(GETDATE() AS date),
    DATEADD(DAY, 30, CAST(GETDATE() AS date)),
    50.00,
    @CodPromo1
  );

IF NOT EXISTS (
  SELECT 1 FROM contenidos_restaurantes 
  WHERE nro_restaurante=@nro_restaurante AND nro_idioma=@nro_idioma AND cod_contenido_restaurante=@CodPromo2
)
  INSERT INTO contenidos_restaurantes (
    nro_restaurante, nro_idioma, nro_sucursal,
    contenido_promocional, imagen_promocional, contenido_a_publicar,
    fecha_ini_vigencia, fecha_fin_vigencia, costo_click, cod_contenido_restaurante
  )
  VALUES (
    @nro_restaurante, @nro_idioma, @nro_sucursal,
    N'Finde en familia: Menú kids con postre incluido.',
    NULL,
    N'Menú especial para chicos disponible sábados y domingos.',
    CAST(GETDATE() AS date),
    DATEADD(DAY, 45, CAST(GETDATE() AS date)),
    35.00,
    @CodPromo2
  );

PRINT '>> Contenidos y promociones de demostración insertados exitosamente.';
GO

/* ==========================================================
   PASO 8: INSERTAR TRADUCCIONES DE ZONAS (08_insert_traducciones_zonas.sql) - OPCIONAL
   ========================================================== */

PRINT '';
PRINT '>> PASO 8: Insertando traducciones de zonas (opcional para i18n)...';
GO

DECLARE 
  @CUIT        VARCHAR(11)   = '30700987654',
  @NomSucursal NVARCHAR(120) = N'Los Aroza - Centro',
  @IdiomaCod   NVARCHAR(16)  = N'en-US';  -- traducimos a inglés (ejemplo)

-- Resolver IDs
DECLARE @nro_restaurante VARCHAR(36), @nro_sucursal VARCHAR(36), @nro_idioma VARCHAR(36);
SELECT @nro_restaurante = nro_restaurante FROM restaurantes WHERE cuit=@CUIT;
IF @nro_restaurante IS NULL BEGIN RAISERROR('Restaurante no encontrado por CUIT.',16,1); RETURN; END

SELECT @nro_sucursal = nro_sucursal
FROM sucursales_restaurantes
WHERE nro_restaurante=@nro_restaurante AND nom_sucursal=@NomSucursal;
IF @nro_sucursal IS NULL BEGIN RAISERROR('Sucursal no encontrada.',16,1); RETURN; END

SELECT @nro_idioma = nro_idioma FROM idiomas WHERE cod_idioma=@IdiomaCod;
IF @nro_idioma IS NULL BEGIN RAISERROR('Idioma no encontrado. Corré 01_insert_basicos.sql',16,1); RETURN; END

-- Traducciones (ejemplos)
;WITH z AS (
  SELECT cod_zona, desc_zona
  FROM zonas_sucursales_restaurantes
  WHERE nro_restaurante=@nro_restaurante AND nro_sucursal=@nro_sucursal
)
INSERT INTO idiomas_zonas_suc_restaurantes (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
SELECT @nro_restaurante, @nro_sucursal, z.cod_zona, @nro_idioma,
       CASE z.desc_zona
         WHEN N'Salón Principal' THEN N'Main Hall'
         WHEN N'Terraza'         THEN N'Terrace'
         ELSE z.desc_zona
       END AS zona,
       NULL
FROM z
WHERE NOT EXISTS (
  SELECT 1 FROM idiomas_zonas_suc_restaurantes t
  WHERE t.nro_restaurante=@nro_restaurante AND t.nro_sucursal=@nro_sucursal
    AND t.cod_zona=z.cod_zona AND t.nro_idioma=@nro_idioma
);

PRINT '>> Traducciones de zonas insertadas exitosamente.';
GO

/* ==========================================================
   PASO 9: INSERTAR PREFERENCIAS DEL RESTAURANTE (09_insert_preferencias_restaurante.sql)
   ========================================================== */

PRINT '';
PRINT '>> PASO 9: Insertando preferencias del restaurante...';
GO

-------------------------------------------------------------
-- Parámetros
-------------------------------------------------------------
DECLARE 
  @CUIT VARCHAR(11) = '30700987654',
  @NomSucursal NVARCHAR(120) = N'Los Aroza - Centro';

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

PRINT '>> Preferencias del restaurante insertadas exitosamente.';
GO

/* ==========================================================
   FINALIZACIÓN
   ========================================================== */

PRINT '';
PRINT '====================================================';
PRINT 'SETUP COMPLETO FINALIZADO EXITOSAMENTE';
PRINT '====================================================';
PRINT '';
PRINT 'Resumen de objetos creados:';
PRINT '- 24 tablas (con campos NVARCHAR para soporte Unicode)';
PRINT '- Catálogos: provincias, localidades e idiomas';
PRINT '- 1 restaurante con 1 sucursal';
PRINT '- Turnos configurados cada 2 horas';
PRINT '- 2 zonas (Salón Principal y Terraza)';
PRINT '- Categorías de preferencias con dominios';
PRINT '- 3 clientes de demostración';
PRINT '- 2 promociones activas';
PRINT '- Traducciones de zonas en inglés';
PRINT '- Preferencias del restaurante configuradas';
PRINT '';
PRINT 'Base de datos lista para usar con soporte completo de Unicode.';
GO

