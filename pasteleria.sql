-- Sección de administración
drop database if exists pasteleria;
drop user if exists 'usuario_pasteleria'@'%';
drop user if exists 'usuario_reportes'@'%';


-- Creación del esquema
create database pasteleria
  default character set utf8mb4
  default collate utf8mb4_unicode_ci;


-- Usuarios de BD (en prod: contraseñas por variable de entorno)
create user 'usuario_pasteleria'@'%' identified by 'Pasteles_Seguro.';
create user 'usuario_reportes'@'%'   identified by 'Pasteles_Reportes.';

-- Permisos (principio de mínimo privilegio)
grant select, insert, update, delete on pasteleria.* to 'usuario_pasteleria'@'%';
grant select on pasteleria.* to 'usuario_reportes'@'%';
flush privileges;

use pasteleria;


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


-- Datos de ejemplo mínimos
-- Productos de muestra para probar
insert into producto (nombre, descripcion, conservacion) values
('Pastel de Vainilla', 'Bizcocho suave con cobertura ligera', 'Refrigerado, consumir en 48 horas'),
('Cheesecake de Fresa', 'Clásico cheesecake con topping', 'Mantener refrigerado hasta 72 horas');

insert into producto_tamano (id_producto, etiqueta) values
(1,'Pequeño'), (1,'Mediano'), (1,'Grande'),
(2,'Individual'), (2,'Mediano');

insert into producto_sabor (id_producto, etiqueta) values
(1,'Vainilla'), (1,'Chocolate'),
(2,'Fresa'), (2,'Frutos rojos');

insert into producto_ingrediente (id_producto, nombre) values
(1,'Harina'), (1,'Huevo'), (1,'Leche'), (1,'Azúcar'),
(2,'Queso crema'), (2,'Galleta'), (2,'Mantequilla'), (2,'Fresa');