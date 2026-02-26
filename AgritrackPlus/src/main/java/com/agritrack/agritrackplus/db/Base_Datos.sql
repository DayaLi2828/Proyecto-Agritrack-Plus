DROP SCHEMA IF EXISTS AgritrackPlus;

CREATE DATABASE AgritrackPlus;
USE AgritrackPlus;

-- ===================== USUARIOS =====================
CREATE TABLE usuarios (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  pass VARCHAR(50) NOT NULL UNIQUE,
  documento VARCHAR(20) NOT NULL,
  direccion VARCHAR(50) NOT NULL UNIQUE,
  estado VARCHAR(20) NOT NULL,
  foto VARCHAR(255) NULL
);
INSERT INTO usuarios (nombre, pass, documento, direccion, estado) VALUES
('Maria Lopez', MD5('test123'), '10957456981', 'Bucaramanga', 'Activo');
SELECT * FROM usuarios;

-- =============== FOTO USUARIO ====================
CREATE TABLE fotos_usuario (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  usuario_id INT NOT NULL,
  ruta VARCHAR(255) NOT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
SELECT * FROM fotos_usuario;
-- ===================== CORREO =====================
CREATE TABLE correo (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  correo VARCHAR(50) NOT NULL UNIQUE,
  usuario_id INT NOT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
INSERT INTO correo (correo, usuario_id) VALUES ('lisarazodayana84@gmail.com', 1);
SELECT * FROM correo;

-- ===================== TELEFONO =====================
CREATE TABLE telefono (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  telefono VARCHAR(50) NOT NULL UNIQUE,
  usuario_id INT NOT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
INSERT INTO telefono (telefono, usuario_id) VALUES ('3001234567', 1);
SELECT * FROM telefono;

-- ===================== ROLES =====================
CREATE TABLE roles (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL
);
INSERT INTO roles (nombre) VALUES
('superAdmin'),
('administrador'),
('trabajador');
SELECT * FROM roles;

-- ===================== ROLES_USUARIOS =====================
CREATE TABLE roles_usuarios (
  id_rol_usuario INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  usuario_id INT NOT NULL,
  rol_id INT NOT NULL,
  FOREIGN KEY (rol_id) REFERENCES roles(id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
INSERT INTO roles_usuarios (usuario_id, rol_id) VALUES (1, 2);
SELECT * FROM roles_usuarios;

-- ===================== PERMISOS =====================
CREATE TABLE permisos (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL
);

-- ===================== PERMISOS_ROLES =====================
CREATE TABLE permisos_roles (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  permiso_id INT NOT NULL,
  rol_id INT NOT NULL,
  FOREIGN KEY (rol_id) REFERENCES roles(id),
  FOREIGN KEY (permiso_id) REFERENCES permisos(id)
);

-- ===================== CULTIVOS =====================
CREATE TABLE cultivos (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  fecha_siembra DATE NOT NULL,
  fecha_cosecha DATE NULL,
  ciclo VARCHAR(50) NOT NULL,
  estado ENUM('Activo', 'Inactivo') NOT NULL DEFAULT 'Activo'
);
SELECT * FROM cultivos;

-- =================== CULTIVO SUPERVISOR ==============
CREATE TABLE cultivo_trabajador (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  cultivo_id INT NOT NULL,
  usuario_id INT NOT NULL,
  FOREIGN KEY (cultivo_id) REFERENCES cultivos(id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
SELECT * FROM cultivo_trabajador;

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
  fecha_vencimiento DATE NULL,
  estado VARCHAR(50) NOT NULL,
  tipo_producto_id INT NOT NULL,
  cantidad INT NOT NULL DEFAULT 1,
  FOREIGN KEY (tipo_producto_id) REFERENCES tipo_producto(id)
);
SELECT * FROM productos;

-- ===================== STOCK_CULTIVO =====================
CREATE TABLE stock_cultivo (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  cultivo_id INT NOT NULL,
  producto_id INT NOT NULL,
  cantidad INT NOT NULL DEFAULT 1,
  FOREIGN KEY (cultivo_id) REFERENCES cultivos(id),
  FOREIGN KEY (producto_id) REFERENCES productos(id)
);
SELECT * FROM stock_cultivo;

-- ===================== SUPERVISOR =====================
CREATE TABLE supervisor (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  descripcion_trabajo VARCHAR(255) NULL,
  nombre_cargo VARCHAR(50) NULL,
  usuario_id INT NULL,
  cultivo_id INT NOT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
  FOREIGN KEY (cultivo_id) REFERENCES cultivos(id)
);
SELECT * FROM supervisor;

-- ===================== PAGOS =====================
CREATE TABLE pagos (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  usuario_id INT NOT NULL,
  fecha_pago DATE NOT NULL,
  estado ENUM('Activo', 'No activo') NOT NULL,
  pago DOUBLE NOT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
SELECT * FROM pagos;

-- ===================== TAREAS =====================
CREATE TABLE tareas (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL
);
SELECT * FROM tareas;

-- ===================== USUARIO_TAREA =====================
CREATE TABLE usuario_tarea (
  cultivo_id INT NOT NULL,
  descripcion_actividad VARCHAR(255) NOT NULL,
  tarea_id INT NOT NULL,
  jornada VARCHAR(50) NOT NULL,
  FOREIGN KEY (cultivo_id) REFERENCES cultivos(id),
  FOREIGN KEY (tarea_id) REFERENCES tareas(id)
);
SELECT * FROM usuario_tarea;