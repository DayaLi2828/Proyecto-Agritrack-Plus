<%@ page import="com.agritrack.agritrackplus.DAO.TareaDAO" %>
<%@ page import="com.agritrack.agritrackplus.modelo.Tarea" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // VALIDACIÓN DE SEGURIDAD (Solo Trabajador)
    HttpSession sesion = request.getSession(false);
    String nombreUsuario = (sesion != null) ? (String) sesion.getAttribute("usuario_nombre") : null;
    String rol = (sesion != null) ? (String) sesion.getAttribute("rol") : null;
    Integer idUsuario = (sesion != null) ? (Integer) sesion.getAttribute("usuario_id") : null;

    if (nombreUsuario == null || !"trabajador".equalsIgnoreCase(rol)) {
        response.sendRedirect("../../index.jsp?error=acceso_denegado");
        return;
    }

    TareaDAO dao = new TareaDAO();
    List<Tarea> misTareas = dao.listarTareasPorTrabajador(idUsuario);
%>
<!DOCTYPE html>
<html lang="es">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Mis tareas asignadas - AgritrackPlus</title>
    <link rel="stylesheet" href="../../asset/Trabajador/style_completarTareas.css" />
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
  </head>
  <body>
    <header>
        <a href="Trabajador.jsp">
            <div class="icono__devolver">
                <img src="../../asset/imagenes/devolver.png" id="icono de devolver">
            </div>
        </a>
      <div class="contenedor__titulo">
        <div class="contenedor__logo">
            <img class="logo" src="../../asset/imagenes/hoja (3).png" alt="hoja del logo" />
        </div>
        <h1 class="titulo">Mis tareas asignadas</h1>
      </div>
    </header>
    <main>
      <div class="buscardor__tareas">
        <div class="contenedor__buscar">
          <input type="text" id="buscadorInput" class="input__buscador" placeholder="Buscar..." aria-label="Buscar"/>
          <img class="icono__buscador" src="../../asset/imagenes/lupa.png" alt="Icono de búsqueda"/>
        </div>

        <select id="filtroEstado" class="select__tareas">
          <option value="todas">Todas las tareas</option>
          <option value="Pendiente">Tareas pendientes</option>
          <option value="En Proceso">Tareas en proceso</option>
          <option value="Completada">Tareas completadas</option>
        </select>
      </div>

      <div class="tarjetas__tareas" id="contenedorTareas">
        <% if (misTareas != null && !misTareas.isEmpty()) {
            for (Tarea t : misTareas) {
                String claseEstadoCSS = t.getEstado().toLowerCase().replace(" ", "");
        %>
        <div class="tarea <%= claseEstadoCSS %>" data-estado="<%= t.getEstado() %>">
          <div class="contenedor__nombre">
            <h3 class="nombre__tarea"><%= t.getNombreTarea() %></h3>
            <h3 class="estado__tarea <%= claseEstadoCSS %>"><%= t.getEstado() %></h3>
          </div>
          <div class="cajas__cultivos">
            <span class="nombre__cultivo">🌱 <%= t.getNombreCultivo() %></span>
            <span class="jornada__cultivo">⏰ <%= t.getJornada() %></span>
            <a class="flecha__link" href="Tarea_Trabajador.jsp?idTarea=<%= t.getId() %>&nombre=<%= java.net.URLEncoder.encode(t.getNombreTarea(), "UTF-8") %>&descripcion=<%= java.net.URLEncoder.encode(t.getDescripcion() != null ? t.getDescripcion() : "", "UTF-8") %>&estado=<%= java.net.URLEncoder.encode(t.getEstado(), "UTF-8") %>">
              <img class="icono__flecha" src="../../asset/imagenes/proximo.png" alt="imagen de una flecha">
            </a>
          </div>
          <p class="descripcion__cultivo"><%= t.getDescripcion() != null ? t.getDescripcion() : "" %></p>
        </div>
        <%  }
        } else { %>
            <p style="text-align: center; margin-top: 20px;">No tienes tareas asignadas actualmente.</p>
        <% } %>
      </div>
    </main>

    <script>
        const buscador = document.getElementById('buscadorInput');
        const filtro = document.getElementById('filtroEstado');
        const tarjetas = document.querySelectorAll('.tarea');

        function filtrar() {
            const texto = buscador.value.toLowerCase();
            const estadoSel = filtro.value;

            tarjetas.forEach(card => {
                const nombre = card.querySelector('.nombre__tarea').textContent.toLowerCase();
                const cultivo = card.querySelector('.nombre__cultivo').textContent.toLowerCase();
                const estado = card.getAttribute('data-estado');

                const coincideTexto = nombre.includes(texto) || cultivo.includes(texto);
                const coincideEstado = estadoSel === 'todas' || estado === estadoSel;

                card.style.display = (coincideTexto && coincideEstado) ? "" : "none";
            });
        }

        buscador.addEventListener('input', filtrar);
        filtro.addEventListener('change', filtrar);

       window.onload = function() {
    const urlParams = new URLSearchParams(window.location.search);
    
    if (urlParams.get('status') === 'success') {
        Swal.fire({
            icon: 'success',
            title: '¡Actualización exitosa!',
            text: 'El estado de la tarea se ha actualizado correctamente.',
            confirmButtonColor: '#047857', // Verde esmeralda de tu proyecto
            timer: 3000, // Se cierra sola tras 3 segundos si el usuario no hace clic
            timerProgressBar: true
        }).then(() => {
            // Limpia la URL (quita el ?status=success)
            window.history.replaceState({}, document.title, window.location.pathname);
        });
    }
};
    </script>
  </body>
</html>