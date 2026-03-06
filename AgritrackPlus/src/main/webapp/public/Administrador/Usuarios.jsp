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
        <img src="../../asset/imagenes/devolver.png" id="icono de devolver" alt="Volver">
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
    <% if ("estado".equals(mensaje)) { %>
      <div class="alerta-exito">Estado actualizado correctamente</div>
    <% } else if ("eliminado".equals(mensaje)) { %>
      <div class="alerta-exito">Usuario eliminado correctamente</div>
    <% } %>

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
        <input type="text" id="buscador" class="input__buscador" placeholder="Buscar usuario por nombre o documento" aria-label="Buscar"/>
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
  <% if (usuarios.isEmpty()) { %>
    <tr>
      <td colspan="10" style="text-align:center; padding: 40px; color: #6b7280;">
        <i class="fas fa-users"></i> No hay usuarios registrados
      </td>
    </tr>
  <% } else { %>
    <% for (Map<String, String> usuario : usuarios) {
        String id = String.valueOf(usuario.get("id"));
        String nombre = usuario.get("nombre") != null ? usuario.get("nombre") : "N/A";
        String doc = usuario.get("documento") != null ? usuario.get("documento") : "N/A";
        String correo = usuario.get("correo") != null ? usuario.get("correo") : "Sin correo";
        String tel = usuario.get("telefono") != null ? usuario.get("telefono") : "Sin tel";
        String rol = usuario.get("rol") != null ? usuario.get("rol") : "Sin rol";
        String estado = usuario.get("estado") != null ? usuario.get("estado") : "Inactivo";
        
        // 1. CORRECCIÓN DE RUTA DE FOTO: Evita parpadeo por rutas nulas o texto "null"
        String fotoRuta = usuario.get("foto");
        if (fotoRuta == null || fotoRuta.trim().isEmpty() || fotoRuta.equalsIgnoreCase("null")) {
            fotoRuta = "asset/imagenes/default-avatar.png";
        }
        boolean esActivo = "Activo".equalsIgnoreCase(estado);
    %>
      <tr>
        <td><strong><%= id %></strong></td>
        <td>
          <%-- 2. CORRECCIÓN ONERROR: El this.onerror=null detiene el bucle de parpadeo --%>
          <img src="../../<%= fotoRuta %>" alt="Foto" class="foto-usuario" 
               style="width: 40px; height: 40px; border-radius: 50%; object-fit: cover;"
               onerror="this.onerror=null; this.src='../../asset/imagenes/default-avatar.png';">
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
    // 3. BUSCADOR OPTIMIZADO: Eliminamos cualquier posible conflicto de recarga
    document.getElementById("buscador").addEventListener("keyup", function() {
      let texto = this.value.toLowerCase().trim();
      let filas = document.querySelectorAll("#tablaUsuarios tbody tr");
      
      filas.forEach(function(fila) {
        // Buscamos en toda la fila para mayor precisión
        let contenidoFila = fila.textContent.toLowerCase();
        if (contenidoFila.includes(texto)) {
          fila.style.display = "";
        } else {
          fila.style.display = "none";
        }
      });
    });
  </script>
</body>
</html>