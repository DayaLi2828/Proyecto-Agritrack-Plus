<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%
    // 1. Validar que el usuario esté logueado
    HttpSession sesion = request.getSession();
    String nombreUsuario = (String) sesion.getAttribute("usuario_nombre");
    String rol = (String) sesion.getAttribute("rol");
    List<String> permisos = (List<String>) sesion.getAttribute("permisos");

    // Si no hay sesión o el rol no es Admin/Supervisor, redirigir al login
    if (nombreUsuario == null || (!"Administrador".equals(rol) && !"Supervisor".equals(rol))) {
        response.sendRedirect("../../login.jsp?error=acceso_denegado");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard | AgriTrack Plus</title>
    <link rel="stylesheet" href="../../css/style_dashboard.css">
    
</head>
<body>

    <header>
        <div>
            <h1 style="margin:0;">AgriTrack Plus</h1>
            <span>Bienvenido, <strong><%= nombreUsuario %></strong> (<%= rol %>)</span>
        </div>
        <a href="../../LogoutServlet" class="logout">Cerrar Sesión</a>
    </header>

    <main>
        <div class="grid-container">

            <% if (permisos != null && permisos.contains("CREAR_USUARIOS")) { %>
            <div class="card">
                <img src="../../asset/imagenes/usuario.png" alt="Usuarios">
                <h3>Gestión de Usuarios</h3>
                <p>Registrar y administrar el personal del campo.</p>
                <a href="Usuarios.jsp" class="btn">Administrar</a>
            </div>
            <% } %>

            <div class="card">
                <img src="../../asset/imagenes/cultivos.png" alt="Cultivos">
                <h3>Cultivos</h3>
                <p>Ver estado de siembras y cosechas pendientes.</p>
                <a href="Cultivos_Registrados.jsp" class="btn">Ver Cultivos</a>
            </div>

            <% if (permisos != null && permisos.contains("CREAR_PRODUCTOS")) { %>
            <div class="card">
                <img src="../../asset/imagenes/productos.png" alt="Productos">
                <h3>Insumos y Productos</h3>
                <p>Control de inventario, abonos y herramientas.</p>
                <a href="Productos.jsp" class="btn">Inventario</a>
            </div>
            <% } %>

            <% if (permisos != null && permisos.contains("CREAR_TAREAS")) { %>
            <div class="card">
                <img src="../../asset/imagenes/tareas.png" alt="Tareas">
                <h3>Asignar Labores</h3>
                <p>Crear nuevas tareas para los trabajadores.</p>
                <a href="Agregar_Tarea.jsp" class="btn">Asignar Tarea</a>
            </div>
            <% } %>

        </div>
    </main>

</body>
</html>