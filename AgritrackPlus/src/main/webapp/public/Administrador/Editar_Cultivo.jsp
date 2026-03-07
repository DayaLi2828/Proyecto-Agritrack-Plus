<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.Registro_CultivoDAO" %>
<%@ page import="com.agritrack.agritrackplus.DAO.UsuarioDAO" %>
<%@ page import="com.agritrack.agritrackplus.DAO.ProductoDAO" %>
<%@ page import="java.util.List, java.util.Map, java.util.ArrayList" %>
<%
    // Recuperación de ID y datos (Lógica necesaria para que el formulario no esté vacío)
    String idParam = request.getParameter("id");
    Registro_CultivoDAO dao = new Registro_CultivoDAO();
    Map<String, String> cultivo = dao.obtenerPorId(idParam);
    
    UsuarioDAO usuarioDAO = new UsuarioDAO();
    List<Map<String, String>> supervisores = usuarioDAO.listarSoloSupervisores();
    List<Map<String, String>> trabajadores = usuarioDAO.listarSoloTrabajadores();
    
    ProductoDAO productoDAO = new ProductoDAO();
    List<Map<String, String>> productos = productoDAO.listarProductos();

    // Listas para marcar lo que ya está asignado
    List<Map<String, String>> trabajadoresAsignados = dao.obtenerTrabajadoresCultivo(Integer.parseInt(idParam));
    List<String> idsTrabajadoresAsignados = new ArrayList<>();
    if (trabajadoresAsignados != null) {
        for (Map<String, String> t : trabajadoresAsignados) {
            idsTrabajadoresAsignados.add(String.valueOf(t.get("id"))); 
        }
    }
    List<Map<String, String>> productosAsignados = dao.obtenerProductosCultivo(Integer.parseInt(idParam));
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Editar Cultivo</title>
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
      <h1 class="titulo">Editar Cultivo</h1>
    </div>
  </header>

  <main class="main">
    <div class="contendor">

    <form class="formulario__registrarcultivo" method="post" action="<%= request.getContextPath() %>/EditarCultivoServlet">
        <input type="hidden" name="id" value="<%= cultivo.get("id") %>">
        
        <div class="contendor__cajas">
          <div class="contendor__subtitulo">
            <div class="caja__logo">
              <img class="logo" src="../../asset/imagenes/planta (2).png" alt="logo planta">
            </div>
            <h2 class="subtitulo">Información Básica</h2>
          </div>

          <div class="campo">
            <label for="nombre">Nombre del cultivo</label>
            <input type="text" id="nombre" name="nombre" value="<%= cultivo.get("nombre") %>" required>
          </div>

          <div class="fechas">
            <div class="campo">
              <label for="fecha_siembra">Fecha de siembra</label>
              <input type="date" id="fecha_siembra" name="fecha_siembra" value="<%= cultivo.get("fecha_siembra") %>" required>
            </div>
            <div class="campo">
              <label for="fecha_cosecha">Fecha de cosecha</label>
              <input type="date" id="fecha_cosecha" name="fecha_cosecha" value="<%= cultivo.get("fecha_cosecha") != null ? cultivo.get("fecha_cosecha") : "" %>">
            </div>
          </div>

          <div class="campo">
            <label for="ciclo">Ciclo del cultivo</label>
            <select id="ciclo" name="ciclo" required>
              <option value="Vegetativo" <%= "Vegetativo".equals(cultivo.get("ciclo")) ? "selected" : "" %>>Vegetativo</option>
              <option value="Floracion" <%= "Floracion".equals(cultivo.get("ciclo")) ? "selected" : "" %>>Floración</option>
              <option value="Maduracion" <%= "Maduracion".equals(cultivo.get("ciclo")) ? "selected" : "" %>>Maduración</option>
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
            <% if (productosAsignados != null && !productosAsignados.isEmpty()) {
                for (Map<String, String> pa : productosAsignados) { %>
                <div class="contenedor__campos fila__stock">
                  <div class="campo">
                    <label>Producto</label>
                    <select name="producto_id">
                      <% for (Map<String, String> p : productos) { %>
                        <option value="<%= p.get("id") %>" <%= String.valueOf(p.get("id")).equals(String.valueOf(pa.get("id"))) ? "selected" : "" %>>
                          <%= p.get("nombre") %> - <%= p.get("tipo") %>
                        </option>
                      <% } %>
                    </select>
                  </div>
                  <div class="campo">
                    <label>Cantidad</label>
                    <input type="number" name="cantidad_producto" min="1" value="<%= pa.get("cantidad") %>">
                  </div>
                  <button type="button" class="boton__eliminar__fila" onclick="this.parentElement.remove()">✕ Quitar</button>
                </div>
            <% } } %>
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
                <% for (Map<String, String> s : supervisores) { %>
                    <option value="<%= s.get("id") %>" <%= String.valueOf(s.get("id")).equals(cultivo.get("supervisor_id")) ? "selected" : "" %>>
                        <%= s.get("nombre") %>
                    </option>
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
                <% for (Map<String, String> t : trabajadores) { %>
                    <li>
                        <label>
                            <input type="checkbox" name="trabajadores" value="<%= t.get("id") %>"
                                <%= idsTrabajadoresAsignados.contains(String.valueOf(t.get("id"))) ? "checked" : "" %>> 
                            <%= t.get("nombre") %>
                        </label>
                    </li>
                <% } %>
            </ul>
        </div>

        <div class="contenedor__boton--enviar">
          <button type="submit" class="boton__enviar">💾 Guardar Cambios</button>
        </div>

      </form>
    </div>
  </main>

    <script>
        function agregarProducto() {
        const contenedor = document.getElementById("contenedor__productos");

        // 1. Crear el contenedor principal de la fila
        const nuevaFila = document.createElement("div");
        nuevaFila.classList.add("contenedor__campos", "fila__stock");

        // --- COLUMNA PRODUCTO ---
        const divProducto = document.createElement("div");
        divProducto.classList.add("campo");

        const labelProducto = document.createElement("label");
        labelProducto.textContent = "Producto";

        const selectProducto = document.createElement("select");
        selectProducto.name = "producto_id";

        const optionDefault = document.createElement("option");
        optionDefault.value = "";
        optionDefault.textContent = "Seleccione...";
        selectProducto.appendChild(optionDefault);

        // Llenar opciones desde JSP
        <% for (Map<String, String> producto : productos) { %>
            const opt<%= producto.get("id") %> = document.createElement("option");
            opt<%= producto.get("id") %>.value = "<%= producto.get("id") %>";
            opt<%= producto.get("id") %>.textContent = "<%= producto.get("nombre") %> - <%= producto.get("tipo") %>";
            selectProducto.appendChild(opt<%= producto.get("id") %>);
        <% } %>

        divProducto.appendChild(labelProducto);
        divProducto.appendChild(selectProducto);

        // --- COLUMNA CANTIDAD ---
        const divCantidad = document.createElement("div");
        divCantidad.classList.add("campo");

        const labelCantidad = document.createElement("label");
        labelCantidad.textContent = "Cantidad";

        const inputCantidad = document.createElement("input");
        inputCantidad.type = "number";
        inputCantidad.name = "cantidad_producto";
        inputCantidad.min = "1";
        inputCantidad.placeholder = "Ingrese la cantidad";

        divCantidad.appendChild(labelCantidad);
        divCantidad.appendChild(inputCantidad);

        // --- BOTÓN ELIMINAR ---
        const btnEliminar = document.createElement("button");
        btnEliminar.type = "button";
        btnEliminar.classList.add("boton__eliminar__fila");
        btnEliminar.textContent = "✕ Quitar";
        btnEliminar.onclick = function() {
            nuevaFila.remove();
        };

        // 2. Armar la estructura final
        nuevaFila.appendChild(divProducto);
        nuevaFila.appendChild(divCantidad);
        nuevaFila.appendChild(btnEliminar);

        // 3. Insertar en el contenedor
        contenedor.appendChild(nuevaFila);
    }
    </script>
</body>
</html>