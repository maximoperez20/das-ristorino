
/* ==========================================================
   RISTORINO - CREACI�N DE TABLAS (formato JKMate previo)
   - IDs VARCHAR(36) DEFAULT NEWID()
   - ON UPDATE/DELETE NO ACTION
   - TIME para horas, DATETIME2(3) para timestamps
   ========================================================== */

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

/* ==========================================================
   CONFIGURACIÓN DE TIMEZONE
   Timezone: UTC-3 (Buenos Aires, Argentina)
   Nota: SQL Server maneja timezone a nivel de aplicación.
   Para consultas con timezone específico usar: AT TIME ZONE 'Argentina Standard Time'
   ========================================================== */
SET DATEFIRST 1; -- Lunes como primer día de la semana
GO

/* ==========================================================
   LIMPIEZA PREVIA
   Elimina todas las tablas del esquema RISTORINO en orden inverso
   de dependencias (de las m�s dependientes a las m�s base)
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

IF OBJECT_ID('dbo.costos', 'U') IS NOT NULL DROP TABLE dbo.costos;

PRINT '>> Todas las tablas eliminadas correctamente (si exist�an).';
GO



/* ===========================
   Cat�logos geogr�ficos
   =========================== */
CREATE TABLE dbo.provincias (
  cod_provincia   VARCHAR(36)  NOT NULL DEFAULT NEWID(),
  nom_provincia   NVARCHAR(80) NOT NULL,
  CONSTRAINT PK_provincias PRIMARY KEY (cod_provincia),
  CONSTRAINT UQ_provincias_nom UNIQUE (nom_provincia)
);
GO

CREATE TABLE dbo.localidades (
  nro_localidad   VARCHAR(36)   NOT NULL DEFAULT NEWID(),
  nom_localidad   NVARCHAR(120) NOT NULL,
  cod_provincia   VARCHAR(36)   NOT NULL,
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
  nro_idioma  INT  NOT NULL IDENTITY(0,1),
  nom_idioma  NVARCHAR(80) NOT NULL,
  cod_idioma  VARCHAR(16) NOT NULL, -- ej.: es-AR, en-US
  CONSTRAINT PK_idiomas PRIMARY KEY (nro_idioma),
  CONSTRAINT UQ_idiomas_cod UNIQUE (cod_idioma)
);
GO

/* ===========================
   Restaurantes y sucursales
   =========================== */
CREATE TABLE dbo.restaurantes (
  nro_restaurante VARCHAR(36)   NOT NULL DEFAULT NEWID(),
  razon_social    NVARCHAR(150) NOT NULL,
  cuit            VARCHAR(11)   NOT NULL,
  tipo_protocolo  VARCHAR(10)   NOT NULL DEFAULT 'SOAP',  -- 'SOAP' o 'REST'
  url_servicio    NVARCHAR(500) NULL,  -- URL completa del servicio (ej: http://localhost:8081/ws/restaurantes.wsdl para SOAP, http://localhost:8082/api para REST)
  CONSTRAINT PK_restaurantes PRIMARY KEY (nro_restaurante),
  CONSTRAINT UQ_restaurantes_cuit UNIQUE (cuit),
  CONSTRAINT CK_restaurantes_protocolo CHECK (tipo_protocolo IN ('SOAP', 'REST'))
);
GO

CREATE TABLE dbo.sucursales_restaurantes (
  nro_restaurante         VARCHAR(36)   NOT NULL,
  nro_sucursal            VARCHAR(36)   NOT NULL DEFAULT NEWID(),
  nom_sucursal            NVARCHAR(120) NOT NULL,
  calle                   NVARCHAR(120) NULL,
  nro_calle               INT           NULL,
  barrio                  NVARCHAR(120) NULL,
  nro_localidad           VARCHAR(36)   NOT NULL,
  cod_postal              VARCHAR(10)  NULL,
  telefonos               VARCHAR(120) NULL,
  total_comensales        INT           NOT NULL,
  min_tolerencia_reserva  INT           NOT NULL,   -- minutos
  cod_sucursal_restaurante VARCHAR(40) NULL,       -- c�digo legible opcional
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
   Atributos y configuraci�n por restaurante
   =========================== */
CREATE TABLE dbo.atributos (
  cod_atributo VARCHAR(36)   NOT NULL DEFAULT NEWID(),
  nom_atributo NVARCHAR(120) NOT NULL,
  tipo_dato    VARCHAR(30)  NOT NULL,  -- string/int/bool/time/decimal/...
  CONSTRAINT PK_atributos PRIMARY KEY (cod_atributo),
  CONSTRAINT UQ_atributos_nom UNIQUE (nom_atributo),
  CONSTRAINT CK_atributos_tipo_dato
    CHECK (tipo_dato IN ('string','int','bool','time','decimal','date','datetime2'))
);
GO

CREATE TABLE dbo.configuracion_restaurantes (
  nro_restaurante VARCHAR(36) NOT NULL,
  cod_atributo    VARCHAR(36) NOT NULL,
  valor           NVARCHAR(500) NULL,
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
   Preferencias (categor�as y dominios)
   =========================== */
CREATE TABLE dbo.categorias_preferencias (
  cod_categoria VARCHAR(36)   NOT NULL DEFAULT NEWID(),
  nom_categoria NVARCHAR(120) NOT NULL,
  CONSTRAINT PK_categorias_preferencias PRIMARY KEY (cod_categoria),
  CONSTRAINT UQ_categorias_nom UNIQUE (nom_categoria)
);
GO

CREATE TABLE dbo.dominio_categorias_preferencias (
  cod_categoria     VARCHAR(36)   NOT NULL,
  nro_valor_dominio INT           NOT NULL,
  nom_valor_dominio NVARCHAR(120) NOT NULL,
  CONSTRAINT PK_dom_cat PRIMARY KEY (cod_categoria, nro_valor_dominio),
  CONSTRAINT FK_dom_cat_categoria
    FOREIGN KEY (cod_categoria) REFERENCES dbo.categorias_preferencias(cod_categoria)
    ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

CREATE TABLE dbo.idiomas_categorias_preferencias (
  cod_categoria  VARCHAR(36)   NOT NULL,
  nro_idioma     INT   NOT NULL,
  categoria      NVARCHAR(120) NOT NULL,
  desc_categoria NVARCHAR(400) NULL,
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
  cod_categoria     VARCHAR(36)   NOT NULL,
  nro_valor_dominio INT           NOT NULL,
  nro_idioma        INT   NOT NULL,
  valor_dominio     NVARCHAR(120) NOT NULL,
  desc_valor_dominio NVARCHAR(400) NULL,
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
  nro_restaurante   VARCHAR(36) NOT NULL,
  cod_categoria     VARCHAR(36) NOT NULL,
  nro_valor_dominio INT         NOT NULL,
  nro_preferencia   INT         NOT NULL,
  observaciones     NVARCHAR(400) NULL,
  nro_sucursal      VARCHAR(36)  NULL, -- opcional por sucursal
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
  nro_cliente   VARCHAR(36)   NOT NULL DEFAULT NEWID(),
  apellido      NVARCHAR(120) NOT NULL,
  nombre        NVARCHAR(120) NOT NULL,
  clave         VARCHAR(200) NOT NULL,  -- hash/secreto
  correo        VARCHAR(150) NOT NULL,
  telefonos     VARCHAR(120) NULL,
  nro_localidad VARCHAR(36)   NOT NULL,
  habilitado    BIT           NOT NULL DEFAULT 1,
  CONSTRAINT PK_clientes PRIMARY KEY (nro_cliente),
  CONSTRAINT UQ_clientes_correo UNIQUE (correo),
  CONSTRAINT FK_clientes_localidad
    FOREIGN KEY (nro_localidad) REFERENCES dbo.localidades(nro_localidad)
    ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

CREATE TABLE dbo.preferencias_clientes (
  nro_cliente       VARCHAR(36) NOT NULL,
  cod_categoria     VARCHAR(36) NOT NULL,
  nro_valor_dominio INT         NOT NULL,
  observaciones     NVARCHAR(400) NULL,
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
  nro_restaurante        VARCHAR(36)   NOT NULL,
  nro_idioma             INT   NOT NULL,
  nro_contenido          VARCHAR(36)   NOT NULL DEFAULT NEWID(),
  nro_sucursal           VARCHAR(36)   NULL,
  contenido_promocional  NVARCHAR(MAX) NULL,
  imagen_promocional     NVARCHAR(500) NULL,  -- URL de la imagen en internet
  contenido_a_publicar   NVARCHAR(MAX) NULL,
  fecha_ini_vigencia     DATE          NULL,
  fecha_fin_vigencia     DATE          NULL,
  costo_click            DECIMAL(12,2) NULL,
  cod_contenido_restaurante VARCHAR(40) NULL,
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
  nro_restaurante    VARCHAR(36)  NOT NULL,
  nro_idioma         INT  NOT NULL,
  nro_contenido      VARCHAR(36)  NOT NULL,
  nro_click          VARCHAR(36)  NOT NULL DEFAULT NEWID(),
  fecha_hora_registro DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
  nro_cliente        VARCHAR(36)   NULL,
  costo_click        DECIMAL(12,2) NULL,
  notificado         BIT           NOT NULL DEFAULT 0,
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
  nro_restaurante VARCHAR(36)   NOT NULL,
  nro_sucursal    VARCHAR(36)   NOT NULL,
  cod_zona        VARCHAR(36)   NOT NULL DEFAULT NEWID(),
  cod_zona_restaurante VARCHAR(36) NULL,  -- Código de zona en la base del restaurante (SOAP)
  desc_zona       NVARCHAR(200) NULL,
  cant_comensales INT           NOT NULL,
  permite_menores BIT           NOT NULL DEFAULT 1,
  habilitada      BIT           NOT NULL DEFAULT 1,
  CONSTRAINT PK_zonas_suc PRIMARY KEY (nro_restaurante, nro_sucursal, cod_zona),
  CONSTRAINT FK_zonas_suc_suc
    FOREIGN KEY (nro_restaurante, nro_sucursal)
    REFERENCES dbo.sucursales_restaurantes(nro_restaurante, nro_sucursal)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT CK_zonas_cap_pos CHECK (cant_comensales > 0)
);
GO

CREATE TABLE dbo.idiomas_zonas_suc_restaurantes (
  nro_restaurante VARCHAR(36)   NOT NULL,
  nro_sucursal    VARCHAR(36)   NOT NULL,
  cod_zona        VARCHAR(36)   NOT NULL,
  nro_idioma      INT   NOT NULL,
  zona            NVARCHAR(120) NOT NULL,
  desc_zona       NVARCHAR(400) NULL,
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
  cod_estado VARCHAR(36)  NOT NULL DEFAULT NEWID(),
  nom_estado NVARCHAR(80) NOT NULL,
  CONSTRAINT PK_estados_reservas PRIMARY KEY (cod_estado),
  CONSTRAINT UQ_estados_nom UNIQUE (nom_estado)
);
GO

CREATE TABLE dbo.idiomas_estados_reservas (
  cod_estado VARCHAR(36)  NOT NULL,
  nro_idioma INT  NOT NULL,
  estado     NVARCHAR(80) NOT NULL,
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
  nro_reserva        VARCHAR(36)  NOT NULL DEFAULT NEWID(),
  nro_restaurante    VARCHAR(36)  NOT NULL,
  nro_sucursal       VARCHAR(36)  NOT NULL,
  cod_zona           VARCHAR(36)  NOT NULL,
  fecha_reserva      DATE         NOT NULL,
  hora_desde         TIME(0)      NOT NULL,  -- FK al turno
  nro_cliente        VARCHAR(36)  NOT NULL,
  cant_adultos       SMALLINT     NOT NULL,
  cant_menores       SMALLINT     NOT NULL,
  cancelada          BIT          NOT NULL DEFAULT 0,
  fecha_hora_registro DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
  fecha_hora_cancelacion DATETIME2(3) NULL,
  cod_estado         VARCHAR(36)  NULL,      -- opcional seg�n flujo
  costo_reserva      DECIMAL(12,2) NULL,
  notas              NVARCHAR(400) NULL,
  cod_reserva_sucursal VARCHAR(36) NULL,     -- Código de reserva en la base del restaurante
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

create table dbo.resenas_sucursales_restaurantes (
    nro_resena VARCHAR(36) NOT NULL DEFAULT NEWID(),
    nro_restaurante VARCHAR(36) NOT NULL,
    nro_sucursal VARCHAR(36) NOT NULL,
    nro_cliente VARCHAR(36) NOT NULL,
    calificacion INT NOT NULL,
    comentario NVARCHAR(1000) NULL,
    fecha_hora_registro DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT PK_resenas_sucursales PRIMARY KEY (nro_resena),
    CONSTRAINT FK_resenas_sucursales_suc
        FOREIGN KEY (nro_restaurante, nro_sucursal)
        REFERENCES dbo.sucursales_restaurantes(nro_restaurante, nro_sucursal)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT FK_resenas_sucursales_cliente
        FOREIGN KEY (nro_cliente) REFERENCES dbo.clientes(nro_cliente)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT CK_resenas_calificacion CHECK (calificacion >= 1 AND calificacion <= 5)
);

/* ===========================
   �ndices sugeridos
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

/* ===========================
   Costos / Fees
   =========================== */
CREATE TABLE dbo.costos (
  tipo_costo        VARCHAR(50)  NOT NULL,
  fecha_ini_vigencia DATE        NOT NULL,
  fecha_fin_vigencia DATE        NULL,
  monto             DECIMAL(12,2) NOT NULL,
  CONSTRAINT PK_costos PRIMARY KEY (tipo_costo, fecha_ini_vigencia),
  CONSTRAINT CK_costos_fechas CHECK (fecha_fin_vigencia IS NULL OR fecha_fin_vigencia >= fecha_ini_vigencia),
  CONSTRAINT CK_costos_monto CHECK (monto >= 0)
);
GO
