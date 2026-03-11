-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: agritrackplus
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `correo`
--

DROP TABLE IF EXISTS `correo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `correo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `usuario_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `correo_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `correo`
--

LOCK TABLES `correo` WRITE;
/*!40000 ALTER TABLE `correo` DISABLE KEYS */;
INSERT INTO `correo` VALUES (1,'lisarazodayana84@gmail.com',1),(2,'am.gomez@agritrack.com',2),(3,'j.perez_dav@agritrack.com',3),(4,'lucia.diaz.f@agritrack.com',4),(5,'lucho.mora@agritrack.com',5),(6,'elena.sanz.s@agritrack.com',6),(7,'pj.gil_campos@agritrack.com',7),(8,'rosa.meza.elena@agritrack.com',8),(9,'jose.toro.ant@agritrack.com',9),(10,'clara.ines.luz@agritrack.com',10),(11,'hugo.paz.armando@agritrack.com',11),(12,'nora.rey.cecilia@agritrack.com',12),(13,'ivan.rios.d@agritrack.com',13),(14,'sara.lee.isabel@agritrack.com',14),(15,'beto.cue.alejo@agritrack.com',15);
/*!40000 ALTER TABLE `correo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cultivo_trabajador`
--

DROP TABLE IF EXISTS `cultivo_trabajador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cultivo_trabajador` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cultivo_id` int NOT NULL,
  `usuario_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `cultivo_id` (`cultivo_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `cultivo_trabajador_ibfk_1` FOREIGN KEY (`cultivo_id`) REFERENCES `cultivos` (`id`),
  CONSTRAINT `cultivo_trabajador_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cultivo_trabajador`
--

LOCK TABLES `cultivo_trabajador` WRITE;
/*!40000 ALTER TABLE `cultivo_trabajador` DISABLE KEYS */;
INSERT INTO `cultivo_trabajador` VALUES (1,1,2),(2,1,3),(3,2,4),(4,3,2),(5,3,5),(6,4,3),(7,4,4),(8,5,5),(9,6,2),(10,7,3),(11,7,4),(12,8,5),(13,9,2),(14,10,3),(15,11,4),(16,12,5),(17,13,2),(18,14,3),(19,15,4);
/*!40000 ALTER TABLE `cultivo_trabajador` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cultivos`
--

DROP TABLE IF EXISTS `cultivos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cultivos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `fecha_siembra` date NOT NULL,
  `fecha_cosecha` date DEFAULT NULL,
  `ciclo` varchar(50) NOT NULL,
  `estado` varchar(20) DEFAULT 'Activo',
  `supervisor_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_cultivo_supervisor` (`supervisor_id`),
  CONSTRAINT `fk_cultivo_supervisor` FOREIGN KEY (`supervisor_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cultivos`
--

LOCK TABLES `cultivos` WRITE;
/*!40000 ALTER TABLE `cultivos` DISABLE KEYS */;
INSERT INTO `cultivos` VALUES (1,'Tomate Chonto','2026-02-10',NULL,'Floración','Activo',NULL),(2,'Cebolla Cabezona','2026-01-15',NULL,'Maduración','Activo',NULL),(3,'Pimentón Rojo','2026-02-20',NULL,'Vegetativo','Activo',NULL),(4,'Papa Pastusa','2026-01-05',NULL,'Maduración','Activo',NULL),(5,'Zanahoria Nantes','2026-02-25',NULL,'Vegetativo','Activo',NULL),(6,'Lechuga Batavia','2026-02-28',NULL,'Vegetativo','Activo',NULL),(7,'Maíz Amarillo','2026-01-20',NULL,'Floración','Activo',NULL),(8,'Frijol Cargamanto','2026-02-05',NULL,'Floración','Activo',NULL),(9,'Arveja Verde','2026-02-12',NULL,'Vegetativo','Activo',NULL),(10,'Cilantro España','2026-03-01',NULL,'Vegetativo','Activo',NULL),(11,'Pepino Cohombro','2026-02-18',NULL,'Floración','Activo',NULL),(12,'Brócoli de Verano','2026-01-30',NULL,'Floración','Activo',NULL),(13,'Espinaca Baby','2026-03-02',NULL,'Vegetativo','Activo',NULL),(14,'Ajo Rosado','2025-11-20',NULL,'Maduración','Activo',NULL),(15,'Remolacha Roja','2026-02-08',NULL,'Vegetativo','Activo',NULL);
/*!40000 ALTER TABLE `cultivos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fotos_usuario`
--

DROP TABLE IF EXISTS `fotos_usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fotos_usuario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `ruta` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `fotos_usuario_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fotos_usuario`
--

LOCK TABLES `fotos_usuario` WRITE;
/*!40000 ALTER TABLE `fotos_usuario` DISABLE KEYS */;
INSERT INTO `fotos_usuario` VALUES (1,1,'perfil_dayana.jpg'),(2,2,'perfil_ana.jpg'),(3,3,'perfil_juan.jpg'),(4,4,'perfil_lucia.jpg'),(5,5,'perfil_luis.jpg'),(6,6,'perfil_elena.jpg'),(7,7,'perfil_pedro.jpg'),(8,8,'perfil_rosa.jpg'),(9,9,'perfil_jose.jpg'),(10,10,'perfil_clara.jpg'),(11,11,'perfil_hugo.jpg'),(12,12,'perfil_nora.jpg'),(13,13,'perfil_ivan.jpg'),(14,14,'perfil_sara.jpg'),(15,15,'perfil_beto.jpg');
/*!40000 ALTER TABLE `fotos_usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pagos`
--

DROP TABLE IF EXISTS `pagos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `fecha_pago` date NOT NULL,
  `estado` enum('Activo','No activo') NOT NULL,
  `pago` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `pagos_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pagos`
--

LOCK TABLES `pagos` WRITE;
/*!40000 ALTER TABLE `pagos` DISABLE KEYS */;
INSERT INTO `pagos` VALUES (1,1,'2026-03-01','Activo',2500000),(2,2,'2026-03-01','Activo',1200000),(3,3,'2026-03-01','Activo',1200000),(4,4,'2026-03-01','Activo',1800000),(5,5,'2026-03-01','Activo',1200000),(6,6,'2026-03-01','Activo',1200000),(7,7,'2026-03-01','Activo',1800000),(8,8,'2026-03-01','Activo',1200000),(9,9,'2026-03-01','Activo',1200000),(10,10,'2026-03-01','Activo',2500000),(11,11,'2026-03-01','Activo',1200000),(12,12,'2026-03-01','Activo',1800000),(13,13,'2026-03-01','Activo',1200000),(14,14,'2026-03-01','Activo',1200000),(15,15,'2026-03-01','Activo',1800000);
/*!40000 ALTER TABLE `pagos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permisos`
--

DROP TABLE IF EXISTS `permisos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permisos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permisos`
--

LOCK TABLES `permisos` WRITE;
/*!40000 ALTER TABLE `permisos` DISABLE KEYS */;
INSERT INTO `permisos` VALUES (1,'VER_USUARIOS'),(2,'CREAR_USUARIOS'),(3,'EDITAR_USUARIOS'),(4,'VER_PRODUCTOS'),(5,'CREAR_PRODUCTOS'),(6,'EDITAR_PRODUCTOS'),(7,'VER_CULTIVOS'),(8,'CREAR_CULTIVOS'),(9,'EDITAR_CULTIVOS'),(10,'CREAR_TAREAS'),(11,'VER_TAREAS_PENDIENTES'),(12,'COMPLETAR_TAREAS'),(13,'VER_HISTORIAL_TAREAS');
/*!40000 ALTER TABLE `permisos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permisos_roles`
--

DROP TABLE IF EXISTS `permisos_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permisos_roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `permiso_id` int NOT NULL,
  `rol_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `rol_id` (`rol_id`),
  KEY `permiso_id` (`permiso_id`),
  CONSTRAINT `permisos_roles_ibfk_1` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `permisos_roles_ibfk_2` FOREIGN KEY (`permiso_id`) REFERENCES `permisos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permisos_roles`
--

LOCK TABLES `permisos_roles` WRITE;
/*!40000 ALTER TABLE `permisos_roles` DISABLE KEYS */;
INSERT INTO `permisos_roles` VALUES (1,1,1),(2,2,1),(3,3,1),(4,4,1),(5,5,1),(6,6,1),(7,7,1),(8,8,1),(9,9,1),(10,10,1),(11,11,1),(12,12,1),(13,13,1),(16,1,2),(17,4,2),(18,7,2),(19,10,2),(20,11,2),(21,13,2),(22,11,3),(23,12,3),(24,13,3);
/*!40000 ALTER TABLE `permisos_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos`
--

DROP TABLE IF EXISTS `productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `unidad_medida` varchar(50) NOT NULL,
  `precio` double NOT NULL,
  `fecha_compra` date NOT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  `tipo_producto_id` int NOT NULL,
  `cantidad` int NOT NULL DEFAULT '1',
  `estado` varchar(20) DEFAULT 'Activo',
  PRIMARY KEY (`id`),
  KEY `tipo_producto_id` (`tipo_producto_id`),
  CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`tipo_producto_id`) REFERENCES `tipo_producto` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos`
--

LOCK TABLES `productos` WRITE;
/*!40000 ALTER TABLE `productos` DISABLE KEYS */;
INSERT INTO `productos` VALUES (1,'Urea 46%','Kilogramo',185000,'2024-01-10','2026-01-10',1,20,'Activo'),(2,'Machete Bellota','Unidad',38000,'2024-01-15',NULL,2,12,'Activo'),(3,'Semilla Tomate Híbrido','Kilogramo',125000,'2024-02-01',NULL,3,10,'Activo'),(4,'Fungicida Ridomil Gold','Litro',98000,'2024-02-10','2025-08-10',4,8,'Activo'),(5,'Abono Orgánico Compost','Kilogramo',42000,'2024-01-20','2024-12-20',1,40,'Activo'),(6,'Pala Herragro Redonda','Unidad',52000,'2024-01-05',NULL,2,6,'Activo'),(7,'Semilla Maíz Dekalb','Kilogramo',480000,'2024-02-15',NULL,3,5,'Activo'),(8,'Herbicida Roundup','Litro',215000,'2024-02-20','2026-02-20',4,4,'Activo'),(9,'Fertilizante NPK 15-15-15','Kilogramo',198000,'2024-01-25','2026-01-25',1,15,'Activo'),(10,'Tijeras de Poda Pro','Unidad',72000,'2024-02-05',NULL,2,8,'Activo'),(11,'Semilla Cilantro Patriota','Kilogramo',28000,'2024-03-01',NULL,3,25,'Activo'),(12,'Insecticida Karate Zeon','Litro',115000,'2024-03-05','2025-09-05',4,10,'Activo'),(13,'Sulfato de Magnesio','Kilogramo',82000,'2024-02-28','2026-02-28',1,10,'Activo'),(14,'Azadón de Acero Forjado','Unidad',45000,'2024-01-12',NULL,2,10,'Activo'),(15,'Semilla Frijol Bolón','Kilogramo',68000,'2024-02-18',NULL,3,15,'Activo');
/*!40000 ALTER TABLE `productos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'administrador'),(2,'Trabajador'),(3,'supervisor'),(4,'superAdmin');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles_usuarios`
--

DROP TABLE IF EXISTS `roles_usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles_usuarios` (
  `id_rol_usuario` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `rol_id` int NOT NULL,
  PRIMARY KEY (`id_rol_usuario`),
  KEY `rol_id` (`rol_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `roles_usuarios_ibfk_1` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `roles_usuarios_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles_usuarios`
--

LOCK TABLES `roles_usuarios` WRITE;
/*!40000 ALTER TABLE `roles_usuarios` DISABLE KEYS */;
INSERT INTO `roles_usuarios` VALUES (1,1,1),(2,2,2),(3,3,2),(4,4,3),(5,5,2),(6,6,2),(7,7,3),(8,8,2),(9,9,2),(10,10,1),(11,11,2),(12,12,3),(13,13,2),(14,14,2),(15,15,3);
/*!40000 ALTER TABLE `roles_usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_cultivo`
--

DROP TABLE IF EXISTS `stock_cultivo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock_cultivo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cultivo_id` int NOT NULL,
  `producto_id` int NOT NULL,
  `cantidad` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `cultivo_id` (`cultivo_id`),
  KEY `fk_stock_producto` (`producto_id`),
  CONSTRAINT `fk_stock_producto` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `stock_cultivo_ibfk_1` FOREIGN KEY (`cultivo_id`) REFERENCES `cultivos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_cultivo`
--

LOCK TABLES `stock_cultivo` WRITE;
/*!40000 ALTER TABLE `stock_cultivo` DISABLE KEYS */;
INSERT INTO `stock_cultivo` VALUES (1,1,1,2),(2,2,4,1),(3,3,5,3),(4,4,9,2),(5,5,8,1),(6,6,12,1),(7,7,13,2),(8,8,2,4),(9,9,6,2),(10,10,14,2),(11,11,10,3),(12,12,1,1),(13,13,4,2),(14,14,5,5),(15,15,9,2);
/*!40000 ALTER TABLE `stock_cultivo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supervisor`
--

DROP TABLE IF EXISTS `supervisor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supervisor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `descripcion_trabajo` varchar(255) DEFAULT NULL,
  `nombre_cargo` varchar(50) DEFAULT NULL,
  `usuario_id` int DEFAULT NULL,
  `cultivo_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  KEY `cultivo_id` (`cultivo_id`),
  CONSTRAINT `supervisor_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `supervisor_ibfk_2` FOREIGN KEY (`cultivo_id`) REFERENCES `cultivos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supervisor`
--

LOCK TABLES `supervisor` WRITE;
/*!40000 ALTER TABLE `supervisor` DISABLE KEYS */;
INSERT INTO `supervisor` VALUES (1,'Control de calidad del fruto','Supervisor de Zona',4,1),(2,'Revisión de hidratación','Líder de Campo',7,2),(3,'Monitoreo de PH suelo','Agrónomo Jr',12,3),(4,'Gestión de cuadrilla','Capatáz',15,4),(5,'Reporte de crecimiento','Supervisor de Zona',4,5),(6,'Control biológico','Líder de Campo',7,6),(7,'Supervisión de abono','Agrónomo Jr',12,7),(8,'Auditoría de riego','Capatáz',15,8),(9,'Inspección de plagas','Supervisor de Zona',4,9),(10,'Coordinación de siembra','Líder de Campo',7,10),(11,'Control de temperatura','Agrónomo Jr',12,11),(12,'Gestión de insumos','Capatáz',15,12),(13,'Verificación de ciclos','Supervisor de Zona',4,13),(14,'Optimización de cosecha','Líder de Campo',7,14),(15,'Mantenimiento preventivo','Agrónomo Jr',12,15);
/*!40000 ALTER TABLE `supervisor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tareas`
--

DROP TABLE IF EXISTS `tareas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tareas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tareas`
--

LOCK TABLES `tareas` WRITE;
/*!40000 ALTER TABLE `tareas` DISABLE KEYS */;
INSERT INTO `tareas` VALUES (1,'Riego Manual'),(2,'Fumigación Controlada'),(3,'Poda de Formación'),(4,'Fertilización Foliar'),(5,'Control de Malezas'),(6,'Monitoreo de Plagas'),(7,'Cosecha Selectiva'),(8,'Preparación de Terreno'),(9,'Siembra Directa'),(10,'Aplicación de Abono'),(11,'Limpieza de Canales'),(12,'Tutorado de Plantas'),(13,'Deshije'),(14,'Recolección de Residuos'),(15,'Mantenimiento de Herramientas');
/*!40000 ALTER TABLE `tareas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `telefono`
--

DROP TABLE IF EXISTS `telefono`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `telefono` (
  `id` int NOT NULL AUTO_INCREMENT,
  `numero` varchar(50) NOT NULL,
  `usuario_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `telefono_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `telefono`
--

LOCK TABLES `telefono` WRITE;
/*!40000 ALTER TABLE `telefono` DISABLE KEYS */;
INSERT INTO `telefono` VALUES (1,'3001234567',1),(2,'3104758291',2),(3,'3158294031',3),(4,'3209182736',4),(5,'3127364510',5),(6,'3182930415',6),(7,'3115647382',7),(8,'3149028173',8),(9,'3138475629',9),(10,'3172635401',10),(11,'3216549870',11),(12,'3164758293',12),(13,'3109283746',13),(14,'3157463521',14),(15,'3228374650',15);
/*!40000 ALTER TABLE `telefono` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo_producto`
--

DROP TABLE IF EXISTS `tipo_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_producto` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tipo_nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo_producto`
--

LOCK TABLES `tipo_producto` WRITE;
/*!40000 ALTER TABLE `tipo_producto` DISABLE KEYS */;
INSERT INTO `tipo_producto` VALUES (1,'Fertilizante'),(2,'Herramienta'),(3,'Semilla'),(4,'Pesticida');
/*!40000 ALTER TABLE `tipo_producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario_tarea`
--

DROP TABLE IF EXISTS `usuario_tarea`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario_tarea` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cultivo_id` int NOT NULL,
  `tarea_id` int NOT NULL,
  `usuario_id` int NOT NULL,
  `descripcion_actividad` text NOT NULL,
  `jornada` varchar(50) NOT NULL,
  `estado` varchar(20) DEFAULT 'Pendiente',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `cultivo_id` (`cultivo_id`),
  KEY `tarea_id` (`tarea_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `usuario_tarea_ibfk_1` FOREIGN KEY (`cultivo_id`) REFERENCES `cultivos` (`id`),
  CONSTRAINT `usuario_tarea_ibfk_2` FOREIGN KEY (`tarea_id`) REFERENCES `tareas` (`id`),
  CONSTRAINT `usuario_tarea_ibfk_3` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario_tarea`
--

LOCK TABLES `usuario_tarea` WRITE;
/*!40000 ALTER TABLE `usuario_tarea` DISABLE KEYS */;
INSERT INTO `usuario_tarea` VALUES (1,1,1,2,'Regar lote central 50L agua','Mañana','Pendiente','2026-03-11 12:50:18'),(2,2,2,3,'Aplicar insecticida preventivo','Mañana','En Proceso','2026-03-11 12:50:18'),(3,3,3,2,'Podar ramas secas bajas','Tarde','Completada','2026-03-11 12:50:18'),(4,4,4,3,'Disolver NPK en tanques','Mañana','Pendiente','2026-03-11 12:50:18'),(5,5,5,2,'Retirar maleza de surcos 1-5','Tarde','En Proceso','2026-03-11 12:50:18'),(6,6,6,3,'Buscar presencia de pulgón','Mañana','Pendiente','2026-03-11 12:50:18'),(7,7,7,2,'Recolectar frutos rojos','Mañana','Completada','2026-03-11 12:50:18'),(8,8,8,3,'Nivelar terreno sector B','Tarde','Pendiente','2026-03-11 12:50:18'),(9,9,9,2,'Sembrar semillas faltantes','Mañana','En Proceso','2026-03-11 12:50:18'),(10,10,10,3,'Esparcir composta orgánica','Tarde','Pendiente','2026-03-11 12:50:18'),(11,11,11,2,'Retirar lodo de drenajes','Mañana','Completada','2026-03-11 12:50:18'),(12,12,12,3,'Amarrar tallos a estacas','Tarde','En Proceso','2026-03-11 12:50:18'),(13,13,13,2,'Quitar brotes laterales','Mañana','Pendiente','2026-03-11 12:50:18'),(14,14,14,3,'Limpiar área de acopio','Tarde','Completada','2026-03-11 12:50:18'),(15,15,15,2,'Aceitar tijeras y machetes','Tarde','Pendiente','2026-03-11 12:50:18');
/*!40000 ALTER TABLE `usuario_tarea` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `pass` varchar(50) NOT NULL,
  `documento` varchar(20) NOT NULL,
  `direccion` varchar(50) NOT NULL,
  `estado` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `documento` (`documento`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Dayana Lisarazo','e1facfedbbfa34697489fccd20d03b7e','1095745698','Calle 36 #12-05, Bucaramanga','Activo'),(2,'Ana Maria Gomez','e9d6efbc4862e3695e1fd59cdecb0023','1098765432','Carrera 27 #54-10, Floridablanca','Activo'),(3,'Juan David Perez','c860899dca11d733f7aa867fb45bed6b','1050403020','Av. Quebradaseca #15-20, Bucaramanga','Activo'),(4,'Lucia Fernanda Diaz','f9d58bdaa8127aa0088386ee7bb55034','1010203045','Calle 105 #22-14, Girón','Activo'),(5,'Luis Eduardo Mora','571dac8419c06397f9125cfde9f89976','1092345678','Carrera 15 #33-45, Piedecuesta','Activo'),(6,'Elena Sofia Sanz','f52b8c656c228a1e6ae0d14f84cb6a0d','1088776655','Calle 56 #9-12, Bucaramanga','Activo'),(7,'Pedro Jose Gil','f04a34261230257653daed1a6eb5be9a','1077665544','Trans. 39 #10-50, Floridablanca','Activo'),(8,'Rosa Elena Meza','544766614f07223cfb108a519b59d1ec','1066554433','Calle 20 #18-05, Lebrija','Activo'),(9,'Jose Antonio Toro','a52feee2583e7fcf3a4ddb36eee838c0','1055443322','Carrera 9 #45-67, Bucaramanga','Activo'),(10,'Clara Ines Luz','d821d243dda8cb71e1227834c2a3999a','1044332211','Calle 12 #14-16, Girón','Activo'),(11,'Hugo Armando Paz','9793d255f12530fea44c0f618417601d','1033221100','Carrera 33 #52-10, Bucaramanga','Activo'),(12,'Nora Cecilia Rey','ba1d9b7190d9d7d16436ecd2d080203e','1022110099','Calle 200 #12-44, Floridablanca','Activo'),(13,'Ivan Dario Rios','f84b64195b05021033751d5d0fcf9c8e','1011009988','Av. Los Samanes #4-12, Bucaramanga','Activo'),(14,'Sara Isabel Lee','a5d3fefc2c998cde276bfd69235dfc7f','1000998877','Carrera 12 #10-30, Piedecuesta','Activo'),(15,'Beto Alejandro Cue','10ce7b4818956a83b27681c7c678df61','1099887766','Calle 5 #8-10, Zapatoca','Activo');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-11  7:52:08
