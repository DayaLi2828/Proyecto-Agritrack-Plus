<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.UsuarioDAO" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%
    // VALIDACIÓN DE SEGURIDAD (Solo Trabajador)
    HttpSession sesion = request.getSession(false);
    String nombreUsuario = (sesion != null) ? (String) sesion.getAttribute("usuario_nombre") : null;
    String rol           = (sesion != null) ? (String) sesion.getAttribute("rol")            : null;
    Integer idUsuario    = (sesion != null) ? (Integer) sesion.getAttribute("usuario_id")    : null;

    if (nombreUsuario == null || !"trabajador".equalsIgnoreCase(rol)) {
        response.sendRedirect("../../index.jsp?error=acceso_denegado");
        return;
    }

    // Obtener datos completos del usuario desde la BD
    UsuarioDAO usuarioDAO = new UsuarioDAO();
    com.agritrack.agritrackplus.modelo.Usuario userFull = null;

    Map<String, Integer> datosTareas   = (Map<String, Integer>) sesion.getAttribute("datosGrafico");
    Map<String, Integer> datosCultivos = (Map<String, Integer>) sesion.getAttribute("datosCultivos");
    Map<String, Double>  datosPago     = (Map<String, Double>)  sesion.getAttribute("datosPago");

    try { userFull = usuarioDAO.listarId(idUsuario); } catch (Exception e) { e.printStackTrace(); }

    String correo    = (userFull != null && userFull.getCorreo()    != null) ? userFull.getCorreo()    : "";
    String telefono  = (userFull != null && userFull.getTelefono()  != null) ? userFull.getTelefono()  : "";
    String direccion = (userFull != null && userFull.getDireccion() != null) ? userFull.getDireccion() : "";
    String fotoNombre = (userFull != null) ? userFull.getFoto() : null;

    int completadas = (datosTareas != null && datosTareas.containsKey("Completada")) ? datosTareas.get("Completada") : 0;
    int proceso     = (datosTareas != null && datosTareas.containsKey("En Proceso"))    ? datosTareas.get("En Proceso")    : 0;
    int pendientes  = (datosTareas != null && datosTareas.containsKey("Pendiente"))  ? datosTareas.get("Pendiente")  : 0;
    int total       = completadas + proceso + pendientes;

    String labelsCultivos  = "['Sin Datos']";
    String valoresCultivos = "[0]";
    if (datosCultivos != null && !datosCultivos.isEmpty()) {
        StringBuilder sbL = new StringBuilder("["), sbV = new StringBuilder("[");
        int i = 0;
        for (Map.Entry<String, Integer> e : datosCultivos.entrySet()) {
            sbL.append("'").append(e.getKey()).append("'");
            sbV.append(e.getValue());
            if (i < datosCultivos.size() - 1) { sbL.append(","); sbV.append(","); }
            i++;
        }
        labelsCultivos = sbL.append("]").toString();
        valoresCultivos = sbV.append("]").toString();
    }

    double pagado  = (datosPago != null && datosPago.containsKey("Pagado"))   ? datosPago.get("Pagado")   : 0.0;
    double porPagar = (datosPago != null && datosPago.containsKey("Pendiente")) ? datosPago.get("Pendiente") : 0.0;    double montoTotalAcumulado = pagado + porPagar;
    String inicial = (nombreUsuario != null && !nombreUsuario.isEmpty()) ? nombreUsuario.substring(0, 1).toUpperCase() : "U";
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
            <a href="Trabajador.jsp">Inicio</a>
            <a href="Completar_Tareas.jsp">Mis tareas</a>
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

                <div class="cirulo__perfil" onclick="togglePerfil()" >
                    <% if (fotoNombre != null && !fotoNombre.isEmpty()) { %>
                        <img src="../../asset/imagenes/<%= fotoNombre %>" style="width:100%; height:100%; object-fit:cover;">
                    <% } else { %>
                        <h3 class="inicial__usuario"><%= inicial %></h3>
                    <% } %>
                </div>

                <div class="cerrar__sesion" onclick="location.href='${pageContext.request.contextPath}/LogoutServlet'" style="cursor:pointer;">
                    <img src="../../asset/imagenes/cerrar-sesion.png" alt="cerrar"/>
                </div>
            </div>
        </div>

        <div class="dashboard__grid">
            <div class="dashboard__card">
                <h3 class="titulo--grafica">Estado de mis Tareas</h3>
                <p class="subtitulo--grafica">Distribución actual de labores</p>
                <div class="chart__container"><canvas id="chartTareas"></canvas></div>
            </div>
            <div class="dashboard__card">
                <h3 class="titulo--grafica">Cumplimiento por Cultivo</h3>
                <p class="subtitulo--grafica">Porcentaje de tareas terminadas</p>
                <div class="chart__container"><canvas id="chartCultivos"></canvas></div>
            </div>
            <div class="dashboard__card">
                <h3 class="titulo--grafica">Estado de Pago</h3>
                <div class="donut__wrapper">
                    <canvas id="chartPago"></canvas>
                    <div class="donut__overlay">
                        <span class="monto__total">$<%= String.format("%,.0f", montoTotalAcumulado) %></span>
                        <span class="monto__label">COP</span>
                    </div>
                </div>
            </div>
            <div class="dashboard__card">
                <h3 class="titulo--grafica">Resumen tareas</h3>
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

        <%-- OVERLAY Y PANEL DE PERFIL --%>
        <div id="overlayPerfil" class="perfil__overlay" onclick="togglePerfil()"></div>
        <div id="cardDatos" class="perfil__card">
            <div class="card__edit-header">
                <h3>Mis Datos Personales</h3>
                <p>Gestiona tu perfil en el sistema.</p>
            </div>
            <form action="${pageContext.request.contextPath}/ActualizarUsuarioServlet" method="POST" enctype="multipart/form-data" class="form__edicion" id="formPerfil" novalidate>
                <input type="hidden" name="idUsuario" value="<%= idUsuario %>">
                <div class="seccion__foto-perfil">
                    <div id="contenedorPreview">
                        <% if (fotoNombre != null && !fotoNombre.isEmpty()) { %>
                            <img src="../../asset/imagenes/<%= fotoNombre %>" class="foto__actual" id="previewFoto">
                        <% } else { %>
                            <div class="foto__vacia" id="previewFoto"><%= inicial %></div>
                        <% } %>
                    </div>
                    <label for="inputFoto" class="btn-subir-foto"><i class="fas fa-camera"></i> Cambiar Foto</label>
                    <input type="file" id="inputFoto" name="fotoPerfil" accept="image/*" style="display:none" onchange="previsualizar(this)">
                </div>
                <div class="grid__campos">
                    <div class="campo__edicion">
                        <label>Nombre Completo *</label>
                        <input type="text" name="txtNombre" id="txtNombre" value="<%= nombreUsuario %>">
                        <span class="campo__error" id="errNombre"></span>
                    </div>
                    <div class="campo__edicion">
                        <label>Correo Electrónico *</label>
                        <input type="text" name="txtCorreo" id="txtCorreo" value="<%= correo %>">
                        <span class="campo__error" id="errCorreo"></span>
                    </div>
                    <div class="campo__edicion">
                        <label>Teléfono</label>
                        <input type="text" name="txtTelefono" id="txtTelefono" value="<%= telefono %>">
                        <span class="campo__error" id="errTelefono"></span>
                    </div>
                    <div class="campo__edicion">
                        <label>Dirección</label>
                        <input type="text" name="txtDireccion" id="txtDireccion" value="<%= direccion %>">
                    </div>
                    <div class="campo__edicion" style="grid-column: span 2;">
                        <label>Nueva Contraseña</label>
                        <input type="password" name="txtPassword" id="txtPassword" placeholder="Dejar en blanco para no cambiar">
                        <span class="campo__error" id="errPassword"></span>
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
        <p>© 2026 Agritrack Plus - Sistema de Gestión</p>
    </footer>

    <script>
        // --- Gráficos ---
        new Chart(document.getElementById('chartTareas'), {
            type: 'line',
            data: { labels: ['Pendientes', 'En Proceso', 'Completadas'],
                datasets: [{ label: 'Cantidad', data: [<%= pendientes %>, <%= proceso %>, <%= completadas %>],
                borderColor: '#065f46', tension: 0.3, fill: true, backgroundColor: 'rgba(6, 95, 70, 0.1)' }] }
        });
        new Chart(document.getElementById('chartCultivos'), {
            type: 'bar',
            data: { labels: <%= labelsCultivos %>, datasets: [{ label: '% Cumplimiento',
                data: <%= valoresCultivos %>, backgroundColor: ['#065f46','#a3e635','#f59e0b','#10b981'] }] },
            options: { scales: { y: { beginAtZero: true, max: 100 } } }
        });
        new Chart(document.getElementById('chartPago'), {
            type: 'doughnut',
            data: { labels: ['Pagado','Pendiente'], datasets: [{ data: [<%= pagado %>, <%= porPagar %>],
                backgroundColor: ['#10b981','#f0fdf4'] }] },
            options: { cutout: '80%', plugins: { legend: { display: false } } }
        });

        // --- Panel de perfil ---
        function togglePerfil() {
            document.getElementById("cardDatos").classList.toggle("activo");
            document.getElementById("overlayPerfil").classList.toggle("activo");
        }
        function previsualizar(input) {
            if (input.files && input.files[0]) {
                const r = new FileReader();
                r.onload = e => { document.getElementById('contenedorPreview').innerHTML =
                    `<img src="${e.target.result}" class="foto__actual" id="previewFoto">`; };
                r.readAsDataURL(input.files[0]);
            }
        }

        // --- Validación de campos ---
        const reglas = {
            txtNombre:   { test: v => v.trim().length >= 3 && /^[A-Za-záéíóúÁÉÍÓÚñÑ\s]+$/.test(v),  err: 'errNombre',   msg: 'Solo letras y espacios, mínimo 3 caracteres.' },
            txtCorreo:   { test: v => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v),                          err: 'errCorreo',   msg: 'Ingresa un correo electrónico válido.' },
            txtTelefono: { test: v => v === '' || /^[\d\+\-\s]{7,15}$/.test(v),                      err: 'errTelefono', msg: 'Solo números, +, - y espacios (7-15 caracteres).' },
            txtPassword: { test: v => v === '' || v.length >= 6,                                      err: 'errPassword', msg: 'La contraseña debe tener al menos 6 caracteres.' }
        };
        Object.keys(reglas).forEach(id => {
            const input = document.getElementById(id);
            if (!input) return;
            input.addEventListener('input', () => validarCampo(id));
        });
        function validarCampo(id) {
            const r = reglas[id];
            const input = document.getElementById(id);
            const span  = document.getElementById(r.err);
            const ok = r.test(input.value);
            span.textContent = ok ? '' : r.msg;
            input.style.borderColor = ok ? '' : '#e53e3e';
            return ok;
        }
        document.getElementById('formPerfil').addEventListener('submit', function(e) {
            const valido = Object.keys(reglas).map(validarCampo).every(Boolean);
            if (!valido) e.preventDefault();
        });
    </script>
</body>
</html>