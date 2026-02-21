<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Agregar Usuario</title>
  <link rel="stylesheet" href="../../asset/Administrador/style_RegistroCultivos.css">
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
      <h1 class="titulo">Agregar Nuevo Usuario</h1>
    </div>
  </header>

  <main class="main">
    <div class="contendor">

      <% if ("true".equals(request.getParameter("error"))) { %>
        <p style="color:red; text-align:center;">Hubo un error al crear el usuario. Intenta de nuevo.</p>
      <% } %>

      <form class="formulario__registrarcultivo" method="post" action="../../CrearUsuarioServlet">

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
            <input type="text" name="nombre" placeholder="Ingresa el nombre completo" required>
          </div>

          <div class="campo">
            <label>Documento</label>
            <input type="text" name="documento" placeholder="Ingresa el número de documento" required>
          </div>

          <div class="campo">
            <label>Dirección</label>
            <input type="text" name="direccion" placeholder="Ingresa la dirección" required>
          </div>

          <div class="fechas">
            <div class="campo">
              <label>Correo electrónico</label>
              <input type="email" name="correo" placeholder="Ingresa el correo" required>
            </div>
            <div class="campo">
              <label>Teléfono</label>
              <input type="text" name="telefono" placeholder="Ingresa el teléfono" required>
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
            <input type="password" name="pass" placeholder="Ingresa la contraseña" required>
          </div>

          <div class="campo">
            <label>Rol</label>
            <select name="rol_id" required>
              <option value="">Seleccione...</option>
              <option value="2">Administrador</option>
              <option value="3">Trabajador</option>
            </select>
          </div>

          <div class="campo">
            <label>Estado</label>
            <select name="estado" required>
              <option value="Activo">Activo</option>
              <option value="Inactivo">Inactivo</option>
            </select>
          </div>
        </div>

        <!-- Botón -->
        <div class="contenedor__boton--enviar">
          <button type="submit" class="boton__enviar">Crear Usuario</button>
        </div>

      </form>
    </div>
  </main>
</body>
</html>