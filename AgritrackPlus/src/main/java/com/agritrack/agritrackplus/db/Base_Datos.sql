DROP SCHEMA IF EXISTS AgritrackPlus;

CREATE DATABASE AgritrackPlus;
USE AgritrackPlus;

-- ===================== USUARIOS =====================
CREATE TABLE usuarios (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  pass VARCHAR(50) NOT NULL,
  documento VARCHAR(20) NOT NULL UNIQUE,
  direccion VARCHAR(50) NOT NULL,
  estado VARCHAR(20) NOT NULL
);
INSERT INTO usuarios (nombre, pass, documento, direccion, estado) VALUES
( 'Dayana Lisarazo', MD5('Admin2026*'), '1095745698', 'Calle 36 #12-05, Bucaramanga', 'Activo'),
( 'Ana Maria Gomez', MD5('An4.Gom3z#9'), '1098765432', 'Carrera 27 #54-10, Floridablanca', 'Activo'),
( 'Juan David Perez', MD5('Jperez_88*Q'), '1050403020', 'Av. Quebradaseca #15-20, Bucaramanga', 'Activo'),
( 'Lucia Fernanda Diaz', MD5('LuCi4$2024&'), '1010203045', 'Calle 105 #22-14, Girón', 'Activo'),
( 'Luis Eduardo Mora', MD5('Lucho#M0r4!'), '1092345678', 'Carrera 15 #33-45, Piedecuesta', 'Activo'),
( 'Elena Sofia Sanz', MD5('Elen4.Sanz*'), '1088776655', 'Calle 56 #9-12, Bucaramanga', 'Activo'),
( 'Pedro Jose Gil', MD5('P3dro_Gil$2'), '1077665544', 'Trans. 39 #10-50, Floridablanca', 'Activo'),
( 'Rosa Elena Meza', MD5('R0s4_M3z4#9'), '1066554433', 'Calle 20 #18-05, Lebrija', 'Activo'),
( 'Jose Antonio Toro', MD5('J0se*Toro!2'), '1055443322', 'Carrera 9 #45-67, Bucaramanga', 'Activo'),
( 'Clara Ines Luz', MD5('Cl4r4.Luz*2'), '1044332211', 'Calle 12 #14-16, Girón', 'Activo'),
( 'Hugo Armando Paz', MD5('Hug0_Paz$20'), '1033221100', 'Carrera 33 #52-10, Bucaramanga', 'Activo'),
( 'Nora Cecilia Rey', MD5('Nor4#R3y*20'), '1022110099', 'Calle 200 #12-44, Floridablanca', 'Activo'),
( 'Ivan Dario Rios', MD5('Iv4n.Rios!8'), '1011009988', 'Av. Los Samanes #4-12, Bucaramanga', 'Activo'),
( 'Sara Isabel Lee', MD5('S4r4_Lee*99'), '1000998877', 'Carrera 12 #10-30, Piedecuesta', 'Activo'),
( 'Beto Alejandro Cue', MD5('B3to_Cue$20'), '1099887766', 'Calle 5 #8-10, Zapatoca', 'Activo');
SELECT * FROM usuarios;

-- =============== FOTO USUARIO ====================
CREATE TABLE fotos_usuario (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  usuario_id INT NOT NULL UNIQUE,
  ruta VARCHAR(255) NOT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
INSERT INTO fotos_usuario (usuario_id, ruta) VALUES
(1, 'perfil_dayana.jpg'), (2, 'perfil_ana.jpg'), (3, 'perfil_juan.jpg'),
(4, 'perfil_lucia.jpg'), (5, 'perfil_luis.jpg'), (6, 'perfil_elena.jpg'),
(7, 'perfil_pedro.jpg'), (8, 'perfil_rosa.jpg'), (9, 'perfil_jose.jpg'),
(10, 'perfil_clara.jpg'), (11, 'perfil_hugo.jpg'), (12, 'perfil_nora.jpg'),
(13, 'perfil_ivan.jpg'), (14, 'perfil_sara.jpg'), (15, 'perfil_beto.jpg');
SELECT * FROM fotos_usuario;

-- ===================== CORREO =====================
CREATE TABLE correo (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(50) NOT NULL UNIQUE,
  usuario_id INT NOT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
INSERT INTO correo (email, usuario_id)  VALUES
('lisarazodayana84@gmail.com', 1),
('am.gomez@gmail.com', 2),
('j.perez_dav@gmail.com', 3),
('lucia.diaz.f@gmail.com', 4),
('lucho.mora@gmail.com', 5),
('elena.sanz.s@gmail.com', 6),
('pj.gil_campos@gmail.com', 7),
('rosa.meza.elena@gmail.com', 8),
('jose.toro.ant@gmail.com', 9),
('clara.ines.luz@gmail.com', 10),
('hugo.paz.armando@gmail.com', 11),
('nora.rey.cecilia@gmail.com', 12),
('ivan.rios.d@gmail.com', 13),
('sara.lee.isabel@gmail.com', 14),
('beto.cue.alejo@gmail.com', 15);
SELECT * FROM correo;
-- ===================== TELEFONO =====================
CREATE TABLE telefono (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  numero VARCHAR(50) NOT NULL,
  usuario_id INT NOT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
INSERT INTO telefono (numero, usuario_id) VALUES
('3001234567', 1),
('3104758291', 2),
('3158294031', 3),
('3209182736', 4),
('3127364510', 5),
('3182930415', 6),
('3115647382', 7),
('3149028173', 8),
('3138475629', 9),
('3172635401', 10),
('3216549870', 11),
('3164758293', 12),
('3109283746', 13),
('3157463521', 14),
('3228374650', 15);
SELECT * FROM telefono;

-- ===================== ROLES =====================
CREATE TABLE roles (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL
);
INSERT INTO roles (nombre) VALUES
('administrador'),
('Trabajador'),
('supervisor'),
('superAdmin');
SELECT * FROM roles;

-- ===================== ROLES_USUARIOS =====================
CREATE TABLE roles_usuarios (
  id_rol_usuario INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  usuario_id INT NOT NULL,
  rol_id INT NOT NULL,
  FOREIGN KEY (rol_id) REFERENCES roles(id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
INSERT INTO roles_usuarios (usuario_id, rol_id) VALUES
(1, 1), (2, 2), (3, 2), (4, 3), (5, 2),
(6, 2), (7, 3), (8, 2), (9, 2), (10, 2),
(11, 2), (12, 3), (13, 2), (14, 2), (15, 3);
SELECT * FROM roles_usuarios;

-- ===================== PERMISOS =====================
CREATE TABLE permisos (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL
);
INSERT INTO permisos (nombre) VALUES
('VER_USUARIOS'), ('CREAR_USUARIOS'), ('EDITAR_USUARIOS'),
('VER_PRODUCTOS'), ('CREAR_PRODUCTOS'), ('EDITAR_PRODUCTOS'),
('VER_CULTIVOS'), ('CREAR_CULTIVOS'), ('EDITAR_CULTIVOS'),
('CREAR_TAREAS'), ('VER_TAREAS_PENDIENTES'), ('COMPLETAR_TAREAS'), ('VER_HISTORIAL_TAREAS');
SELECT * FROM permisos;
-- ===================== PERMISOS_ROLES =====================
CREATE TABLE permisos_roles (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  permiso_id INT NOT NULL,
  rol_id INT NOT NULL,
  FOREIGN KEY (rol_id) REFERENCES roles(id),
  FOREIGN KEY (permiso_id) REFERENCES permisos(id)
);
-- 1. Asignar TODO al Administrador (Rol 1)
INSERT INTO permisos_roles (permiso_id, rol_id)
SELECT id, 1 FROM permisos;

-- 2. Asignar permisos específicos al SUPERVISOR (Rol 2)
INSERT INTO permisos_roles (permiso_id, rol_id) VALUES
(1, 2), -- Ver usuarios
(4, 2), -- Ver productos
(7, 2), -- Ver cultivos
(10, 2), -- Crear tareas
(11, 2), -- Ver tareas (él también debe verlas para supervisar)
(13, 2); -- Ver historial

-- 3. Asignar permisos específicos al TRABAJADOR (Rol 3)
INSERT INTO permisos_roles (permiso_id, rol_id) VALUES
(11, 3), -- Ver tareas pendientes
(12, 3), -- Completar tareas
(13, 3); -- Ver historial de sus propias tareas
SELECT * FROM permisos_roles;
-- ===================== CULTIVOS =====================
CREATE TABLE cultivos (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  fecha_siembra DATE NOT NULL,
  fecha_cosecha DATE NULL,
  ciclo VARCHAR(50) NOT NULL,
  estado VARCHAR(20) DEFAULT 'Activo'
);
INSERT INTO cultivos (nombre, fecha_siembra, fecha_cosecha, ciclo, estado) VALUES
('Tomate Chonto', '2026-02-10', NULL, 'Floración','Activo'),
('Cebolla Cabezona', '2026-01-15', NULL, 'Maduración','Activo'),
('Pimentón Rojo', '2026-02-20', NULL, 'Vegetativo','Activo'),
('Papa Pastusa', '2026-01-05', NULL, 'Maduración','Activo'),
('Zanahoria Nantes', '2026-02-25', NULL, 'Vegetativo','Activo'),
('Lechuga Batavia', '2026-02-28', NULL, 'Vegetativo','Activo'),
('Maíz Amarillo', '2026-01-20', NULL, 'Floración','Activo'),
('Frijol Cargamanto', '2026-02-05', NULL, 'Floración','Activo'),
('Arveja Verde', '2026-02-12', NULL, 'Vegetativo','Activo'),
('Cilantro España', '2026-03-01', NULL, 'Vegetativo','Activo'),
('Pepino Cohombro', '2026-02-18', NULL, 'Floración','Activo'),
('Brócoli de Verano', '2026-01-30', NULL, 'Floración','Activo'),
('Espinaca Baby', '2026-03-02', NULL, 'Vegetativo','Activo'),
('Ajo Rosado', '2025-11-20', NULL, 'Maduración','Activo'),
('Remolacha Roja', '2026-02-08', NULL, 'Vegetativo','Activo');
SELECT * FROM cultivos;

-- =================== CULTIVO TRABAJADOR ==============
CREATE TABLE cultivo_trabajador (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  cultivo_id INT NOT NULL,
  usuario_id INT NOT NULL,
  FOREIGN KEY (cultivo_id) REFERENCES cultivos(id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
INSERT INTO cultivo_trabajador (cultivo_id, usuario_id) VALUES
-- Cultivo 1: Tomate Chonto
(1, 2), (1, 2), (1, 3), 
-- Cultivo 2: Cebolla Cabezona
(2, 4), (2, 6), 
-- Cultivo 3: Pimentón Rojo
(3, 5), (3, 8), 
-- Cultivo 4: Papa Pastusa
(4, 9), (4, 10), 
-- Cultivo 5: Zanahoria Nantes
(5, 11), (5, 7), 
-- Cultivo 6: Lechuga Batavia
(6, 13), (6, 14), 
-- Cultivo 7: Maíz Amarillo
(7, 15), (7, 2), 
-- Cultivo 8: Frijol Cargamanto
(8, 3), (8, 4), 
-- Cultivo 9: Arveja Verde
(9, 5), (9, 6), 
-- Cultivo 10: Cilantro España
(10, 7), (10, 8), 
-- Cultivo 11: Pepino Cohombro
(11, 9), (11, 11), 
-- Cultivo 12: Brócoli de Verano
(12, 12), (12, 13), 
-- Cultivo 13: Espinaca Baby
(13, 14), (13, 15), 
-- Cultivo 14: Ajo Rosado
(14, 1), (14, 2), 
-- Cultivo 15: Remolacha Roja
(15, 3), (15, 4);
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
  unidad_medida VARCHAR (50) NOT NULL,
  precio DOUBLE NOT NULL,
  fecha_compra DATE NOT NULL,
  fecha_vencimiento DATE NULL,
  tipo_producto_id INT NOT NULL,
  cantidad INT NOT NULL DEFAULT 1,
  FOREIGN KEY (tipo_producto_id) REFERENCES tipo_producto(id)
);
INSERT INTO productos (nombre, unidad_medida, precio, fecha_compra, fecha_vencimiento, tipo_producto_id, cantidad) VALUES
( 'Urea 46%', 'Kilogramo', 185000, '2024-01-10', '2026-01-10', 1, 20),
( 'Machete Bellota', 'Unidad', 38000, '2024-01-15', NULL, 2, 12),
( 'Semilla Tomate Híbrido', 'Kilogramo', 125000, '2024-02-01', NULL, 3, 10),
( 'Fungicida Ridomil Gold', 'Litro', 98000, '2024-02-10', '2025-08-10', 4, 8),
( 'Abono Orgánico Compost', 'Kilogramo', 42000, '2024-01-20', '2024-12-20', 1, 40),
( 'Pala Herragro Redonda', 'Unidad', 52000, '2024-01-05', NULL, 2, 6),
( 'Semilla Maíz Dekalb', 'Kilogramo', 480000, '2024-02-15', NULL, 3, 5),
( 'Herbicida Roundup', 'Litro', 215000, '2024-02-20', '2026-02-20', 4, 4),
( 'Fertilizante NPK 15-15-15', 'Kilogramo', 198000, '2024-01-25', '2026-01-25', 1, 15),
( 'Tijeras de Poda Pro', 'Unidad', 72000, '2024-02-05', NULL, 2, 8),
( 'Semilla Cilantro Patriota', 'Kilogramo', 28000, '2024-03-01', NULL, 3, 25),
( 'Insecticida Karate Zeon', 'Litro', 115000, '2024-03-05', '2025-09-05', 4, 10),
( 'Sulfato de Magnesio', 'Kilogramo', 82000, '2024-02-28', '2026-02-28', 1, 10),
( 'Azadón de Acero Forjado', 'Unidad', 45000, '2024-01-12', NULL, 2, 10),
( 'Semilla Frijol Bolón', 'Kilogramo', 68000, '2024-02-18', NULL, 3, 15);
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
INSERT INTO stock_cultivo (cultivo_id, producto_id, cantidad) VALUES
(1, 1, 2), (2, 4, 1), (3, 5, 3), (4, 9, 2), (5, 8, 1),
(6, 12, 1), (7, 13, 2), (8, 2, 4), (9, 6, 2), (10, 14, 2),
(11, 10, 3), (12, 1, 1), (13, 4, 2), (14, 5, 5), (15, 9, 2);
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
INSERT INTO supervisor (descripcion_trabajo, nombre_cargo, usuario_id, cultivo_id) VALUES
('Control de calidad del fruto', 'Supervisor de Zona', 4, 1),
('Revisión de hidratación', 'Líder de Campo', 7, 2),
('Monitoreo de PH suelo', 'Agrónomo Jr', 12, 3),
('Gestión de cuadrilla', 'Capatáz', 15, 4),
('Reporte de crecimiento', 'Supervisor de Zona', 4, 5),
('Control biológico', 'Líder de Campo', 7, 6),
('Supervisión de abono', 'Agrónomo Jr', 12, 7),
('Auditoría de riego', 'Capatáz', 15, 8),
('Inspección de plagas', 'Supervisor de Zona', 4, 9),
('Coordinación de siembra', 'Líder de Campo', 7, 10),
('Control de temperatura', 'Agrónomo Jr', 12, 11),
('Gestión de insumos', 'Capatáz', 15, 12),
('Verificación de ciclos', 'Supervisor de Zona', 4, 13),
('Optimización de cosecha', 'Líder de Campo', 7, 14),
('Mantenimiento preventivo', 'Agrónomo Jr', 12, 15);
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
INSERT INTO pagos (usuario_id, fecha_pago, estado, pago) VALUES
(1, '2026-03-01', 'Activo', 2500000), (2, '2026-03-01', 'Activo', 1200000),
(3, '2026-03-01', 'Activo', 1200000), (4, '2026-03-01', 'Activo', 1800000),
(5, '2026-03-01', 'Activo', 1200000), (6, '2026-03-01', 'Activo', 1200000),
(7, '2026-03-01', 'Activo', 1800000), (8, '2026-03-01', 'Activo', 1200000),
(9, '2026-03-01', 'Activo', 1200000), (10, '2026-03-01', 'Activo', 2500000),
(11, '2026-03-01', 'Activo', 1200000), (12, '2026-03-01', 'Activo', 1800000),
(13, '2026-03-01', 'Activo', 1200000), (14, '2026-03-01', 'Activo', 1200000),
(15, '2026-03-01', 'Activo', 1800000);
SELECT * FROM pagos;

-- ===================== TAREAS =====================
CREATE TABLE tareas (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL
);
INSERT INTO tareas (nombre) VALUES
('Riego Manual'),
('Fumigación Controlada'),
('Poda de Formación'),
('Fertilización Foliar'),
('Control de Malezas'),
('Monitoreo de Plagas'),
('Cosecha Selectiva'),
('Preparación de Terreno'),
('Siembra Directa'),
('Aplicación de Abono'),
('Limpieza de Canales'),
('Tutorado de Plantas'),
('Deshije'),
('Recolección de Residuos'),
('Mantenimiento de Herramientas');
SELECT * FROM tareas;

-- ===================== USUARIO_TAREA =====================
CREATE TABLE usuario_tarea (
id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
cultivo_id INT NOT NULL,
tarea_id INT NOT NULL,                      
usuario_id INT NOT NULL,
descripcion_actividad TEXT NOT NULL,      
jornada VARCHAR(50) NOT NULL,
estado VARCHAR(20) DEFAULT 'Pendiente',    
fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (cultivo_id) REFERENCES cultivos(id),
FOREIGN KEY (tarea_id) REFERENCES tareas(id),
FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
INSERT INTO usuario_tarea (cultivo_id, tarea_id, usuario_id, descripcion_actividad, jornada, estado) VALUES
(1, 1, 6, 'Supervisión inicial del lote tomate', 'Mañana', 'Completada'),
(1, 2, 2, 'Fumigación de plaga blanca', 'Mañana', 'En Proceso'),
(2, 3, 3, 'Poda de mantenimiento cebolla', 'Tarde', 'Pendiente'),
(3, 4, 4, 'Fertilización foliar pimentón', 'Mañana', 'Pendiente'),
(4, 5, 5, 'Limpieza de maleza en papa', 'Tarde', 'En Proceso'),
(5, 6, 6, 'Monitoreo de pulgón en zanahoria', 'Mañana', 'Pendiente'),
(6, 7, 7, 'Cosecha de lechuga batavia', 'Mañana', 'Completada'),
(7, 8, 8, 'Arado manual sector maíz', 'Tarde', 'Pendiente'),
(8, 9, 9, 'Siembra de frijol cargamanto', 'Mañana', 'En Proceso'),
(9, 10, 10, 'Abono orgánico en arveja', 'Tarde', 'Completada'),
(10, 11, 11, 'Mantenimiento de riego cilantro', 'Mañana', 'Pendiente'),
(11, 12, 12, 'Entutorado de pepino', 'Tarde', 'En Proceso'),
(12, 13, 13, 'Deshije de brócoli', 'Mañana', 'Completada'),
(13, 14, 14, 'Recolección de plásticos espinaca', 'Tarde', 'Pendiente'),
(14, 15, 15, 'Afilado de herramienta ajo', 'Mañana', 'Completada'),
-- Tareas adicionales para que algunos tengan más de una
(1, 4, 2, 'Segunda dosis fertilizante', 'Tarde', 'Pendiente'),
(2, 5, 3, 'Control manual de malezas', 'Mañana', 'En Proceso'),
(7, 10, 7, 'Refuerzo de abono en maíz', 'Tarde', 'Pendiente');
SELECT * FROM usuario_tarea;

DESCRIBE telefono;
ALTER TABLE productos ADD COLUMN estado VARCHAR(20) DEFAULT 'Activo';
ALTER TABLE stock_cultivo DROP FOREIGN KEY stock_cultivo_ibfk_2;
ALTER TABLE stock_cultivo
ADD CONSTRAINT fk_stock_producto
FOREIGN KEY (producto_id) REFERENCES productos(id)
ON DELETE CASCADE;
USE AgritrackPlus;

-- Agregamos la columna que falta
ALTER TABLE cultivos ADD COLUMN supervisor_id INT NULL;

-- Creamos la relación de llave foránea
ALTER TABLE cultivos
ADD CONSTRAINT fk_cultivo_supervisor
FOREIGN KEY (supervisor_id) REFERENCES usuarios(id);