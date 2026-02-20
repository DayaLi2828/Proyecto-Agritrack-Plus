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
      <a href="CrearUsuario.jsp" class="boton">Agregar Usuario</a>
    </div>
  </header>

  <main>
    <div class="buscador__contenedor">
      <input type="text" id="buscador" placeholder=" Buscar usuario por nombre o documento..."/>
      <select id="filtroEstado">
        <option value="todos">Todos</option>
        <option value="Activo">Activo</option>
        <option value="Inactivo">Inactivo</option>
      </select>
    </div>

    <div class="main__contenedor">
      <%
        UsuarioDAO dao = new UsuarioDAO();
        List<Map<String, String>> usuarios = dao.listarUsuarios();
        if (usuarios.isEmpty()) {
      %>
        <p>No hay usuarios registrados.</p>
      <%
        } else {
          for (Map<String, String> usuario : usuarios) {
      %>
        <section class="contendor__informacion contenedor__usuario">
          <div class="caja__titulo">
            <img class="logo" src="../../asset/imagenes/supervisor.png" alt="icono usuario"/>
            <h2 class="titulo">Información del Usuario</h2>
          </div>
          <div class="contenedor__registros">
            <div class="fila__producto">
              <div class="dato__producto">
                <p><strong>ID:</strong> <%= usuario.get("id") %></p>
              </div>
              <div class="dato__producto">
                <p><strong>Nombre:</strong> <%= usuario.get("nombre") %></p>
              </div>
            </div>
            <div class="fila__producto">
              <div class="dato__producto">
                <p><strong>Documento:</strong> <%= usuario.get("documento") %></p>
              </div>
              <div class="dato__producto">
                <p><strong>Dirección:</strong> <%= usuario.get("direccion") %></p>
              </div>
            </div>
            <div class="fila__producto">
              <div class="dato__producto">
                <p><strong>Correo:</strong> <%= usuario.get("correo") %></p>
              </div>
              <div class="dato__producto">
                <p><strong>Teléfono:</strong> <%= usuario.get("telefono") %></p>
              </div>
            </div>
            <div class="fila__producto">
              <div class="dato__producto">
                <p><strong>Estado:</strong> <%= usuario.get("estado") %></p>
              </div>
              <div class="dato__producto">
                <p><strong>Rol:</strong> <%= usuario.get("rol") %></p>
              </div>
            </div>
            <div class="fila__producto botones__acciones">
              <a href="EditarUsuario.jsp?id=<%= usuario.get("id") %>" class="boton__editar">Editar</a>
              <a href="../../EliminarUsuarioServlet?id=<%= usuario.get("id") %>" class="boton__eliminar" onclick="return confirm('¿Estás seguro de eliminar este usuario?')">Eliminar</a>
            </div>
          </div>
        </section>
      <%
          }
        }
      %>
    </div>
  </main>
</body>
</html>