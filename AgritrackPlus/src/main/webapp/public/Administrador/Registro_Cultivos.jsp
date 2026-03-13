<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.ProductoDAO" %>
<%@ page import="java.util.List, java.util.Map" %>
<%@ page import="com.agritrack.agritrackplus.DAO.UsuarioDAO" %>
<%
    UsuarioDAO usuarioDAO = new UsuarioDAO();
    List<Map<String, String>> supervisores = usuarioDAO.listarSoloSupervisores();
    List<Map<String, String>> trabajadores = usuarioDAO.listarSoloTrabajadores();
    
    ProductoDAO productoDAO = new ProductoDAO();
    List<Map<String, String>> productos = productoDAO.listarProductos();
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Registrar Cultivo</title>
  <link rel="stylesheet" href="../../asset/Administrador/style_RegistroCultivos.css">
  
</head>
<body>
  <header>
    <a href="Admin.jsp">
      <div class="icono__devolver">
        <img src="../../asset/imagenes/devolver.png" id="icono de devolver">
      </div>
    </a>
    <div class="contenedor__titulo">
      <div class="contenedor__logo">
        <img class="logo" src="../../asset/imagenes/hoja (3).png" alt="hoja del logo">
      </div>
      <h1 class="titulo">Registrar Nuevo Cultivo</h1>
    </div>
  </header>

  <main class="main">
    <div class="contendor">

      <% if ("true".equals(request.getParameter("error"))) { %>
        <p style="color:red; font-weight: bold;">Hubo un error al registrar el cultivo. Intenta de nuevo.</p>
      <% } %>

    <form class="formulario__registrarcultivo" method="post" action="<%= request.getContextPath() %>/Registro_CultivoServlet">
        
        <input type="hidden" name="estado" value="Activo">

        <div class="contendor__cajas">
          <div class="contendor__subtitulo">
            <div class="caja__logo">
              <img class="logo" src="../../asset/imagenes/planta (2).png" alt="logo planta">
            </div>
            <h2 class="subtitulo">Información Básica</h2>
          </div>

          <div class="campo">
            <label for="nombre">Nombre del cultivo</label>
            <input type="text" id="nombre" name="nombre" placeholder="Ingresa el nombre del cultivo" required>
          </div>

          <div class="fechas">
            <div class="campo">
              <label for="fecha_siembra">Fecha de siembra</label>
              <input type="date" id="fecha_siembra" name="fecha_siembra" required>
            </div>
          </div>

          <div class="campo">
            <label for="ciclo">Ciclo del cultivo</label>
            <select id="ciclo" name="ciclo" required>
              <option value="">Seleccione...</option>
              <option value="Vegetativo">Vegetativo</option>
              <option value="Floracion">Floración</option>
              <option value="Maduracion">Maduración</option>
            </select>
          </div>
        </div>

        <div class="contendor__cajas" id="stockBase">
          <div class="contendor__subtitulo">
            <div class="caja__logo">
              <img class="logo" src="../../asset/imagenes/stock.png" alt="icono stock">
            </div>
            <h2 class="subtitulo">Stock de Productos</h2>
          </div>

          <div id="contenedor__productos">
            <div class="contenedor__campos fila__stock">
              <div class="campo">
                <label>Producto</label>
                <select name="producto_id">
                  <option value="">Seleccione...</option>
                  <% for (Map<String, String> producto : productos) { %>
                    <option value="<%= producto.get("id") %>">
                      <%= producto.get("nombre") %> - <%= producto.get("tipo_nombre") %>
                    </option>
                  <% } %>
                </select>
              </div>
              <div class="campo">
                <label>Cantidad</label>
                <input type="number" name="cantidad_producto" class="input-cantidad" min="0" placeholder="Ingrese la cantidad">
              </div>
            </div>
          </div>

          <button type="button" class="boton__agregar__producto" onclick="agregarProducto()">+ Agregar otro producto</button>
        </div>

        <div class="contendor__cajas supervisor__caja">
          <div class="contendor__subtitulo">
            <div class="caja__logo">
              <img class="logo" src="../../asset/imagenes/supervisor.png" alt="icono supervisor">
            </div>
            <h2 class="subtitulo">Supervisor de campo</h2>
          </div>

          <div class="campo">
            <label for="supervisor">Supervisor asignado</label>
            <select id="supervisor" name="supervisor_id" required>
                <option value="">Seleccione un supervisor...</option>
                <% for (Map<String, String> s : supervisores) { %>
                    <option value="<%= s.get("id") %>"><%= s.get("nombre") %></option>
                <% } %>
            </select>
          </div>
        </div>

        <div class="contendor__cajas trabajadores__caja">
          <div class="contendor__subtitulo">
            <div class="caja__logo">
              <img class="logo" src="../../asset/imagenes/pala.png" alt="icono trabajadores">
            </div>
            <h2 class="subtitulo">Trabajadores</h2>
          </div>

          <p>Seleccione los trabajadores que van a laborar en su cultivo:</p>
             <ul class="lista__trabajadores">
                <% if (trabajadores.isEmpty()) { %>
                    <li>No hay trabajadores activos disponibles.</li>
                <% } else {
                    for (Map<String, String> t : trabajadores) { %>
                    <li>
                        <label>
                            <input type="checkbox" name="trabajadores" value="<%= t.get("id") %>"> 
                            <%= t.get("nombre") %>
                        </label>
                    </li>
                <% } } %>
            </ul>
        </div>

        <div class="contenedor__boton--enviar">
          <button type="submit" class="boton__enviar">&#128640; Enviar</button>
        </div>

      </form>
    </div>
  </main>

  <script>
    /**
     * Función para validar que no haya negativos y mostrar alerta visual
     */
    function validarCantidad(input) {
        const alerta = input.nextElementSibling;
        
        input.addEventListener('input', function() {
            if (this.value < 0) {
                this.value = 0;
                alerta.style.display = 'block';
                this.classList.add('input-error');
            } else {
                alerta.style.display = 'none';
                this.classList.remove('input-error');
            }
        });

        // Bloquea la tecla física del signo menos
        input.addEventListener('keydown', function(e) {
            if (e.key === '-') {
                e.preventDefault();
            }
        });
    }

    function agregarProducto() {
      var opciones = `<option value="">Seleccione...</option>
      <% for (Map<String, String> producto : productos) { %>
        <option value="<%= producto.get("id") %>"><%= producto.get("nombre") %> - <%= producto.get("tipo_nombre") %></option>
      <% } %>`;

      var nuevaFila = document.createElement("div");
      nuevaFila.classList.add("contenedor__campos", "fila__stock");
      nuevaFila.innerHTML = `
        <div class="campo">
          <label>Producto</label>
          <select name="producto_id">` + opciones + `</select>
        </div>
        <div class="campo">
          <label>Cantidad</label>
          <input type="number" name="cantidad_producto" class="input-cantidad" min="0" placeholder="Ingrese la cantidad">
          
          
        </div>
        <button type="button" class="boton__eliminar__fila" onclick="this.parentElement.remove()">✕ Quitar</button>
      `;
      document.getElementById("contenedor__productos").appendChild(nuevaFila);
      
      // Aplicar la validación al nuevo campo creado
      validarCantidad(nuevaFila.querySelector('.input-cantidad'));
    }
    
    document.addEventListener("DOMContentLoaded", function() {
        const inputSiembra = document.getElementById('fecha_siembra');

        // 1. Obtener la fecha de hoy en formato local (Colombia)
        const hoy = new Date();
        const anio = hoy.getFullYear();
        const mes = String(hoy.getMonth() + 1).padStart(2, '0');
        const dia = String(hoy.getDate()).padStart(2, '0');
        
        const fechaActual = `\${anio}-\${mes}-\${dia}`;

        // 2. Bloquear visualmente el calendario
        inputSiembra.setAttribute('max', fechaActual);

        // 3. Validación de seguridad fecha
        inputSiembra.addEventListener('blur', function() {
            if (this.value > fechaActual) {
                alert("La fecha de siembra no puede ser mayor a la fecha actual.");
                this.value = fechaActual;
            }
        });

        // 4. Aplicar validación de cantidad a los campos existentes al cargar
        document.querySelectorAll('.input-cantidad').forEach(validarCantidad);
    });
  </script>
</body>
</html>