<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.ProductoDAO" %>
<%@ page import="java.util.List, java.util.Map" %>
<%@ page import="com.agritrack.agritrackplus.DAO.UsuarioDAO" %>
<%
  ProductoDAO productoDAO = new ProductoDAO();
  List<Map<String, String>> productos = productoDAO.listarProductos();
  
  UsuarioDAO usuarioDAO = new UsuarioDAO();
  List<Map<String, String>> usuarios = usuarioDAO.listarUsuarios();
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
        <p style="color:red;">Hubo un error al registrar el cultivo. Intenta de nuevo.</p>
      <% } %>

      <form class="formulario__registrarcultivo" method="post" action="../../RegistroCultivoServlet">

        <!-- Información básica -->
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
            <div class="campo">
              <label for="fecha_cosecha">Fecha de cosecha</label>
              <input type="date" id="fecha_cosecha" name="fecha_cosecha">
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

          <div class="campo">
            <label for="estado">Estado</label>
            <select id="estado" name="estado" required>
              <option value="Activo">Activo</option>
              <option value="Inactivo">Inactivo</option>
            </select>
          </div>
        </div>

        <!-- Stock de productos -->
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
                <select name="producto_id[]">
                  <option value="">Seleccione...</option>
                  <%
                    for (Map<String, String> producto : productos) {
                  %>
                    <option value="<%= producto.get("id") %>">
                      <%= producto.get("nombre") %> - <%= producto.get("tipo") %>
                    </option>
                  <%
                    }
                  %>
                </select>
              </div>
              <div class="campo">
                <label>Cantidad</label>
                <input type="number" name="cantidad_producto[]" min="1" placeholder="Ingrese la cantidad">
              </div>
            </div>
          </div>

          <button type="button" class="boton__agregar__producto" onclick="agregarProducto()">+ Agregar otro producto</button>
        </div>

        <!-- Supervisor -->
        <div class="contendor__cajas supervisor__caja">
          <div class="contendor__subtitulo">
            <div class="caja__logo">
              <img class="logo" src="../../asset/imagenes/supervisor.png" alt="icono supervisor">
            </div>
            <h2 class="subtitulo">Supervisor de campo</h2>
          </div>

          <div class="campo">
            <label for="supervisor">Supervisor asignado</label>
            <select id="supervisor" name="supervisor_id">
              <option value="">Seleccione...</option>
              <%
                for (Map<String, String> usuario : usuarios) {
              %>
                <option value="<%= usuario.get("id") %>">
                  <%= usuario.get("nombre") %>
                </option>
              <%
                }
              %>
            </select>
</div>
        </div>

        <!-- Trabajadores -->
        <div class="contendor__cajas trabajadores__caja">
          <div class="contendor__subtitulo">
            <div class="caja__logo">
              <img class="logo" src="../../asset/imagenes/pala.png" alt="icono trabajadores">
            </div>
            <h2 class="subtitulo">Trabajadores</h2>
          </div>

          <p>Seleccione los trabajadores que van a laborar en su cultivo:</p>
          <ul class="lista__trabajadores">
            <li><label><input type="checkbox" name="trabajadores[]" value="1"> Juan Pérez</label></li>
            <li><label><input type="checkbox" name="trabajadores[]" value="2"> María Gómez</label></li>
            <li><label><input type="checkbox" name="trabajadores[]" value="3"> Carlos Rodríguez</label></li>
            <li><label><input type="checkbox" name="trabajadores[]" value="4"> Laura Martínez</label></li>
            <li><label><input type="checkbox" name="trabajadores[]" value="5"> Andrés López</label></li>
          </ul>
        </div>

        <!-- Botón enviar -->
        <div class="contenedor__boton--enviar">
          <button type="submit" class="boton__enviar">&#128640; Enviar</button>
        </div>

      </form>
    </div>
  </main>
        <script>
            function agregarProducto() {
              var opciones = `<option value="">Seleccione...</option>
              <%
                for (Map<String, String> producto : productos) {
              %>
                <option value="<%= producto.get("id") %>"><%= producto.get("nombre") %> - <%= producto.get("tipo") %></option>
              <%
                }
              %>`;

              var nuevaFila = document.createElement("div");
              nuevaFila.classList.add("contenedor__campos", "fila__stock");
              nuevaFila.innerHTML = `
                <div class="campo">
                  <label>Producto</label>
                  <select name="producto_id[]">` + opciones + `</select>
                </div>
                <div class="campo">
                  <label>Cantidad</label>
                  <input type="number" name="cantidad_producto[]" min="1" placeholder="Ingrese la cantidad">
                </div>
                <button type="button" class="boton__eliminar__fila" onclick="this.parentElement.remove()">✕ Quitar</button>
              `;
              document.getElementById("contenedor__productos").appendChild(nuevaFila);
            }
        </script>
</body>
</html>