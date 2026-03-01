<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.UsuarioDAO" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.net.URLEncoder" %>

<%
  String idUsuario = request.getParameter("id");
  Map<String, String> usuario = null;
  if (idUsuario != null) {
      UsuarioDAO usuarioDAO = new UsuarioDAO();
      usuario = usuarioDAO.obtenerPorId(idUsuario);
  }
  
  // ✅ RECUPERAR TODOS LOS DATOS (NO SE PIERDEN)
  String paramNombre = request.getParameter("nombre");
  String paramDoc = request.getParameter("documento");
  String paramCorreo = request.getParameter("correo");
  String paramTel = request.getParameter("telefono");
  String paramDir = request.getParameter("direccion");
  String paramEstado = request.getParameter("estado");
  String paramRol = request.getParameter("rol_id");
  
  // ✅ ERRORES DEL SERVLET
  String errorDoc = request.getParameter("error_doc");
  String errorTel = request.getParameter("error_tel");
  String errorCorreo = request.getParameter("error_correo");
  String errorNombre = request.getParameter("error_nombre");
  String errorRol = request.getParameter("error_rol");
  String errorDuplicado = request.getParameter("error_duplicado");
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
        <img src="../../asset/imagenes/devolver.png">
      </div>
    </a>
    <div class="contenedor__titulo">
      <div class="contenedor__logo">
        <img class="logo" src="../../asset/imagenes/hoja (3).png">
      </div>
      <h1 class="titulo"><%= usuario != null ? "Editar Usuario" : "Agregar Nuevo Usuario" %></h1>
    </div>
  </header>

  <main class="main">
    <div class="contendor">
      
      <!-- ✅ INFO EDICIÓN -->
      <% if (usuario != null) { %>
        <div class="info-edicion">
           <strong>Contraseña:</strong> La contraseña actual se mantiene. Para cambiarla contacta al administrador.
        </div>
      <% } %>

      <!-- ✅ ALERTA DUPLICADO -->
      <% if ("true".equals(errorDuplicado)) { %>
        <div class="alerta-error">
          <strong> DOCUMENTO O CORREO YA EXISTE</strong>
        </div>
      <% } %>

      <form class="formulario__registrarcultivo" method="post" 
            action="<%= request.getContextPath() %>/<%= usuario != null ? "EditarUsuarioServlet" : "CrearUsuarioServlet" %>" 
            enctype="multipart/form-data">

        <% if (usuario != null) { %>
          <input type="hidden" name="id" value="<%= usuario.get("id") %>">
          <!-- 🚀 CLAVE: Campo oculto pass vacío para evitar errores en servlet -->
          <input type="hidden" name="pass" value="">
          <input type="hidden" name="estado" value="Activo">
        <% } %>

        <!-- INFORMACIÓN PERSONAL -->
        <div class="contendor__cajas">
          <div class="contendor__subtitulo">
            <div class="caja__logo">
              <img class="logo" src="../../asset/imagenes/supervisor.png">
            </div>
            <h2 class="subtitulo">Información Personal</h2>
          </div>

          <!-- NOMBRE -->
          <div class="campo <%= "true".equals(errorNombre) ? "campo-error" : "" %>">
            <label> Nombre completo <span style="color:#dc3545;">*</span></label>
            <input type="text" name="nombre" placeholder="Juan Pérez" 
                   value="<%= paramNombre != null ? paramNombre : (usuario != null ? usuario.get("nombre") : "") %>" required>
            <% if ("true".equals(errorNombre)) { %>
              <span class="error-mensaje"> Solo letras, máximo 50 caracteres (sin números)</span>
            <% } %>
          </div>

          <!-- DOCUMENTO -->
          <div class="campo <%= "true".equals(errorDoc) ? "campo-error" : "" %>">
            <label> Documento <span style="color:#dc3545;">*</span></label>
            <input type="text" name="documento" placeholder="1234567890" 
                   value="<%= paramDoc != null ? paramDoc : (usuario != null ? usuario.get("documento") : "") %>" required
                   oninput="this.value=this.value.replace(/[^0-9]/g,'')" maxlength="10">
            <% if ("true".equals(errorDoc)) { %>
              <span class="error-mensaje"> DEBE SER EXACTAMENTE 10 NÚMEROS</span>
            <% } %>
          </div>

          <!-- DIRECCIÓN -->
          <div class="campo">
            <label> Dirección <span style="color:#dc3545;">*</span></label>
            <input type="text" name="direccion" placeholder="Calle 123 #45-67"
                   value="<%= paramDir != null ? paramDir : (usuario != null ? usuario.get("direccion") : "") %>" required>
          </div>

          <div class="fechas">
            <!-- CORREO -->
            <div class="campo <%= "true".equals(errorCorreo) ? "campo-error" : "" %>">
              <label>Correo electrónico <span style="color:#dc3545;">*</span></label>
              <input type="email" name="correo" placeholder="usuario@dominio.com"
                     value="<%= paramCorreo != null ? paramCorreo : (usuario != null ? usuario.get("correo") : "") %>" required>
              <% if ("true".equals(errorCorreo)) { %>
                <span class="error-mensaje">Formato de correo inválido</span>
              <% } %>
            </div>

            <!-- TELÉFONO -->
            <div class="campo <%= "true".equals(errorTel) ? "campo-error" : "" %>">
              <label>Teléfono <span style="color:#dc3545;">*</span></label>
              <input type="tel" name="telefono" placeholder="3001234567" maxlength="10"
                     value="<%= paramTel != null ? paramTel : (usuario != null ? usuario.get("telefono") : "") %>" required
                     oninput="this.value=this.value.replace(/[^0-9]/g,'')">
              <% if ("true".equals(errorTel)) { %>
                <span class="error-mensaje"> DEBE SER EXACTAMENTE 10 NÚMEROS</span>
              <% } %>
            </div>

            <!-- FOTO -->
            <div class="campo">
              <label> Foto</label>
              <div class="custom-file-upload">
                <label for="imagenInput" class="btn-upload">
                  <i class="fa-solid fa-cloud-arrow-up"></i> Subir Foto
                </label>
                <input type="file" name="foto" id="imagenInput" accept="image/*">
                <span id="file-name" class="file-name">Ningún archivo seleccionado</span>
              </div>
              <% if (usuario != null && usuario.get("foto") != null) { %>
                <div style="margin-top:10px;font-size:0.9em;color:#666;">
                   Foto actual: <%= usuario.get("foto") %>
                </div>
              <% } %>
            </div>
          </div>
        </div>
            
        <div class="contendor__cajas">
            <div class="contendor__subtitulo">
                <h2 class="subtitulo">Seguridad y Acceso</h2>
            </div>

            <% if (usuario == null) { %>
                <div class="campo <%= "true".equals(request.getParameter("error_pass")) ? "campo-error" : "" %>">
                    <label> Contraseña <span style="color:#dc3545;">*</span></label>
                    <input type="password" name="pass" placeholder="Mínimo 6 caracteres" required>
                    <% if ("true".equals(request.getParameter("error_pass"))) { %>
                        <span class="error-mensaje"> La contraseña debe tener al menos 6 caracteres</span>
                    <% } %>
                </div>
            <% } else { %>
                <input type="hidden" name="id" value="<%= usuario.get("id") %>">
                <input type="hidden" name="pass" value=""> <% } %>
        </div>
        <!-- ROL -->
        <div class="contendor__cajas">
          <div class="contendor__subtitulo">
            <div class="caja__logo">
              <img class="logo" src="../../asset/imagenes/stock.png">
            </div>
            <h2 class="subtitulo">Rol del Usuario</h2>
          </div>

          <!-- ROL - SIEMPRE PRESELECCIONADO -->
          <div class="campo <%= "true".equals(errorRol) ? "campo-error" : "" %>">
            <label> Rol <span style="color:#dc3545;">*</span></label>
            <select name="rol_id" required>
              <option value="">Selecciona...</option>
              <option value="2" <%= "1".equals(paramRol) || 
                  (usuario != null && ("Trabajador".equals(usuario.get("rol")) || "1".equals(usuario.get("rol")) || "1".equals(usuario.get("rol_id")))) ? "selected" : "" %>>Trabajador</option>
              <option value="3" <%= "2".equals(paramRol) || 
                  (usuario != null && ("Supervisor".equals(usuario.get("rol")) || "2".equals(usuario.get("rol")) || "2".equals(usuario.get("rol_id")))) ? "selected" : "" %>>Supervisor</option>
            </select>
            <% if ("true".equals(errorRol)) { %>
              <span class="error-mensaje"> Selecciona un rol</span>
            <% } %>
          </div>
        </div>

        <div class="contenedor__boton--enviar">
          <button type="submit" class="boton__enviar">
            <%= usuario != null ? "💾 Guardar Cambios" : "➕ Crear Usuario" %>
          </button>
        </div>
      </form>
    </div>
  </main>

  <script>
    document.getElementById('imagenInput').addEventListener('change', function() {
      document.getElementById('file-name').textContent = 
        this.files[0] ? this.files[0].name : 'Ningún archivo seleccionado';
    });
  </script>
</body>
</html>
