<%@ page import="com.agritrack.agritrackplus.DAO.TareaDAO" %>
<%@ page import="com.agritrack.agritrackplus.modelo.Tarea" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // VALIDACIÓN DE SEGURIDAD (Solo Supervisor)
    HttpSession sesion = request.getSession(false);
    String nombreUsuario = (sesion != null) ? (String) sesion.getAttribute("usuario_nombre") : null;
    String rol           = (sesion != null) ? (String) sesion.getAttribute("rol")            : null;
    Integer idUsuario    = (sesion != null) ? (Integer) sesion.getAttribute("usuario_id")    : null;

    if (nombreUsuario == null || !"supervisor".equalsIgnoreCase(rol)) {
        response.sendRedirect("../../index.jsp?error=acceso_denegado");
        return;
    }

    TareaDAO dao = new TareaDAO();
    List<Tarea> listaCultivos    = dao.listarCultivos();
    List<Tarea> listaTrabajadores = dao.listarTrabajadores();
    List<Tarea> catalogoTareas   = dao.listarCatalogoTareas();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agregar Tarea - AgritrackPlus</title>
    <link rel="stylesheet" href="../../asset/Supervisor/style-Agregar-Tarea.css">

</head>
<body>
    <header>
        <a href="Tareas.jsp">
            <div class="icono__devolver">
                <img src="../../asset/imagenes/devolver.png" alt="volver">
            </div>
        </a>
        <div class="contenedor__titulo">
            <div class="contenedor__logo">
                <img class="logo" src="../../asset/imagenes/hoja (3).png" alt="logo">
            </div>
            <h1 class="titulo">Agregar Tarea</h1>
        </div>
    </header>

    <main>
        <form action="${pageContext.request.contextPath}/AsignarTareaServlet" method="POST" id="formTarea" novalidate>
            <div class="contendor__padre">

                <%-- SECCIÓN: Información de la tarea --%>
                <div class="seccion">
                    <div class="seccion__header">
                        <h3 class="seccion__titulo">Información de la Tarea</h3>
                    </div>
                    <div class="seccion__contenido">

                        <div class="campo">
                            <label class="label">Seleccionar Cultivo <span class="requerido">*</span></label>
                            <select class="input" name="cboCultivo" id="cboCultivo">
                                <option value="">Seleccionar...</option>
                                <% for (Tarea c : listaCultivos) { %>
                                    <option value="<%= c.getCultivoId() %>"><%= c.getNombreCultivo() %></option>
                                <% } %>
                            </select>
                            <span class="campo__error" id="errCultivo"></span>
                        </div>

                        <div class="campo">
                            <label class="label">Nombre de la Tarea <span class="requerido">*</span></label>
                            <input type="text" class="input" name="txtNombreTarea" id="txtNombreTarea"
                                   placeholder="Escriba el nombre de la labor...">
                            <span class="campo__error" id="errNombreTarea"></span>
                        </div>

                        <div class="campo">
                            <label class="label">Descripción <span class="requerido">*</span></label>
                            <textarea class="input textarea" name="txtDescripcion" id="txtDescripcion"
                                      rows="3" placeholder="Describe la tarea en detalle..."></textarea>
                            <span class="campo__error" id="errDescripcion"></span>
                        </div>

                        <div class="campo__grupo">
                            <div class="campo">
                                <label class="label">Estado <span class="requerido">*</span></label>
                                <select class="input" name="cboEstado" id="cboEstado">
                                    <option value="">Seleccionar...</option>
                                    <option value="pendiente">Pendiente</option>
                                    <option value="proceso">En Proceso</option>
                                </select>
                                <span class="campo__error" id="errEstado"></span>
                            </div>

                            <div class="campo">
                                <label class="label">Fecha Programada <span class="requerido">*</span></label>
                                <input type="date" name="txtFecha" id="txtFecha" class="input">
                                <span class="campo__error" id="errFecha"></span>
                            </div>
                        </div>
                    </div>
                </div>

                <%-- SECCIÓN: Asignar trabajadores --%>
                <div class="seccion seccion__trabajadores">
                    <div class="seccion__header seccion__header--trabajadores">
                        <h3 class="seccion__titulo">Asignar Trabajadores</h3>
                    </div>
                    <div class="seccion__contenido">
                        <div class="asignacion__card">
                            <div class="campo__grupo--asignacion">
                                <div class="campo campo--small">
                                    <label class="label label--small">Usuario <span class="requerido">*</span></label>
                                    <select class="input input--small" name="cboTrabajador" id="cboTrabajador">
                                        <option value="">Seleccionar...</option>
                                        <% for (Tarea tr : listaTrabajadores) { %>
                                            <option value="<%= tr.getId() %>"><%= tr.getNombreTrabajador() %></option>
                                        <% } %>
                                    </select>
                                    <span class="campo__error" id="errTrabajador"></span>
                                </div>

                                <div class="campo campo--small">
                                    <label class="label label--small">Jornada <span class="requerido">*</span></label>
                                    <select class="input input--small" name="cboJornada" id="cboJornada">
                                        <option value="">Seleccionar...</option>
                                        <option value="Mañana">Mañana</option>
                                        <option value="Tarde">Tarde</option>
                                        <option value="Completa">Completa</option>
                                    </select>
                                    <span class="campo__error" id="errJornada"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="botones__contenedor">
                    <button type="submit" class="btn btn__guardar">Crear Tarea</button>
                    <button type="button" class="btn btn__cancelar" onclick="window.location.href='Tareas.jsp'">Cancelar</button>
                </div>
            </div>
        </form>
    </main>

    <script>
        // Fecha mínima = hoy
        document.addEventListener("DOMContentLoaded", function() {
            const inputFecha = document.getElementById('txtFecha');
            const hoy = new Date().toISOString().split('T')[0];
            inputFecha.setAttribute('min', hoy);
            inputFecha.value = hoy;
        });

        // Reglas de validación por campo
        const reglas = {
            cboCultivo:    { test: v => v !== '',          err: 'errCultivo',    msg: 'Selecciona un cultivo.' },
            txtNombreTarea:{ test: v => v.trim().length >= 3, err: 'errNombreTarea', msg: 'El nombre debe tener al menos 3 caracteres.' },
            txtDescripcion:{ test: v => v.trim().length >= 10, err: 'errDescripcion', msg: 'La descripción debe tener al menos 10 caracteres.' },
            cboEstado:     { test: v => v !== '',          err: 'errEstado',     msg: 'Selecciona un estado.' },
            txtFecha:      { test: v => v !== '',          err: 'errFecha',      msg: 'Selecciona una fecha programada.' },
            cboTrabajador: { test: v => v !== '',          err: 'errTrabajador', msg: 'Selecciona un trabajador.' },
            cboJornada:    { test: v => v !== '',          err: 'errJornada',    msg: 'Selecciona una jornada.' }
        };

        function validarCampo(id) {
            const r = reglas[id];
            const el = document.getElementById(id);
            const span = document.getElementById(r.err);
            const ok = r.test(el.value);
            span.textContent = ok ? '' : r.msg;
            el.classList.toggle('input--invalido', !ok);
            el.classList.toggle('input--valido', ok);
            return ok;
        }

        // Validar en tiempo real al cambiar cada campo
        Object.keys(reglas).forEach(id => {
            const el = document.getElementById(id);
            if (!el) return;
            el.addEventListener('input',  () => validarCampo(id));
            el.addEventListener('change', () => validarCampo(id));
        });

        // Validación al enviar
        document.getElementById('formTarea').addEventListener('submit', function(e) {
            const todosValidos = Object.keys(reglas).map(validarCampo).every(Boolean);
            if (!todosValidos) {
                e.preventDefault();
                // Scroll al primer campo con error
                const primerError = document.querySelector('.input--invalido');
                if (primerError) primerError.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        });
    </script>
</body>
</html>