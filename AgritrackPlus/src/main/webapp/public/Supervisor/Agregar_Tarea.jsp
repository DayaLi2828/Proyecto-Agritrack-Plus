<%@ page import="com.agritrack.agritrackplus.DAO.TareaDAO" %>
<%@ page import="com.agritrack.agritrackplus.modelo.Tarea" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Instanciamos el DAO y obtenemos las listas dinámicas
    TareaDAO dao = new TareaDAO();
    List<Tarea> listaCultivos = dao.listarCultivos();
    List<Tarea> listaTrabajadores = dao.listarTrabajadores();
    
    // Necesitaremos este método en TareaDAO para el select de "Nombre de la Tarea"
    // Si no lo tienes, puedes usar un input de texto, pero lo ideal es el catálogo de la BD
    List<Tarea> catalogoTareas = dao.listarCatalogoTareas(); 
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
          <img src="../../asset/imagenes/devolver.png" id="icono de devolver">
        </div>
      </a>
        <div class="contenedor__titulo">
            <div class="contenedor__logo">
              <img class="logo" src="../../asset/imagenes/hoja (3).png" alt="hoja del logo" />
            </div>
            <h1 class="titulo">Agregar Tarea</h1>
        </div>
    </header>
    <main>
        <form action="${pageContext.request.contextPath}/AsignarTareaServlet" method="POST">
            <div class="contendor__padre">
                 <div class="seccion">
                    <div class="seccion__header">
                        <h3 class="seccion__titulo">Información de la Tarea</h3>
                    </div>
                    
                    <div class="seccion__contenido">
                        <div class="campo">
                            <label class="label">Seleccionar Cultivo <span class="requerido">*</span></label>
                            <select class="input" name="cboCultivo" required>
                                <option value="">Seleccionar...</option>
                                <% for(Tarea c : listaCultivos) { %>
                                    <option value="<%= c.getCultivoId() %>"><%= c.getNombreCultivo() %></option>
                                <% } %>
                            </select>
                        </div>

                        <div class="campo">
                            <label class="label">Nombre de la Tarea <span class="requerido">*</span></label>
                            <input type="text" 
                                   class="input" 
                                   name="txtNombreTarea" 
                                   placeholder="Escriba el nombre de la labor..." 
                                   required>
                        </div>

                        <div class="campo">
                            <label class="label">Descripción <span class="requerido">*</span></label>
                            <textarea class="input textarea" name="txtDescripcion" rows="3" placeholder="Describe la tarea en detalle..." required></textarea>
                        </div>

                        <div class="campo__grupo">
                            <div class="campo">
                                <label class="label">Estado <span class="requerido">*</span></label>
                                <select class="input" name="cboEstado" required>
                                    <option value="pendiente">Pendiente</option>
                                    <option value="proceso">En Proceso</option>
                                </select>
                            </div>

                            <div class="campo">
                                <label class="label">Fecha Programada <span class="requerido">*</span></label>
                                <input type="date" name="txtFecha" class="input" required>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="seccion seccion__trabajadores">
                    <div class="seccion__header seccion__header--trabajadores">
                        <h3 class="seccion__titulo"> Asignar Trabajadores</h3>
                    </div>

                    <div class="seccion__contenido">
                        <div class="asignacion__card">
                            <div class="campo__grupo--asignacion">
                                <div class="campo campo--small">
                                    <label class="label label--small">Usuario</label>
                                    <select class="input input--small" name="cboTrabajador" required>
                                        <option value="">Seleccionar...</option>
                                        <% for(Tarea tr : listaTrabajadores) { %>
                                            <option value="<%= tr.getId() %>"><%= tr.getNombreTrabajador() %></option>
                                        <% } %>
                                    </select>
                                </div>

                                <div class="campo campo--small">
                                    <label class="label label--small">Jornada</label>
                                    <select class="input input--small" name="cboJornada" required>
                                        <option value="">Seleccionar...</option>
                                        <option value="Mañana">Mañana</option>
                                        <option value="Tarde">Tarde</option>
                                        <option value="Completa">Completa</option>
                                    </select>
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
    document.addEventListener("DOMContentLoaded", function() {
        // 1. Limitar la fecha para que no sea anterior a hoy
        const inputFecha = document.querySelector('input[type="date"]');
        const hoy = new Date().toISOString().split('T')[0];
        inputFecha.setAttribute('min', hoy);
        inputFecha.value = hoy; // Opcional: poner hoy por defecto

        // 2. Validación de envío del formulario
        const formulario = document.querySelector('form');
        formulario.addEventListener('submit', function(event) {
            const selects = formulario.querySelectorAll('select[required]');
            let valido = true;

            selects.forEach(select => {
                if (select.value === "") {
                    valido = false;
                    select.style.borderColor = "red"; // Marca el error visualmente
                } else {
                    select.style.borderColor = ""; // Limpia si ya seleccionó
                }
            });

            if (!valido) {
                event.preventDefault();
                alert("Por favor, completa todos los campos obligatorios marcados con asterisco (*).");
            }
        });
    });
</script>
</body>
</html>