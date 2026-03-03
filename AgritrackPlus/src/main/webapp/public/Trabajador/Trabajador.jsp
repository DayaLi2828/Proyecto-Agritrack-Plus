<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<%
    // Protección contra Error 500 por sesión nula
    Integer idUsuario = (Integer) session.getAttribute("usuario_id");
    String nombreUsuario = (String) session.getAttribute("usuario_nombre");
    
    // Recuperar los datos dinámicos enviados desde el LoginServlet
    Map<String, Integer> datosTareas = (Map<String, Integer>) session.getAttribute("datosGrafico");
    
    if (idUsuario == null || nombreUsuario == null) {
        response.sendRedirect("../../index.jsp");
        return; 
    }
    
    // Valores por defecto para evitar errores si el mapa es nulo
    int completadas = (datosTareas != null && datosTareas.containsKey("Completada")) ? datosTareas.get("Completada") : 0;
    int proceso = (datosTareas != null && datosTareas.containsKey("Proceso")) ? datosTareas.get("Proceso") : 0;
    int pendientes = (datosTareas != null && datosTareas.containsKey("Pendiente")) ? datosTareas.get("Pendiente") : 0;
    int total = completadas + proceso + pendientes;

    String inicial = (!nombreUsuario.isEmpty()) ? nombreUsuario.substring(0, 1).toUpperCase() : "U";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agritrack Plus - Dashboard</title>
    <link rel="stylesheet" href="../../asset/Trabajador/style_Trabajador.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <header>
        <div class="contenedor__titulo">
            <img class="logo" src="../../asset/imagenes/hoja.png" alt="logo">
            <h1 class="titulo">AGRITRACK<br>PLUS</h1>
        </div>
    </header>

    <aside class="sidebar__barra">
        <nav>
            <a href="#"><i class="fas fa-home"></i> Inicio</a>
            <a href="#"><i class="fas fa-seedling"></i> Mis Cultivos</a>
            <a href="Tarea_Trabajador.jsp"><i class="fas fa-tasks"></i> Tareas</a>
            <a href="#"><i class="fas fa-wallet"></i> Pagos</a>
        </nav>
    </aside>

    <main class="main">
        <div class="main__header">
            <div class="main__cajatexto">
                <h1 class="main__titulo">¡Bienvenido, <%= nombreUsuario %>!</h1>
                <p class="main__texto">Panel de Rendimiento Agrícola</p>
            </div>

            <div class="contenedor__perfil">
                <div class="perfil__texto">
                    <h2 class="nombre__usuario"><%= nombreUsuario %></h2>
                    <p class="descripcion__usuario">Especialista de Campo</p>
                </div>
                <div class="cirulo__perfil">
                    <h3 class="inicial__usuario"><%= inicial %></h3>
                </div>
                <div class="cerrar__sesion">
                    <a href="../../LogoutServlet">
                        <img src="../../asset/imagenes/cerrar-sesion.png" alt="Cerrar">
                    </a>
                </div>
            </div>
        </div>

        <div class="dashboard__grid">
            
            <div class="dashboard__card">
                <h3 class="titulo--grafica">Estado de mis Tareas</h3>
                <p class="subtitulo--grafica">Distribución actual de labores</p>
                <div class="chart__container">
                    <canvas id="chartTareas"></canvas>
                </div>
            </div>

            <div class="dashboard__card">
                <h3 class="titulo--grafica">Cumplimiento por Cultivo</h3>
                <p class="subtitulo--grafica">Porcentaje de tareas terminadas</p>
                <div class="chart__container">
                    <canvas id="chartCultivos"></canvas>
                </div>
            </div>

            <div class="dashboard__card">
                <h3 class="titulo--grafica">Estado de Pago</h3>
                <div class="donut__wrapper">
                    <canvas id="chartPago"></canvas>
                    <div class="donut__overlay">
                        <span class="monto__total">$1.2M</span>
                        <span class="monto__label">COP</span>
                    </div>
                </div>
            </div>

            <div class="dashboard__card">
                <h3 class="titulo--grafica">Resumen del Mes</h3>
                <div class="mini__stats">
                    <div class="stat__box">
                        <span class="stat__num"><%= total %></span>
                        <span class="stat__txt">Tareas Totales</span>
                    </div>
                    <div class="stat__box">
                        <span class="stat__num"><%= completadas %></span>
                        <span class="stat__txt">Completadas</span>
                    </div>
                </div>
            </div>

        </div>
    </main>

    <footer class="footer">
        <p>© 2026 Agritrack Plus - Sistema de Gestión</p>
    </footer>

    <script>
        // Gráfico 1: Tareas (Usa los datos del Mapa de Java)
        new Chart(document.getElementById('chartTareas'), {
            type: 'line',
            data: {
                labels: ['Pendientes', 'En Proceso', 'Completadas'],
                datasets: [{
                    label: 'Cantidad',
                    data: [<%= pendientes %>, <%= proceso %>, <%= completadas %>],
                    borderColor: '#065f46',
                    tension: 0.3,
                    fill: true,
                    backgroundColor: 'rgba(6, 95, 70, 0.1)'
                }]
            }
        });

        // Gráfico 2: Cultivos (Estático por ahora hasta tener el DAO de cultivos)
        new Chart(document.getElementById('chartCultivos'), {
            type: 'bar',
            data: {
                labels: ['Tomate', 'Lechuga', 'Zanahoria', 'Pim.'],
                datasets: [{
                    data: [90, 40, 25, 65],
                    backgroundColor: ['#065f46', '#a3e635', '#f59e0b', '#10b981']
                }]
            }
        });

        // Gráfico 3: Pago
        new Chart(document.getElementById('chartPago'), {
            type: 'doughnut',
            data: {
                labels: ['Pagado', 'Pendiente'],
                datasets: [{
                    data: [80, 20],
                    backgroundColor: ['#10b981', '#f0fdf4']
                }]
            },
            options: { cutout: '80%' }
        });
    </script>
</body>
</html>