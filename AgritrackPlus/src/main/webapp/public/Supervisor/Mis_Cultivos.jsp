<%@ page import="com.agritrack.agritrackplus.DAO.TareaDAO" %>
<%@ page import="com.agritrack.agritrackplus.modelo.Tarea" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // VALIDACIÓN DE SEGURIDAD (Solo Supervisor)
    HttpSession sesion = request.getSession(false);
    String nombreUsuario = (sesion != null) ? (String) sesion.getAttribute("usuario_nombre") : null;
    String rol = (sesion != null) ? (String) sesion.getAttribute("rol") : null;
    Integer idUsuario = (sesion != null) ? (Integer) sesion.getAttribute("usuario_id") : null;

    if (nombreUsuario == null || !"supervisor".equalsIgnoreCase(rol)) {
        response.sendRedirect("../../index.jsp?error=acceso_denegado");
        return;
    }

    int idSupervisorLogueado = idUsuario;
    TareaDAO dao = new TareaDAO();
    List<Tarea> misCultivos = dao.listarCultivosPorSupervisor(idSupervisorLogueado);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mis Cultivos Supervisados - AgritrackPlus</title>
    <link rel="stylesheet" href="../../asset/Supervisor/style_MisCultivos.css">
</head>
<body>
    <header>
        <a href="Supervisor.jsp">
            <div class="icono__devolver">
                <img src="../../asset/imagenes/devolver.png" id="icono de devolver">
            </div>
        </a>
        <div class="contenedor__titulo">
            <div class="contenedor__logo">
                <a href="Supervisor.jsp">
                    <img class="logo" src="../../asset/imagenes/hoja (3).png" alt="hoja del logo" />
                </a>
            </div>
            <h1 class="titulo">Cultivos bajo mi supervisión</h1>
        </div>
    </header>

    <main>
        <div class="contenedor__cultivos_grid">
            <% if (misCultivos != null && !misCultivos.isEmpty()) { 
                for (Tarea c : misCultivos) { %>
                
                <div class="contenedor__cultivo_card">
                    <div class="header__cultivo">
                        <h2 class="titulo__nombre_cultivo"><%= c.getNombreCultivo() %></h2>
                        <div class="estado__cultivo"><%= c.getEstado() %></div>
                    </div>
                    
                    <div class="info__detalle">
                        <p class="texto__etapa">
                            <img src="../../asset/imagenes/hoja (3).png" class="icono__mini" alt="ciclo">
                            <strong>Etapa:</strong> <%= c.getJornada() %>
                        </p>
                    </div>
                    
                    <div class="seccion__personal">
                        <p class="titulo__personal">Personal Asignado</p>
                        <div class="lista__trabajadores">
                            <% if (c.getNombreTrabajador() != null) { 
                                String[] nombres = c.getNombreTrabajador().split(", ");
                                for (String nombre : nombres) { %>
                                    <div class="tag__trabajador">
                                        <img src="../../asset/imagenes/usuario(2).png" class="icono__mini" alt="trabajador">
                                        <%= nombre %>
                                    </div>
                                <% } 
                            } %>
                        </div>
                    </div>
                </div>

            <% } 
            } else { %>
                <p style="text-align: center; margin-top: 20px;">No tienes cultivos asignados actualmente.</p>
            <% } %>
        </div>
    </main>
</body>
</html>