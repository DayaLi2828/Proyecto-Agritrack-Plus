<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.UsuarioDAO" %>
<%@ page import="java.util.List, java.util.Map" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Gestión de Usuarios</title>
  <link rel="stylesheet" href="../../asset/Administrador/style_Usuarios.css"/>
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
        <img class="logo" src="../../asset/imagenes/hoja (3).png" alt="hoja del logo"/>
      </div>
      <h1 class="titulo">Gestión de Usuarios</h1>
    </div>
  </header>

  <main>
    <%
      UsuarioDAO dao = new UsuarioDAO();
      List<Map<String, String>> usuarios = dao.listarUsuarios();
      int totalUsuarios = usuarios.size();
      int totalActivos = 0;
      int totalInactivos = 0;
      for (Map<String, String> u : usuarios) {
        if ("Activo".equals(u.get("estado"))) totalActivos++;
        else totalInactivos++;
      }
    %>

    <div class="contenedor__tarjetas">
      <div class="tarjeta__contador">
        <h2><%= totalUsuarios %></h2>
        <p>Total de usuarios</p>
      </div>
      <div class="tarjeta__contador">
        <h2><%= totalActivos %></h2>
        <p>Usuarios activos</p>
      </div>
      <div class="tarjeta__contador">
        <h2><%= totalInactivos %></h2>
        <p>Usuarios inactivos</p>
      </div>
    </div>

    <div class="buscardor__usuario">
      <div class="contenedor__buscar">
        <input type="text" id="buscador" class="input__buscador" placeholder="Buscar usuario por nombre o número de documento" aria-label="Buscar"/>
        <img class="icono__buscador" src="../../asset/imagenes/lupa.png" alt="Icono de búsqueda"/>
      </div>
        <a href="Agregar_Usuario.jsp" class="boton">Añadir Usuario</a>
    </div>

    <table id="tablaUsuarios">
      <caption>Lista de usuarios</caption>
      <thead>
        <tr>
          <th>ID</th>
          <th>Foto</th>
          <th>Nombre</th>
          <th>Documento</th>
          <th>Dirección</th>
          <th>Correo</th>
          <th>Teléfono</th>
          <th>Rol</th>
          <th>Estado</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <%
          if (usuarios.isEmpty()) {
        %>
          <tr>
            <td colspan="10" style="text-align:center;">No hay usuarios registrados.</td>
          </tr>
        <%
          } else {
            for (Map<String, String> usuario : usuarios) {
             String fotoRuta = usuario.get("foto");
        %>
          <tr>
            <td><%= usuario.get("id") %></td>
            <td>
              <% if (fotoRuta != null) { %>
                <img src="../../<%= fotoRuta %>" alt="Foto usuario" style="width:50px; height:50px; object-fit:cover;">
              <% } else { %>
                <span>Sin foto</span>
              <% } %>
            </td>
            <td><%= usuario.get("nombre") %></td>
            <td><%= usuario.get("documento") %></td>
            <td><%= usuario.get("direccion") %></td>
            <td><%= usuario.get("correo") %></td>
            <td><%= usuario.get("telefono") %></td>
            <td><%= usuario.get("rol") %></td>
            <td><%= usuario.get("estado") %></td>
            <td>
              <a href="Agregar_Usuario.jsp?id=<%= usuario.get("id") %>" class="boton__editar">Editar</a>
              <a href="../../EliminarUsuarioServlet?id=<%= usuario.get("id") %>"
                 class="boton__eliminar"
                 onclick="return confirm('¿Estás seguro de eliminar este usuario?')">Eliminar</a>
            </td>
          </tr>
        <%
            }
          }
        %>
      </tbody>
    </table>
  </main>

  <script>
    document.getElementById("buscador").addEventListener("input", function() {
      let texto = this.value.toLowerCase();
      let filas = document.querySelectorAll("#tablaUsuarios tbody tr");
      filas.forEach(function(fila) {
        let nombre = fila.cells[2] ? fila.cells[2].textContent.toLowerCase() : "";
        let documento = fila.cells[3] ? fila.cells[3].textContent.toLowerCase() : "";
        if (nombre.includes(texto) || documento.includes(texto)) {
          fila.style.display = "";
        } else {
          fila.style.display = "none";
        }
      });
    });
  </script>
</body>
</html>
