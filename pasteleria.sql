-- Sección de administración (CORREGIDA)

-- 1. Borrar BD y usuarios si existen
DROP DATABASE IF EXISTS pasteleria;

DROP USER IF EXISTS 'usuario_pasteleria'@'localhost';
DROP USER IF EXISTS 'usuario_pasteleria'@'%';
DROP USER IF EXISTS 'usuario_reportes'@'localhost';
DROP USER IF EXISTS 'usuario_reportes'@'%';

FLUSH PRIVILEGES;

-- 2. Crear esquema
CREATE DATABASE pasteleria
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- 3. Crear usuarios con las mismas contraseñas que usa Spring Boot
CREATE USER 'usuario_pasteleria'@'localhost' IDENTIFIED BY 'Pasteles_Seguro.';
CREATE USER 'usuario_pasteleria'@'%'        IDENTIFIED BY 'Pasteles_Seguro.';

CREATE USER 'usuario_reportes'@'localhost'  IDENTIFIED BY 'Pasteles_Reportes.';
CREATE USER 'usuario_reportes'@'%'          IDENTIFIED BY 'Pasteles_Reportes.';

-- 4. Asignar permisos
GRANT SELECT, INSERT, UPDATE, DELETE
  ON pasteleria.*
  TO 'usuario_pasteleria'@'localhost';

GRANT SELECT, INSERT, UPDATE, DELETE
  ON pasteleria.*
  TO 'usuario_pasteleria'@'%';

GRANT SELECT
  ON pasteleria.*
  TO 'usuario_reportes'@'localhost';

GRANT SELECT
  ON pasteleria.*
  TO 'usuario_reportes'@'%';

FLUSH PRIVILEGES;

-- 5. Usar la base de datos
USE pasteleria;


-- Tablas
-- Usuarios (registro)
create table usuario (
  id_usuario         bigint unsigned not null auto_increment,
  nombre             varchar(120)    not null,
  correo             varchar(190)    not null,
  password_hash      varchar(255)    not null,
  activo             boolean         not null default true,
  fecha_creacion         timestamp       not null default current_timestamp,
  fecha_modificacion         timestamp       not null default current_timestamp on update current_timestamp,
  primary key (id_usuario),
  unique key uq_usuario_correo (correo),
  check (correo regexp '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$')
) engine=InnoDB;

create index ix_usuario_correo on usuario(correo);

-- Recuperación de contraseña por código con expiración
create table expiracion_token_contraseña (
  id_token    bigint unsigned not null auto_increment,
  id_usuario  bigint unsigned not null,
  codigo      char(6)         not null,
  expira_en   datetime        not null,
  usado       tinyint         not null default 0,
  fecha_creacion  timestamp       not null default current_timestamp,
  fecha_modificacion  timestamp       not null default current_timestamp on update current_timestamp,
  primary key (id_token),
  key ix_token_usuario_codigo (id_usuario, codigo, usado),
  constraint fk_token_usuario
    foreign key (id_usuario) references usuario(id_usuario)
    on delete cascade on update cascade
) engine=InnoDB;

-- Catálogo de productos con tamaños, sabores e ingredientes
create table producto (
  id_producto   bigint unsigned not null auto_increment,
  nombre        varchar(140)    not null,
  descripcion   text            null,
  conservacion  text            null,     -- HU10: instrucciones de conservación
  precio        decimal(10,2)   not null default 0.00,
  activo        boolean         not null default true,
 fecha_creacion    timestamp       not null default current_timestamp,
 fecha_modificacion    timestamp       not null default current_timestamp on update current_timestamp,
  primary key (id_producto),
  unique key uq_producto_nombre (nombre)
) engine=InnoDB;

create table producto_tamano (
  id_tamano    bigint unsigned not null auto_increment,
  id_producto  bigint unsigned not null,
  etiqueta     varchar(60)     not null,  -- Pequeño/Mediano/Grande
  fecha_creacion   timestamp       not null default current_timestamp,
  fecha_modificacion   timestamp       not null default current_timestamp on update current_timestamp,
  primary key (id_tamano),
  unique key uq_tamano_producto (id_producto, etiqueta),
  constraint fk_tamano_producto
    foreign key (id_producto) references producto(id_producto)
    on delete cascade on update cascade
) engine=InnoDB;

create table producto_sabor (
  id_sabor     bigint unsigned not null auto_increment,
  id_producto  bigint unsigned not null,
  etiqueta     varchar(60)     not null,  -- Vainilla/Chocolate/Fresa
  fecha_creacion   timestamp       not null default current_timestamp,
  fecha_modificacion   timestamp       not null default current_timestamp on update current_timestamp,
  primary key (id_sabor),
  unique key uq_sabor_producto (id_producto, etiqueta),
  constraint fk_sabor_producto
    foreign key (id_producto) references producto(id_producto)
    on delete cascade on update cascade
) engine=InnoDB;

create table producto_ingrediente (
  id_ingrediente  bigint unsigned not null auto_increment,
  id_producto     bigint unsigned not null,
  nombre          varchar(120)    not null,  -- Harina/Huevo/Leche
  fecha_creacion      timestamp       not null default current_timestamp,
  fecha_modificacion      timestamp       not null default current_timestamp on update current_timestamp,
  primary key (id_ingrediente),
  unique key uq_ing_producto (id_producto, nombre),
  constraint fk_ing_producto
    foreign key (id_producto) references producto(id_producto)
    on delete cascade on update cascade
) engine=InnoDB;

-- Insertar los nuevos productos del catálogo
INSERT INTO producto (nombre, descripcion, conservacion, precio) VALUES
-- Pies y tortas
('Pie de manzana', 'Delicioso pie de manzana con canela', 'Consumir en 24 horas, puede refrigerarse', 3000),
('Pie de limón', 'Pie refrescante con relleno de limón', 'Refrigerado, consumir en 48 horas', 3000),
('Torta chilena', 'Torta tradicional con capas de bizcocho y manjar', 'Ambiente fresco, consumir en 3 días', 3000),
('Pavlova', 'Merengue crujiente con crema y frutas frescas', 'Consumir inmediatamente', 3000),

-- Macarons
('Caja de macarons', 'Selección de macarons surtidos', 'Ambiente fresco, consumir en 5 días', 8000),
('Caja de donas', 'Donas glaseadas y con toppings variados', 'Consumir en 24 horas', 6000),
('Caja de brownies', 'Brownies de chocolate intenso', 'Ambiente fresco, consumir en 4 días', 7000),

-- Galletas
('Galletas personalizadas', 'Galletas decoradas según tu diseño', 'Consumir en 7 días', 5000),
('Galletas corazón', 'Galletas en forma de corazón con decoración', 'Consumir en 10 días', 3000),
('Brookies', 'Combinación de brownie y cookie', 'Consumir en 5 días', 5000),

-- Pasteles
('Pasteles clásicos', 'Selección de pasteles tradicionales', 'Refrigerado, consumir en 3 días', 13000),
('Pasteles personalizados', 'Pasteles decorados para ocasiones especiales', 'Refrigerado, consumir en 3 días', 15000),
('Pasteles fríos', 'Pasteles refrigerados con cremas frescas', 'Mantener refrigerado, consumir en 2 días', 12500),
('Lunch cake', 'Porción individual para lunch', 'Consumir en 24 horas', 10000);

-- Insertar tamaños para los productos (ejemplo)
INSERT INTO producto_tamano (id_producto, etiqueta) VALUES
(1, 'Individual'), (1, 'Familiar'),
(2, 'Individual'), (2, 'Familiar'),
(3, 'Mediano'), (3, 'Grande'),
(4, 'Individual'), (4, 'Mediano'),
(5, 'Pequeña (6u)'), (5, 'Grande (12u)'),
(6, 'Pequeña (6u)'), (6, 'Grande (12u)'),
(7, 'Pequeña (9u)'), (7, 'Grande (16u)'),
(8, 'Docena'), (8, 'Media docena'),
(9, 'Docena'), (9, 'Media docena'),
(10, 'Individual'), (10, 'Caja (4u)'),
(11, 'Pequeño'), (11, 'Mediano'), (11, 'Grande'),
(12, 'Pequeño'), (12, 'Mediano'), (12, 'Grande'),
(13, 'Individual'), (13, 'Mediano'),
(14, 'Individual');

-- Insertar sabores (ejemplo)
INSERT INTO producto_sabor (id_producto, etiqueta) VALUES
(1, 'Manzana canela'), (1, 'Manzana caramelo'),
(2, 'Limón natural'), (2, 'Limón merengue'),
(3, 'Vainilla'), (3, 'Chocolate'),
(4, 'Frutas tropicales'), (4, 'Frutas rojas'),
(5, 'Surtido'), (5, 'Vainilla'), (5, 'Chocolate'),
(6, 'Glaseado'), (6, 'Chocolate'), (6, 'Azúcar'),
(7, 'Chocolate'), (7, 'Nuez'), (7, 'Chips'),
(8, 'Vainilla'), (8, 'Chocolate'), (8, 'Limon'),
(9, 'Vainilla'), (9, 'Chocolate'),
(10, 'Chocolate'), (10, 'Vainilla'),
(11, 'Vainilla'), (11, 'Chocolate'), (11, 'Red Velvet'),
(12, 'Vainilla'), (12, 'Chocolate'), (12, 'Fresa'),
(13, 'Tres leches'), (13, 'Chocolate'), (13, 'Vainilla'),
(14, 'Vainilla'), (14, 'Chocolate');

-- Insertar ingredientes (ejemplo)
INSERT INTO producto_ingrediente (id_producto, nombre) VALUES
-- Pies y tortas
(1, 'Manzana'), (1, 'Canela'), (1, 'Harina'), (1, 'Mantequilla'),
(2, 'Limón'), (2, 'Azúcar'), (2, 'Harina'), (2, 'Huevo'),
(3, 'Harina'), (3, 'Manjar'), (3, 'Merengue'), (3, 'Azúcar'),
(4, 'Merengue'), (4, 'Crema'), (4, 'Frutas frescas'), (4, 'Azúcar');
-- Macarons 
INSERT INTO producto_ingrediente (id_producto, nombre) VALUES
(5, 'Almendra molida'), (5, 'Azúcar glass'), (5, 'Clara de huevo'), 
(5, 'Colorantes naturales'), (5, 'Relleno de mantequilla');

-- Donas
INSERT INTO producto_ingrediente (id_producto, nombre) VALUES
(6, 'Harina de trigo'), (6, 'Levadura'), (6, 'Azúcar'), 
(6, 'Huevo'), (6, 'Mantequilla'), (6, 'Glaseado variado');

-- Brownies 
INSERT INTO producto_ingrediente (id_producto, nombre) VALUES
(7, 'Chocolate negro'), (7, 'Mantequilla'), (7, 'Azúcar'), 
(7, 'Huevo'), (7, 'Harina'), (7, 'Nueces');

-- Galletas personalizadas 
INSERT INTO producto_ingrediente (id_producto, nombre) VALUES
(8, 'Harina de trigo'), (8, 'Mantequilla'), (8, 'Azúcar'), 
(8, 'Huevo'), (8, 'Esencia de vainilla'), (8, 'Colorantes comestibles');

-- Galletas corazón 
INSERT INTO producto_ingrediente (id_producto, nombre) VALUES
(9, 'Harina de trigo'), (9, 'Mantequilla'), (9, 'Azúcar'), 
(9, 'Huevo'), (9, 'Esencia de vainilla'), (9, 'Decoración en azúcar');

-- Brookies 
INSERT INTO producto_ingrediente (id_producto, nombre) VALUES
(10, 'Harina de trigo'), (10, 'Chocolate'), (10, 'Mantequilla'), 
(10, 'Azúcar'), (10, 'Huevo'), (10, 'Chips de chocolate');

-- Pasteles clásicos 
INSERT INTO producto_ingrediente (id_producto, nombre) VALUES
(11, 'Harina de trigo'), (11, 'Azúcar'), (11, 'Huevo'), 
(11, 'Mantequilla'), (11, 'Leche'), (11, 'Levadura');

-- Pasteles personalizados 
INSERT INTO producto_ingrediente (id_producto, nombre) VALUES
(12, 'Harina de trigo'), (12, 'Azúcar'), (12, 'Huevo'), 
(12, 'Mantequilla'), (12, 'Fondant'), (12, 'Colorantes comestibles');

-- Pasteles fríos 
INSERT INTO producto_ingrediente (id_producto, nombre) VALUES
(13, 'Crema para batir'), (13, 'Gelatina'), (13, 'Fruta fresca'), 
(13, 'Bizcocho'), (13, 'Azúcar'), (13, 'Leche condensada');

-- Lunch cake 
INSERT INTO producto_ingrediente (id_producto, nombre) VALUES
(14, 'Harina de trigo'), (14, 'Azúcar'), (14, 'Huevo'), 
(14, 'Mantequilla'), (14, 'Leche'), (14, 'Esencia de vainilla');

-- Agregar campo stock a la tabla producto
ALTER TABLE producto ADD COLUMN stock INT NOT NULL DEFAULT 0;

-- Actualizar stocks de ejemplo (algunos agotados para prueba)
UPDATE producto SET stock = 10 WHERE id_producto = 1;  -- Pie de manzana
UPDATE producto SET stock = 0 WHERE id_producto = 2;  -- Pie de limón (AGOTADO)
UPDATE producto SET stock = 5 WHERE id_producto = 3;  -- Torta chilena
UPDATE producto SET stock = 0 WHERE id_producto = 4;  -- Pavlova (AGOTADO)
UPDATE producto SET stock = 15 WHERE id_producto = 5; -- Caja de macarons
UPDATE producto SET stock = 0 WHERE id_producto = 6;  -- Caja de donas (AGOTADO)
UPDATE producto SET stock = 8 WHERE id_producto = 7;  -- Caja de brownies
UPDATE producto SET stock = 20 WHERE id_producto = 8; -- Galletas personalizadas
UPDATE producto SET stock = 0 WHERE id_producto = 9;  -- Galletas corazón (AGOTADO)
UPDATE producto SET stock = 12 WHERE id_producto = 10; -- Brookies
UPDATE producto SET stock = 6 WHERE id_producto = 11; -- Pasteles clásicos
UPDATE producto SET stock = 0 WHERE id_producto = 12; -- Pasteles personalizados (AGOTADO)
UPDATE producto SET stock = 10 WHERE id_producto = 13; -- Pasteles fríos
UPDATE producto SET stock = 0 WHERE id_producto = 14; -- Lunch cake (AGOTADO)

-- Tablas para pedidos personalizados
create table opcion_pedido (
  id_opcion          bigint unsigned not null auto_increment,
  categoria          varchar(50)     not null,  -- 'producto', 'sabor_bizcocho', 'sabor_relleno', 'tamano'
  valor              varchar(120)    not null,  -- 'Pastel', 'Chocolate', etc.
  descripcion        text            null,
  precio_adicional   decimal(10,2)   not null default 0.00,
  activo             boolean         not null default true,
  fecha_creacion     timestamp       not null default current_timestamp,
  fecha_modificacion timestamp       not null default current_timestamp on update current_timestamp,
  primary key (id_opcion),
  unique key uq_opcion_categoria_valor (categoria, valor)
) engine=InnoDB;

create table pedido_personalizado (
  id_pedido          bigint unsigned not null auto_increment,
  producto           varchar(120)    not null,
  sabor_bizcocho     varchar(120)    not null,
  sabor_relleno      varchar(120)    not null,
  tamano             varchar(120)    not null,
  personalizacion    text            null,
  precio             decimal(10,2)   not null default 0.00,
  estado             varchar(50)     not null default 'pendiente', -- 'pendiente', 'confirmado', 'en_proceso', 'completado', 'cancelado'
  fecha_creacion     timestamp       not null default current_timestamp,
  fecha_entrega      datetime        null,
  id_usuario         bigint unsigned null,
  nombre_cliente     varchar(120)    not null,
  telefono           varchar(20)     not null,
  email              varchar(190)    not null,
  primary key (id_pedido),
  constraint fk_pedido_usuario
    foreign key (id_usuario) references usuario(id_usuario)
    on delete set null on update cascade,
  key ix_pedido_estado (estado),
  key ix_pedido_fecha_creacion (fecha_creacion)
) engine=InnoDB;

-- Insertar opciones para pedidos personalizados (precios en colones)
INSERT INTO opcion_pedido (categoria, valor, descripcion, precio_adicional, activo) VALUES
-- Productos
('producto', 'Pastel', 'Pastel tradicional horneado', 0.00, true),
('producto', 'Galleta', 'Galletas personalizadas', 0.00, true),
('producto', 'Lunch Cake', 'Lunch Cake especial', 0.00, true),
('producto', 'Pastel Frio', 'Pastel refrigerado', 2500.00, true),

-- Sabores de bizcocho
('sabor_bizcocho', 'Vainilla', 'Bizcocho clásico de vainilla', 0.00, true),
('sabor_bizcocho', 'Chocolate', 'Bizcocho intenso de chocolate', 1000.00, true),
('sabor_bizcocho', 'Red Velvet', 'Bizcocho rojo aterciopelado', 1500.00, true),
('sabor_bizcocho', 'Lemon Pound', 'Bizcocho denso de limón', 1200.00, true),
('sabor_bizcocho', 'Zanahoria', 'Bizcocho húmedo de zanahoria con nueces', 1500.00, true),

-- Sabores de relleno
('sabor_relleno', 'Leche condensada', 'Relleno dulce de leche condensada', 0.00, true),
('sabor_relleno', 'Leche condensada cocinada', 'Manjar casero cocido', 1000.00, true),
('sabor_relleno', 'Buttercream', 'Crema de mantequilla suave', 1500.00, true),
('sabor_relleno', 'Chocolate', 'Relleno cremoso de chocolate', 1200.00, true),
('sabor_relleno', 'Mermelada Fresa', 'Mermelada natural de fresa', 1000.00, true),
('sabor_relleno', 'Frutas', 'Relleno con frutas frescas de temporada', 2000.00, true),

-- Tamaños
('tamano', 'Pequeño', 'Para 4-6 personas', 7500.00, true),
('tamano', 'Mediano', 'Para 8-10 personas', 12500.00, true),
('tamano', 'Grande', 'Para 12-15 personas', 17500.00, true),
('tamano', 'Familiar', 'Para 20+ personas', 25000.00, true);

-- Crear índices para mejor performance
CREATE INDEX ix_opcion_pedido_categoria_activo ON opcion_pedido(categoria, activo);
CREATE INDEX ix_pedido_personalizado_email ON pedido_personalizado(email);
CREATE INDEX ix_pedido_personalizado_fecha_entrega ON pedido_personalizado(fecha_entrega);

-- ... (Todo tu script anterior está bien hasta llegar a la parte de las tablas nuevas) ...

USE pasteleria;

-- 1. Crear tabla Pedido (Corregido a UNSIGNED para consistencia)
CREATE TABLE pedido (
    id_pedido BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED, -- Debe ser UNSIGNED para coincidir con la tabla 'usuario'
    fecha DATETIME(6),
    total DOUBLE,
    metodo_pago VARCHAR(255),
    estado VARCHAR(255),
    -- Opcional: Ya que estamos, agregamos la relación real con usuario
    CONSTRAINT fk_pedido_usuario_real FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

-- 2. Crear tabla Detalle Pedido (Corregido IDs a UNSIGNED)
CREATE TABLE detalle_pedido (
    id_detalle BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    cantidad INT,
    precio_unitario DOUBLE,
    id_pedido BIGINT UNSIGNED,   -- UNSIGNED para coincidir con tabla pedido
    id_producto BIGINT UNSIGNED, -- UNSIGNED para coincidir con tabla producto
    CONSTRAINT fk_detalle_pedido FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
) ENGINE=InnoDB;

USE pasteleria;

INSERT INTO usuario (nombre, correo, password_hash, activo) 
VALUES ('Cliente de Prueba', 'prueba@gmail.com', 'password123', 1);


USE pasteleria;
SELECT id_usuario, nombre, correo, activo, fecha_creacion
FROM usuario
ORDER BY id_usuario DESC
LIMIT 5;