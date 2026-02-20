DROP SCHEMA IF EXISTS Agritrackplus;

CREATE DATABASE AgritrackPlus;
USE AgritrackPlus;

-- ===================== USUARIOS =====================
CREATE TABLE usuarios (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  pass varchar(50) NOT NULL,
  documento VARCHAR(20) NOT NULL,
  direccion VARCHAR(50) NOT NULL,
  estado VARCHAR(20) NOT NULL
);
INSERT INTO usuarios (nombre, pass, documento, direccion, estado) VALUES
("Maria Lopez","test123", "10957456981", "Bucaramanga", "Activo");
SELECT * FROM usuarios;
-- ===================== CORREO =====================
CREATE TABLE correo (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  correo VARCHAR(50) NOT NULL,
  usuario_id INT NOT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);
INSERT INTO correo (correo, usuario_id) VALUES ('lisarazodayana84@gmail.com', 1);
SELECT * FROM correo;
-- ===================== TELEFONO =====================
CREATE TABLE telefono (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  telefono VARCHAR(50) NOT NULL,
  usuario_id INT NOT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);
INSERT INTO telefono (telefono, usuario_id) VALUES ('3001234567', 1);
SELECT * FROM telefono;
-- ===================== CULTIVOS =====================
CREATE TABLE cultivos (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  fecha_siembra DATE NOT NULL,
  fecha_cosecha DATE NOT NULL,
  ciclo VARCHAR(50) NOT NULL,
  estado ENUM('Activo', 'Inactivo') NOT NULL DEFAULT 'Activo'
);
INSERT INTO cultivos (nombre, fecha_siembra, fecha_cosecha, ciclo, estado) VALUES
('Tomate Cherry', '2024-01-15', '2024-04-15', 'Vegetativo', 'Activo'),
('Aguacate Criollo', '2024-02-01', '2024-08-01', 'Floracion', 'Activo'),
('Piña Oromiel', '2024-03-10', '2024-09-10', 'Maduracion', 'Activo'),
('Mango Tommy', '2024-04-05', '2024-10-05', 'Vegetativo', 'Inactivo');
SELECT * FROM cultivos;

-- ===================== PAGOS =====================
CREATE TABLE pagos (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  usuario_id INT NOT NULL,
  fecha_pago DATE NOT NULL,
  estado ENUM('Activo', 'No activo') NOT NULL,
  pago DOUBLE NOT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

-- ===================== PERMISOS =====================
CREATE TABLE permisos (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL
);

-- ===================== ROLES =====================
CREATE TABLE roles (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL
);
INSERT INTO roles (nombre) VALUES
('superAdmin'),
('administrador'),
('usuario');
SELECT * FROM roles;

-- ===================== PERMISOS_ROLES =====================
CREATE TABLE permisos_roles (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  permiso_id INT NOT NULL,
  rol_id INT NOT NULL,
  FOREIGN KEY (rol_id) REFERENCES roles (id),
  FOREIGN KEY (permiso_id) REFERENCES permisos (id)
);

-- ===================== ROLES_USUARIOS =====================
CREATE TABLE roles_usuarios (
  id_rol_usuario INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  usuario_id INT NOT NULL,
  rol_id INT NOT NULL,
  FOREIGN KEY (rol_id) REFERENCES roles (id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);
INSERT INTO roles_usuarios (usuario_id, rol_id) VALUES (1, 2);

-- ===================== TIPO_PRODUCTO =====================
CREATE TABLE tipo_producto (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  tipo_nombre VARCHAR(50) NOT NULL
);
INSERT INTO tipo_producto (tipo_nombre) VALUES
('Fertilizante'),
('Herramienta'),
('Semilla'),
('Pesticida');
SELECT * FROM tipo_producto;

-- ===================== PRODUCTOS =====================
CREATE TABLE productos (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  unidad_medida DOUBLE NOT NULL,
  precio DOUBLE NOT NULL,
  fecha_compra DATE NOT NULL,
  fecha_vencimiento DATE NOT NULL,
  estado VARCHAR(50) NOT NULL,
  tipo_producto_id INT NOT NULL,
  FOREIGN KEY (tipo_producto_id) REFERENCES tipo_producto (id)
);

-- ===================== STOCK_CULTIVO =====================
CREATE TABLE stock_cultivo (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  cultivo_id INT NOT NULL,
  FOREIGN KEY (cultivo_id) REFERENCES cultivos (id)
);

-- ===================== STOCK_DETALLE_CULTIVO =====================
CREATE TABLE stock_detalle_cultivo (
  stock_cultivo_id INT NOT NULL,
  producto_id INT NOT NULL,
  cantidad_producto DECIMAL(10,0) NOT NULL,
  FOREIGN KEY (stock_cultivo_id) REFERENCES stock_cultivo (id),
  FOREIGN KEY (producto_id) REFERENCES productos (id)
);

-- ===================== SUPERVISOR =====================
CREATE TABLE supervisor (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  descripcion_trabajo VARCHAR(255) NOT NULL,
  nombre_cargo VARCHAR(50) NOT NULL,
  trabajador_id INT NOT NULL,
  cultivo_id INT NOT NULL,
  FOREIGN KEY (trabajador_id) REFERENCES usuarios (id),
  FOREIGN KEY (cultivo_id) REFERENCES cultivos (id)
);

-- ===================== TAREAS =====================
CREATE TABLE tareas (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL
);

-- ===================== USUARIO_TAREA =====================
CREATE TABLE usuario_tarea (
  cultivo_id INT NOT NULL,
  descripcion_actividad VARCHAR(255) NOT NULL,
  tarea_id INT NOT NULL,
  jornada VARCHAR(50) NOT NULL,
  FOREIGN KEY (cultivo_id) REFERENCES cultivos (id),
  FOREIGN KEY (tarea_id) REFERENCES tareas (id)
);