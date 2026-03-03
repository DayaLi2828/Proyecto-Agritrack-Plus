<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.*, java.util.*" %>
<%
    // 1. CONTROL DE ACCESO SEGÚN EL ROL
    HttpSession sesion = request.getSession(false);
    String nombre = (sesion != null) ? (String) sesion.getAttribute("usuario_nombre") : null;
    String rol = (sesion != null) ? (String) sesion.getAttribute("rol") : null;
    Integer idUsuario = (sesion != null) ? (Integer) sesion.getAttribute("usuario_id") : null;

    // Si no es supervisor, lo mandamos al login
    if (nombre == null || !"supervisor".equalsIgnoreCase(rol)) {
        response.sendRedirect("../../index.jsp?error=acceso_denegado");
        return;
    }

    String inicial = nombre.substring(0, 1).toUpperCase();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel Supervisor | AgriTrack Plus</title>
    
    <link rel="stylesheet" href="../../asset/Administrador/style_Admin.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

    <header class="header-principal">
        <div class="contenedor__titulo">
            <img class="logo" src="../../asset/imagenes/hoja.png" alt="logo">
            <h1 class="titulo">AGRITRACK PLUS - MÓDULO SUPERVISIÓN</h1>
        </div>
    </header>

    <div class="layout-container">
        <aside class="sidebar__barra">
            <nav id="nav-menu">
                <a href="Supervisor.jsp" class="active"><i class="fas fa-home"></i> Inicio</a>
                <a href="../Metodos_Fertlización.jsp"><i class="fas fa-flask"></i> Fertilización</a>
                <a href="../Calendario.jsp"><i class="fas fa-calendar-alt"></i> Calendario</a>
                <a href="../Administrador/Tareas.jsp"><i class="fas fa-tasks"></i> Tareas</a>
                <a href="../Administrador/Cultivos_Registrados.jsp"><i class="fas fa-leaf"></i> Mis Cultivos</a>
            </nav>
        </aside>

        <main class="main">
            <section class="bienvenida-seccion">
                <div class="main__cajatexto">
                    <h1 class="main__titulo">Hola, <%= nombre %></h1>
                    <p class="main__texto">Gestión de cultivos y personal de campo.</p>
                </div>

                <div class="contenedor__perfil">
                    <div class="perfil__texto">
                        <h2 class="nombre__usuario"><%= nombre %></h2>
                        <p class="descripcion__usuario">Supervisor de Zona</p>
                    </div>
                    <div class="cirulo__perfil" onclick="togglePerfil()">
                        <h3 class="inicial__usuario"><%= inicial %></h3>
                    </div>
                    <button class="btn-logout" onclick="location.href='../../LogoutServlet'">
                        <img src="../../asset/imagenes/cerrar-sesion.png" alt="Salir">
                    </button>
                </div>
            </section>

            <div id="cardDatos" class="perfil__card">
                <h3>Datos de Sesión</h3>
                <p><strong>Rol:</strong> <%= rol.toUpperCase() %></p>
                <p><strong>ID Usuario:</strong> <%= idUsuario %></p>
                <hr>
                <p style="font-size: 0.8em; color: #666;">Cosechas próximas: Por determinar</p>
                <button class="btn-perfil" onclick="location.href='Perfil.jsp'">Configuración</button>
                <button class="btn-cerrar-card" onclick="togglePerfil()">Cerrar</button>
            </div>

            <div class="main__boxs-container" style="display: flex; gap: 20px; margin-top: 30px;">
                <div class="main__boxs" onclick="location.href='../Administrador/Cultivos_Registrados.jsp'">
                    <div class="main__contimagen--secundario"><img src="../../asset/imagenes/bloc.png"></div>
                    <div class="caja__texto">
                        <h3>Mis Cultivos</h3>
                    </div>
                </div>
                
                <div class="main__boxs" onclick="location.href='../Administrador/Tareas.jsp'">
                    <div class="main__contimagen--secundario"><img src="../../asset/imagenes/te-verde.png"></div>
                    <div class="caja__texto">
                        <h3>Asignar Tareas</h3>
                    </div>
                </div>
            </div>

            <section class="estadisticas-grid" style="margin-top: 40px;">
                <div class="main__tarjetas">
                    <h4>Cosechas Programadas</h4>
                    <span class="main__numero">0</span>
                    <p>Pendiente por determinar fecha.</p>
                </div>
            </section>
        </main>
    </div>

    <script>const ROL_USUARIO = "supervisor";</script>
    <script src="../../asset/Administrador/Admin_Logic.js"></script>
</body>
</html>