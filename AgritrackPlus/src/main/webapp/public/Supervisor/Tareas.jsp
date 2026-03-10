<%@ page import="com.agritrack.agritrackplus.DAO.TareaDAO" %>
<%@ page import="com.agritrack.agritrackplus.modelo.Tarea" %>
<%@ page import="java.util.List" %>
<%
    TareaDAO dao = new TareaDAO();
    List<Tarea> listaTareas = dao.listarTareas();

    int totalPendientes = 0;
    int totalEnProceso = 0;
    int totalCompletadas = 0;
    int total = 0;

    if (listaTareas != null) {
        total = listaTareas.size();
        for (Tarea t : listaTareas) {
            if (t.getEstado().equalsIgnoreCase("Pendiente")) totalPendientes++;
            else if (t.getEstado().equalsIgnoreCase("En Proceso")) totalEnProceso++;
            else if (t.getEstado().equalsIgnoreCase("Completada")) totalCompletadas++;
        }
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Tareas - AgritrackPlus</title>
    <link rel="stylesheet" href="../../asset/Supervisor/style-tareas.css">
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
                <img class="logo" src="../../asset/imagenes/hoja (3).png" alt="hoja del logo" />
            </div>
            <h1 class="titulo"> Gestión de tareas</h1>
            <a href="Agregar_Tarea.jsp" class="boton">Agregar tarea</a>
        </div>
    </header>

    <main>
        <div class="contenedor__tareas">
            <div class="cajas__tareas">
                <img class="logo__total" src="../../asset/imagenes/comprobacion.png" alt="Icono de tarea">
                <h2><%= total %></h2> <p>Total de Tareas</p>
            </div>

            <div class="cajas__tareas caja__segunda">
                <img class="logo__total" src="../../asset/imagenes/comprobacion.png" alt="Icono de tarea">
                <h2><%= totalPendientes %></h2>
                <p>Pendientes</p>
            </div>

            <div class="cajas__tareas caja__tercera">
                <img class="logo__total" src="../../asset/imagenes/comprobacion.png" alt="Icono de tarea">
                <h2><%= totalCompletadas %></h2>
                <p>Completadas</p>
            </div>

            <div class="cajas__tareas caja__cuarta">
              <img class="logo__total" src="../../asset/imagenes/comprobacion.png" alt="Icono de tarea">
              <h2><%= totalEnProceso %></h2>
              <p>En proceso</p>
          </div>
        </div>

        <div class="buscardor__tareas">
            <div class="contenedor__buscar">
              <input type="text" id="buscadorInput" class="input__buscador" placeholder="Buscar..." aria-label="Buscar">
              <img class="icono__buscador" src="../../asset/imagenes/lupa.png" alt="Icono de búsqueda">
            </div>

            <select id="filtroEstado" class="select__tareas">
                <option value="todas">Todas las tareas</option>
                <option value="Pendiente">Tareas pendientes</option>
                <option value="En Proceso">Tareas en proceso</option>
                <option value="Completada">Tareas completadas</option>
            </select>
        </div>

        <% if(listaTareas != null && !listaTareas.isEmpty()) { 
            for(Tarea tarea : listaTareas) { 
                // Generamos un string limpio para la clase CSS (ej: "enproceso")
                String claseEstado = tarea.getEstado().toLowerCase().replace(" ", "");
        %>
        
        <div class="contendor__padre">
          <div class="contenedor__cultivotarea">
            <div class="logo__tarea">
              <img class="logo" src="../../asset/imagenes/planta (2).png" alt="hoja del logo" />
            </div>
            <h2 class="titulo__cultivo"><%= tarea.getNombreCultivo() %></h2>
            
            <div class="boxs">
              <p class="texto-estado-<%= claseEstado %>"><%= tarea.getEstado() %></p>
            </div>
          </div>
          
          <div class="contenedores__tareas">
            <h2><%= tarea.getNombreTarea() %></h2>
            
            <p class="texto__tarea"><%= tarea.getDescripcion() %></p>
            <p class="texto__tarea fecha">Jornada: <%= tarea.getJornada() %></p>
  
            <div class="contendor__asignaciones">
              <div class="logo__texto">
                <img class="iconos__asignaciones" src="../../asset/imagenes/usuario(2).png" id="perfil de usuario">
                <p class="texto__asignaciones">Asignado a:</p>
              </div>
              
              <div class="contenedor__usuario">
                <div class="tarjeta__usuario">
                  <div class="cirulo__perfil">
                    <p class="incial__perfil"><%= (tarea.getNombreTrabajador() != null && !tarea.getNombreTrabajador().isEmpty()) ? tarea.getNombreTrabajador().substring(0,1).toUpperCase() : "?" %></p>
                  </div>
                  <h4><%= (tarea.getNombreTrabajador() != null) ? tarea.getNombreTrabajador() : "Sin asignar" %></h4>
                  <p class="texto__jornada"><%= tarea.getJornada() %></p>
                  
                  <%-- Aplicamos la clase de color dinámicamente al div estado --%>
                  <div class="estado estado-<%= claseEstado %>">
                    <p><%= tarea.getEstado() %></p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <%    } 
           } else { %>
            <p style="text-align: center; margin-top: 20px; color: var(--color-texto-mensaje-vacio);">No hay tareas asignadas actualmente.</p>
        <% } %>

    </main>

    <script>
        // Lógica de búsqueda y filtrado
        const buscador = document.getElementById('buscadorInput');
        const filtro = document.getElementById('filtroEstado');
        const tareasCards = document.querySelectorAll('.contendor__padre');

        function filtrar() {
            const texto = buscador.value.toLowerCase();
            const estadoSel = filtro.value;

            tareasCards.forEach(card => {
                const tituloTarea = card.querySelector('.contenedores__tareas h2').textContent.toLowerCase();
                const tituloCultivo = card.querySelector('.titulo__cultivo').textContent.toLowerCase();
                const estadoTarea = card.querySelector('.estado p').textContent.trim();

                const coincideTexto = tituloTarea.includes(texto) || tituloCultivo.includes(texto);
                const coincideEstado = estadoSel === 'todas' || estadoTarea === estadoSel;

                card.style.display = (coincideTexto && coincideEstado) ? "" : "none";
            });
        }

        buscador.addEventListener('input', filtrar);
        filtro.addEventListener('change', filtrar);

        // MODIFICACIÓN: Detectar éxito al agregar tarea
        window.onload = function() {
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('status') === 'success') {
                alert("¡Tarea creada y asignada con éxito!");
                // Limpia la URL para que el mensaje no salga de nuevo al recargar
                window.history.replaceState({}, document.title, window.location.pathname);
            }
        };
    </script>
</body>
</html>