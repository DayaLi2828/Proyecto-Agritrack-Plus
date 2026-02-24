<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.Registro_CultivoDAO" %>
<%@ page import="java.util.Map" %>
<%
  String id = request.getParameter("id");
  Registro_CultivoDAO dao = new Registro_CultivoDAO();
  Map<String, String> cultivo = dao.obtenerPorId(id);
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
    <a href="Cultivos_Registrados.jsp">
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

      <% if ("true".equals(request.getParameter("error"))) { %>
        <p style="color:red;">Hubo un error al editar el cultivo. Intenta de nuevo.</p>
      <% } %>

      <form class="formulario__registrarcultivo" method="post" action="../../EditarCultivoServlet">

        <input type="hidden" name="id" value="<%= cultivo.get("id") %>">

        <div class="contendor__cajas">
          <div class="contendor__subtitulo">
            <div class="caja__logo">
              <img class="logo" src="../../asset/imagenes/planta (2).png" alt="logo planta">
            </div>
            <h2 class="subtitulo">Información del Cultivo</h2>
          </div>

          <div class="campo">
            <label for="nombre">Nombre del cultivo</label>
            <input type="text" id="nombre" name="nombre" 
                   value="<%= cultivo.get("nombre") %>" required>
          </div>

          <div class="campo">
            <label for="fecha_siembra">Fecha de siembra</label>
            <input type="date" id="fecha_siembra" name="fecha_siembra" 
                   value="<%= cultivo.get("fecha_siembra") %>" required>
          </div>

          <div class="campo">
            <label for="fecha_cosecha">Fecha de cosecha</label>
            <input type="date" id="fecha_cosecha" name="fecha_cosecha"
                   value="<%= cultivo.get("fecha_cosecha") != null ? cultivo.get("fecha_cosecha") : "" %>">
          </div>

          <div class="campo">
            <label for="ciclo">Ciclo del cultivo</label>
            <select id="ciclo" name="ciclo" required>
              <option value="Vegetativo" <%= "Vegetativo".equals(cultivo.get("ciclo")) ? "selected" : "" %>>Vegetativo</option>
              <option value="Floracion" <%= "Floracion".equals(cultivo.get("ciclo")) ? "selected" : "" %>>Floración</option>
              <option value="Maduracion" <%= "Maduracion".equals(cultivo.get("ciclo")) ? "selected" : "" %>>Maduración</option>
            </select>
          </div>

          <div class="campo">
            <label for="estado">Estado</label>
            <select id="estado" name="estado" required>
              <option value="Activo" <%= "Activo".equals(cultivo.get("estado")) ? "selected" : "" %>>Activo</option>
              <option value="Inactivo" <%= "Inactivo".equals(cultivo.get("estado")) ? "selected" : "" %>>Inactivo</option>
            </select>
          </div>
        </div>

        <div class="contenedor__boton--enviar">
          <button type="submit" class="boton__enviar">Guardar cambios</button>
        </div>

      </form>
    </div>
  </main>
</body>
</html>