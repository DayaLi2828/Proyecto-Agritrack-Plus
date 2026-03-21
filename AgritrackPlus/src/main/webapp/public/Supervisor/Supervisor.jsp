<%@page import="com.agritrack.agritrackplus.DAO.UsuarioDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.Registro_CultivoDAO" %>
<%@ page import="com.agritrack.agritrackplus.DAO.ProductoDAO" %>
<%@ page import="com.agritrack.agritrackplus.DAO.CultivoDAO" %>
<%@ page import="java.util.List" %>
<%@page import="com.agritrack.agritrackplus.DAO.UsuarioDAO"%>
<%@page import="com.agritrack.agritrackplus.DAO.TareaDAO"%>
<%@ page import="com.agritrack.agritrackplus.DAO.ProductoDAO" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%
    HttpSession sesion = request.getSession(false);
    String nombreUsuario = (sesion != null) ? (String) sesion.getAttribute("usuario_nombre") : null;
    String rol = (sesion != null) ? (String) sesion.getAttribute("rol") : null;
    Integer idUsuario = (sesion != null) ? (Integer) sesion.getAttribute("usuario_id") : null;

    if (nombreUsuario == null || !"supervisor".equalsIgnoreCase(rol)) {
        response.sendRedirect("../../index.jsp?error=acceso_denegado");
        return;
    }

    UsuarioDAO usuarioDAO = new UsuarioDAO();
    TareaDAO tareaDAO = new TareaDAO();
    com.agritrack.agritrackplus.modelo.Usuario userFull = null;

    // Datos para gráficos
    Map<String, Integer> datosDia        = new java.util.LinkedHashMap<>();
    Map<String, Integer> datosCultivo    = new java.util.LinkedHashMap<>();
    Map<String, Integer> datosRendimiento = new java.util.LinkedHashMap<>();

    try {
        userFull          = usuarioDAO.listarId(idUsuario);
        datosDia          = tareaDAO.contarTareasPorDiaSemana(idUsuario);
        datosCultivo      = tareaDAO.cumplimientoPorCultivo(idUsuario);
        datosRendimiento  = tareaDAO.rendimientoSemanal(idUsuario);
    } catch (Exception e) {
        e.printStackTrace();
    }

    String correo    = (userFull != null && userFull.getCorreo()    != null) ? userFull.getCorreo()    : "";
    String telefono  = (userFull != null && userFull.getTelefono()  != null) ? userFull.getTelefono()  : "";
    String direccion = (userFull != null && userFull.getDireccion() != null) ? userFull.getDireccion() : "";
    String fotoNombre = (userFull != null) ? userFull.getFoto() : null;
    String inicial = (nombreUsuario != null && !nombreUsuario.isEmpty()) ? nombreUsuario.substring(0, 1).toUpperCase() : "?";

    // Construir arrays JSON para Chart.js
    StringBuilder labelsDia = new StringBuilder("[");
    StringBuilder valuesDia = new StringBuilder("[");
    int i = 0;
    for (Map.Entry<String, Integer> e : datosDia.entrySet()) {
        labelsDia.append("'").append(e.getKey()).append("'");
        valuesDia.append(e.getValue());
        if (i < datosDia.size() - 1) { labelsDia.append(","); valuesDia.append(","); }
        i++;
    }
    labelsDia.append("]"); valuesDia.append("]");

    StringBuilder labelsCultivo = new StringBuilder("[");
    StringBuilder valuesCultivo = new StringBuilder("[");
    i = 0;
    for (Map.Entry<String, Integer> e : datosCultivo.entrySet()) {
        labelsCultivo.append("'").append(e.getKey().replace("'", "\\'")).append("'");
        valuesCultivo.append(e.getValue());
        if (i < datosCultivo.size() - 1) { labelsCultivo.append(","); valuesCultivo.append(","); }
        i++;
    }
    labelsCultivo.append("]"); valuesCultivo.append("]");

    int completadas = datosRendimiento.getOrDefault("Completada", 0);
    int enProceso   = datosRendimiento.getOrDefault("En Proceso", 0);
    int pendientes  = datosRendimiento.getOrDefault("Pendiente",  0);
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Supervisor | AgriTrack Plus</title>
  <link rel="stylesheet" href="../../asset/Supervisor/style_Supervisor.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>

  <header>
    <div class="contenedor__titulo">
      <img class="logo" src="../../asset/imagenes/hoja.png" alt="logo">
      <h1 class="titulo">AGRITRACK<br> PLUS</h1>
    </div>
  </header>

  <aside class="sidebar__barra">
    <nav>
      <a href="Supervisor.jsp">Inicio</a>
      <a href="../Calendario.jsp">Calendario</a>
      <a href="Tareas.jsp">Asignar Tareas</a>
      <a href="Mis_Cultivos.jsp">Mis Cultivos</a>
    </nav>
  </aside>

  <main class="main">
    <div class="main__cajatexto">
        <h1 class="main__titulo">Bienvenido, <%= nombreUsuario %></h1>
        <p class="main__texto">Gestiona tus cultivos de manera eficiente!</p>
        
        <div class="contenedor__perfil">
            <div class="perfil__texto">
                <h2 class="nombre__usuario"><%= nombreUsuario %></h2>
                <p class="descripcion__usuario"><%= rol %></p>
            </div>
            
            <div class="cirulo__perfil" onclick="togglePerfil()" style="cursor:pointer; overflow:hidden; display:flex; align-items:center; justify-content:center; background:#e2e8f0; border-radius:50%; width:50px; height:50px; border: 2px solid #10b981;">
                <% if (fotoNombre != null && !fotoNombre.isEmpty()) { %>
                    <%-- Buscamos la foto en asset/imagenes/ --%>
                    <img src="../../asset/imagenes/<%= fotoNombre %>" style="width:100%; height:100%; object-fit:cover;">
                <% } else { %>
                    <h3 class="inicial__usuario"><%= inicial %></h3>
                <% } %>
            </div>

            <div class="cerrar__sesion" onclick="location.href='${pageContext.request.contextPath}/LogoutServlet'">
                <img src="../../asset/imagenes/cerrar-sesion.png" alt="cerrar"/>
            </div>
        </div>
    </div>

    <%-- Sección de Dashboard (Mantenida igual) --%>
    <div class="dashboard-section">
        <h2 class="dashboard-section-title">Panel de Supervisión (Dashboard)</h2>
        <div class="dashboard-grid">
            <div class="dashboard-card full-width">
                <div class="card-header">
                    
                    <h3>Trazabilidad y Estado de Tareas</h3>
                </div>
                <div class="card-content chart-container">
                    <canvas id="chartTareasPuntos"></canvas>
                </div>
            </div>

            <div class="dashboard-card">
                <div class="card-header">
                   
                    <h3>Cumplimiento por Cultivo (% Terminadas)</h3>
                </div>
                <div class="card-content chart-container">
                    <canvas id="chartCumplimientoBarras"></canvas>
                </div>
            </div>

            <div class="dashboard-card">
                <div class="card-header">
                   
                    <h3>Rendimiento Semanal</h3>
                </div>
                <div class="card-content chart-container circular-chart">
                    <canvas id="chartRendimientoCircular"></canvas>
                </div>
            </div>
        </div>
    </div>

    <div id="overlayPerfil" class="perfil__overlay" onclick="togglePerfil()"></div>
    
    <div id="cardDatos" class="perfil__card">
        <div class="card__edit-header">
            <h3>Mis Datos Personales</h3>
            <p>Gestiona tu perfil en el sistema.</p>
        </div>

        <form action="${pageContext.request.contextPath}/ActualizarPerfilServlet" method="POST" enctype="multipart/form-data" class="form__edicion">
            <input type="hidden" name="idUsuario" value="<%= idUsuario %>">
            
            <div class="seccion__foto-perfil">
                <div id="contenedorPreview">
                    <% if (fotoNombre != null && !fotoNombre.isEmpty()) { %>
                        <img src="../../asset/imagenes/<%= fotoNombre %>" class="foto__actual" id="previewFoto">
                    <% } else { %>
                        <div class="foto__vacia" id="previewFoto"><%= inicial %></div>
                    <% } %>
                </div>
                <label for="inputFoto" class="btn-subir-foto">
                    <i class="fas fa-camera"></i> Cambiar Foto
                </label>
                <input type="file" id="inputFoto" name="fotoPerfil" accept="image/*" style="display:none" onchange="previsualizar(this)">
            </div>

            <div class="grid__campos">
                <div class="campo__edicion">
                    <label>Nombre Completo</label>
                    <input type="text" name="txtNombre" value="<%= nombreUsuario %>" required>
                </div>
                <div class="campo__edicion">
                    <label>Correo Electrónico</label>
                    <input type="email" name="txtCorreo" value="<%= correo %>" required>
                </div>
                <div class="campo__edicion">
                    <label>Teléfono</label>
                    <input type="text" name="txtTelefono" value="<%= telefono %>">
                </div>
                <div class="campo__edicion">
                    <label>Dirección</label>
                    <input type="text" name="txtDireccion" value="<%= direccion %>">
                </div>
                <div class="campo__edicion" style="grid-column: span 2;">
                    <label>Nueva Contraseña</label>
                    <input type="password" name="txtPassword" placeholder="Dejar en blanco para no cambiar">
                </div>
            </div>

            <div class="botones__edicion">
                <button type="button" class="btn-cancelar" onclick="togglePerfil()">Cancelar</button>
                <button type="submit" class="btn-guardar">Guardar Cambios</button>
            </div>
        </form>
    </div>
  </main>

  <footer class="footer">
    <p>© 2026 Agritrack Plus - Módulo Supervisor</p>
  </footer>

  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<script>
    // Configuración de Gráficos
    const colorVerdeAgritrack = '#10b981';

    // GRÁFICO 1: Tareas por día de la semana
    const ctx1 = document.getElementById('chartTareasPuntos').getContext('2d');
    new Chart(ctx1, {
        type: 'line',
        data: {
            labels: <%= labelsDia %>,
            datasets: [{ label: 'Tareas', data: <%= valuesDia %>, borderColor: colorVerdeAgritrack, fill: true, tension: 0.3, backgroundColor: 'rgba(16,185,129,0.1)' }]
        },
        options: { responsive: true, maintainAspectRatio: false }
    });

    // GRÁFICO 2: Cumplimiento por cultivo
    const ctx2 = document.getElementById('chartCumplimientoBarras').getContext('2d');
    new Chart(ctx2, {
        type: 'bar',
        data: {
            labels: <%= labelsCultivo %>,
            datasets: [{ label: '%', data: <%= valuesCultivo %>, backgroundColor: colorVerdeAgritrack }]
        },
        options: { responsive: true, maintainAspectRatio: false, scales: { y: { beginAtZero: true, max: 100 } } }
    });

    // GRÁFICO 3: Rendimiento semanal (donut)
    const ctx3 = document.getElementById('chartRendimientoCircular').getContext('2d');
    new Chart(ctx3, {
        type: 'doughnut',
        data: {
            labels: ['Completadas', 'En Proceso', 'Pendientes'],
            datasets: [{ data: [<%= completadas %>, <%= enProceso %>, <%= pendientes %>], backgroundColor: [colorVerdeAgritrack, '#3b82f6', '#e2e8f0'] }]
        },
        options: { responsive: true, maintainAspectRatio: false }
    });

    // Lógica del Perfil
    function togglePerfil() {
        document.getElementById("cardDatos").classList.toggle("activo");
        document.getElementById("overlayPerfil").classList.toggle("activo");
    }

    function previsualizar(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function(e) {
                const contenedor = document.getElementById('contenedorPreview');
                contenedor.innerHTML = `<img src="${e.target.result}" class="foto__actual" id="previewFoto">`;
            }
            reader.readAsDataURL(input.files[0]);
        }
    }
    
    /*MENSAJE DE CONFIRMACION DE CAMBIO DE DATOS */
    document.addEventListener("DOMContentLoaded", function() {
        const urlParams = new URLSearchParams(window.location.search);
        
        if (urlParams.get('update') === 'success') {
            Swal.fire({
                icon: 'success',
                title: '¡Perfil Actualizado!',
                text: 'Tus datos se han guardado correctamente.',
                confirmButtonColor: '#10b981',
                timer: 3000
            });
            window.history.replaceState({}, document.title, window.location.pathname);
        }

        if (urlParams.get('update') === 'error') {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No se pudieron actualizar los datos. Intenta de nuevo.',
                confirmButtonColor: '#ef4444'
            });
            window.history.replaceState({}, document.title, window.location.pathname);
        }
    });
</script>
</body>
</html>