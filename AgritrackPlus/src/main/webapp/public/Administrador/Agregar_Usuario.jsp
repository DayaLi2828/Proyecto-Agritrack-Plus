<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.UsuarioDAO" %>
<%@ page import="java.util.Map" %>

<%
  String idUsuario = request.getParameter("id");
  Map<String, String> usuario = null;
  if (idUsuario != null) {
      UsuarioDAO usuarioDAO = new UsuarioDAO();
      usuario = usuarioDAO.obtenerPorId(idUsuario);
  }
%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><%= usuario != null ? "Editar Usuario" : "Agregar Usuario" %></title>
  <link rel="stylesheet" href="../../asset/Administrador/style_RegistroCultivos.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>
  <header>
    <a href="Usuarios.jsp">
      <div class="icono__devolver">
        <img src="../../asset/imagenes/devolver.png" id="icono de devolver">
      </div>
    </a>
    <div class="contenedor__titulo">
      <div class="contenedor__logo">
        <img class="logo" src="../../asset/imagenes/hoja (3).png" alt="hoja del logo">
      </div>
      <h1 class="titulo"><%= usuario != null ? "Editar Usuario" : "Agregar Nuevo Usuario" %></h1>
    </div>
  </header>

  <main class="main">
    <div class="contendor">

      <% if ("true".equals(request.getParameter("error"))) { %>
        <p style="color:red; text-align:center;">Hubo un error al <%= usuario != null ? "editar" : "crear" %> el usuario. Intenta de nuevo.</p>
      <% } %>

      <!-- ✅ ACTION CORREGIDO - ESTA ES LA CLAVE -->
      <form class="formulario__registrarcultivo" method="post" 
            action="<%= request.getContextPath() %>/<%= usuario != null ? "EditarUsuarioServlet" : "CrearUsuarioServlet" %>" 
            enctype="multipart/form-data">

        <% if (usuario != null) { %>
          <input type="hidden" name="id" value="<%= usuario.get("id") %>">
        <% } %>

        <!-- Información personal -->
        <div class="contendor__cajas">
          <div class="contendor__subtitulo">
            <div class="caja__logo">
              <img class="logo" src="../../asset/imagenes/supervisor.png" alt="icono usuario">
            </div>
            <h2 class="subtitulo">Información Personal</h2>
          </div>

          <div class="campo">
            <label>Nombre completo</label>
            <input type="text" name="nombre" placeholder="Ingresa el nombre completo" 
                   value="<%= usuario != null ? usuario.get("nombre") : "" %>" required>
          </div>

          <div class="campo">
            <label>Documento</label>
            <input type="text" name="documento" placeholder="Ingresa el número de documento" 
                   value="<%= usuario != null ? usuario.get("documento") : "" %>" required>
          </div>

          <div class="campo">
            <label>Dirección</label>
            <input type="text" name="direccion" placeholder="Ingresa la dirección" 
                   value="<%= usuario != null ? usuario.get("direccion") : "" %>" required>
          </div>

          <div class="fechas">
            <div class="campo">
              <label>Correo electrónico</label>
              <input type="email" name="correo" placeholder="Ingresa el correo" 
                     value="<%= usuario != null ? usuario.get("correo") : "" %>" required>
            </div>
            <div class="campo">
              <label>Teléfono</label>
              <input type="text" name="telefono" placeholder="Ingresa el teléfono" 
                     value="<%= usuario != null ? usuario.get("telefono") : "" %>" required>
            </div>
              <div class="campo">
                <label></label>
                <div class="custom-file-upload">
                  <label for="imagenInput" class="btn-upload">
                    <i class="fa-solid fa-cloud-arrow-up"></i> Subir Foto
                  </label>
                  <!-- ✅ FUNCIÓN JS CORREGIDA -->
                  <input type="file" name="foto" id="imagenInput" 
                         accept="image/*" onchange="mostrarNombreArchivo(this)">
                  <span id="file-name" class="file-name">Ningún archivo seleccionado</span>
                </div>
              </div>
          </div>
        </div>

        <!-- Credenciales -->
        <div class="contendor__cajas">
          <div class="contendor__subtitulo">
            <div class="caja__logo">
              <img class="logo" src="../../asset/imagenes/stock.png" alt="icono credenciales">
            </div>
            <h2 class="subtitulo">Credenciales de Acceso</h2>
          </div>

          <div class="campo">
            <label>Contraseña</label>
            <input type="password" name="pass" placeholder="Ingresa la contraseña" 
                   value="<%= usuario != null ? usuario.get("pass") : "" %>" required>
          </div>

          <div class="campo">
            <label>Rol</label>
            <select name="rol_id" required>
              <option value="">Seleccione...</option>
              <option value="2" <%= usuario != null && "Administrador".equals(usuario.get("rol")) ? "selected" : "" %>>Administrador</option>
              <option value="3" <%= usuario != null && "Trabajador".equals(usuario.get("rol")) ? "selected" : "" %>>Trabajador</option>
            </select>
          </div>

          <div class="campo">
            <label>Estado</label>
            <select name="estado" required>
              <option value="Activo" <%= usuario != null && "Activo".equals(usuario.get("estado")) ? "selected" : "" %>>Activo</option>
              <option value="Inactivo" <%= usuario != null && "Inactivo".equals(usuario.get("estado")) ? "selected" : "" %>>Inactivo</option>
            </select>
          </div>
        </div>

        <!-- Botón -->
        <div class="contenedor__boton--enviar">
          <button type="submit" class="boton__enviar"><%= usuario != null ? "Guardar Cambios" : "Crear Usuario" %></button>
        </div>

      </form>
    </div>
  </main>

  <script>

    function mostrarNombreArchivo(input) {
        const nombreArchivo = document.getElementById("file-name");
        if (input.files && input.files[0]) {
            nombreArchivo.textContent = input.files[0].name;
        } else {
            nombreArchivo.textContent = "Ningún archivo seleccionado";
        }
    }

    function filtrar() {
        let texto = document.getElementById("buscador").value.toLowerCase();
        let filas = document.querySelectorAll("tbody tr");

        filas.forEach(function(fila) {
            let nombre = fila.cells[2].textContent.toLowerCase();
            let correo = fila.cells[4].textContent.toLowerCase();

            if (nombre.includes(texto) || correo.includes(texto)) {
                fila.style.display = "";
            } else {
                fila.style.display = "none";
            }
        });
    }

    document.addEventListener("DOMContentLoaded", function() {
        let buscador = document.getElementById("buscador");
        if (buscador) {
            buscador.addEventListener("input", filtrar);
        }
    });
  </script>
</body>
</html>
