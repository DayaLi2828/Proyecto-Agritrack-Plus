<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.UsuarioDAO" %>
<%@ page import="java.util.List, java.util.Map" %>

<%
    UsuarioDAO dao = new UsuarioDAO();
        List<Map<String, String>> usuarios = dao.listarUsuarios();

    if (usuarios == null) {
        usuarios = new java.util.ArrayList<>();
    }

        int totalUsuarios = usuarios.size();
        int totalActivos = 0;
        int totalInactivos = 0;

    String mensaje = request.getParameter("mensaje");

    for (Map<String, String> u : usuarios) {

        String estado = u.get("estado");

        if ("Activo".equalsIgnoreCase(estado)) {
            totalActivos++;
        } else {
            totalInactivos++;
        }

    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Gestión de Usuarios</title>
  <link rel="stylesheet" href="../../asset/Administrador/style_Usuarios.css"/>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
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
    <!-- ✅ MENSAJES ÉXITO -->
    <% if ("estado".equals(mensaje)) { %>
      <div class="alerta-exito">
         Estado actualizado correctamente
      </div>
    <% } else if ("eliminado".equals(mensaje)) { %>
      <div class="alerta-exito">
         Usuario eliminado correctamente
      </div>
    <% } %>

    <!-- CONTADORES -->
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

    <!-- BUSCADOR + BOTÓN -->
    <div class="buscardor__usuario">
      <div class="contenedor__buscar">
        <input type="text" id="buscador" class="input__buscador" placeholder="Buscar usuario por nombre o número de documento" aria-label="Buscar"/>
        <img class="icono__buscador" src="../../asset/imagenes/lupa.png" alt="Icono de búsqueda"/>
      </div>
      <a href="Agregar_Usuario.jsp" class="boton">Añadir Usuario</a>
    </div>

    <!-- TABLA  CORREGIDA COMPLETA -->
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
  <% if (usuarios == null || usuarios.isEmpty()) { %>
    <tr>
      <td colspan="10" style="text-align:center; padding: 40px; color: #6b7280;">
        <i class="fas fa-users"></i>
        No hay usuarios registrados
      </td>
    </tr>
  <% } else { %>
    <% for (Map<String, String> usuario : usuarios) {
        // Aseguramos que no haya valores nulos para evitar errores de renderizado
        String id = String.valueOf(usuario.get("id"));
        String nombre = usuario.get("nombre") != null ? usuario.get("nombre") : "N/A";
        String doc = usuario.get("documento") != null ? usuario.get("documento") : "N/A";
        String correo = usuario.get("correo") != null ? usuario.get("correo") : "Sin correo";
        String tel = usuario.get("telefono") != null ? usuario.get("telefono") : "Sin tel";
        String rol = usuario.get("rol") != null ? usuario.get("rol") : "Sin rol";
        String estado = usuario.get("estado") != null ? usuario.get("estado") : "Inactivo";
        String fotoRuta = usuario.get("foto");
        boolean esActivo = "Activo".equalsIgnoreCase(estado);
    %>
      <tr>
        <td><strong><%= id %></strong></td>
        <td>
          <% if (fotoRuta != null && !fotoRuta.trim().isEmpty()) { %>
            <img src="../../<%= fotoRuta %>" alt="Foto" class="foto-usuario" 
                 onerror="this.src='../../asset/imagenes/default-avatar.png';">
          <% } else { %>
            <div >S/F</div>
          <% } %>
        </td>
        <td><%= nombre %></td>
        <td><%= doc %></td>
        <td><%= usuario.get("direccion") != null ? usuario.get("direccion") : "-" %></td>
        <td><%= correo %></td>
        <td><%= tel %></td>
        <td>
          <span class="rol-badge <%= "trabajador".equalsIgnoreCase(rol) ? "rol-trabajador" : "rol-supervisor" %>">
            <%= rol %>
          </span>
        </td>
        <td>
          <form method="POST" action="<%= request.getContextPath() %>/CambiarEstadoServlet">
            <input type="hidden" name="id" value="<%= id %>">
            <button type="submit" class="btn-estado <%= esActivo ? "btn-activo" : "btn-inactivo" %>">
              <%= esActivo ? "🟢 ACTIVO" : "🔴 INACTIVO" %>
            </button>
          </form>
        </td>
        <td class="acciones">
          <a href="Agregar_Usuario.jsp?id=<%= id %>" class="btn-editar">
            <i class="fas fa-edit"></i> Editar
          </a>
          <form method="POST" action="<%= request.getContextPath() %>/EliminarUsuarioServlet" 
                onsubmit="return confirm('¿Eliminar a <%= nombre %>?')">
            <input type="hidden" name="id" value="<%= id %>">
            <button type="submit" class="btn-eliminar">
              <i class="fas fa-trash"></i> Eliminar
            </button>
          </form>
        </td>
      </tr>
    <% } %>
  <% } %>
</tbody>
    </table>
  </main>

  <script>
    // ✅ BUSCADOR MEJORADO (funciona con botones)
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
