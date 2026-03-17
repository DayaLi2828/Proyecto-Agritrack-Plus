<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

    String idTareaStr   = request.getParameter("idTarea");
    String nombreTarea  = request.getParameter("nombre");
    String descripcion  = request.getParameter("descripcion");
    String estadoActual = request.getParameter("estado");

    if (idTareaStr == null || idTareaStr.trim().isEmpty()) {
        response.sendRedirect("Completar_Tareas.jsp");
        return;
    }
    if (nombreTarea  == null) nombreTarea  = "";
    if (descripcion  == null) descripcion  = "";
    if (estadoActual == null) estadoActual = "Pendiente";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= nombreTarea %> - AgritrackPlus</title>
    <link rel="stylesheet" href="../../asset/Trabajador/style_tareaTrabajador.css">

</head>
<body>
    <header>
        <div class="contenedor__titulo">
            <div class="contenedor__logo">
                <a href="Completar_Tareas.jsp">
                    <img class="logo" src="../../asset/imagenes/hoja (3).png" alt="hoja del logo">
                </a>
            </div>
            <h1 class="titulo"><%= nombreTarea %></h1>
        </div>
    </header>

    <main class="main">
        <form action="${pageContext.request.contextPath}/ActualizarEstadoTareaServlet" method="POST" id="formTarea" novalidate>
            <input type="hidden" name="idTarea" value="<%= idTareaStr %>">

            <%-- Descripción --%>
            <div class="contenedor__descripcion">
                <div class="contenedor__subtitulo">
                    <img class="icono__descripcion" src="../../asset/imagenes/descripcion-del-trabajo.png" alt="descripción">
                    <h2 class="subtitulo__descripcion">Descripción de la tarea</h2>
                </div>
                <div class="Descripcion__tarea">
                    <p><%= descripcion %></p>
                </div>
            </div>

            <%-- Estado --%>
            <div class="contenedor__estado">
                <div class="logo__titulo">
                    <img class="icono__estado" src="../../asset/imagenes/grafico-de-barras.png" alt="estado">
                    <h2 class="subtitulo__estado">Actualizar estado de la tarea</h2>
                </div>
                <div class="estado" id="grupoEstado">
                    <div class="estado__pendiente">
                        <label>
                            <input type="radio" name="estado" value="Pendiente" <%= "Pendiente".equalsIgnoreCase(estadoActual) ? "checked" : "" %>>
                            <h3 id="pendiente">Pendiente</h3>
                            <p class="texto__pendiente">(Tarea nueva, aún no iniciada)</p>
                        </label>
                    </div>
                    <div class="estado__completada">
                        <label>
                            <input type="radio" name="estado" value="Completada" <%= "Completada".equalsIgnoreCase(estadoActual) ? "checked" : "" %>>
                            <h3 id="completada">Completada</h3>
                            <p class="texto__completada">(Tarea finalizada con éxito)</p>
                        </label>
                    </div>
                    <div class="estado__proceso">
                        <label>
                            <input type="radio" name="estado" value="En Proceso" <%= "En Proceso".equalsIgnoreCase(estadoActual) ? "checked" : "" %>>
                            <h3 id="proceso">En proceso</h3>
                            <p class="texto__proceso">(Tarea aún no terminada)</p>
                        </label>
                    </div>
                </div>
                <span class="campo__error" id="errEstado"></span>
            </div>

            <%-- Observaciones (nuevo campo) --%>
            <div class="contenedor__observacion">
                <div class="contenedor__subtitulo">
                    <img class="icono__descripcion" src="../../asset/imagenes/descripcion-del-trabajo.png" alt="observación">
                    <h2 class="subtitulo__descripcion">Observaciones / Comentarios</h2>
                </div>
                <textarea class="textarea__observacion" name="txtObservacion" id="txtObservacion"
                          maxlength="300"
                          placeholder="Agrega notas, dificultades encontradas o detalles del trabajo realizado... (opcional)"></textarea>
                <div class="contador__chars"><span id="contadorChars">0</span> / 300</div>
            </div>

            <div class="contenedor__botones">
                <button type="submit" class="boton__guardar">Guardar Cambios</button>
                <button type="button" class="boton__cancelar" onclick="window.location.href='Completar_Tareas.jsp'">Cancelar</button>
            </div>
        </form>
    </main>

    <script>
        // Contador de caracteres en observaciones
        const txtObs = document.getElementById('txtObservacion');
        const contador = document.getElementById('contadorChars');
        txtObs.addEventListener('input', () => { contador.textContent = txtObs.value.length; });

        // Validación: debe seleccionarse un estado antes de guardar
        document.getElementById('formTarea').addEventListener('submit', function(e) {
            const radios = document.querySelectorAll('input[name="estado"]');
            const seleccionado = Array.from(radios).some(r => r.checked);
            const errSpan = document.getElementById('errEstado');
            if (!seleccionado) {
                errSpan.textContent = 'Debes seleccionar un estado para la tarea.';
                e.preventDefault();
            } else {
                errSpan.textContent = '';
            }
        });
    </script>
</body>
</html>